package com.smartarchive.archivemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartarchive.archivemanage.dto.ArchiveSummaryResponse;
import com.smartarchive.archivemanage.dto.PendingAuditAttachmentRef;
import com.smartarchive.archivemanage.dto.PendingDocumentWriteCommand;
import com.smartarchive.archivemanage.service.ArchiveManagementService;
import com.smartarchive.archivemanage.service.DocumentTypeExtFieldService;
import com.smartarchive.archivemanage.service.PendingArchiveBatchImportService;
import com.smartarchive.archivemanage.service.support.PendingArchiveBatchImportHeaderResolver;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.companyproject.domain.CompanyProject;
import com.smartarchive.companyproject.mapper.CompanyProjectMapper;
import com.smartarchive.workspace.domain.WorkspaceIoJob;
import com.smartarchive.workspace.dto.WorkspaceIoJobCreateCommand;
import com.smartarchive.workspace.dto.WorkspaceIoJobSummaryResponse;
import com.smartarchive.workspace.mapper.WorkspaceIoJobMapper;
import com.smartarchive.workspace.service.WorkspaceIoJobService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PendingArchiveBatchImportServiceImpl implements PendingArchiveBatchImportService {

    private static final int MAX_DATA_ROWS = 5000;
    private static final int MAX_BASE64_CHARS = 25_000_000;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Set<String> TOP_LEVEL_KEYS = Set.of(
        "companyProjectCode", "archiveTypeCode", "businessCode", "beginPeriod", "endPeriod",
        "archiveDestination", "originPlace", "documentName", "documentDate", "dutyPerson",
        "dutyDepartment", "carrierTypeCode", "sourceSystem", "securityLevelCode", "remark",
        "documentOrganizationCode", "retentionPeriodYears", "custodyStatus", "visibility", "barcodeModule"
    );

    private final WorkspaceIoJobService workspaceIoJobService;
    private final WorkspaceIoJobMapper workspaceIoJobMapper;
    private final ArchiveManagementService archiveManagementService;
    private final CompanyProjectMapper companyProjectMapper;
    private final DocumentTypeExtFieldService documentTypeExtFieldService;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    @Qualifier("pendingArchiveBatchExecutor")
    private final Executor taskExecutor;

    @Override
    public WorkspaceIoJobSummaryResponse submit(
        MultipartFile file,
        String documentTypeCode,
        String operationRemark,
        List<PendingAuditAttachmentRef> auditAttachments,
        long operatorUserId
    ) {
        if (!StringUtils.hasText(documentTypeCode)) {
            throw new BusinessException("documentTypeCode is required");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请上传 CSV 文件");
        }
        String fname = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename().trim() : "import.csv";
        WorkspaceIoJobCreateCommand create = new WorkspaceIoJobCreateCommand();
        create.setJobType("IMPORT_PENDING_ARCHIVE");
        create.setDataType("PENDING_ARCHIVE");
        create.setJobName(fname);
        create.setDocumentTypeCode(documentTypeCode.trim());
        create.setInputFileName(fname);
        create.setJobStatus("RUNNING");
        create.setExportFileFormat("XLSX");
        WorkspaceIoJobSummaryResponse started = workspaceIoJobService.create(create, operatorUserId);
        Long jobId = started.getJobId();
        final byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new BusinessException("读取上传文件失败: " + e.getMessage());
        }
        final String dt = documentTypeCode.trim();
        final String opRemark = StringUtils.hasText(operationRemark) ? operationRemark.trim() : null;
        final List<PendingAuditAttachmentRef> auditRefs = auditAttachments == null || auditAttachments.isEmpty()
            ? List.of()
            : List.copyOf(auditAttachments);
        taskExecutor.execute(() -> runImport(jobId, bytes, dt, operatorUserId, opRemark, auditRefs));
        return workspaceIoJobService.get(jobId, operatorUserId);
    }

    private void runImport(
        long jobId,
        byte[] fileBytes,
        String documentTypeCode,
        long operatorUserId,
        String operationRemark,
        List<PendingAuditAttachmentRef> auditAttachments
    ) {
        long t0 = System.currentTimeMillis();
        try {
            List<String> headers;
            List<CSVRecord> dataRecords;
            try {
                CSVParser parser = CSVParser.parse(
                    new String(fileBytes, StandardCharsets.UTF_8),
                    CSVFormat.DEFAULT
                );
                List<CSVRecord> all = parser.getRecords();
                parser.close();
                if (all.isEmpty()) {
                    persistJobOutcome(jobId, operatorUserId, 0, 0, System.currentTimeMillis() - t0, "FAILED",
                        "CSV 为空", fatalWorkbookBase64("CSV 为空"));
                    return;
                }
                List<String> rawHeader = new ArrayList<>(all.get(0).toList());
                if (!rawHeader.isEmpty()) {
                    String h0 = rawHeader.get(0);
                    if (h0 != null && !h0.isEmpty() && h0.charAt(0) == '\uFEFF') {
                        rawHeader.set(0, h0.substring(1));
                    }
                }
                headers = rawHeader.stream().map(h -> h == null ? "" : h.trim()).toList();
                dataRecords = new ArrayList<>();
                for (int i = 1; i < all.size(); i++) {
                    CSVRecord rec = all.get(i);
                    if (recordAllBlank(rec)) {
                        continue;
                    }
                    dataRecords.add(rec);
                }
            } catch (Exception ex) {
                log.warn("pending batch import parse error jobId={}", jobId, ex);
                persistJobOutcome(jobId, operatorUserId, 0, 0, System.currentTimeMillis() - t0, "FAILED",
                    "解析 CSV 失败: " + ex.getMessage(), fatalWorkbookBase64("解析 CSV 失败: " + ex.getMessage()));
                return;
            }

            if (headers.stream().allMatch(h -> !StringUtils.hasText(h))) {
                persistJobOutcome(jobId, operatorUserId, 0, 0, System.currentTimeMillis() - t0, "FAILED",
                    "表头无效", fatalWorkbookBase64("表头无效"));
                return;
            }

            if (dataRecords.size() > MAX_DATA_ROWS) {
                persistJobOutcome(jobId, operatorUserId, dataRecords.size(), 0, System.currentTimeMillis() - t0, "FAILED",
                    "单次最多导入 " + MAX_DATA_ROWS + " 行", fatalWorkbookBase64("超过行数上限"));
                return;
            }

            Map<String, String> fieldDisplayMap;
            try {
                fieldDisplayMap = PendingArchiveBatchImportHeaderResolver.buildFieldDisplayNameToCodeMap(
                    documentTypeExtFieldService.listEffective(documentTypeCode));
            } catch (Exception ex) {
                log.warn("pending batch ext field label map jobId={}", jobId, ex);
                fieldDisplayMap = new LinkedHashMap<>();
            }
            List<String> canonicalHeaders = new ArrayList<>(headers.size());
            for (String h : headers) {
                canonicalHeaders.add(PendingArchiveBatchImportHeaderResolver.resolve(h, fieldDisplayMap));
            }
            ImportLookupMaps lookupMaps = loadLookupMaps();

            List<List<String>> sheet1Rows = new ArrayList<>();
            List<ArchiveSummaryResponse> successes = new ArrayList<>();
            int ok = 0;
            List<String> sheet1Headers = new ArrayList<>(headers);
            sheet1Headers.add("导入结果");
            sheet1Headers.add("失败原因");

            for (CSVRecord rec : dataRecords) {
                String importStatus;
                String failureReason = "";
                try {
                    Map<String, String> rowVals = mergeRowValues(headers, canonicalHeaders, rec);
                    PendingDocumentWriteCommand cmd = buildCommandFromValues(
                        rowVals,
                        documentTypeCode,
                        operatorUserId,
                        operationRemark,
                        auditAttachments,
                        lookupMaps
                    );
                    enrichExtFromCompany(cmd);
                    ArchiveSummaryResponse created = archiveManagementService.createPendingDocument(cmd);
                    successes.add(created);
                    ok++;
                    importStatus = "成功";
                } catch (Exception ex) {
                    importStatus = "失败";
                    failureReason = ex.getMessage() != null ? ex.getMessage() : "未知错误";
                    log.debug("pending batch row failed jobId={} msg={}", jobId, failureReason);
                }
                List<String> line = new ArrayList<>();
                for (int hi = 0; hi < headers.size(); hi++) {
                    line.add(cell(rec, hi));
                }
                line.add(importStatus);
                line.add(failureReason);
                sheet1Rows.add(line);
            }

            byte[] xlsx = buildResultWorkbook(sheet1Headers, sheet1Rows, successes);
            String b64 = java.util.Base64.getEncoder().encodeToString(xlsx);
            if (b64.length() > MAX_BASE64_CHARS) {
                persistJobOutcome(jobId, operatorUserId, dataRecords.size(), ok, System.currentTimeMillis() - t0, "FAILED",
                    "结果文件过大", null);
                return;
            }

            int n = dataRecords.size();
            String status = ok == n ? "SUCCESS" : (ok == 0 ? "FAILED" : "PARTIAL_FAILED");
            String err = ok == n ? null : (ok == 0 ? "全部失败" : "部分失败");
            persistJobOutcome(jobId, operatorUserId, n, ok, System.currentTimeMillis() - t0, status, err, b64);
        } catch (Exception e) {
            log.error("pending batch import jobId={}", jobId, e);
            try {
                persistJobOutcome(jobId, operatorUserId, 0, 0, System.currentTimeMillis() - t0, "FAILED",
                    e.getMessage(), fatalWorkbookBase64(e.getMessage() != null ? e.getMessage() : "系统错误"));
            } catch (Exception ignored) {
                workspaceIoJobMapper.update(null, new LambdaUpdateWrapper<WorkspaceIoJob>()
                    .eq(WorkspaceIoJob::getJobId, jobId)
                    .set(WorkspaceIoJob::getJobStatus, "FAILED")
                    .set(WorkspaceIoJob::getErrorMessage, e.getMessage())
                    .set(WorkspaceIoJob::getLastUpdateDate, java.time.LocalDateTime.now()));
            }
        }
    }

    private static boolean recordAllBlank(CSVRecord rec) {
        for (String s : rec) {
            if (StringUtils.hasText(s)) {
                return false;
            }
        }
        return true;
    }

    private String fatalWorkbookBase64(String message) {
        try {
            return java.util.Base64.getEncoder().encodeToString(buildFatalWorkbook(message));
        } catch (IOException e) {
            return null;
        }
    }

    private void persistJobOutcome(long jobId, long opId, int inputTotal, int resultOk, long durationMs, String status,
        String errSummary, String base64) {
        workspaceIoJobMapper.update(null, new LambdaUpdateWrapper<WorkspaceIoJob>()
            .eq(WorkspaceIoJob::getJobId, jobId)
            .set(WorkspaceIoJob::getInputTotal, inputTotal)
            .set(WorkspaceIoJob::getResultTotal, resultOk)
            .set(WorkspaceIoJob::getDurationMs, durationMs)
            .set(WorkspaceIoJob::getJobStatus, status)
            .set(WorkspaceIoJob::getErrorMessage, errSummary)
            .set(WorkspaceIoJob::getResultArtifactBase64, base64)
            .set(WorkspaceIoJob::getArtifactExpiresAt, java.time.LocalDateTime.now().plusDays(7))
            .set(WorkspaceIoJob::getLastUpdatedBy, opId)
            .set(WorkspaceIoJob::getLastUpdateDate, java.time.LocalDateTime.now()));
    }

    private byte[] buildFatalWorkbook(String message) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("导入说明");
            Row r0 = sh.createRow(0);
            r0.createCell(0).setCellValue("说明");
            Row r1 = sh.createRow(1);
            r1.createCell(0).setCellValue(message);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            return bos.toByteArray();
        }
    }

    private byte[] buildResultWorkbook(List<String> sheet1Headers, List<List<String>> sheet1Rows,
        List<ArchiveSummaryResponse> successes) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet s1 = wb.createSheet("导入明细");
            Row h1 = s1.createRow(0);
            for (int c = 0; c < sheet1Headers.size(); c++) {
                h1.createCell(c).setCellValue(sheet1Headers.get(c));
            }
            for (int r = 0; r < sheet1Rows.size(); r++) {
                Row row = s1.createRow(r + 1);
                List<String> cells = sheet1Rows.get(r);
                for (int c = 0; c < cells.size(); c++) {
                    row.createCell(c).setCellValue(cells.get(c) != null ? cells.get(c) : "");
                }
            }

            Sheet s2 = wb.createSheet("成功明细");
            String[] h2 = new String[] {
                "文档ID", "文档类型编码", "文档类型名称", "公司编码", "公司名称", "业务模块编码", "文档业务编码",
                "开始档期", "结束档期", "文档名称", "文档生成日期", "归档责任人", "责任部门", "归档地", "产生地",
                "密级", "文档组织", "保管状态", "生命周期", "描述", "保管年限", "载体类型", "系统来源", "是否可见",
                "国家", "代表处", "地区部", "公司标签", "发票号",
                "其他相关编号1", "其他相关编号2", "其他相关编号3", "其他相关编号4", "其他相关编号5",
                "会计", "扫描员", "开立日期", "到期日", "保函失效日期", "保函台账状态", "银行名称", "币种", "金额", "签发机构",
                "报废时间", "业务册号", "保函电子流编号", "保函编号", "条码模块"
            };
            Row hr = s2.createRow(0);
            for (int c = 0; c < h2.length; c++) {
                hr.createCell(c).setCellValue(h2[c]);
            }
            if (successes.isEmpty()) {
                Row r = s2.createRow(1);
                r.createCell(0).setCellValue("（无成功记录）");
            } else {
                int rowIdx = 1;
                for (ArchiveSummaryResponse d : successes) {
                    Row row = s2.createRow(rowIdx++);
                    Map<String, String> ext = d.getExtValues() == null ? Map.of() : d.getExtValues();
                    String[] refs = splitRefNos(ext.get("refNo"));
                    int c = 0;
                    setStr(row, c++, d.getArchiveId() != null ? String.valueOf(d.getArchiveId()) : "");
                    setStr(row, c++, d.getDocumentTypeCode());
                    setStr(row, c++, d.getDocumentTypeName());
                    setStr(row, c++, d.getCompanyProjectCode());
                    setStr(row, c++, d.getCompanyProjectName());
                    setStr(row, c++, d.getBusinessModuleTypeCode());
                    setStr(row, c++, d.getBusinessCode());
                    setStr(row, c++, d.getBeginPeriod());
                    setStr(row, c++, d.getEndPeriod());
                    setStr(row, c++, d.getDocumentName());
                    row.createCell(c++).setCellValue(d.getDocumentDate() != null ? DT_FMT.format(d.getDocumentDate()) : "");
                    setStr(row, c++, d.getDutyPerson());
                    setStr(row, c++, d.getDutyDepartment());
                    setStr(row, c++, d.getArchiveDestination());
                    setStr(row, c++, d.getOriginPlace());
                    setStr(row, c++, d.getSecurityLevelName());
                    setStr(row, c++, d.getDocumentOrganizationCode());
                    setStr(row, c++, d.getCustodyStatus());
                    setStr(row, c++, d.getLifecycleStatus());
                    setStr(row, c++, d.getRemark());
                    setStr(row, c++, d.getRetentionPeriodYears() != null ? String.valueOf(d.getRetentionPeriodYears()) : "");
                    setStr(row, c++, d.getCarrierTypeCode());
                    setStr(row, c++, d.getSourceSystem());
                    setStr(row, c++, d.getDocumentVisibility());
                    setStr(row, c++, ext.getOrDefault("country", ""));
                    setStr(row, c++, ext.getOrDefault("repOffice", ""));
                    setStr(row, c++, ext.getOrDefault("region", ""));
                    setStr(row, c++, ext.getOrDefault("companyTag", ""));
                    setStr(row, c++, ext.getOrDefault("invoiceNo", ""));
                    setStr(row, c++, refs[0]);
                    setStr(row, c++, refs[1]);
                    setStr(row, c++, refs[2]);
                    setStr(row, c++, refs[3]);
                    setStr(row, c++, refs[4]);
                    setStr(row, c++, ext.getOrDefault("accountant", ""));
                    setStr(row, c++, ext.getOrDefault("scannedBy", ""));
                    setStr(row, c++, ext.getOrDefault("issueDateRange", ""));
                    setStr(row, c++, ext.getOrDefault("maturityDateRange", ""));
                    setStr(row, c++, ext.getOrDefault("lgExpiryDateRange", ""));
                    setStr(row, c++, ext.getOrDefault("lgLedgerStatus", ""));
                    setStr(row, c++, ext.getOrDefault("bankName", ""));
                    setStr(row, c++, ext.getOrDefault("currency", ""));
                    setStr(row, c++, ext.getOrDefault("amount", ""));
                    setStr(row, c++, ext.getOrDefault("issuingAuthority", ""));
                    setStr(row, c++, ext.getOrDefault("disposalTimeRange", ""));
                    setStr(row, c++, ext.getOrDefault("businessVolumeNo", ""));
                    setStr(row, c++, ext.getOrDefault("lgWorkflowNo", ""));
                    setStr(row, c++, ext.getOrDefault("lgNo", ""));
                    setStr(row, c, ext.getOrDefault("barcodeModule", ""));
                }
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            return bos.toByteArray();
        }
    }

    private static void setStr(Row row, int idx, String v) {
        Cell cell = row.createCell(idx);
        cell.setCellValue(v != null ? v : "");
    }

    private static String[] splitRefNos(String rawRefNo) {
        String[] out = new String[] {"", "", "", "", ""};
        if (!StringUtils.hasText(rawRefNo)) {
            return out;
        }
        String[] parts = rawRefNo.split(";");
        for (int i = 0; i < parts.length && i < out.length; i++) {
            out[i] = parts[i] == null ? "" : parts[i].trim();
        }
        return out;
    }

    private record ImportLookupMaps(
        Map<String, String> carrierTypeCodeByInput,
        Map<String, String> securityLevelCodeByInput,
        Map<String, String> custodyStatusCodeByInput
    ) {}

    private ImportLookupMaps loadLookupMaps() {
        Map<String, String> carrier = new LinkedHashMap<>();
        jdbcTemplate.query(
            """
            select item_code, item_name
              from fdc_dict_item_t
             where category_code = 'ARCHIVE_CARRIER_TYPE'
               and enable_flag = 'Y'
               and delete_flag = 'N'
            """,
            rs -> {
                while (rs.next()) {
                    String code = rs.getString("item_code");
                    String name = rs.getString("item_name");
                    putCodeLookup(carrier, code, name);
                }
            }
        );
        Map<String, String> security = new LinkedHashMap<>();
        jdbcTemplate.query(
            """
            select security_level_code, security_level_name
              from fdc_security_level_t
             where enable_flag = 'Y'
               and delete_flag = 'N'
            """,
            rs -> {
                while (rs.next()) {
                    String code = rs.getString("security_level_code");
                    String name = rs.getString("security_level_name");
                    putCodeLookup(security, code, name);
                }
            }
        );
        Map<String, String> custody = new LinkedHashMap<>();
        putCodeLookup(custody, "UNARCHIVED", "未归档");
        putCodeLookup(custody, "ARCHIVED", "已归档");
        putCodeLookup(custody, "DRAFT", "草稿");
        return new ImportLookupMaps(carrier, security, custody);
    }

    private static void putCodeLookup(Map<String, String> lookup, String code, String name) {
        if (StringUtils.hasText(code)) {
            String c = code.trim();
            lookup.putIfAbsent(c.toLowerCase(), c);
            lookup.putIfAbsent(c, c);
        }
        if (StringUtils.hasText(name) && StringUtils.hasText(code)) {
            lookup.putIfAbsent(name.trim().toLowerCase(), code.trim());
            lookup.putIfAbsent(name.trim(), code.trim());
        }
    }

    private static String resolveCodeByDisplayOrCode(String raw, Map<String, String> lookup) {
        if (!StringUtils.hasText(raw)) {
            return raw;
        }
        String v = raw.trim();
        if (lookup == null || lookup.isEmpty()) {
            return v;
        }
        String hit = lookup.get(v);
        if (StringUtils.hasText(hit)) {
            return hit;
        }
        hit = lookup.get(v.toLowerCase());
        return StringUtils.hasText(hit) ? hit : v;
    }

    private static Map<String, String> mergeRowValues(List<String> rawHeaders, List<String> canonicalHeaders, CSVRecord rec) {
        Map<String, String> row = new LinkedHashMap<>();
        int limit = Math.min(rawHeaders.size(), canonicalHeaders.size());
        for (int i = 0; i < limit; i++) {
            String canon = canonicalHeaders.get(i);
            if (!StringUtils.hasText(canon)) {
                continue;
            }
            String cellVal = cell(rec, i);
            row.merge(canon, cellVal, (a, b) -> StringUtils.hasText(b) ? b : (a != null ? a : ""));
        }
        return row;
    }

    private PendingDocumentWriteCommand buildCommandFromValues(
        Map<String, String> values,
        String documentTypeCode,
        long operatorUserId,
        String operationRemark,
        List<PendingAuditAttachmentRef> auditAttachments,
        ImportLookupMaps lookupMaps
    ) {
        PendingDocumentWriteCommand cmd = new PendingDocumentWriteCommand();
        cmd.setDocumentTypeCode(documentTypeCode);
        cmd.setCompanyProjectCode(getv(values, "companyProjectCode"));
        cmd.setArchiveTypeCode(getv(values, "archiveTypeCode"));
        cmd.setBusinessCode(getv(values, "businessCode"));
        cmd.setBeginPeriod(getv(values, "beginPeriod"));
        String end = getv(values, "endPeriod");
        cmd.setEndPeriod(StringUtils.hasText(end) ? end : null);
        cmd.setArchiveDestination(getv(values, "archiveDestination"));
        cmd.setOriginPlace(getv(values, "originPlace"));
        cmd.setDocumentName(getv(values, "documentName"));
        cmd.setDocumentDate(getv(values, "documentDate"));
        cmd.setDutyPerson(getv(values, "dutyPerson"));
        cmd.setDutyDepartment(getv(values, "dutyDepartment"));
        cmd.setCarrierTypeCode(resolveCodeByDisplayOrCode(getv(values, "carrierTypeCode"), lookupMaps.carrierTypeCodeByInput()));
        cmd.setSourceSystem(getv(values, "sourceSystem"));
        cmd.setSecurityLevelCode(resolveCodeByDisplayOrCode(getv(values, "securityLevelCode"), lookupMaps.securityLevelCodeByInput()));
        cmd.setRemark(getv(values, "remark"));
        cmd.setDocumentOrganizationCode(getv(values, "documentOrganizationCode"));
        cmd.setCustodyStatus(resolveCodeByDisplayOrCode(getv(values, "custodyStatus"), lookupMaps.custodyStatusCodeByInput()));

        String ry = getv(values, "retentionPeriodYears");
        if (StringUtils.hasText(ry)) {
            try {
                cmd.setRetentionPeriodYears(Integer.parseInt(ry.trim()));
            } catch (NumberFormatException ignored) {
                // ignore invalid
            }
        }

        Map<String, String> ext = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : values.entrySet()) {
            String key = e.getKey();
            if (!StringUtils.hasText(key)) {
                continue;
            }
            String v = e.getValue() != null ? e.getValue().trim() : "";
            if (TOP_LEVEL_KEYS.contains(key)) {
                if ("visibility".equals(key) || "barcodeModule".equals(key)) {
                    ext.put(key, v);
                }
                continue;
            }
            ext.put(key, v);
        }
        if (!ext.containsKey("visibility") || !StringUtils.hasText(ext.get("visibility"))) {
            ext.putIfAbsent("visibility", "是");
        }
        cmd.setExtValues(ext);
        cmd.setSubmitMode("SUBMIT");
        cmd.setOperationTypeCode("BATCH_CREATE");
        cmd.setOperatorUserId(operatorUserId);
        cmd.setOperationRemark(operationRemark);
        if (auditAttachments != null && !auditAttachments.isEmpty()) {
            cmd.setAuditAttachments(new ArrayList<>(auditAttachments));
        }
        return cmd;
    }

    private static String getv(Map<String, String> m, String k) {
        String v = m.get(k);
        return v != null ? v.trim() : "";
    }

    private void enrichExtFromCompany(PendingDocumentWriteCommand cmd) {
        String code = cmd.getCompanyProjectCode();
        if (!StringUtils.hasText(code)) {
            return;
        }
        CompanyProject cp = companyProjectMapper.selectOne(
            new LambdaQueryWrapper<CompanyProject>()
                .eq(CompanyProject::getCompanyProjectCode, code.trim())
                .eq(CompanyProject::getDeleteFlag, "N")
                .last("limit 1")
        );
        if (cp == null) {
            return;
        }
        Map<String, String> ext = cmd.getExtValues() != null ? new LinkedHashMap<>(cmd.getExtValues()) : new LinkedHashMap<>();
        String cc = cp.getCountryCode() != null ? cp.getCountryCode().trim() : "";
        ext.put("country", cc);
        List<Map<String, Object>> geo = jdbcTemplate.queryForList(
            """
            select min(rep_office_name) as rep_office_name, min(region_name) as region_name
              from fdc_geo_region_t
             where delete_flag = 'N' and country_code = ?
             group by country_code
            """,
            cc
        );
        if (!geo.isEmpty()) {
            ext.put("repOffice", Objects.toString(geo.get(0).get("rep_office_name"), ""));
            ext.put("region", Objects.toString(geo.get(0).get("region_name"), ""));
        } else {
            ext.put("repOffice", "");
            ext.put("region", cp.getManagementArea() != null ? cp.getManagementArea().trim() : "");
        }
        ext.put("companyTag", cp.getCompanyTag() != null ? cp.getCompanyTag().trim() : "");
        cmd.setExtValues(ext);
    }

    private static String cell(CSVRecord rec, int colIndex) {
        if (colIndex < 0 || colIndex >= rec.size()) {
            return "";
        }
        String v = rec.get(colIndex);
        return v != null ? v.trim() : "";
    }
}

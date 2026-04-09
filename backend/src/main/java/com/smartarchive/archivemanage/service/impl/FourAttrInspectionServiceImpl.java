package com.smartarchive.archivemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartarchive.archivemanage.domain.FourAttrInspection;
import com.smartarchive.archivemanage.domain.FourAttrInspectionDetail;
import com.smartarchive.archivemanage.dto.FourAttrInspectionDetailBatchSaveCommand;
import com.smartarchive.archivemanage.dto.FourAttrInspectionDetailResponse;
import com.smartarchive.archivemanage.dto.FourAttrInspectionDetailSaveCommand;
import com.smartarchive.archivemanage.dto.FourAttrInspectionQueryCommand;
import com.smartarchive.archivemanage.dto.FourAttrInspectionResponse;
import com.smartarchive.archivemanage.dto.FourAttrInspectionSaveCommand;
import com.smartarchive.archivemanage.mapper.FourAttrInspectionDetailMapper;
import com.smartarchive.archivemanage.mapper.FourAttrInspectionMapper;
import com.smartarchive.archivemanage.service.FourAttrInspectionService;
import com.smartarchive.common.exception.BusinessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class FourAttrInspectionServiceImpl implements FourAttrInspectionService {
    private static final Long SYSTEM_OPERATOR_ID = 1L;
    private static final Long DEFAULT_TENANT_ID = 1L;

    private final FourAttrInspectionMapper inspectionMapper;
    private final FourAttrInspectionDetailMapper detailMapper;

    @Override
    public List<FourAttrInspectionResponse> list(FourAttrInspectionQueryCommand command) {
        Long tenantId = normalizeTenant(command.getTenantid());
        LambdaQueryWrapper<FourAttrInspection> wrapper = new LambdaQueryWrapper<FourAttrInspection>()
            .eq(FourAttrInspection::getTenantid, tenantId)
            .eq(FourAttrInspection::getDeleteFlag, "N")
            .orderByDesc(FourAttrInspection::getLastUpdateDate)
            .orderByDesc(FourAttrInspection::getInspectionId);
        if (StringUtils.hasText(command.getInspectionName())) {
            wrapper.like(FourAttrInspection::getInspectionName, command.getInspectionName().trim());
        }
        if (StringUtils.hasText(command.getInspectionStage())) {
            wrapper.eq(FourAttrInspection::getInspectionStage, command.getInspectionStage().trim());
        }
        if (StringUtils.hasText(command.getEnableFlag())) {
            wrapper.eq(FourAttrInspection::getEnableFlag, command.getEnableFlag().trim().toUpperCase());
        }
        return inspectionMapper.selectList(wrapper).stream().map(this::toResponseWithoutDetails).toList();
    }

    @Override
    public FourAttrInspectionResponse detail(Long inspectionId, Long tenantid) {
        FourAttrInspection inspection = requireInspection(inspectionId, normalizeTenant(tenantid));
        List<FourAttrInspectionDetailResponse> details = detailMapper.selectList(new LambdaQueryWrapper<FourAttrInspectionDetail>()
                .eq(FourAttrInspectionDetail::getInspectionId, inspectionId)
                .eq(FourAttrInspectionDetail::getTenantid, inspection.getTenantid())
                .eq(FourAttrInspectionDetail::getDeleteFlag, "N")
                .orderByAsc(FourAttrInspectionDetail::getInspectionType)
                .orderByAsc(FourAttrInspectionDetail::getDisplayOrder)
                .orderByAsc(FourAttrInspectionDetail::getDetailId))
            .stream()
            .map(this::toDetailResponse)
            .sorted(Comparator.comparing(FourAttrInspectionDetailResponse::getInspectionType)
                .thenComparing(item -> item.getDisplayOrder() == null ? Integer.MAX_VALUE : item.getDisplayOrder()))
            .toList();
        FourAttrInspectionResponse response = toResponseWithoutDetails(inspection);
        response.setDetails(details);
        return response;
    }

    @Override
    @Transactional
    public FourAttrInspectionResponse create(FourAttrInspectionSaveCommand command) {
        Long tenantId = normalizeTenant(command.getTenantid());
        FourAttrInspection entity = new FourAttrInspection();
        fillInspection(entity, command, tenantId, true);
        inspectionMapper.insert(entity);
        return toResponseWithoutDetails(entity);
    }

    @Override
    @Transactional
    public FourAttrInspectionResponse update(Long inspectionId, FourAttrInspectionSaveCommand command) {
        Long tenantId = normalizeTenant(command.getTenantid());
        FourAttrInspection entity = requireInspection(inspectionId, tenantId);
        fillInspection(entity, command, tenantId, false);
        inspectionMapper.updateById(entity);
        return toResponseWithoutDetails(entity);
    }

    @Override
    @Transactional
    public FourAttrInspectionResponse saveDetails(FourAttrInspectionDetailBatchSaveCommand command) {
        Long tenantId = normalizeTenant(command.getTenantid());
        FourAttrInspection inspection = requireInspection(command.getInspectionId(), tenantId);
        detailMapper.update(null, new LambdaUpdateWrapper<FourAttrInspectionDetail>()
            .eq(FourAttrInspectionDetail::getInspectionId, inspection.getInspectionId())
            .eq(FourAttrInspectionDetail::getTenantid, tenantId)
            .eq(FourAttrInspectionDetail::getDeleteFlag, "N")
            .set(FourAttrInspectionDetail::getDeleteFlag, "Y")
            .set(FourAttrInspectionDetail::getLastUpdatedBy, SYSTEM_OPERATOR_ID)
            .set(FourAttrInspectionDetail::getLastUpdateDate, LocalDateTime.now()));

        List<FourAttrInspectionDetailSaveCommand> details = command.getDetails() == null ? new ArrayList<>() : command.getDetails();
        int index = 1;
        for (FourAttrInspectionDetailSaveCommand item : details) {
            FourAttrInspectionDetail entity = new FourAttrInspectionDetail();
            entity.setInspectionId(inspection.getInspectionId());
            entity.setInspectionType(requireText(item.getInspectionType(), "inspectionType"));
            entity.setInspectionCode(trimToNull(item.getInspectionCode()));
            entity.setInspectionItem(trimToNull(item.getInspectionItem()));
            entity.setInspectionPurpose(trimToNull(item.getInspectionPurpose()));
            entity.setInspectionObject(trimToNull(item.getInspectionObject()));
            entity.setInspectionBasisMethod(trimToNull(item.getInspectionBasisMethod()));
            entity.setDisplayOrder(item.getDisplayOrder() == null ? index : item.getDisplayOrder());
            entity.setEnableFlag(normalizeFlag(item.getEnableFlag(), "Y"));
            entity.setDeleteFlag("N");
            entity.setCreatedBy(SYSTEM_OPERATOR_ID);
            entity.setCreationDate(LocalDateTime.now());
            entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            entity.setLastUpdateDate(LocalDateTime.now());
            entity.setLastUpdateVersion(0);
            entity.setTenantid(tenantId);
            detailMapper.insert(entity);
            index++;
        }

        return detail(inspection.getInspectionId(), tenantId);
    }

    @Override
    public byte[] exportCsv(FourAttrInspectionQueryCommand command) {
        List<FourAttrInspectionResponse> inspections = list(command);
        StringBuilder builder = new StringBuilder();
        builder.append("inspectionName,inspectionStage,dataPackageSpec,metadataSpec,enableFlag,inspectionType,inspectionCode,inspectionItem,inspectionPurpose,inspectionObject,inspectionBasisMethod,detailEnableFlag,displayOrder\n");
        for (FourAttrInspectionResponse inspection : inspections) {
            FourAttrInspectionResponse full = detail(inspection.getInspectionId(), command.getTenantid());
            if (full.getDetails() == null || full.getDetails().isEmpty()) {
                appendHeaderColumns(builder, full);
                builder.append(",,,,,,,\n");
                continue;
            }
            for (FourAttrInspectionDetailResponse d : full.getDetails()) {
                appendHeaderColumns(builder, full);
                builder.append(csvCell(d.getInspectionType())).append(',')
                    .append(csvCell(d.getInspectionCode())).append(',')
                    .append(csvCell(d.getInspectionItem())).append(',')
                    .append(csvCell(d.getInspectionPurpose())).append(',')
                    .append(csvCell(d.getInspectionObject())).append(',')
                    .append(csvCell(d.getInspectionBasisMethod())).append(',')
                    .append(csvCell(d.getEnableFlag())).append(',')
                    .append(d.getDisplayOrder() == null ? "" : d.getDisplayOrder())
                    .append('\n');
            }
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional
    public Integer importCsv(InputStream inputStream, Long tenantid) {
        Long tenantId = normalizeTenant(tenantid);
        Map<String, FourAttrInspectionSaveCommand> headers = new HashMap<>();
        Map<String, List<FourAttrInspectionDetailSaveCommand>> detailsMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (line == null) {
                return 0;
            }
            int importedRows = 0;
            while ((line = reader.readLine()) != null) {
                if (!StringUtils.hasText(line)) {
                    continue;
                }
                String[] cols = parseCsvLine(line);
                if (cols.length < 5) {
                    continue;
                }
                String inspectionName = safeCol(cols, 0);
                String inspectionStage = safeCol(cols, 1);
                if (!StringUtils.hasText(inspectionName) || !StringUtils.hasText(inspectionStage)) {
                    continue;
                }
                String key = inspectionName.trim() + "|" + inspectionStage.trim();
                FourAttrInspectionSaveCommand header = new FourAttrInspectionSaveCommand();
                header.setInspectionName(inspectionName.trim());
                header.setInspectionStage(inspectionStage.trim());
                header.setDataPackageSpec(safeCol(cols, 2));
                header.setMetadataSpec(safeCol(cols, 3));
                header.setEnableFlag(StringUtils.hasText(safeCol(cols, 4)) ? safeCol(cols, 4).trim().toUpperCase() : "Y");
                header.setTenantid(tenantId);
                headers.put(key, header);
                if (StringUtils.hasText(safeCol(cols, 5))) {
                    FourAttrInspectionDetailSaveCommand detail = new FourAttrInspectionDetailSaveCommand();
                    detail.setInspectionType(safeCol(cols, 5).trim());
                    detail.setInspectionCode(safeCol(cols, 6));
                    detail.setInspectionItem(safeCol(cols, 7));
                    detail.setInspectionPurpose(safeCol(cols, 8));
                    detail.setInspectionObject(safeCol(cols, 9));
                    detail.setInspectionBasisMethod(safeCol(cols, 10));
                    detail.setEnableFlag(StringUtils.hasText(safeCol(cols, 11)) ? safeCol(cols, 11).trim().toUpperCase() : "Y");
                    String orderText = safeCol(cols, 12);
                    detail.setDisplayOrder(StringUtils.hasText(orderText) ? Integer.parseInt(orderText.trim()) : null);
                    detailsMap.computeIfAbsent(key, k -> new ArrayList<>()).add(detail);
                }
                importedRows++;
            }
            for (Map.Entry<String, FourAttrInspectionSaveCommand> entry : headers.entrySet()) {
                FourAttrInspection target = inspectionMapper.selectOne(new LambdaQueryWrapper<FourAttrInspection>()
                    .eq(FourAttrInspection::getTenantid, tenantId)
                    .eq(FourAttrInspection::getInspectionName, entry.getValue().getInspectionName())
                    .eq(FourAttrInspection::getInspectionStage, entry.getValue().getInspectionStage())
                    .eq(FourAttrInspection::getDeleteFlag, "N")
                    .last("limit 1"));
                FourAttrInspectionResponse saved = (target == null)
                    ? create(entry.getValue())
                    : update(target.getInspectionId(), entry.getValue());
                FourAttrInspectionDetailBatchSaveCommand detailCommand = new FourAttrInspectionDetailBatchSaveCommand();
                detailCommand.setInspectionId(saved.getInspectionId());
                detailCommand.setTenantid(tenantId);
                detailCommand.setDetails(detailsMap.getOrDefault(entry.getKey(), List.of()));
                saveDetails(detailCommand);
            }
            return importedRows;
        } catch (IOException ex) {
            throw new BusinessException("导入文件读取失败");
        } catch (NumberFormatException ex) {
            throw new BusinessException("导入文件中的 displayOrder 不是数字");
        }
    }

    private void fillInspection(FourAttrInspection entity, FourAttrInspectionSaveCommand command, Long tenantId, boolean creating) {
        entity.setInspectionName(requireText(command.getInspectionName(), "inspectionName"));
        entity.setInspectionStage(requireText(command.getInspectionStage(), "inspectionStage"));
        entity.setDataPackageSpec(requireText(command.getDataPackageSpec(), "dataPackageSpec"));
        entity.setMetadataSpec(requireText(command.getMetadataSpec(), "metadataSpec"));
        entity.setEnableFlag(normalizeFlag(command.getEnableFlag(), "Y"));
        entity.setTenantid(tenantId);
        entity.setDeleteFlag("N");
        if (creating) {
            entity.setCreatedBy(SYSTEM_OPERATOR_ID);
            entity.setCreationDate(LocalDateTime.now());
            entity.setLastUpdateVersion(0);
        }
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
    }

    private FourAttrInspection requireInspection(Long inspectionId, Long tenantId) {
        FourAttrInspection inspection = inspectionMapper.selectOne(new LambdaQueryWrapper<FourAttrInspection>()
            .eq(FourAttrInspection::getInspectionId, inspectionId)
            .eq(FourAttrInspection::getTenantid, tenantId)
            .eq(FourAttrInspection::getDeleteFlag, "N")
            .last("limit 1"));
        if (inspection == null) {
            throw new BusinessException("四性检测方案不存在");
        }
        return inspection;
    }

    private String requireText(String value, String name) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(name + "不能为空");
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeFlag(String flag, String defaultValue) {
        if (!StringUtils.hasText(flag)) {
            return defaultValue;
        }
        String normalized = flag.trim().toUpperCase();
        if (!List.of("Y", "N").contains(normalized)) {
            throw new BusinessException("标识字段仅支持Y/N");
        }
        return normalized;
    }

    private Long normalizeTenant(Long tenantid) {
        return tenantid == null ? DEFAULT_TENANT_ID : tenantid;
    }

    private FourAttrInspectionResponse toResponseWithoutDetails(FourAttrInspection item) {
        return FourAttrInspectionResponse.builder()
            .inspectionId(item.getInspectionId())
            .inspectionName(item.getInspectionName())
            .inspectionStage(item.getInspectionStage())
            .dataPackageSpec(item.getDataPackageSpec())
            .metadataSpec(item.getMetadataSpec())
            .enableFlag(item.getEnableFlag())
            .createdBy(item.getCreatedBy())
            .creationDate(item.getCreationDate())
            .lastUpdatedBy(item.getLastUpdatedBy())
            .lastUpdateDate(item.getLastUpdateDate())
            .details(List.of())
            .build();
    }

    private FourAttrInspectionDetailResponse toDetailResponse(FourAttrInspectionDetail item) {
        return FourAttrInspectionDetailResponse.builder()
            .detailId(item.getDetailId())
            .inspectionType(item.getInspectionType())
            .inspectionCode(item.getInspectionCode())
            .inspectionItem(item.getInspectionItem())
            .inspectionPurpose(item.getInspectionPurpose())
            .inspectionObject(item.getInspectionObject())
            .inspectionBasisMethod(item.getInspectionBasisMethod())
            .displayOrder(item.getDisplayOrder())
            .enableFlag(item.getEnableFlag())
            .build();
    }

    private void appendHeaderColumns(StringBuilder builder, FourAttrInspectionResponse full) {
        builder.append(csvCell(full.getInspectionName())).append(',')
            .append(csvCell(full.getInspectionStage())).append(',')
            .append(csvCell(full.getDataPackageSpec())).append(',')
            .append(csvCell(full.getMetadataSpec())).append(',')
            .append(csvCell(full.getEnableFlag())).append(',');
    }

    private String csvCell(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    private String safeCol(String[] cols, int idx) {
        if (idx >= cols.length) {
            return null;
        }
        return cols[idx];
    }
}

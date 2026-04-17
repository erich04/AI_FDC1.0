package com.smartarchive.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartarchive.archive.command.CreateBorrowOrderCommand;
import com.smartarchive.archive.command.CreateBorrowOrderDetailCommand;
import com.smartarchive.archive.command.CreateBorrowRecordCommand;
import com.smartarchive.archive.domain.BorrowOrder;
import com.smartarchive.archive.domain.BorrowOrderDetail;
import com.smartarchive.archive.dto.BorrowDocumentQueryRequest;
import com.smartarchive.archive.dto.BorrowOrderDetailDto;
import com.smartarchive.archive.dto.BorrowOrderDto;
import com.smartarchive.archive.dto.BorrowOrderQueryRequest;
import com.smartarchive.archive.dto.MyBorrowDocumentDto;
import com.smartarchive.archive.dto.RenewableBorrowOrderDto;
import com.smartarchive.archive.mapper.BorrowOrderDetailMapper;
import com.smartarchive.archive.mapper.BorrowOrderMapper;
import com.smartarchive.archive.service.BorrowOrderService;
import com.smartarchive.archive.service.BorrowRecordService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BorrowOrderServiceImpl implements BorrowOrderService {
    private final BorrowOrderMapper borrowOrderMapper;
    private final BorrowOrderDetailMapper borrowOrderDetailMapper;
    private final BorrowRecordService borrowRecordService;

    @Override
    public List<BorrowOrderDto> listOrders(BorrowOrderQueryRequest request) {
        var wrapper = new LambdaQueryWrapper<BorrowOrder>()
                .orderByDesc(BorrowOrder::getApplyTime);
        if (StringUtils.hasText(request.getOrderNo())) {
            wrapper.like(BorrowOrder::getOrderNo, request.getOrderNo());
        }
        if (StringUtils.hasText(request.getApplicantName())) {
            wrapper.eq(BorrowOrder::getApplicantName, request.getApplicantName());
        }
        if (StringUtils.hasText(request.getUserName())) {
            wrapper.eq(BorrowOrder::getUserName, request.getUserName());
        }
        if (StringUtils.hasText(request.getStatus())) {
            wrapper.eq(BorrowOrder::getStatus, request.getStatus());
        }

        List<BorrowOrder> orders = borrowOrderMapper.selectList(wrapper);
        return filterAndAssembleOrders(orders, request);
    }

    @Override
    public BorrowOrderDto getOrder(String orderNo) {
        BorrowOrder order = borrowOrderMapper.selectOne(
                new LambdaQueryWrapper<BorrowOrder>().eq(BorrowOrder::getOrderNo, orderNo)
        );
        return order == null ? null : assembleOrders(List.of(order)).stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public BorrowOrderDto createOrder(CreateBorrowOrderCommand command) {
        BorrowOrder order = new BorrowOrder();
        order.setOrderNo(StringUtils.hasText(command.getOrderNo()) ? command.getOrderNo() : "BOR-" + System.currentTimeMillis());
        order.setUserName(command.getUserName());
        order.setUserDepartment(command.getUserDepartment());
        order.setApplicantName(command.getApplicantName());
        order.setApplyTime(command.getApplyTime() == null ? LocalDateTime.now() : command.getApplyTime());
        order.setPurpose(command.getPurpose());
        order.setReason(command.getReason());
        order.setReasonAttachment(command.getReasonAttachment());
        order.setApprovalComment(command.getApprovalComment());
        order.setDemandApprover(command.getDemandApprover());
        order.setDemandReviewer(command.getDemandReviewer());
        order.setDemandAnalyst(command.getDemandAnalyst());
        order.setCcUsers(joinUsers(command.getCcUsers()));
        order.setStatus(StringUtils.hasText(command.getStatus()) ? command.getStatus() : "DRAFT");
        order.setWorkflowInstanceId(command.getWorkflowInstanceId());
        order.setCurrentHandler(command.getCurrentHandler());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        borrowOrderMapper.insert(order);

        for (CreateBorrowOrderDetailCommand detailCommand : command.getDetails()) {
            BorrowOrderDetail detail = new BorrowOrderDetail();
            detail.setBorrowOrderId(order.getId());
            detail.setBusinessCode(detailCommand.getBusinessCode());
            detail.setDocumentName(detailCommand.getDocumentName());
            detail.setCompany(detailCommand.getCompany());
            detail.setDocumentType(detailCommand.getDocumentType());
            detail.setDetailDescription(detailCommand.getDescription());
            detail.setDemandType(detailCommand.getDemandType());
            detail.setNeedReturn(Boolean.TRUE.equals(detailCommand.getNeedReturn()) ? "Y" : "N");
            detail.setExpectedReturnDate(detailCommand.getExpectedReturnDate());
            detail.setLendingApprover(detailCommand.getLendingApprover());
            detail.setLendingRemark(detailCommand.getLendingRemark());
            detail.setHandler(detailCommand.getHandler());
            detail.setHandlerRemark(detailCommand.getHandlerRemark());
            detail.setCreatedAt(LocalDateTime.now());
            detail.setUpdatedAt(LocalDateTime.now());
            borrowOrderDetailMapper.insert(detail);

            CreateBorrowRecordCommand recordCommand = new CreateBorrowRecordCommand();
            recordCommand.setArchiveCode(StringUtils.hasText(detailCommand.getBusinessCode()) ? detailCommand.getBusinessCode() : "BR-" + System.currentTimeMillis());
            String archiveTitle = StreamSupport.joinNonBlank(" | ", detailCommand.getCompany(), detailCommand.getDocumentName(), detailCommand.getDescription());
            recordCommand.setArchiveTitle(archiveTitle);
            recordCommand.setBorrower(command.getUserName());
            recordCommand.setBorrowType(detailCommand.getDemandType());
            recordCommand.setExpectedReturnDate(detailCommand.getExpectedReturnDate() == null ? LocalDate.now().plusDays(7) : detailCommand.getExpectedReturnDate());
            borrowRecordService.createRecord(recordCommand);
        }

        return getOrder(order.getOrderNo());
    }

    @Override
    @Transactional
    public BorrowOrderDto updateOrder(String orderNo, CreateBorrowOrderCommand command) {
        BorrowOrder order = borrowOrderMapper.selectOne(
                new LambdaQueryWrapper<BorrowOrder>().eq(BorrowOrder::getOrderNo, orderNo)
        );
        if (order == null) {
            return null;
        }

        order.setUserName(command.getUserName());
        order.setUserDepartment(command.getUserDepartment());
        order.setApplicantName(command.getApplicantName());
        order.setApplyTime(command.getApplyTime() == null ? order.getApplyTime() : command.getApplyTime());
        order.setPurpose(command.getPurpose());
        order.setReason(command.getReason());
        order.setReasonAttachment(command.getReasonAttachment());
        order.setApprovalComment(command.getApprovalComment());
        order.setDemandApprover(command.getDemandApprover());
        order.setDemandReviewer(command.getDemandReviewer());
        order.setDemandAnalyst(command.getDemandAnalyst());
        order.setCcUsers(joinUsers(command.getCcUsers()));
        if (StringUtils.hasText(command.getStatus())) {
            order.setStatus(command.getStatus());
        }
        if (StringUtils.hasText(command.getWorkflowInstanceId())) {
            order.setWorkflowInstanceId(command.getWorkflowInstanceId());
        }
        order.setCurrentHandler(command.getCurrentHandler());
        order.setUpdatedAt(LocalDateTime.now());
        borrowOrderMapper.updateById(order);

        borrowOrderDetailMapper.delete(new LambdaQueryWrapper<BorrowOrderDetail>()
                .eq(BorrowOrderDetail::getBorrowOrderId, order.getId()));

        for (CreateBorrowOrderDetailCommand detailCommand : command.getDetails()) {
            BorrowOrderDetail detail = new BorrowOrderDetail();
            detail.setBorrowOrderId(order.getId());
            detail.setBusinessCode(detailCommand.getBusinessCode());
            detail.setDocumentName(detailCommand.getDocumentName());
            detail.setCompany(detailCommand.getCompany());
            detail.setDocumentType(detailCommand.getDocumentType());
            detail.setDetailDescription(detailCommand.getDescription());
            detail.setDemandType(detailCommand.getDemandType());
            detail.setNeedReturn(Boolean.TRUE.equals(detailCommand.getNeedReturn()) ? "Y" : "N");
            detail.setExpectedReturnDate(detailCommand.getExpectedReturnDate());
            detail.setLendingApprover(detailCommand.getLendingApprover());
            detail.setLendingRemark(detailCommand.getLendingRemark());
            detail.setHandler(detailCommand.getHandler());
            detail.setHandlerRemark(detailCommand.getHandlerRemark());
            detail.setCreatedAt(LocalDateTime.now());
            detail.setUpdatedAt(LocalDateTime.now());
            borrowOrderDetailMapper.insert(detail);
        }

        return getOrder(orderNo);
    }

    @Override
    public List<RenewableBorrowOrderDto> listRenewableOrders(String applicantName) {
        List<BorrowOrderDto> orders = assembleOrders(borrowOrderMapper.selectList(
                new LambdaQueryWrapper<BorrowOrder>()
                        .eq(StringUtils.hasText(applicantName), BorrowOrder::getApplicantName, applicantName)
                        .orderByDesc(BorrowOrder::getApplyTime)
        ));

        return orders.stream()
                .filter(order -> order.getDetails().stream().anyMatch(detail ->
                        detail.getExpectedReturnDate() != null
                                && !detail.getExpectedReturnDate().isBefore(LocalDate.now())
                                && detail.getNeedReturn()))
                .map(order -> {
                    RenewableBorrowOrderDto dto = new RenewableBorrowOrderDto();
                    dto.setOrderNo(order.getOrderNo());
                    dto.setApplyTime(order.getApplyTime());
                    dto.setBorrowTime(order.getApplyTime() == null ? null : order.getApplyTime().toLocalDate());
                    dto.setExpireTime(order.getDetails().stream()
                            .map(BorrowOrderDetailDto::getExpectedReturnDate)
                            .filter(Objects::nonNull)
                            .max(LocalDate::compareTo)
                            .orElse(null));
                    dto.setCurrentHandler(StringUtils.hasText(order.getCurrentHandler()) ? order.getCurrentHandler() : order.getDemandReviewer());
                    dto.setApplicantName(order.getApplicantName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MyBorrowDocumentDto> listMyBorrowDocuments(BorrowDocumentQueryRequest request) {
        List<BorrowOrderDto> orders = listOrders(toOrderQuery(request));
        return orders.stream()
                .flatMap(order -> order.getDetails().stream().map(detail -> {
                    MyBorrowDocumentDto dto = new MyBorrowDocumentDto();
                    dto.setCompany(detail.getCompany());
                    dto.setBusinessCode(detail.getBusinessCode());
                    dto.setDocumentName(detail.getDocumentName());
                    dto.setDocumentType(detail.getDocumentType());
                    dto.setBusinessModule("借阅管理");
                    dto.setArchivePeriod(detail.getExpectedReturnDate() == null ? "-" : detail.getExpectedReturnDate().toString());
                    dto.setOrderNo(order.getOrderNo());
                    dto.setStatus(order.getStatus());
                    dto.setBorrowTime(order.getApplyTime() == null ? null : order.getApplyTime().toLocalDate());
                    dto.setAttachment(order.getReasonAttachment());
                    return dto;
                }))
                .filter(dto -> !StringUtils.hasText(request.getCompany()) || request.getCompany().equals(dto.getCompany()))
                .filter(dto -> !StringUtils.hasText(request.getBusinessCode()) || safeContains(dto.getBusinessCode(), request.getBusinessCode()))
                .filter(dto -> !StringUtils.hasText(request.getDocumentName()) || safeContains(dto.getDocumentName(), request.getDocumentName()))
                .filter(dto -> !StringUtils.hasText(request.getDocumentType()) || request.getDocumentType().equals(dto.getDocumentType()))
                .filter(dto -> !StringUtils.hasText(request.getOrderNo()) || safeContains(dto.getOrderNo(), request.getOrderNo()))
                .filter(dto -> !StringUtils.hasText(request.getStatus()) || request.getStatus().equals(dto.getStatus()))
                .collect(Collectors.toList());
    }

    private BorrowOrderQueryRequest toOrderQuery(BorrowDocumentQueryRequest request) {
        BorrowOrderQueryRequest query = new BorrowOrderQueryRequest();
        query.setApplicantName(request.getApplicantName());
        query.setOrderNo(request.getOrderNo());
        query.setStatus(request.getStatus());
        return query;
    }

    private List<BorrowOrderDto> filterAndAssembleOrders(List<BorrowOrder> orders, BorrowOrderQueryRequest request) {
        return assembleOrders(orders).stream()
                .filter(order -> !StringUtils.hasText(request.getCompany()) || order.getDetails().stream().anyMatch(detail -> request.getCompany().equals(detail.getCompany())))
                .filter(order -> !StringUtils.hasText(request.getBusinessCode()) || order.getDetails().stream().anyMatch(detail -> safeContains(detail.getBusinessCode(), request.getBusinessCode())))
                .filter(order -> !StringUtils.hasText(request.getDocumentName()) || order.getDetails().stream().anyMatch(detail -> safeContains(detail.getDocumentName(), request.getDocumentName())))
                .collect(Collectors.toList());
    }

    private List<BorrowOrderDto> assembleOrders(List<BorrowOrder> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = orders.stream().map(BorrowOrder::getId).toList();
        Map<Long, List<BorrowOrderDetailDto>> detailMap = borrowOrderDetailMapper.selectList(
                        new LambdaQueryWrapper<BorrowOrderDetail>().in(BorrowOrderDetail::getBorrowOrderId, ids).orderByAsc(BorrowOrderDetail::getId)
                ).stream()
                .map(this::toDetailDto)
                .collect(Collectors.groupingBy(BorrowOrderDetailDto::getBorrowOrderId));

        return orders.stream().map(order -> {
            BorrowOrderDto dto = new BorrowOrderDto();
            dto.setId(order.getId());
            dto.setOrderNo(order.getOrderNo());
            dto.setUserName(order.getUserName());
            dto.setUserDepartment(order.getUserDepartment());
            dto.setApplicantName(order.getApplicantName());
            dto.setApplyTime(order.getApplyTime());
            dto.setPurpose(order.getPurpose());
            dto.setReason(order.getReason());
            dto.setReasonAttachment(order.getReasonAttachment());
            dto.setApprovalComment(order.getApprovalComment());
            dto.setDemandApprover(order.getDemandApprover());
            dto.setDemandReviewer(order.getDemandReviewer());
            dto.setDemandAnalyst(order.getDemandAnalyst());
            dto.setCcUsers(splitUsers(order.getCcUsers()));
            dto.setStatus(order.getStatus());
            dto.setWorkflowInstanceId(order.getWorkflowInstanceId());
            dto.setCurrentHandler(order.getCurrentHandler());
            dto.setDetails(detailMap.getOrDefault(order.getId(), Collections.emptyList()));
            return dto;
        }).collect(Collectors.toList());
    }

    private BorrowOrderDetailDto toDetailDto(BorrowOrderDetail detail) {
        BorrowOrderDetailDto dto = new BorrowOrderDetailDto();
        dto.setId(detail.getId());
        dto.setBorrowOrderId(detail.getBorrowOrderId());
        dto.setBusinessCode(detail.getBusinessCode());
        dto.setDocumentName(detail.getDocumentName());
        dto.setCompany(detail.getCompany());
        dto.setDocumentType(detail.getDocumentType());
        dto.setDescription(detail.getDetailDescription());
        dto.setDemandType(detail.getDemandType());
        dto.setNeedReturn("Y".equalsIgnoreCase(detail.getNeedReturn()));
        dto.setExpectedReturnDate(detail.getExpectedReturnDate());
        dto.setLendingApprover(detail.getLendingApprover());
        dto.setLendingRemark(detail.getLendingRemark());
        dto.setHandler(detail.getHandler());
        dto.setHandlerRemark(detail.getHandlerRemark());
        dto.setCreatedAt(detail.getCreatedAt());
        return dto;
    }

    private String joinUsers(List<String> values) {
        return values == null || values.isEmpty() ? null : String.join(",", values);
    }

    private List<String> splitUsers(String values) {
        return !StringUtils.hasText(values) ? Collections.emptyList() : List.of(values.split(","));
    }

    private boolean safeContains(String source, String keyword) {
        return source != null && source.contains(keyword);
    }

    private static final class StreamSupport {
        private StreamSupport() {
        }

        private static String joinNonBlank(String delimiter, String... values) {
            return java.util.Arrays.stream(values)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.joining(delimiter));
        }
    }
}

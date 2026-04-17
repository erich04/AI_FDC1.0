package com.smartarchive.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartarchive.archive.command.CreateBorrowRenewDetailCommand;
import com.smartarchive.archive.command.CreateBorrowRenewOrderCommand;
import com.smartarchive.archive.domain.BorrowRenewDetail;
import com.smartarchive.archive.domain.BorrowRenewOrder;
import com.smartarchive.archive.dto.BorrowRenewDetailDto;
import com.smartarchive.archive.dto.BorrowRenewOrderDto;
import com.smartarchive.archive.mapper.BorrowRenewDetailMapper;
import com.smartarchive.archive.mapper.BorrowRenewOrderMapper;
import com.smartarchive.archive.service.BorrowRenewOrderService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BorrowRenewOrderServiceImpl implements BorrowRenewOrderService {
    private final BorrowRenewOrderMapper borrowRenewOrderMapper;
    private final BorrowRenewDetailMapper borrowRenewDetailMapper;

    @Override
    public List<BorrowRenewOrderDto> listOrders(String applicantName) {
        List<BorrowRenewOrder> orders = borrowRenewOrderMapper.selectList(
                new LambdaQueryWrapper<BorrowRenewOrder>()
                        .eq(StringUtils.hasText(applicantName), BorrowRenewOrder::getApplicantName, applicantName)
                        .orderByDesc(BorrowRenewOrder::getApplyTime)
        );
        return assembleOrders(orders);
    }

    @Override
    public BorrowRenewOrderDto getOrder(String renewOrderNo) {
        BorrowRenewOrder order = borrowRenewOrderMapper.selectOne(
                new LambdaQueryWrapper<BorrowRenewOrder>().eq(BorrowRenewOrder::getRenewOrderNo, renewOrderNo)
        );
        return order == null ? null : assembleOrders(List.of(order)).stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public BorrowRenewOrderDto createOrder(CreateBorrowRenewOrderCommand command) {
        BorrowRenewOrder order = new BorrowRenewOrder();
        order.setRenewOrderNo(StringUtils.hasText(command.getRenewOrderNo()) ? command.getRenewOrderNo() : "REN-" + System.currentTimeMillis());
        order.setSourceOrderNo(command.getSourceOrderNo());
        order.setUserName(command.getUserName());
        order.setUserDepartment(command.getUserDepartment());
        order.setApplicantName(command.getApplicantName());
        order.setApplyTime(command.getApplyTime() == null ? LocalDateTime.now() : command.getApplyTime());
        order.setPurpose(command.getPurpose());
        order.setReason(command.getReason());
        order.setReasonAttachment(command.getReasonAttachment());
        order.setReviewer(command.getReviewer());
        order.setHandler(command.getHandler());
        order.setCcUsers(command.getCcUsers() == null || command.getCcUsers().isEmpty() ? null : String.join(",", command.getCcUsers()));
        order.setStatus(StringUtils.hasText(command.getStatus()) ? command.getStatus() : "DRAFT");
        order.setWorkflowInstanceId(command.getWorkflowInstanceId());
        order.setCurrentHandler(command.getCurrentHandler());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        borrowRenewOrderMapper.insert(order);

        for (CreateBorrowRenewDetailCommand detailCommand : command.getDetails()) {
            BorrowRenewDetail detail = new BorrowRenewDetail();
            detail.setBorrowRenewOrderId(order.getId());
            detail.setSourceDetailId(detailCommand.getSourceDetailId());
            detail.setBusinessCode(detailCommand.getBusinessCode());
            detail.setDocumentName(detailCommand.getDocumentName());
            detail.setCompany(detailCommand.getCompany());
            detail.setBorrowType(detailCommand.getBorrowType());
            detail.setBorrowTime(detailCommand.getBorrowTime());
            detail.setCurrentExpireTime(detailCommand.getCurrentExpireTime());
            detail.setRenewExpireTime(detailCommand.getRenewExpireTime());
            detail.setRenewReason(detailCommand.getRenewReason());
            detail.setCreatedAt(LocalDateTime.now());
            detail.setUpdatedAt(LocalDateTime.now());
            borrowRenewDetailMapper.insert(detail);
        }

        return getOrder(order.getRenewOrderNo());
    }

    private List<BorrowRenewOrderDto> assembleOrders(List<BorrowRenewOrder> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = orders.stream().map(BorrowRenewOrder::getId).toList();
        Map<Long, List<BorrowRenewDetailDto>> detailMap = borrowRenewDetailMapper.selectList(
                        new LambdaQueryWrapper<BorrowRenewDetail>().in(BorrowRenewDetail::getBorrowRenewOrderId, ids).orderByAsc(BorrowRenewDetail::getId)
                ).stream()
                .map(this::toDetailDto)
                .collect(Collectors.groupingBy(BorrowRenewDetailDto::getBorrowRenewOrderId));

        return orders.stream().map(order -> {
            BorrowRenewOrderDto dto = new BorrowRenewOrderDto();
            dto.setId(order.getId());
            dto.setRenewOrderNo(order.getRenewOrderNo());
            dto.setSourceOrderNo(order.getSourceOrderNo());
            dto.setUserName(order.getUserName());
            dto.setUserDepartment(order.getUserDepartment());
            dto.setApplicantName(order.getApplicantName());
            dto.setApplyTime(order.getApplyTime());
            dto.setPurpose(order.getPurpose());
            dto.setReason(order.getReason());
            dto.setReasonAttachment(order.getReasonAttachment());
            dto.setReviewer(order.getReviewer());
            dto.setHandler(order.getHandler());
            dto.setCcUsers(!StringUtils.hasText(order.getCcUsers()) ? Collections.emptyList() : List.of(order.getCcUsers().split(",")));
            dto.setStatus(order.getStatus());
            dto.setWorkflowInstanceId(order.getWorkflowInstanceId());
            dto.setCurrentHandler(order.getCurrentHandler());
            dto.setDetails(detailMap.getOrDefault(order.getId(), Collections.emptyList()));
            return dto;
        }).collect(Collectors.toList());
    }

    private BorrowRenewDetailDto toDetailDto(BorrowRenewDetail detail) {
        BorrowRenewDetailDto dto = new BorrowRenewDetailDto();
        dto.setId(detail.getId());
        dto.setBorrowRenewOrderId(detail.getBorrowRenewOrderId());
        dto.setSourceDetailId(detail.getSourceDetailId());
        dto.setBusinessCode(detail.getBusinessCode());
        dto.setDocumentName(detail.getDocumentName());
        dto.setCompany(detail.getCompany());
        dto.setBorrowType(detail.getBorrowType());
        dto.setBorrowTime(detail.getBorrowTime());
        dto.setCurrentExpireTime(detail.getCurrentExpireTime());
        dto.setRenewExpireTime(detail.getRenewExpireTime());
        dto.setRenewReason(detail.getRenewReason());
        return dto;
    }
}

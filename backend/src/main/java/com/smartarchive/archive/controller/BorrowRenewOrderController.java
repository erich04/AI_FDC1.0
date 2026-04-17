package com.smartarchive.archive.controller;

import com.smartarchive.archive.command.CreateBorrowRenewOrderCommand;
import com.smartarchive.archive.dto.BorrowRenewOrderDto;
import com.smartarchive.archive.service.BorrowRenewOrderService;
import com.smartarchive.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/archive/borrow-renew-orders")
@RequiredArgsConstructor
public class BorrowRenewOrderController {
    private final BorrowRenewOrderService borrowRenewOrderService;

    @GetMapping
    public ApiResponse<List<BorrowRenewOrderDto>> list(@RequestParam(required = false) String applicantName) {
        return ApiResponse.success(borrowRenewOrderService.listOrders(applicantName));
    }

    @GetMapping("/{renewOrderNo}")
    public ApiResponse<BorrowRenewOrderDto> get(@PathVariable String renewOrderNo) {
        return ApiResponse.success(borrowRenewOrderService.getOrder(renewOrderNo));
    }

    @PostMapping
    public ApiResponse<BorrowRenewOrderDto> create(@Valid @RequestBody CreateBorrowRenewOrderCommand command) {
        return ApiResponse.success(borrowRenewOrderService.createOrder(command));
    }
}

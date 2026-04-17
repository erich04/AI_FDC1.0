package com.smartarchive.archive.controller;

import com.smartarchive.archive.command.CreateBorrowOrderCommand;
import com.smartarchive.archive.dto.BorrowDocumentQueryRequest;
import com.smartarchive.archive.dto.BorrowOrderDto;
import com.smartarchive.archive.dto.BorrowOrderQueryRequest;
import com.smartarchive.archive.dto.MyBorrowDocumentDto;
import com.smartarchive.archive.dto.RenewableBorrowOrderDto;
import com.smartarchive.archive.service.BorrowOrderService;
import com.smartarchive.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/archive/borrow-orders")
@RequiredArgsConstructor
public class BorrowOrderController {
    private final BorrowOrderService borrowOrderService;

    @GetMapping
    public ApiResponse<List<BorrowOrderDto>> list(@ModelAttribute BorrowOrderQueryRequest request) {
        return ApiResponse.success(borrowOrderService.listOrders(request));
    }

    @GetMapping("/{orderNo}")
    public ApiResponse<BorrowOrderDto> get(@PathVariable String orderNo) {
        return ApiResponse.success(borrowOrderService.getOrder(orderNo));
    }

    @PostMapping
    public ApiResponse<BorrowOrderDto> create(@Valid @RequestBody CreateBorrowOrderCommand command) {
        return ApiResponse.success(borrowOrderService.createOrder(command));
    }

    @PutMapping("/{orderNo}")
    public ApiResponse<BorrowOrderDto> update(@PathVariable String orderNo, @Valid @RequestBody CreateBorrowOrderCommand command) {
        return ApiResponse.success(borrowOrderService.updateOrder(orderNo, command));
    }

    @GetMapping("/renewable")
    public ApiResponse<List<RenewableBorrowOrderDto>> renewable(@RequestParam(required = false) String applicantName) {
        return ApiResponse.success(borrowOrderService.listRenewableOrders(applicantName));
    }

    @GetMapping("/my-documents")
    public ApiResponse<List<MyBorrowDocumentDto>> myDocuments(@ModelAttribute BorrowDocumentQueryRequest request) {
        return ApiResponse.success(borrowOrderService.listMyBorrowDocuments(request));
    }
}

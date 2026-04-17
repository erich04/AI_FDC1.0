package com.smartarchive.archive.service;

import com.smartarchive.archive.command.CreateBorrowRenewOrderCommand;
import com.smartarchive.archive.dto.BorrowRenewOrderDto;
import java.util.List;

public interface BorrowRenewOrderService {
    List<BorrowRenewOrderDto> listOrders(String applicantName);
    BorrowRenewOrderDto getOrder(String renewOrderNo);
    BorrowRenewOrderDto createOrder(CreateBorrowRenewOrderCommand command);
}

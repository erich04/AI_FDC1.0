package com.smartarchive.archive.service;

import com.smartarchive.archive.command.CreateBorrowOrderCommand;
import com.smartarchive.archive.dto.BorrowDocumentQueryRequest;
import com.smartarchive.archive.dto.BorrowOrderDto;
import com.smartarchive.archive.dto.BorrowOrderQueryRequest;
import com.smartarchive.archive.dto.MyBorrowDocumentDto;
import com.smartarchive.archive.dto.RenewableBorrowOrderDto;
import java.util.List;

public interface BorrowOrderService {
    List<BorrowOrderDto> listOrders(BorrowOrderQueryRequest request);
    BorrowOrderDto getOrder(String orderNo);
    BorrowOrderDto createOrder(CreateBorrowOrderCommand command);
    BorrowOrderDto updateOrder(String orderNo, CreateBorrowOrderCommand command);
    List<RenewableBorrowOrderDto> listRenewableOrders(String applicantName);
    List<MyBorrowDocumentDto> listMyBorrowDocuments(BorrowDocumentQueryRequest request);
}

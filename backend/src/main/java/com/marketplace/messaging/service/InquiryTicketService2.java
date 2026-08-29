package com.marketplace.messaging.service;

import com.marketplace.customer.domain.Customer;
import com.marketplace.messaging.domain.CustomerInquiryTicket2;
import com.marketplace.seller.domain.Seller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryTicketService2 {

    @Transactional
    public CustomerInquiryTicket2 openTicket(Customer customer, Seller seller, String subject, String body) {
        String tNum = "TCK-2-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        CustomerInquiryTicket2 ticket = CustomerInquiryTicket2.builder()
                .ticketNumber(tNum)
                .customer(customer)
                .seller(seller)
                .inquirySubject(subject)
                .category("PRODUCT_QUESTION")
                .messageBody(body)
                .status("OPEN")
                .build();
        log.info("Ticket opened: {}", tNum);
        return ticket;
    }

    @Transactional
    public void resolveTicket(CustomerInquiryTicket2 ticket) {
        ticket.setStatus("RESOLVED");
        ticket.setResolvedAt(Instant.now());
    }
}

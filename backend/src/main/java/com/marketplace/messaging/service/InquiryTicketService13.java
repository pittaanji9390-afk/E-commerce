package com.marketplace.messaging.service;

import com.marketplace.customer.domain.Customer;
import com.marketplace.messaging.domain.CustomerInquiryTicket13;
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
public class InquiryTicketService13 {

    @Transactional
    public CustomerInquiryTicket13 openTicket(Customer customer, Seller seller, String subject, String body) {
        String tNum = "TCK-13-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        CustomerInquiryTicket13 ticket = CustomerInquiryTicket13.builder()
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
    public void resolveTicket(CustomerInquiryTicket13 ticket) {
        ticket.setStatus("RESOLVED");
        ticket.setResolvedAt(Instant.now());
    }
}

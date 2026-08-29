package com.marketplace.shared.exception;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessRuleException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessRuleException(String message) {
        super(message);
        this.errorCode = ErrorCode.BUSINESS_RULE_VIOLATION;
    }
}

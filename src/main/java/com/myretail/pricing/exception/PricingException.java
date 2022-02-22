package com.myretail.pricing.exception;

/**
 * @author Sunith Soman
 */
public class PricingException extends Exception{
    private ErrorType errorType;
    public enum ErrorType{
        DATA_ERROR,
        SYSTEM_ERROR;
    }
    public PricingException(String message,ErrorType errorType,Throwable exception){
        super(message,exception);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}

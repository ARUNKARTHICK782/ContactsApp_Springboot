package com.example.SpringPractice.model;

public class CustomException extends Exception{
    private int statusCode;
    public CustomException(String msg,int statusCode){
        super(msg);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

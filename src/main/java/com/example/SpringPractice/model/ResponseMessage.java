package com.example.SpringPractice.model;

public class ResponseMessage {
    private boolean success;
    private String message;
    private int status_code;
    private Object data;

    public ResponseMessage(boolean success, String message, int status_code, Object data) {
        this.success = success;
        this.message = message;
        this.status_code = status_code;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", status_code=" + status_code +
                ", data=" + data +
                '}';
    }
}

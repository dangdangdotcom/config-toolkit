package com.dangdang.config.face.entity;

public class CommonResponse<T> {

    private boolean suc;
    private T body;
    private String message;

    public CommonResponse(boolean suc, T body, String message) {
        this.suc = suc;
        this.body = body;
        this.message = message;
    }

    public boolean isSuc() {
        return suc;
    }

    public T getBody() {
        return body;
    }

    public String getMessage() {
        return message;
    }
}

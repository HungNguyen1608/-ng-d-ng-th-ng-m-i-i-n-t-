package com.example.app.model;

import java.util.List;

public class DanhGiaModle {
    boolean success;
    String message;

    List<DanhGia> result;

    public List<DanhGia> getResult() {
        return result;
    }

    public void setResult(List<DanhGia> result) {
        this.result = result;
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

}

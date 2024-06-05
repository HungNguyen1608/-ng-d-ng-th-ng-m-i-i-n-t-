package com.example.app.model;

import java.util.List;

public class LoaiHangModel {
    boolean success;
    String message;
    List<LoaiHang> result;

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

    public List<LoaiHang> getResult() {
        return result;
    }

    public void setResult(List<LoaiHang> result) {
        this.result = result;
    }
}

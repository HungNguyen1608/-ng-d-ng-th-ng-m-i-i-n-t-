package com.example.app.model;

public class LoaiSp {
    int id;
    String tendanhmuc;
    String hinhanh;

    public LoaiSp(String tendanhmuc, String hinhanh) {
        this.tendanhmuc = tendanhmuc;
        this.hinhanh = hinhanh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTendanhmuc() {
        return tendanhmuc;
    }

    public void setTendanhmuc(String tendanhmuc) {
        this.tendanhmuc = tendanhmuc;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }
}

package com.example.model;

import java.time.LocalDate;

public class DutyShiftModel {
    private String maBacSi;
    private String tenNguoiTruc;
    private Role vaiTro;
    private LocalDate ngay;
    private String caTruc;

    public DutyShiftModel(String maBacSi, String tenNguoiTruc, Role vaiTro, LocalDate ngay, String caTruc) {
        this.maBacSi = maBacSi;
        this.tenNguoiTruc = tenNguoiTruc;
        this.vaiTro = vaiTro;
        this.ngay = ngay;
        this.caTruc = caTruc;
    }

    public String getMaBacSi() { return maBacSi; }
    public String getTenNguoiTruc() { return tenNguoiTruc; }
    public Role getVaiTro() { return vaiTro; }
    public LocalDate getNgay() { return ngay; }
    public String getCaTruc() { return caTruc; }
}

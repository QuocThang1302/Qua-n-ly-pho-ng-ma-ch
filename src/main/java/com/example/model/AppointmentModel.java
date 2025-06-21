package com.example.model;

import java.time.LocalDate;
public class AppointmentModel {
    private String maKhamBenh;
    private String maBenhNhan;
    private String hoTen;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String gioiTinh;
    private String lyDoKham;
    private LocalDate ngayKham;
    private LocalDate ngayKetThuc;
    private String maBacSi;
    private String tinhTrang;

    public AppointmentModel(String maKhamBenh, String maBenhNhan, String hoTen, LocalDate ngaySinh, String soDienThoai, String gioiTinh, String lyDoKham, LocalDate ngayKham, LocalDate ngayKetThuc, String maBacSi, String tinhTrang) {
        this.maKhamBenh = maKhamBenh;
        this.maBenhNhan = maBenhNhan;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.gioiTinh = gioiTinh;
        this.lyDoKham = lyDoKham;
        this.ngayKham = ngayKham;
        this.ngayKetThuc = ngayKetThuc;
        this.maBacSi = maBacSi;
        this.tinhTrang = tinhTrang;
    }

    public AppointmentModel() {

    }

    public String getMaKhamBenh() {
        return maKhamBenh;
    }

    public void setMaKhamBenh(String maKhamBenh) {
        this.maKhamBenh = maKhamBenh;
    }

    public String getMaBenhNhan() {
        return maBenhNhan;
    }

    public void setMaBenhNhan(String maBenhNhan) {
        this.maBenhNhan = maBenhNhan;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getLyDoKham() {
        return lyDoKham;
    }

    public void setLyDoKham(String lyDoKham) {
        this.lyDoKham = lyDoKham;
    }

    public LocalDate getNgayKham() {
        return ngayKham;
    }

    public void setNgayKham(LocalDate ngayKham) {
        this.ngayKham = ngayKham;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getMaBacSi() {
        return maBacSi;
    }

    public void setMaBacSi(String maBacSi) {
        this.maBacSi = maBacSi;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }
}

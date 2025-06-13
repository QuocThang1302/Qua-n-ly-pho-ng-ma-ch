CREATE TABLE "BenhNhan" (
  "MaBenhNhan" NVARCHAR(20) PRIMARY KEY,
  "Ho" NVARCHAR(50),
  "Ten" NVARCHAR(50),
  "NgaySinh" DATE,
  "GioiTinh" NVARCHAR(10),
  "SDT" NVARCHAR(20)
);

CREATE TABLE "NhanVien" (
  "MaNhanVien" NVARCHAR(20) PRIMARY KEY,
  "Ho" NVARCHAR(50),
  "Ten" NVARCHAR(50),
  "RoleID" NVARCHAR(20),
  "NgaySinh" DATE,
  "GioiTinh" NVARCHAR(10),
  "CCCD" NVARCHAR(20),
  "DiaChi" NTEXT,
  "SDT" NVARCHAR(20),
  "Email" NVARCHAR(100),
  "MatKhau" NVARCHAR(100)
);

CREATE TABLE "Role" (
  "RoleID" NVARCHAR(20) PRIMARY KEY,
  "TenRole" NVARCHAR(50)
);

CREATE TABLE "LichTruc" (
  "MaLichTruc" NVARCHAR(20) PRIMARY KEY,
  "MaBacSi" NVARCHAR(20),
  "NgayTruc" DATE,
  "CongViec" NVARCHAR(100),
  "TrangThai" NVARCHAR(50)
);

CREATE TABLE "HenKhamBenh" (
  "MaKhamBenh" NVARCHAR(20) PRIMARY KEY,
  "MaBenhNhan" NVARCHAR(20),
  "LyDoKham" NTEXT,
  "NgayKham" DATE,
  "MaBacSi" NVARCHAR(20),
  "TinhTrang" NTEXT
);

CREATE TABLE "PhieuKhamBenh" (
  "MaPhieuKham" NVARCHAR(20) PRIMARY KEY,
  "MaBenhNhan" NVARCHAR(20),
  "NgayKham" DATE,
  "ChanDoan" NTEXT,
  "KetQuaKham" NTEXT,
  "DieuTri" NTEXT,
  "MaBacSi" NVARCHAR(20)
);

CREATE TABLE "Thuoc" (
  "MaThuoc" NVARCHAR(20) PRIMARY KEY,
  "TenThuoc" NVARCHAR(100),
  "CongDung" NTEXT,
  "SoLuong" INT,
  "GiaTien" DECIMAL(10,2),
  "HanSuDung" DATE
);

CREATE TABLE "DonThuoc" (
  "MaDonThuoc" NVARCHAR(20) PRIMARY KEY,
  "MaPhieuKham" NVARCHAR(20),
  "NgayLapDon" DATE
);

CREATE TABLE "CTDonThuoc" (
  "MaDonThuoc" NVARCHAR(20),
  "MaThuoc" NVARCHAR(20),
  "SoLuong" INT,
  "GiaTien" DECIMAL(10,2),
  "HuongDanSuDung" NTEXT,
  PRIMARY KEY ("MaDonThuoc", "MaThuoc")
);

CREATE TABLE "HoaDon" (
  "MaHoaDon" NVARCHAR(20) PRIMARY KEY,
  "TenHoaDon" NVARCHAR(50),
  "MaDonThuoc" NVARCHAR(20),
  "NgayLapHoaDon" DATE,
  "GiaTien" DECIMAL(10,2),
  "TrangThai" NVARCHAR(50)
);

CREATE TABLE "QuiDinh" (
  "MaQuiDinh" NVARCHAR(20) PRIMARY KEY,
  "TenQuiDinh" NVARCHAR(100),
  "MoTa" NTEXT,
  "NgayCapNhat" DATE,
  "NguoiCapNhat" NVARCHAR(20)
);

ALTER TABLE "HoaDon" ADD FOREIGN KEY ("MaDonThuoc") REFERENCES "DonThuoc" ("MaDonThuoc");

ALTER TABLE "DonThuoc" ADD FOREIGN KEY ("MaPhieuKham") REFERENCES "PhieuKhamBenh" ("MaPhieuKham");

ALTER TABLE "LichTruc" ADD FOREIGN KEY ("MaBacSi") REFERENCES "NhanVien" ("MaNhanVien");

ALTER TABLE "NhanVien" ADD FOREIGN KEY ("RoleID") REFERENCES "Role" ("RoleID");

ALTER TABLE "PhieuKhamBenh" ADD FOREIGN KEY ("MaBenhNhan") REFERENCES "BenhNhan" ("MaBenhNhan");

ALTER TABLE "PhieuKhamBenh" ADD FOREIGN KEY ("MaBacSi") REFERENCES "NhanVien" ("MaNhanVien");

ALTER TABLE "HenKhamBenh" ADD FOREIGN KEY ("MaBenhNhan") REFERENCES "BenhNhan" ("MaBenhNhan");

ALTER TABLE "HenKhamBenh" ADD FOREIGN KEY ("MaBacSi") REFERENCES "NhanVien" ("MaNhanVien");

ALTER TABLE "CTDonThuoc" ADD FOREIGN KEY ("MaDonThuoc") REFERENCES "DonThuoc" ("MaDonThuoc");

ALTER TABLE "CTDonThuoc" ADD FOREIGN KEY ("MaThuoc") REFERENCES "Thuoc" ("MaThuoc");

ALTER TABLE "QuiDinh" ADD FOREIGN KEY ("NguoiCapNhat") REFERENCES "NhanVien" ("MaNhanVien");

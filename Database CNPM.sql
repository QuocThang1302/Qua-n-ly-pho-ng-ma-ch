CREATE TABLE "BenhNhan" (
  "MaBenhNhan" VARCHAR(20) PRIMARY KEY,
  "Ho" VARCHAR(50),
  "Ten" VARCHAR(50),
  "NgaySinh" DATE,
  "GioiTinh" VARCHAR(10),
  "SDT" VARCHAR(20)
);

CREATE TABLE "NhanVien" (
  "MaNhanVien" VARCHAR(20) PRIMARY KEY,
  "Ho" VARCHAR(50),
  "Ten" VARCHAR(50),
  "RoleID" VARCHAR(20),
  "NgaySinh" DATE,
  "GioiTinh" VARCHAR(10),
  "CCCD" VARCHAR(20),
  "DiaChi" TEXT,
  "SDT" VARCHAR(20),
  "Email" VARCHAR(100),
  "MatKhau" VARCHAR(100)
);

CREATE TABLE "Role" (
  "RoleID" VARCHAR(20) PRIMARY KEY,
  "TenRole" VARCHAR(50)
);

CREATE TABLE "LichTruc" (
  "MaLichTruc" VARCHAR(20) PRIMARY KEY,
  "MaBacSi" VARCHAR(20),
  "NgayTruc" DATE,
  "CongViec" VARCHAR(100),
  "TrangThai" VARCHAR(50)
);

CREATE TABLE "HenKhamBenh" (
  "MaKhamBenh" VARCHAR(20) PRIMARY KEY,
  "MaBenhNhan" VARCHAR(20),
  "LyDoKham" TEXT,
  "NgayKham" DATE,
  "NgayketThuc" DATE,
  "MaBacSi" VARCHAR(20),
  "TinhTrang" TEXT
  CHECK (NgayKham = NgayKetThuc)
);

CREATE TABLE "PhieuKhamBenh" (
  "MaPhieuKham" VARCHAR(20) PRIMARY KEY,
  "MaBenhNhan" VARCHAR(20),
  "NgayKham" DATE,
  "ChanDoan" TEXT,
  "KetQuaKham" TEXT,
  "DieuTri" TEXT,
  "MaBacSi" VARCHAR(20)
);

CREATE TABLE "Thuoc" (
  "MaThuoc" VARCHAR(20) PRIMARY KEY,
  "TenThuoc" VARCHAR(100),
  "CongDung" TEXT,
  "SoLuong" INT,
  "GiaTien" DECIMAL(10,2),
  "HanSuDung" DATE
);

CREATE TABLE "DonThuoc" (
  "MaDonThuoc" VARCHAR(20) PRIMARY KEY,
  "MaPhieuKham" VARCHAR(20),
  "NgayLapDon" DATE
);

CREATE TABLE "CTDonThuoc" (
  "MaDonThuoc" VARCHAR(20),
  "MaThuoc" VARCHAR(20),
  "SoLuong" INT,
  "GiaTien" DECIMAL(10,2),
  "HuongDanSuDung" TEXT,
  PRIMARY KEY ("MaDonThuoc", "MaThuoc")
);

CREATE TABLE "HoaDon" (
  "MaHoaDon" VARCHAR(20) PRIMARY KEY,
  "TenHoaDon" VARCHAR(50),
  "MaDonThuoc" VARCHAR(20),
  "NgayLapHoaDon" DATE,
  "GiaTien" DECIMAL(10,2),
  "TrangThai" VARCHAR(50)
);

CREATE TABLE "QuiDinh" (
  "MaQuiDinh" VARCHAR(20) PRIMARY KEY,
  "TenQuiDinh" VARCHAR(100),
  "MoTa" TEXT,
  "NgayCapNhat" DATE,
  "NguoiCapNhat" VARCHAR(20)
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

INSERT INTO "Role" ("RoleID", "TenRole") VALUES
('ADMIN', 'Quản trị viên'),
('DOCTOR', 'Bác sĩ'),
('MANAGER', 'Quản lý'),
('NURSE', 'Y Tá');

INSERT INTO "NhanVien" (
  "MaNhanVien", "Ho", "Ten", "RoleID", "NgaySinh", "GioiTinh",
  "CCCD", "DiaChi", "SDT", "Email", "MatKhau"
) VALUES
('NV001', 'Nguyen', 'Admin', 'ADMIN', '1985-01-01', 'Nam',
 '0123456789', 'Hà Nội', '0900000001', 'admin@example.com', 'admin123'),

('NV002', 'Le', 'Bac Si', 'DOCTOR', '1990-02-02', 'Nữ',
 '0123456790', 'Hồ Chí Minh', '0900000002', 'doctor@example.com', 'doctor123'),

('NV003', 'Tran', 'Quan Ly', 'MANAGER', '1988-03-03', 'Nam',
 '0123456791', 'Đà Nẵng', '0900000003', 'manager@example.com', 'manager123'),

('NV004', 'Hoang', 'Y Ta', 'RECEPTIONIST', '1995-04-04', 'Nữ',
 '0123456792', 'Cần Thơ', '0900000004', 'reception@example.com', 'reception123');

INSERT INTO "BenhNhan" (
  "MaBenhNhan", "Ho", "Ten", "NgaySinh", "GioiTinh", "SDT"
) VALUES
('BN001', 'Pham', 'Van A', '2000-05-01', 'Nam', '0987654321'),
('BN002', 'Nguyen', 'Thi B', '1998-07-15', 'Nữ', '0912345678');

INSERT INTO "Thuoc" (
  "MaThuoc", "TenThuoc", "CongDung", "SoLuong", "GiaTien", "HanSuDung"
) VALUES
('T001', 'Paracetamol', 'Hạ sốt, giảm đau', 100, 1500.00, '2026-01-01'),
('T002', 'Amoxicillin', 'Kháng sinh', 50, 3000.00, '2025-12-31');

INSERT INTO "QuiDinh" (
  "MaQuiDinh", "TenQuiDinh", "MoTa", "NgayCapNhat", "NguoiCapNhat"
) VALUES
('QD001', 'Thời gian khám', 'Khám bệnh từ 8h đến 17h hàng ngày', '2024-12-01', 'NV003');

INSERT INTO "LichTruc" (
  "MaLichTruc", "MaBacSi", "NgayTruc", "CongViec", "TrangThai"
) VALUES
('LT001', 'NV002', '2025-06-16', 'Khám tổng quát', 'Sẵn sàng');


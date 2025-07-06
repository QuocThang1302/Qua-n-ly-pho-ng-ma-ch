CREATE TABLE BenhNhan (
  MaBenhNhan VARCHAR(20) PRIMARY KEY,
  Ho VARCHAR(50),
  Ten VARCHAR(50),
  NgaySinh DATE,
  GioiTinh VARCHAR(10),
  SDT VARCHAR(20)
);

CREATE TABLE NhanVien (
  MaNhanVien VARCHAR(20) PRIMARY KEY,
  Ho VARCHAR(50),
  Ten VARCHAR(50),
  RoleID VARCHAR(20),
  Luong DECIMAL(15,2),
  NgaySinh DATE,
  GioiTinh VARCHAR(10),
  CCCD VARCHAR(20),
  DiaChi TEXT,
  SDT VARCHAR(20),
  Email VARCHAR(100),
  MatKhau VARCHAR(100)
);

CREATE TABLE Role (
  RoleID VARCHAR(20) PRIMARY KEY,
  TenRole VARCHAR(50)
);

CREATE TABLE LichTruc (
  MaLichTruc VARCHAR(20) PRIMARY KEY,
  MaBacSi VARCHAR(20),
  NgayTruc DATE,
  CaTruc TEXT
);

CREATE TABLE HenKhamBenh (
  MaKhamBenh VARCHAR(20) PRIMARY KEY,
  MaBenhNhan VARCHAR(20),
  LyDoKham TEXT,
  NgayKham DATE,
  GioBatDau TIME,
  GioKetThuc TIME,
  MaBacSi VARCHAR(20),
);

CREATE TABLE PhieuKhamBenh (
  MaPhieuKham VARCHAR(20) PRIMARY KEY,
  MaBenhNhan VARCHAR(20),
  NgayKham DATE,
  NgayLapPhieu DATE,
  ChanDoan TEXT,
  KetQuaKham TEXT,
  DieuTri TEXT,
  TienKham DECIMAL(10,2),
  MaBacSi VARCHAR(20)
);
CREATE TABLE Thuoc (
  MaThuoc VARCHAR(20) PRIMARY KEY,
  TenThuoc VARCHAR(100),
  CongDung TEXT,
  SoLuong INT,
  GiaTien DECIMAL(10,2),
  DonVi VARCHAR(50),
  HuongDanSuDung TEXT,
  HanSuDung DATE
);

CREATE TABLE DonThuoc (
  MaDonThuoc VARCHAR(20) PRIMARY KEY,
  MaPhieuKham VARCHAR(20),
  NgayLapDon DATE
);

CREATE TABLE CTDonThuoc (
  MaDonThuoc VARCHAR(20),
  MaThuoc VARCHAR(20),
  SoLuong INT,
  GiaTien DECIMAL(10,2),
  HuongDanSuDung TEXT,
  PRIMARY KEY (MaDonThuoc, MaThuoc)
);

CREATE TABLE HoaDon (
  MaHoaDon VARCHAR(20) PRIMARY KEY,
  TenHoaDon VARCHAR(50),
  MaDonThuoc VARCHAR(20),
  MaPhieuKham VARCHAR(20),
  NgayLapHoaDon DATE,
  GiaTien DECIMAL(10,2),
  TrangThai VARCHAR(50)
);

CREATE TABLE QuiDinh (
  TenQuiDinh VARCHAR(50) PRIMARY KEY,
  GiaTri DECIMAL(15, 2)
);

-- Foreign keys
ALTER TABLE HoaDon ADD FOREIGN KEY (MaDonThuoc) REFERENCES DonThuoc (MaDonThuoc);
ALTER TABLE HoaDon ADD FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKhamBenh (MaPhieuKham);
ALTER TABLE DonThuoc ADD FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKhamBenh (MaPhieuKham);
ALTER TABLE LichTruc ADD FOREIGN KEY (MaBacSi) REFERENCES NhanVien (MaNhanVien);
ALTER TABLE NhanVien ADD FOREIGN KEY (RoleID) REFERENCES Role (RoleID);
ALTER TABLE PhieuKhamBenh ADD FOREIGN KEY (MaBenhNhan) REFERENCES BenhNhan (MaBenhNhan);
ALTER TABLE PhieuKhamBenh ADD FOREIGN KEY (MaBacSi) REFERENCES NhanVien (MaNhanVien);
ALTER TABLE HenKhamBenh ADD FOREIGN KEY (MaBenhNhan) REFERENCES BenhNhan (MaBenhNhan);
ALTER TABLE HenKhamBenh ADD FOREIGN KEY (MaBacSi) REFERENCES NhanVien (MaNhanVien);
ALTER TABLE CTDonThuoc ADD FOREIGN KEY (MaDonThuoc) REFERENCES DonThuoc (MaDonThuoc);
ALTER TABLE CTDonThuoc ADD FOREIGN KEY (MaThuoc) REFERENCES Thuoc (MaThuoc);

-- Data
INSERT INTO Role (RoleID, TenRole) VALUES
('ADMIN', 'Quản trị viên'),
('DOCTOR', 'Bác sĩ'),
('MANAGER', 'Quản lý'),
('NURSE', 'Y Tá');

INSERT INTO NhanVien (MaNhanVien, Ho, Ten, RoleID, Luong, NgaySinh, GioiTinh, CCCD, DiaChi, SDT, Email, MatKhau) VALUES
('123', 'Nguyen', 'Van A', 'ADMIN', 15000000, '1990-05-15', 'Nam', '123456789012', '123 Nguyen Trai, Q1, TP.HCM', '0901234567', 'admin@phongkham.com', '123456'),
('BS001', 'Tran', 'Thi B', 'DOCTOR', 20000000, '1985-03-20', 'Nu', '234567890123', '456 Le Loi, Q3, TP.HCM', '0912345678', 'bs001@phongkham.com', 'password123'),
('BS002', 'Le', 'Van C', 'DOCTOR', 22000000, '1988-07-10', 'Nam', '345678901234', '789 Hai Ba Trung, Q1, TP.HCM', '0923456789', 'bs002@phongkham.com', 'password123'),
('BS003', 'Pham', 'Thi D', 'DOCTOR', 21000000, '1987-11-25', 'Nu', '456789012345', '321 Vo Van Tan, Q3, TP.HCM', '0934567890', 'bs003@phongkham.com', 'password123'),
('YT001', 'Hoang', 'Van E', 'NURSE', 8000000, '1992-01-08', 'Nam', '567890123456', '654 Cach Mang Thang 8, Q10, TP.HCM', '0945678901', 'yt001@phongkham.com', 'password123'),
('YT002', 'Vo', 'Thi F', 'NURSE', 8500000, '1993-09-12', 'Nu', '678901234567', '987 Pasteur, Q1, TP.HCM', '0956789012', 'yt002@phongkham.com', 'password123'),
('QL001', 'Dang', 'Van G', 'MANAGER', 12000000, '1983-04-30', 'Nam', '789012345678', '159 Dong Khoi, Q1, TP.HCM', '0967890123', 'ql001@phongkham.com', 'password123');

-- Bệnh nhân
INSERT INTO BenhNhan (MaBenhNhan, Ho, Ten, NgaySinh, GioiTinh, SDT) VALUES
('BN001', 'Nguyen', 'Van H', '1980-05-15', 'Nam', '0978901234'),
('BN002', 'Tran', 'Thi I', '1992-08-20', 'Nu', '0989012345'),
('BN003', 'Le', 'Van J', '1975-12-10', 'Nam', '0990123456'),
('BN004', 'Pham', 'Thi K', '1988-03-25', 'Nu', '0901234567'),
('BN005', 'Hoang', 'Van L', '1995-07-08', 'Nam', '0912345678'),
('BN006', 'Vo', 'Thi M', '1990-11-12', 'Nu', '0923456789'),
('BN007', 'Dang', 'Van N', '1985-09-30', 'Nam', '0934567890'),
('BN008', 'Bui', 'Thi O', '1982-02-14', 'Nu', '0945678901'),
('BN009', 'Ngo', 'Van P', '1991-06-18', 'Nam', '0956789012'),
('BN010', 'Do', 'Thi Q', '1987-10-22', 'Nu', '0967890123'),
('BN011', 'Luu', 'Van R', '1993-04-05', 'Nam', '0978901234'),
('BN012', 'Cao', 'Thi S', '1989-01-17', 'Nu', '0989012345'),
('BN013', 'Truong', 'Van T', '1984-08-29', 'Nam', '0990123456'),
('BN014', 'Ly', 'Thi U', '1996-12-03', 'Nu', '0901234567'),
('BN015', 'Mai', 'Van V', '1981-05-26', 'Nam', '0912345678');

-- Lịch trực
INSERT INTO LichTruc (MaLichTruc, MaBacSi, NgayTruc, CaTruc) VALUES
('LT001', 'BS001', '2025-07-01', 'Tối'),
('LT002', 'BS002', '2025-07-01', 'Chiều'),
('LT003', 'BS003', '2025-07-02', 'Tối'),
('LT004', 'BS001', '2025-07-02', 'Chiều'),
('LT005', 'BS002', '2025-07-03', 'Tối'),
('LT006', 'BS003', '2025-07-03', 'Chiều'),
('LT007', 'BS001', '2025-07-04', 'Sáng'),
('LT008', 'BS002', '2025-07-04', 'Chiều'),
('LT009', 'BS003', '2025-07-05', 'Sáng'),
('LT010', 'BS001', '2025-07-05', 'Chiều');

-- Hẹn khám bệnh (tập trung vào tháng 7/2025)
INSERT INTO HenKhamBenh (MaKhamBenh, MaBenhNhan, LyDoKham, NgayKham, GioBatDau, GioKetThuc, MaBacSi) VALUES
('HKB001', 'BN001', 'Đau đầu thường xuyên', '2025-07-01', '08:00', '08:30', 'BS001'),
('HKB002', 'BN002', 'Khám sức khỏe định kỳ', '2025-07-01', '08:30', '09:00', 'BS001'),
('HKB003', 'BN003', 'Đau bụng', '2025-07-02', '09:00', '09:30', 'BS002'),
('HKB004', 'BN004', 'Ho kéo dài', '2025-07-02', '09:30', '10:00', 'BS003'),
('HKB005', 'BN005', 'Khám tim mạch', '2025-07-03', '08:00', '08:30', 'BS001'),
('HKB006', 'BN006', 'Đau lưng', '2025-07-03', '08:30', '09:00', 'BS002'),
('HKB007', 'BN007', 'Khám mắt', '2025-07-04', '09:00', '09:30', 'BS003'),
('HKB008', 'BN008', 'Đau khớp', '2025-07-04', '09:30', '10:00', 'BS001'),
('HKB009', 'BN009', 'Khám da liễu', '2025-07-05', '08:00', '08:30', 'BS002'),
('HKB010', 'BN010', 'Khám phụ khoa', '2025-07-05', '08:30', '09:00', 'BS003'),
('HKB011', 'BN011', 'Đau răng', '2025-07-08', '09:00', '09:30', 'BS001'),
('HKB012', 'BN012', 'Khám tai mũi họng', '2025-07-08', '09:30', '10:00', 'BS002'),
('HKB013', 'BN013', 'Đau dạ dày', '2025-07-09', '08:00', '08:30', 'BS003'),
('HKB014', 'BN014', 'Khám tổng quát', '2025-07-09', '08:30', '09:00', 'BS001'),
('HKB015', 'BN015', 'Huyết áp cao', '2025-07-10', '09:00', '09:30', 'BS002'),
('HKB016', 'BN001', 'Tái khám đau đầu', '2025-07-15', '08:00', '08:30', 'BS001'),
('HKB017', 'BN003', 'Tái khám đau bụng', '2025-07-16', '08:30', '09:00', 'BS002'),
('HKB018', 'BN005', 'Tái khám tim mạch', '2025-07-17', '09:00', '09:30', 'BS001'),
('HKB019', 'BN007', 'Tái khám mắt', '2025-07-18', '09:30', '10:00', 'BS003'),
('HKB020', 'BN012', 'Tái khám tai mũi họng', '2025-07-19', '08:00', '08:30', 'BS002'),
('HKB021', 'BN002', 'Khám sức khỏe định kỳ', '2025-07-22', '08:30', '09:00', 'BS001'),
('HKB022', 'BN004', 'Tái khám ho', '2025-07-23', '09:00', '09:30', 'BS003'),
('HKB023', 'BN006', 'Tái khám đau lưng', '2025-07-24', '09:30', '10:00', 'BS002'),
('HKB024', 'BN008', 'Tái khám đau khớp', '2025-07-25', '08:00', '08:30', 'BS001'),
('HKB025', 'BN010', 'Tái khám phụ khoa', '2025-07-26', '08:30', '09:00', 'BS003');

-- Phiếu khám bệnh
INSERT INTO PhieuKhamBenh (MaPhieuKham, MaBenhNhan, NgayKham, NgayLapPhieu, ChanDoan, KetQuaKham, DieuTri, TienKham, MaBacSi) VALUES
('PKB001', 'BN001', '2025-07-01', '2025-07-01', 'Đau đầu do căng thẳng', 'Huyết áp bình thường, không có dấu hiệu bất thường', 'Nghỉ ngơi, uống thuốc giảm đau', 200000, 'BS001'),
('PKB002', 'BN002', '2025-07-01', '2025-07-01', 'Khỏe mạnh', 'Tất cả chỉ số đều bình thường', 'Duy trì lối sống lành mạnh', 150000, 'BS001'),
('PKB003', 'BN003', '2025-07-02', '2025-07-02', 'Viêm dạ dày', 'Đau bụng vùng thượng vị', 'Thuốc kháng acid, chế độ ăn nhạt', 250000, 'BS002'),
('PKB004', 'BN004', '2025-07-02', '2025-07-02', 'Viêm họng', 'Họng đỏ, ho khô', 'Thuốc ho, súc miệng nước muối', 180000, 'BS003'),
('PKB005', 'BN005', '2025-07-03', '2025-07-03', 'Rối loạn nhịp tim nhẹ', 'Tim đập không đều nhẹ', 'Thuốc điều hòa nhịp tim', 300000, 'BS001'),
('PKB006', 'BN006', '2025-07-03', '2025-07-03', 'Đau cơ lưng', 'Cơ lưng căng cứng', 'Thuốc giảm đau, massage', 220000, 'BS002'),
('PKB007', 'BN007', '2025-07-04', '2025-07-04', 'Cận thị', 'Thị lực giảm', 'Kê đơn kính cận', 200000, 'BS003'),
('PKB008', 'BN008', '2025-07-04', '2025-07-04', 'Viêm khớp', 'Khớp sưng đau', 'Thuốc chống viêm', 280000, 'BS001'),
('PKB009', 'BN009', '2025-07-05', '2025-07-05', 'Viêm da cơ địa', 'Da đỏ, ngứa', 'Thuốc bôi da, tránh dị ứng', 240000, 'BS002'),
('PKB010', 'BN010', '2025-07-05', '2025-07-05', 'Viêm phụ khoa', 'Khí hư bất thường', 'Thuốc kháng sinh, vệ sinh', 320000, 'BS003'),
('PKB011', 'BN011', '2025-07-08', '2025-07-08', 'Sâu răng', 'Răng hàm bị sâu', 'Hàn răng, vệ sinh răng miệng', 200000, 'BS001'),
('PKB012', 'BN012', '2025-07-08', '2025-07-08', 'Viêm xoang', 'Nghẹt mũi, đau đầu', 'Thuốc xịt mũi, kháng sinh', 260000, 'BS002'),
('PKB013', 'BN013', '2025-07-09', '2025-07-09', 'Loét dạ dày', 'Đau dạ dày sau ăn', 'Thuốc ức chế acid, chế độ ăn', 350000, 'BS003'),
('PKB014', 'BN014', '2025-07-09', '2025-07-09', 'Sức khỏe tốt', 'Các chỉ số bình thường', 'Tiếp tục duy trì', 150000, 'BS001'),
('PKB015', 'BN015', '2025-07-10', '2025-07-10', 'Tăng huyết áp độ 1', 'Huyết áp 150/90', 'Thuốc hạ huyết áp, ít muối', 300000, 'BS002'),
('PKB016', 'BN001', '2025-07-15', '2025-07-15', 'Đau đầu cải thiện', 'Tình trạng tốt hơn', 'Tiếp tục thuốc', 180000, 'BS001'),
('PKB017', 'BN003', '2025-07-16', '2025-07-16', 'Viêm dạ dày thuyên giảm', 'Đau bụng giảm', 'Tiếp tục điều trị', 200000, 'BS002'),
('PKB018', 'BN005', '2025-07-17', '2025-07-17', 'Nhịp tim ổn định', 'Tim đập đều', 'Giảm liều thuốc', 250000, 'BS001'),
('PKB019', 'BN007', '2025-07-18', '2025-07-18', 'Thị lực ổn định', 'Đeo kính phù hợp', 'Khám lại 6 tháng', 150000, 'BS003'),
('PKB020', 'BN012', '2025-07-19', '2025-07-19', 'Viêm xoang cải thiện', 'Mũi thông thoáng hơn', 'Tiếp tục xịt mũi', 200000, 'BS002'),
('PKB021', 'BN002', '2025-07-22', '2025-07-22', 'Sức khỏe tốt', 'Tất cả chỉ số bình thường', 'Duy trì lối sống', 150000, 'BS001'),
('PKB022', 'BN004', '2025-07-23', '2025-07-23', 'Viêm họng khỏi', 'Họng không còn đỏ', 'Ngừng thuốc', 120000, 'BS003'),
('PKB023', 'BN006', '2025-07-24', '2025-07-24', 'Đau lưng cải thiện', 'Cơ lưng mềm hơn', 'Tiếp tục vật lý trị liệu', 180000, 'BS002'),
('PKB024', 'BN008', '2025-07-25', '2025-07-25', 'Viêm khớp giảm', 'Khớp ít sưng', 'Giảm liều thuốc', 220000, 'BS001'),
('PKB025', 'BN010', '2025-07-26', '2025-07-26', 'Phụ khoa bình thường', 'Không còn viêm nhiễm', 'Ngừng thuốc', 250000, 'BS003');

-- Thuốc
INSERT INTO Thuoc (MaThuoc, TenThuoc, CongDung, SoLuong, GiaTien, DonVi, HuongDanSuDung, HanSuDung) VALUES
('T001', 'Paracetamol 500mg', 'Giảm đau, hạ sốt', 500, 2000, 'Viên', 'Uống 1-2 viên khi đau, tối đa 6 viên/ngày', '2026-12-31'),
('T002', 'Omeprazole 20mg', 'Ức chế acid dạ dày', 300, 5000, 'Viên', 'Uống 1 viên/ngày trước ăn sáng', '2026-10-15'),
('T003', 'Amoxicillin 500mg', 'Kháng sinh', 200, 8000, 'Viên', 'Uống 1 viên x 3 lần/ngày', '2025-12-20'),
('T004', 'Cetirizine 10mg', 'Chống dị ứng', 400, 3000, 'Viên', 'Uống 1 viên/ngày', '2027-05-30'),
('T005', 'Ibuprofen 400mg', 'Giảm đau, chống viêm', 350, 4000, 'Viên', 'Uống 1-2 viên khi đau', '2026-08-25'),
('T006', 'Metformin 500mg', 'Điều trị tiểu đường', 250, 6000, 'Viên', 'Uống 1 viên x 2 lần/ngày', '2026-11-10'),
('T007', 'Amlodipine 5mg', 'Hạ huyết áp', 180, 7000, 'Viên', 'Uống 1 viên/ngày', '2027-02-28'),
('T008', 'Loratadine 10mg', 'Chống dị ứng', 320, 3500, 'Viên', 'Uống 1 viên/ngày', '2026-09-15'),
('T009', 'Dexamethasone 0.5mg', 'Chống viêm', 150, 4500, 'Viên', 'Uống theo chỉ định bác sĩ', '2025-11-30'),
('T010', 'Vitamin C 500mg', 'Bổ sung vitamin', 600, 1500, 'Viên', 'Uống 1 viên/ngày', '2027-01-20');

-- Đơn thuốc
INSERT INTO DonThuoc (MaDonThuoc, MaPhieuKham, NgayLapDon) VALUES
('DT001', 'PKB001', '2025-07-01'),
('DT002', 'PKB003', '2025-07-02'),
('DT003', 'PKB004', '2025-07-02'),
('DT004', 'PKB005', '2025-07-03'),
('DT005', 'PKB006', '2025-07-03'),
('DT006', 'PKB008', '2025-07-04'),
('DT007', 'PKB009', '2025-07-05'),
('DT008', 'PKB010', '2025-07-05'),
('DT009', 'PKB012', '2025-07-08'),
('DT010', 'PKB013', '2025-07-09'),
('DT011', 'PKB015', '2025-07-10'),
('DT012', 'PKB016', '2025-07-15'),
('DT013', 'PKB017', '2025-07-16'),
('DT014', 'PKB018', '2025-07-17'),
('DT015', 'PKB020', '2025-07-19'),
('DT016', 'PKB023', '2025-07-24'),
('DT017', 'PKB024', '2025-07-25');

-- Chi tiết đơn thuốc
INSERT INTO CTDonThuoc (MaDonThuoc, MaThuoc, SoLuong, GiaTien, HuongDanSuDung) VALUES
('DT001', 'T001', 10, 20000, 'Uống khi đau đầu'),
('DT002', 'T002', 14, 70000, 'Uống trước ăn sáng'),
('DT003', 'T003', 21, 168000, 'Uống đủ liều kháng sinh'),
('DT004', 'T007', 30, 210000, 'Uống hàng ngày'),
('DT005', 'T005', 20, 80000, 'Uống khi đau'),
('DT006', 'T005', 30, 120000, 'Uống sau ăn'),
('DT006', 'T009', 10, 45000, 'Uống theo chỉ định'),
('DT007', 'T004', 14, 42000, 'Uống mỗi tối'),
('DT008', 'T003', 21, 168000, 'Uống đủ liều'),
('DT009', 'T003', 21, 168000, 'Uống đủ liều kháng sinh'),
('DT010', 'T002', 30, 150000, 'Uống trước ăn'),
('DT011', 'T007', 30, 210000, 'Uống hàng ngày cùng giờ'),
('DT012', 'T001', 10, 20000, 'Uống khi cần'),
('DT013', 'T002', 14, 70000, 'Tiếp tục uống'),
('DT014', 'T007', 15, 105000, 'Giảm liều'),
('DT015', 'T003', 7, 56000, 'Uống thêm 1 tuần'),
('DT016', 'T005', 14, 56000, 'Uống khi đau'),
('DT017', 'T005', 15, 60000, 'Giảm liều dần');

-- Hóa đơn (tập trung vào tháng 7/2025)
INSERT INTO HoaDon (MaHoaDon, TenHoaDon, MaDonThuoc, MaPhieuKham, NgayLapHoaDon, GiaTien, TrangThai) VALUES
('HD001', 'Hóa đơn khám bệnh BN001', 'DT001', 'PKB001', '2025-07-01', 220000, 'Đã thanh toán'),
('HD002', 'Hóa đơn khám bệnh BN002', NULL, 'PKB002', '2025-07-01', 150000, 'Đã thanh toán'),
('HD003', 'Hóa đơn khám bệnh BN003', 'DT002', 'PKB003', '2025-07-02', 320000, 'Đã thanh toán'),
('HD004', 'Hóa đơn khám bệnh BN004', 'DT003', 'PKB004', '2025-07-02', 348000, 'Đã thanh toán'),
('HD005', 'Hóa đơn khám bệnh BN005', 'DT004', 'PKB005', '2025-07-03', 510000, 'Đã thanh toán'),
('HD006', 'Hóa đơn khám bệnh BN006', 'DT005', 'PKB006', '2025-07-03', 300000, 'Đã thanh toán'),
('HD007', 'Hóa đơn khám bệnh BN007', NULL, 'PKB007', '2025-07-04', 200000, 'Đã thanh toán'),
('HD008', 'Hóa đơn khám bệnh BN008', 'DT006', 'PKB008', '2025-07-04', 445000, 'Đã thanh toán'),
('HD009', 'Hóa đơn khám bệnh BN009', 'DT007', 'PKB009', '2025-07-05', 282000, 'Đã thanh toán'),
('HD010', 'Hóa đơn khám bệnh BN010', 'DT008', 'PKB010', '2025-07-05', 488000, 'Đã thanh toán'),
('HD011', 'Hóa đơn khám bệnh BN011', NULL, 'PKB011', '2025-07-08', 200000, 'Đã thanh toán'),
('HD012', 'Hóa đơn khám bệnh BN012', 'DT009', 'PKB012', '2025-07-08', 428000, 'Đã thanh toán'),
('HD013', 'Hóa đơn khám bệnh BN013', 'DT010', 'PKB013', '2025-07-09', 500000, 'Đã thanh toán'),
('HD014', 'Hóa đơn khám bệnh BN014', NULL, 'PKB014', '2025-07-09', 150000, 'Đã thanh toán'),
('HD015', 'Hóa đơn khám bệnh BN015', 'DT011', 'PKB015', '2025-07-10', 510000, 'Đã thanh toán'),
('HD016', 'Hóa đơn tái khám BN001', 'DT012', 'PKB016', '2025-07-15', 200000, 'Đã thanh toán'),
('HD017', 'Hóa đơn tái khám BN003', 'DT013', 'PKB017', '2025-07-16', 270000, 'Đã thanh toán'),
('HD018', 'Hóa đơn tái khám BN005', 'DT014', 'PKB018', '2025-07-17', 355000, 'Đã thanh toán'),
('HD019', 'Hóa đơn tái khám BN007', NULL, 'PKB019', '2025-07-18', 150000, 'Đã thanh toán'),
('HD020', 'Hóa đơn tái khám BN012', 'DT015', 'PKB020', '2025-07-19', 256000, 'Đã thanh toán'),
('HD021', 'Hóa đơn khám định kỳ BN002', NULL, 'PKB021', '2025-07-22', 150000, 'Đã thanh toán'),
('HD022', 'Hóa đơn tái khám BN004', NULL, 'PKB022', '2025-07-23', 120000, 'Đã thanh toán'),
('HD023', 'Hóa đơn tái khám BN006', 'DT016', 'PKB023', '2025-07-24', 236000, 'Đã thanh toán'),
('HD024', 'Hóa đơn tái khám BN008', 'DT017', 'PKB024', '2025-07-25', 280000, 'Đã thanh toán'),
('HD025', 'Hóa đơn tái khám BN010', NULL, 'PKB025', '2025-07-26', 250000, 'Đã thanh toán'),
('HD026', 'Hóa đơn khám BN011 - Thẻ BHYT', NULL, 'PKB011', '2025-07-08', 80000, 'Đã thanh toán'),
('HD027', 'Hóa đơn khám BN006 - Gói VIP', 'DT005', 'PKB006', '2025-07-03', 450000, 'Đã thanh toán'),
('HD028', 'Hóa đơn khám BN013 - Điều trị dài hạn', 'DT010', 'PKB013', '2025-07-09', 600000, 'Đã thanh toán'),
('HD029', 'Hóa đơn khám BN015 - Gói cao huyết áp', 'DT011', 'PKB015', '2025-07-10', 480000, 'Đã thanh toán'),
('HD030', 'Hóa đơn khám BN005 - Theo dõi tim mạch', 'DT004', 'PKB005', '2025-07-03', 550000, 'Đã thanh toán');
-- Thêm dữ liệu cho bảng QuiDinh
INSERT INTO QuiDinh (TenQuiDinh, GiaTri) VALUES
('MAX_PATIENT_PER_DAY', 50),
('DEFAULT_TIEN_KHAM', 150000);

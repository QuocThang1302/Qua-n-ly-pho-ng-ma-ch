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
  CaTruc TEXT,
  CongViec VARCHAR(100),
  TrangThai VARCHAR(50)
);

CREATE TABLE HenKhamBenh (
  MaKhamBenh VARCHAR(20) PRIMARY KEY,
  MaBenhNhan VARCHAR(20),
  LyDoKham TEXT,
  NgayKham DATE,
  NgayketThuc DATE,
  MaBacSi VARCHAR(20),
  TinhTrang TEXT,
  CHECK (NgayKham = NgayketThuc)
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
  MaQuiDinh VARCHAR(20) PRIMARY KEY,
  TenQuiDinh VARCHAR(100),
  MoTa TEXT,
  NgayCapNhat DATE,
  NguoiCapNhat VARCHAR(20)
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
ALTER TABLE QuiDinh ADD FOREIGN KEY (NguoiCapNhat) REFERENCES NhanVien (MaNhanVien);

-- Data
INSERT INTO Role (RoleID, TenRole) VALUES
('ADMIN', 'Quản trị'),
('DOCTOR', 'Bác sĩ'),
('MANAGER', 'Quản lý'),
('NURSE', 'Y Tá');

-- Thêm dữ liệu cho bảng BenhNhan
INSERT INTO BenhNhan (MaBenhNhan, Ho, Ten, NgaySinh, GioiTinh, SDT) VALUES
('BN001', 'Nguyễn', 'Văn An', '1985-03-15', 'Nam', '0901234567'),
('BN002', 'Trần', 'Thị Bình', '1990-07-22', 'Nữ', '0912345678'),
('BN003', 'Lê', 'Hoàng Cường', '1978-11-08', 'Nam', '0923456789'),
('BN004', 'Phạm', 'Thị Dung', '1995-01-30', 'Nữ', '0934567890'),
('BN005', 'Hoàng', 'Văn Em', '1987-09-12', 'Nam', '0945678901'),
('BN006', 'Vũ', 'Thị Phương', '1992-05-18', 'Nữ', '0956789012'),
('BN007', 'Đặng', 'Minh Quang', '1980-12-25', 'Nam', '0967890123'),
('BN008', 'Bùi', 'Thị Hoa', '1993-04-07', 'Nữ', '0978901234');

-- Thêm dữ liệu cho bảng NhanVien
INSERT INTO NhanVien (MaNhanVien, Ho, Ten, RoleID, Luong, NgaySinh, GioiTinh, CCCD, DiaChi, SDT, Email, MatKhau) VALUES
('NV001', 'Nguyễn', 'Thị Admin', 'ADMIN', 25000000.00, '1980-01-15', 'Nữ', '123456789012', '123 Đường ABC, Quận 1, TP.HCM', '0901111111', 'admin@phongkham.com', 'admin123'),
('NV002', 'Trần', 'Văn Bách', 'DOCTOR', 35000000.00, '1975-05-20', 'Nam', '234567890123', '456 Đường DEF, Quận 2, TP.HCM', '0902222222', 'bs.bach@phongkham.com', 'doctor123'),
('NV003', 'Lê', 'Thị Cẩm', 'DOCTOR', 32000000.00, '1982-08-10', 'Nữ', '345678901234', '789 Đường GHI, Quận 3, TP.HCM', '0903333333', 'bs.cam@phongkham.com', 'doctor456'),
('NV004', 'Phạm', 'Minh Đức', 'MANAGER', 28000000.00, '1978-12-03', 'Nam', '456789012345', '321 Đường JKL, Quận 4, TP.HCM', '0904444444', 'ql.duc@phongkham.com', 'manager123'),
('NV005', 'Hoàng', 'Thị Ên', 'NURSE', 18000000.00, '1990-03-25', 'Nữ', '567890123456', '654 Đường MNO, Quận 5, TP.HCM', '0905555555', 'yt.en@phongkham.com', 'nurse123'),
('NV006', 'Vũ', 'Văn Phúc', 'NURSE', 17500000.00, '1988-06-18', 'Nam', '678901234567', '987 Đường PQR, Quận 6, TP.HCM', '0906666666', 'yt.phuc@phongkham.com', 'nurse456'),
('NV007', 'Đặng', 'Thị Giang', 'DOCTOR', 34000000.00, '1979-09-14', 'Nữ', '789012345678', '147 Đường STU, Quận 7, TP.HCM', '0907777777', 'bs.giang@phongkham.com', 'doctor789');

-- Thêm dữ liệu cho bảng LichTruc
INSERT INTO LichTruc (MaLichTruc, MaBacSi, NgayTruc, CaTruc, CongViec, TrangThai) VALUES
('LT001', 'NV002', '2025-07-01', 'Sáng', 'Khám bệnh tổng quát', 'Đã xác nhận'),
('LT002', 'NV003', '2025-07-01', 'Chiều', 'Khám chuyên khoa nội', 'Đã xác nhận'),
('LT003', 'NV007', '2025-07-01', 'Tối', 'Khám cấp cứu', 'Đã xác nhận'),
('LT004', 'NV002', '2025-07-02', 'Sáng', 'Khám bệnh tổng quát', 'Chờ xác nhận'),
('LT005', 'NV003', '2025-07-02', 'Chiều', 'Khám chuyên khoa nội', 'Đã xác nhận'),
('LT006', 'NV007', '2025-07-03', 'Sáng', 'Khám chuyên khoa phụ khoa', 'Đã xác nhận'),
('LT007', 'NV002', '2025-07-03', 'Tối', 'Khám cấp cứu', 'Đã xác nhận'),
('LT008', 'NV003', '2025-07-04', 'Sáng', 'Khám nội tổng quát', 'Chờ xác nhận');

-- Thêm dữ liệu cho bảng HenKhamBenh
INSERT INTO HenKhamBenh (MaKhamBenh, MaBenhNhan, LyDoKham, NgayKham, NgayketThuc, MaBacSi, TinhTrang) VALUES
('HKB001', 'BN001', 'Đau đầu, chóng mặt kéo dài 3 ngày', '2025-07-01', '2025-07-01', 'NV002', 'Đã hoàn thành'),
('HKB002', 'BN002', 'Khám sức khỏe định kỳ', '2025-07-01', '2025-07-01', 'NV003', 'Đã hoàn thành'),
('HKB003', 'BN003', 'Đau bụng, buồn nôn', '2025-07-02', '2025-07-02', 'NV002', 'Đã xác nhận'),
('HKB004', 'BN004', 'Khám thai định kỳ', '2025-07-02', '2025-07-02', 'NV007', 'Đã xác nhận'),
('HKB005', 'BN005', 'Ho khan, sốt nhẹ', '2025-07-03', '2025-07-03', 'NV003', 'Chờ khám'),
('HKB006', 'BN006', 'Đau lưng, tê tay', '2025-07-03', '2025-07-03', 'NV002', 'Chờ khám');

-- Thêm dữ liệu cho bảng PhieuKhamBenh
INSERT INTO PhieuKhamBenh (MaPhieuKham, MaBenhNhan, NgayKham, NgayLapPhieu, ChanDoan, KetQuaKham, DieuTri, TienKham, MaBacSi) VALUES
('PKB001', 'BN001', '2025-07-01', '2025-07-01', 'Hội chứng căng thẳng, đau đầu do stress', 'Huyết áp bình thường, nhiệt độ 36.5°C, mạch 72 lần/phút', 'Nghỉ ngơi, uống thuốc giảm đau theo chỉ định', 200000.00, 'NV002'),
('PKB002', 'BN002', '2025-07-01', '2025-07-01', 'Sức khỏe bình thường', 'Các chỉ số sinh hiệu ổn định, không có dấu hiệu bất thường', 'Duy trì lối sống lành mạnh, tái khám sau 6 tháng', 150000.00, 'NV003'),
('PKB003', 'BN007', '2025-06-28', '2025-06-28', 'Viêm dạ dày cấp', 'Đau vùng thượng vị, buồn nôn, không sốt', 'Kiêng cay nóng, ăn nhạt, uống thuốc theo đơn', 180000.00, 'NV002'),
('PKB004', 'BN008', '2025-06-29', '2025-06-29', 'Cảm cúm thông thường', 'Sốt nhẹ 37.2°C, ho có đờm, nghẹt mũi', 'Nghỉ ngơi, uống thuốc cảm cúm, nhiều nước', 120000.00, 'NV003');

-- Thêm dữ liệu cho bảng Thuoc
INSERT INTO Thuoc (MaThuoc, TenThuoc, CongDung, SoLuong, GiaTien, DonVi, HuongDanSuDung, HanSuDung) VALUES
('T001', 'Paracetamol 500mg', 'Giảm đau, hạ sốt', 500, 2000.00, 'Viên', 'Uống 1-2 viên/lần, tối đa 8 viên/ngày', '2026-12-31'),
('T002', 'Amoxicillin 500mg', 'Kháng sinh điều trị nhiễm khuẩn', 200, 5000.00, 'Viên', 'Uống 1 viên/lần, ngày 3 lần sau ăn', '2026-10-15'),
('T003', 'Omeprazole 20mg', 'Điều trị loét dạ dày, trào ngược dạ dày', 300, 8000.00, 'Viên', 'Uống 1 viên/ngày trước ăn sáng 30 phút', '2026-08-20'),
('T004', 'Loratadine 10mg', 'Chống dị ứng', 150, 3000.00, 'Viên', 'Uống 1 viên/ngày, có thể uống lúc đói', '2027-01-10'),
('T005', 'Vitamin C 500mg', 'Bổ sung vitamin C, tăng sức đề kháng', 400, 1500.00, 'Viên', 'Uống 1 viên/ngày sau ăn', '2026-11-30'),
('T006', 'Dextromethorphan', 'Thuốc ho', 100, 12000.00, 'Chai', 'Uống 1 thìa/lần, ngày 3 lần', '2026-09-15'),
('T007', 'Domperidone 10mg', 'Chống nôn, đầy hơi', 200, 4000.00, 'Viên', 'Uống 1 viên trước ăn 15-30 phút', '2026-07-25'),
('T008', 'Ibuprofen 400mg', 'Giảm đau, chống viêm', 250, 3500.00, 'Viên', 'Uống 1 viên/lần, tối đa 3 lần/ngày sau ăn', '2026-12-20');

-- Thêm dữ liệu cho bảng DonThuoc
INSERT INTO DonThuoc (MaDonThuoc, MaPhieuKham, NgayLapDon) VALUES
('DT001', 'PKB001', '2025-07-01'),
('DT002', 'PKB002', '2025-07-01'),
('DT003', 'PKB003', '2025-06-28'),
('DT004', 'PKB004', '2025-06-29');

-- Thêm dữ liệu cho bảng CTDonThuoc
INSERT INTO CTDonThuoc (MaDonThuoc, MaThuoc, SoLuong, GiaTien, HuongDanSuDung) VALUES
('DT001', 'T001', 10, 20000.00, 'Uống khi đau đầu, 1-2 viên/lần'),
('DT001', 'T005', 14, 21000.00, 'Uống 1 viên/ngày sau ăn sáng'),
('DT002', 'T005', 30, 45000.00, 'Bổ sung vitamin, 1 viên/ngày'),
('DT003', 'T003', 14, 112000.00, 'Uống trước ăn sáng 30 phút, 1 viên/ngày'),
('DT003', 'T007', 20, 80000.00, 'Uống trước ăn 15 phút, ngày 3 lần'),
('DT004', 'T001', 16, 32000.00, 'Uống khi sốt, 1-2 viên/lần'),
('DT004', 'T006', 1, 12000.00, 'Uống 1 thìa khi ho, ngày 3 lần'),
('DT004', 'T005', 7, 10500.00, 'Tăng sức đề kháng, 1 viên/ngày');

-- Thêm dữ liệu cho bảng HoaDon
INSERT INTO HoaDon (MaHoaDon, TenHoaDon, MaDonThuoc, MaPhieuKham, NgayLapHoaDon, GiaTien, TrangThai) VALUES
('HD001', 'Hóa đơn khám bệnh - BN001', 'DT001', 'PKB001', '2025-07-01', 241000.00, 'Đã thanh toán'),
('HD002', 'Hóa đơn khám sức khỏe - BN002', 'DT002', 'PKB002', '2025-07-01', 195000.00, 'Đã thanh toán'),
('HD003', 'Hóa đơn điều trị - BN007', 'DT003', 'PKB003', '2025-06-28', 372000.00, 'Đã thanh toán'),
('HD004', 'Hóa đơn khám cảm cúm - BN008', 'DT004', 'PKB004', '2025-06-29', 174500.00, 'Chờ thanh toán');

-- Thêm dữ liệu cho bảng QuiDinh
INSERT INTO QuiDinh (MaQuiDinh, TenQuiDinh, MoTa, NgayCapNhat, NguoiCapNhat) VALUES
('QD001', 'Quy định giờ làm việc', 'Phòng khám hoạt động từ 7:00 - 22:00 hàng ngày. Ca sáng: 7:00-12:00, Ca chiều: 13:00-18:00, Ca tối: 18:00-22:00', '2025-01-15', 'NV001'),
('QD002', 'Quy định về tiền khám', 'Phí khám bệnh cơ bản: 120.000đ - 200.000đ tùy theo chuyên khoa. Khám sức khỏe định kỳ: 150.000đ', '2025-01-20', 'NV004'),
('QD003', 'Quy định đặt lịch hẹn', 'Bệnh nhân có thể đặt lịch trước 1-7 ngày. Hủy lịch phải thông báo trước ít nhất 2 giờ', '2025-02-01', 'NV001'),
('QD004', 'Quy định về thuốc', 'Kiểm tra hạn sử dụng thuốc hàng tuần. Thuốc gần hết hạn (❤ tháng) cần báo cáo để xử lý', '2025-02-10', 'NV004'),
('QD005', 'Quy định an toàn và vệ sinh', 'Khử trùng phòng khám sau mỗi ca làm việc. Đeo khẩu trang và rửa tay thường xuyên', '2025-01-25', 'NV001');
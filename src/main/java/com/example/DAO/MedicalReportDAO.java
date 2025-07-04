package com.example.DAO;


import com.example.model.BillModel;
import com.example.utils.DatabaseConnector;
import com.example.model.MedicalReportModel;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicalReportDAO {

    // INSERT - Thêm PhieuKhamBenh
    public static boolean insertPhieuKhamBenh(MedicalReportModel medicalReport, LocalDateTime ngayKham, LocalDateTime ngayLapPhieu, String dieuTri, String ketQuaKham, double tienKham) {
        String insertPhieuKham = "INSERT INTO PhieuKhamBenh (MaPhieuKham, MaBenhNhan, NgayKham, NgayLapPhieu, ChanDoan, KetQuaKham, DieuTri, TienKham, MaBacSi) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            try {
                // Insert into PhieuKhamBenh
                try (PreparedStatement stmtPhieu = conn.prepareStatement(insertPhieuKham)) {
                    stmtPhieu.setString(1, medicalReport.getMaPhieuKham());
                    stmtPhieu.setString(2, medicalReport.getMaBenhNhan());
                    stmtPhieu.setDate(3, Date.valueOf(ngayKham.toLocalDate()));
                    stmtPhieu.setTimestamp(4, Timestamp.valueOf(ngayLapPhieu));
                    stmtPhieu.setString(5, medicalReport.getChanDoan());
                    stmtPhieu.setString(6, ketQuaKham);
                    stmtPhieu.setString(7, dieuTri);
                    stmtPhieu.setDouble(8, tienKham);
                    stmtPhieu.setString(9, medicalReport.getMaBacSi());

                    int result = stmtPhieu.executeUpdate();
                    conn.commit();
                    return result > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm phiếu khám bệnh: " + e.getMessage());
        }
        return false;
    }

    // READ - Lấy tất cả medical reports
    public static List<MedicalReportModel> getAllMedicalReports() {
        List<MedicalReportModel> reports = new ArrayList<>();
        String sql = """
                SELECT 
                    h.MaKhamBenh,
                    p.MaPhieuKham,
                    h.MaBenhNhan,
                    h.MaBacSi,
                    CONCAT(bn.Ho, ' ', bn.Ten) as HoTenBenhNhan,
                    CONCAT(nv.Ho, ' ', nv.Ten) as TenBacSi,
                    bn.NgaySinh,
                    bn.SDT as SoDienThoai,
                    bn.GioiTinh,
                    h.LyDoKham,
                    p.NgayLapPhieu,
                    p.ChanDoan,
                    hd.MaHoaDon
                FROM HenKhamBenh h
                INNER JOIN PhieuKhamBenh p ON h.MaBenhNhan = p.MaBenhNhan
                INNER JOIN BenhNhan bn ON h.MaBenhNhan = bn.MaBenhNhan
                INNER JOIN NhanVien nv ON h.MaBacSi = nv.MaNhanVien
                LEFT JOIN HoaDon hd ON p.MaPhieuKham = hd.MaPhieuKham
                WHERE nv.RoleID = 'DOCTOR' OR nv.RoleID = 'BS'
                ORDER BY p.NgayLapPhieu DESC
            """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MedicalReportModel report = new MedicalReportModel();
                report.setMaKhamBenh(rs.getString("MaKhamBenh"));
                report.setMaPhieuKham(rs.getString("MaPhieuKham"));
                report.setMaBenhNhan(rs.getString("MaBenhNhan"));
                report.setMaBacSi(rs.getString("MaBacSi"));
                report.setHoTen(rs.getString("HoTenBenhNhan"));
                report.setTenBacSi(rs.getString("TenBacSi"));

                Date ngaySinh = rs.getDate("NgaySinh");
                if (ngaySinh != null) {
                    report.setNgaySinh(ngaySinh.toLocalDate());
                }

                report.setSoDienThoai(rs.getString("SoDienThoai"));
                report.setGioiTinh(rs.getString("GioiTinh"));
                report.setLyDoKham(rs.getString("LyDoKham"));

                Timestamp ngayLap = rs.getTimestamp("NgayLapPhieu");
                if (ngayLap != null) {
                    report.setNgayKham(ngayLap.toLocalDateTime());
                }

                report.setChanDoan(rs.getString("ChanDoan"));

                // Tạo BillModel nếu có thông tin tiền khám
                String maHoaDon = rs.getString("MaHoaDon");
                if (maHoaDon != null && !maHoaDon.isEmpty()) {
                    BillModel bill = BillDAO.getBillById(maHoaDon);
                    report.setHoaDon(bill);
                }

                reports.add(report);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách phiếu khám bệnh: " + e.getMessage());
        }

        return reports;
    }

    // READ - Lấy medical report theo ID
    public static MedicalReportModel getMedicalReportById(String maKhamBenh) {
        String sql = """
                SELECT 
                    h.MaKhamBenh,
                    p.MaPhieuKham,
                    h.MaBenhNhan,
                    h.MaBacSi,
                    CONCAT(bn.Ho, ' ', bn.Ten) as HoTenBenhNhan,
                    CONCAT(nv.Ho, ' ', nv.Ten) as TenBacSi,
                    bn.NgaySinh,
                    bn.SDT as SoDienThoai,
                    bn.GioiTinh,
                    h.LyDoKham,
                    p.NgayLapPhieu,
                    p.ChanDoan,
                    hd.MaHoaDon
                FROM HenKhamBenh h
                INNER JOIN PhieuKhamBenh p ON h.MaBenhNhan = p.MaBenhNhan
                INNER JOIN BenhNhan bn ON h.MaBenhNhan = bn.MaBenhNhan
                INNER JOIN NhanVien nv ON h.MaBacSi = nv.MaNhanVien
                LEFT JOIN HoaDon hd ON p.MaPhieuKham = hd.MaPhieuKham
                WHERE h.MaKhamBenh = ? AND nv.RoleID = 'DOCTOR' 
                ORDER BY p.NgayLapPhieu DESC
            """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maKhamBenh);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MedicalReportModel report = new MedicalReportModel();
                    report.setMaKhamBenh(rs.getString("MaKhamBenh"));
                    report.setMaPhieuKham(rs.getString("MaPhieuKham"));
                    report.setMaBenhNhan(rs.getString("MaBenhNhan"));
                    report.setMaBacSi(rs.getString("MaBacSi"));
                    report.setHoTen(rs.getString("HoTenBenhNhan"));
                    report.setTenBacSi(rs.getString("TenBacSi"));

                    Date ngaySinh = rs.getDate("NgaySinh");
                    if (ngaySinh != null) {
                        report.setNgaySinh(ngaySinh.toLocalDate());
                    }

                    report.setSoDienThoai(rs.getString("SoDienThoai"));
                    report.setGioiTinh(rs.getString("GioiTinh"));
                    report.setLyDoKham(rs.getString("LyDoKham"));

                    Timestamp ngayLap = rs.getTimestamp("NgayLapPhieu");
                    if (ngayLap != null) {
                        report.setNgayKham(ngayLap.toLocalDateTime());
                    }

                    report.setChanDoan(rs.getString("ChanDoan"));

                    // Tạo BillModel nếu có thông tin tiền khám
                    String maHoaDon = rs.getString("MaHoaDon");
                    if (maHoaDon != null && !maHoaDon.isEmpty()) {
                        BillModel bill = BillDAO.getBillById(maHoaDon);
                        report.setHoaDon(bill);
                    }

                    return report;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy phiếu khám bệnh theo ID: " + e.getMessage());
        }

        return null;
    }
    public static List<MedicalReportModel> getMedicalReportsByPatientId(String maBenhNhan) {
        List<MedicalReportModel> reports = new ArrayList<>();

        String sql = """
        SELECT 
            h.MaKhamBenh,
            p.MaPhieuKham,
            h.MaBenhNhan,
            h.MaBacSi,
            CONCAT(bn.Ho, ' ', bn.Ten) as HoTenBenhNhan,
            CONCAT(nv.Ho, ' ', nv.Ten) as TenBacSi,
            bn.NgaySinh,
            bn.SDT as SoDienThoai,
            bn.GioiTinh,
            h.LyDoKham,
            p.NgayLapPhieu,
            p.ChanDoan,
            hd.MaHoaDon
        FROM HenKhamBenh h
        INNER JOIN PhieuKhamBenh p ON h.MaBenhNhan = p.MaBenhNhan
        INNER JOIN BenhNhan bn ON h.MaBenhNhan = bn.MaBenhNhan
        INNER JOIN NhanVien nv ON h.MaBacSi = nv.MaNhanVien
        LEFT JOIN HoaDon hd ON p.MaPhieuKham = hd.MaPhieuKham
        WHERE h.MaBenhNhan = ? AND nv.RoleID = 'DOCTOR'
        ORDER BY p.NgayLapPhieu DESC
    """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maBenhNhan);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicalReportModel report = new MedicalReportModel();
                    report.setMaKhamBenh(rs.getString("MaKhamBenh"));
                    report.setMaPhieuKham(rs.getString("MaPhieuKham"));
                    report.setMaBenhNhan(rs.getString("MaBenhNhan"));
                    report.setMaBacSi(rs.getString("MaBacSi"));
                    report.setHoTen(rs.getString("HoTenBenhNhan"));
                    report.setTenBacSi(rs.getString("TenBacSi"));

                    Date ngaySinh = rs.getDate("NgaySinh");
                    if (ngaySinh != null) {
                        report.setNgaySinh(ngaySinh.toLocalDate());
                    }

                    report.setSoDienThoai(rs.getString("SoDienThoai"));
                    report.setGioiTinh(rs.getString("GioiTinh"));
                    report.setLyDoKham(rs.getString("LyDoKham"));

                    Timestamp ngayLap = rs.getTimestamp("NgayLapPhieu");
                    if (ngayLap != null) {
                        report.setNgayKham(ngayLap.toLocalDateTime());
                    }

                    report.setChanDoan(rs.getString("ChanDoan"));

                    String maHoaDon = rs.getString("MaHoaDon");
                    if (maHoaDon != null && !maHoaDon.isEmpty()) {
                        BillModel bill = BillDAO.getBillById(maHoaDon);
                        report.setHoaDon(bill);
                    }

                    reports.add(report);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy medical reports: " + e.getMessage());
        }

        return reports;
    }

    public static List<MedicalReportModel> getMedicalReportsByDate(LocalDate date) {
        List<MedicalReportModel> reports = new ArrayList<>();
        String sql = """
            SELECT 
                h.MaKhamBenh,
                p.MaPhieuKham,
                h.MaBenhNhan,
                h.MaBacSi,
                CONCAT(bn.Ho, ' ', bn.Ten) as HoTenBenhNhan,
                h.LyDoKham,
                p.KetQuaKham,
                p.ChanDoan,
                p.DieuTri,
                p.TienKham,
                p.NgayKham,
                hd.MaHoaDon
            FROM HenKhamBenh h
            INNER JOIN PhieuKhamBenh p ON h.MaBenhNhan = p.MaBenhNhan
            INNER JOIN BenhNhan bn ON h.MaBenhNhan = bn.MaBenhNhan
            LEFT JOIN HoaDon hd ON p.MaPhieuKham = hd.MaPhieuKham
            WHERE p.NgayKham = ?
            ORDER BY p.NgayKham DESC
            LIMIT 40
        """;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicalReportModel report = new MedicalReportModel();
                    report.setMaKhamBenh(rs.getString("MaKhamBenh"));
                    report.setMaPhieuKham(rs.getString("MaPhieuKham"));
                    report.setMaBenhNhan(rs.getString("MaBenhNhan"));
                    report.setMaBacSi(rs.getString("MaBacSi"));
                    report.setHoTen(rs.getString("HoTenBenhNhan"));
                    report.setLyDoKham(rs.getString("LyDoKham"));
                    report.setKetQuaKham(rs.getString("KetQuaKham"));
                    report.setChanDoan(rs.getString("ChanDoan"));
                    report.setDieuTri(rs.getString("DieuTri"));
                    report.setTienKham(rs.getDouble("TienKham"));
                    // Nếu cần lấy chi tiết hóa đơn:
                    String maHoaDon = rs.getString("MaHoaDon");
                    if (maHoaDon != null && !maHoaDon.isEmpty()) {
                        BillModel bill = BillDAO.getBillById(maHoaDon);
                        report.setHoaDon(bill);
                    }
                    reports.add(report);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách phiếu khám bệnh theo ngày: " + e.getMessage());
        }
        return reports;
    }

    // UPDATE - Cập nhật medical report
    public static boolean updatePhieuKhamBenh(MedicalReportModel medicalReport, LocalDateTime ngayKham, LocalDateTime ngayLapPhieu, String dieuTri, String ketQuaKham, double tienKham) {
        String updatePhieuKham = "UPDATE PhieuKhamBenh SET MaBenhNhan = ?, NgayKham = ?, NgayLapPhieu = ?, ChanDoan = ?, KetQuaKham = ?, DieuTri = ?, TienKham = ?, MaBacSi = ? " +
                "WHERE MaPhieuKham = ?";

        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmtPhieu = conn.prepareStatement(updatePhieuKham)) {
                    stmtPhieu.setString(1, medicalReport.getMaBenhNhan());
                    stmtPhieu.setDate(2, Date.valueOf(ngayKham.toLocalDate()));
                    stmtPhieu.setTimestamp(3, Timestamp.valueOf(ngayLapPhieu));
                    stmtPhieu.setString(4, medicalReport.getChanDoan());
                    stmtPhieu.setString(5, ketQuaKham);
                    stmtPhieu.setString(6, dieuTri);
                    stmtPhieu.setDouble(7, tienKham);
                    stmtPhieu.setString(8, medicalReport.getMaBacSi());
                    stmtPhieu.setString(9, medicalReport.getMaPhieuKham()); // WHERE condition

                    int result = stmtPhieu.executeUpdate();
                    conn.commit();
                    return result > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật phiếu khám bệnh: " + e.getMessage());
        }
        return false;
    }

    // DELETE - Xóa medical report
    public static boolean deleteMedicalReport(String maKhamBenh) {
        String deletePhieuKham = "DELETE FROM PhieuKhamBenh WHERE MaPhieuKham = ?";

        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            try {
                // Delete PhieuKhamBenh first (foreign key constraint)
                try (PreparedStatement stmtPhieu = conn.prepareStatement(deletePhieuKham)) {
                    stmtPhieu.setString(1, maKhamBenh);
                    int result = stmtPhieu.executeUpdate();
                    conn.commit();
                    return result > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa phiếu khám bệnh: " + e.getMessage());
        }
        return false;
    }

    public static MedicalReportModel getByMaPhieuKham(String maPhieuKham) {
        String sql = "SELECT * FROM PhieuKhamBenh WHERE MaPhieuKham = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhieuKham);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MedicalReportModel report = new MedicalReportModel();
                    report.setMaPhieuKham(rs.getString("MaPhieuKham"));
                    // Có thể set thêm các trường khác nếu cần
                    return report;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra mã phiếu khám: " + e.getMessage());
        }
        return null;
    }
}
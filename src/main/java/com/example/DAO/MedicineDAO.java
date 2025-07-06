package com.example.DAO;

import com.example.utils.DatabaseConnector;
import com.example.model.MedicineModel;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public static List<MedicineModel> getAllMedicines() {
        List<MedicineModel> list = new ArrayList<>();
        String sql = """
                SELECT MaThuoc, TenThuoc, CongDung, SoLuong, GiaTien, DonVi, HuongDanSuDung
                FROM Thuoc
                ORDER BY MaThuoc
                """; // Thêm ORDER BY để đảm bảo thứ tự

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MedicineModel m = new MedicineModel();
                m.setMaThuoc(rs.getString("MaThuoc"));
                m.setTenThuoc(rs.getString("TenThuoc"));
                m.setCongDung(rs.getString("CongDung"));
                m.setSoLuong(rs.getInt("SoLuong"));
                m.setGiaTien(rs.getDouble("GiaTien"));
                m.setDonVi(rs.getString("DonVi"));
                m.setHuongDanSuDung(rs.getString("HuongDanSuDung"));
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách thuốc: " + e.getMessage());
        }
        return list;
    }

    public static MedicineModel getMedicineById(String maThuoc) {
        String sql = """
                SELECT MaThuoc, TenThuoc, CongDung, SoLuong, GiaTien, DonVi, HuongDanSuDung
                FROM Thuoc WHERE MaThuoc = ?
                """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maThuoc);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MedicineModel m = new MedicineModel();
                    m.setMaThuoc(rs.getString("MaThuoc"));
                    m.setTenThuoc(rs.getString("TenThuoc"));
                    m.setCongDung(rs.getString("CongDung"));
                    m.setSoLuong(rs.getInt("SoLuong"));
                    m.setGiaTien(rs.getDouble("GiaTien"));
                    m.setDonVi(rs.getString("DonVi"));
                    m.setHuongDanSuDung(rs.getString("HuongDanSuDung"));
                    return m;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thuốc theo mã: " + e.getMessage());
        }
        return null;
    }

    public static boolean insertMedicine(MedicineModel medicine, LocalDate hanSuDung) {
        String sql = """
            INSERT INTO Thuoc (MaThuoc, TenThuoc, CongDung, SoLuong, GiaTien, DonVi, HuongDanSuDung, HanSuDung)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicine.getMaThuoc());
            stmt.setString(2, medicine.getTenThuoc());
            stmt.setString(3, medicine.getCongDung());
            stmt.setInt(4, medicine.getSoLuong());
            stmt.setDouble(5, medicine.getGiaTien());
            stmt.setString(6, medicine.getDonVi());
            stmt.setString(7, medicine.getHuongDanSuDung());
            stmt.setDate(8, java.sql.Date.valueOf(hanSuDung));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm thuốc: " + e.getMessage());
            throw new RuntimeException(e); // Ném lại ngoại lệ để xử lý ở controller
        }
    }

    public static boolean updateMedicine(MedicineModel medicine) {
        String sql = """
            UPDATE Thuoc
            SET TenThuoc = ?, CongDung = ?, SoLuong = ?, GiaTien = ?, DonVi = ?, HuongDanSuDung = ?
            WHERE MaThuoc = ?
            """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicine.getTenThuoc());
            stmt.setString(2, medicine.getCongDung());
            stmt.setInt(3, medicine.getSoLuong());
            stmt.setDouble(4, medicine.getGiaTien());
            stmt.setString(5, medicine.getDonVi());
            stmt.setString(6, medicine.getHuongDanSuDung());
            stmt.setString(7, medicine.getMaThuoc());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật thuốc: " + e.getMessage());
            throw new RuntimeException(e); // Ném lại ngoại lệ
        }
    }

    public static boolean deleteMedicine(String maThuoc) {
        String sql = "DELETE FROM Thuoc WHERE MaThuoc = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maThuoc);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xoá thuốc: " + e.getMessage());
            throw new RuntimeException(e); // Ném lại ngoại lệ
        }
    }

    public static int countDistinctPrescriptionsByMedicineInMonth(String maThuoc, int year, int month) {
        String sql = """
        SELECT COUNT(DISTINCT ct.MaDonThuoc)
        FROM CTDonThuoc ct
        JOIN DonThuoc dt ON ct.MaDonThuoc = dt.MaDonThuoc
        WHERE ct.MaThuoc = ?
          AND EXTRACT(YEAR FROM dt.NgayLapDon) = ?
          AND EXTRACT(MONTH FROM dt.NgayLapDon) = ?
    """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maThuoc);
            stmt.setInt(2, year);
            stmt.setInt(3, month);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm đơn thuốc theo tháng cho thuốc " + maThuoc + ": " + e.getMessage());
        }

        return 0;
    }

    public static List<MedicineModel> getMedicinesUsedInMonth(int year, int month) {
        List<MedicineModel> list = new ArrayList<>();

        String sql = """
        SELECT DISTINCT t.MaThuoc, t.TenThuoc, t.CongDung, t.SoLuong, t.GiaTien, t.DonVi, t.HuongDanSuDung
        FROM Thuoc t
        JOIN CTDonThuoc ct ON t.MaThuoc = ct.MaThuoc
        JOIN DonThuoc dt ON ct.MaDonThuoc = dt.MaDonThuoc
        WHERE EXTRACT(YEAR FROM dt.NgayLapDon) = ? AND EXTRACT(MONTH FROM dt.NgayLapDon) = ?
        ORDER BY t.MaThuoc
        """; // Thêm ORDER BY để đảm bảo thứ tự

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);
            stmt.setInt(2, month);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MedicineModel m = new MedicineModel();
                m.setMaThuoc(rs.getString("MaThuoc"));
                m.setTenThuoc(rs.getString("TenThuoc"));
                m.setCongDung(rs.getString("CongDung"));
                m.setSoLuong(rs.getInt("SoLuong"));
                m.setGiaTien(rs.getDouble("GiaTien"));
                m.setDonVi(rs.getString("DonVi"));
                m.setHuongDanSuDung(rs.getString("HuongDanSuDung"));
                list.add(m);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc thuốc theo tháng: " + e.getMessage());
        }

        return list;
    }

    public static int getNextIdNumber(String prefix) {
        String sql = "SELECT MaThuoc " +
                "FROM thuoc " +
                "WHERE MaThuoc LIKE ? " +
                "AND MaThuoc ~ ? " +
                "ORDER BY CAST(SUBSTRING(MaThuoc FROM ?) AS INTEGER) DESC " +
                "LIMIT 1";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, prefix + "%");
            stmt.setString(2, "^" + prefix + "[0-9]+$");
            stmt.setString(3, String.format("^.{%d}(\\d+)$", prefix.length()));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String medicineId = rs.getString("MaThuoc");
                if (medicineId != null && medicineId.startsWith(prefix)) {
                    try {
                        String numberPart = medicineId.substring(prefix.length());
                        if (numberPart.matches("\\d+")) {
                            int currentNumber = Integer.parseInt(numberPart);
                            System.out.println("Found max ID: " + medicineId + ", number: " + currentNumber);
                            return currentNumber + 1;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing number from: " + medicineId);
                    }
                }
            }

            System.out.println("No existing ID found for prefix: " + prefix);
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in getNextIdNumber: " + e.getMessage());
            return 1;
        }
    }

    // Phương thức mới: Giảm số lượng thuốc trong kho
    public static boolean reduceMedicineQuantity(String maThuoc, int soLuongGiam) {
        String sql = "UPDATE Thuoc SET SoLuong = SoLuong - ? WHERE MaThuoc = ? AND SoLuong >= ?";
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, soLuongGiam);
            stmt.setString(2, maThuoc);
            stmt.setInt(3, soLuongGiam); // Đảm bảo số lượng còn lại >= số lượng cần giảm
            
            int result = stmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi giảm số lượng thuốc: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Phương thức kiểm tra số lượng thuốc có đủ không
    public static boolean checkMedicineAvailability(String maThuoc, int soLuongCan) {
        String sql = "SELECT SoLuong FROM Thuoc WHERE MaThuoc = ?";
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maThuoc);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int soLuongHienTai = rs.getInt("SoLuong");
                return soLuongHienTai >= soLuongCan;
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra số lượng thuốc: " + e.getMessage());
        }
        
        return false;
    }
}
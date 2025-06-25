package com.example.DAO;

import com.example.model.DatabaseConnector;
import com.example.model.MedicineModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public static List<MedicineModel> getAllMedicines() {
        List<MedicineModel> list = new ArrayList<>();
        String sql = """
                SELECT t.MaThuoc, TenThuoc, CongDung, SoLuong, GiaTien,
                       (SELECT HuongDanSuDung FROM CTDonThuoc ct WHERE ct.MaThuoc = t.MaThuoc LIMIT 1) AS HuongDanSuDung
                FROM Thuoc t
                """;

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
                SELECT t.MaThuoc, TenThuoc, CongDung, SoLuong, GiaTien,
                       (SELECT HuongDanSuDung FROM CTDonThuoc ct WHERE ct.MaThuoc = t.MaThuoc LIMIT 1) AS HuongDanSuDung
                FROM Thuoc t WHERE t.MaThuoc = ?
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
                    m.setHuongDanSuDung(rs.getString("HuongDanSuDung"));
                    return m;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thuốc theo mã: " + e.getMessage());
        }
        return null;
    }

    public static void insertMedicine(MedicineModel medicine) {
        String sql = "INSERT INTO Thuoc (MaThuoc, TenThuoc, CongDung, SoLuong, GiaTien, HanSuDung) VALUES (?, ?, ?, ?, ?, CURRENT_DATE)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicine.getMaThuoc());
            stmt.setString(2, medicine.getTenThuoc());
            stmt.setString(3, medicine.getCongDung());
            stmt.setInt(4, medicine.getSoLuong());
            stmt.setDouble(5, medicine.getGiaTien());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm thuốc: " + e.getMessage());
        }
    }

    public static void updateMedicine(MedicineModel medicine) {
        String sql = "UPDATE Thuoc SET TenThuoc = ?, CongDung = ?, SoLuong = ?, GiaTien = ? WHERE MaThuoc = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicine.getTenThuoc());
            stmt.setString(2, medicine.getCongDung());
            stmt.setInt(3, medicine.getSoLuong());
            stmt.setDouble(4, medicine.getGiaTien());
            stmt.setString(5, medicine.getMaThuoc());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật thuốc: " + e.getMessage());
        }
    }

    public static void deleteMedicine(String maThuoc) {
        String sql = "DELETE FROM Thuoc WHERE MaThuoc = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maThuoc);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi khi xoá thuốc: " + e.getMessage());
        }
    }
}

package com.example.DAO;

import com.example.model.PatientModel;
import com.example.model.DatabaseConnector;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.time.LocalDate;

public class PatientDAO {
    // CREATE
    public static boolean insert(PatientModel patient) {
        String sql = "INSERT INTO \"BenhNhan\" (\"MaBenhNhan\", \"Ho\", \"Ten\", \"NgaySinh\", \"SDT\", \"GioiTinh\") VALUES (?, ?, ?, ?, ?, ?)";
        String[] nameParts = patient.getHoTen().split(" ", 2);
        String ho = nameParts.length > 1 ? nameParts[0] : "";
        String ten = nameParts.length > 1 ? nameParts[1] : nameParts[0];

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getMaBenhNhan());
            stmt.setString(2, ho);
            stmt.setString(3, ten);
            stmt.setDate(4, Date.valueOf(patient.getNgaySinh()));
            stmt.setString(5, patient.getSoDienThoai());
            stmt.setString(6, patient.getGioiTinh()); // Giả sử bạn thêm field này vào PatientModel

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm bệnh nhân: " + e.getMessage());
        }
        return false;
    }

    //READ
    public static PatientModel getById(String maBenhNhan) {
        String sql = "SELECT * FROM \"BenhNhan\" WHERE \"MaBenhNhan\" = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maBenhNhan);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hoTen = rs.getString("Ho") + " " + rs.getString("Ten");
                return new PatientModel(
                        rs.getString("MaBenhNhan"),
                        hoTen,
                        rs.getDate("NgaySinh").toLocalDate(),
                        rs.getString("SDT"),
                        rs.getString("GioiTinh")
                );
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin bệnh nhân: " + e.getMessage());
        }

        return null;
    }

    // UPDATE
    public static boolean update(PatientModel patient) {
        String sql = "UPDATE \"BenhNhan\" SET \"Ho\" = ?, \"Ten\" = ?, \"NgaySinh\" = ?, \"SDT\" = ?, \"GioiTinh\" = ? WHERE \"MaBenhNhan\" = ?";
        String[] nameParts = patient.getHoTen().split(" ", 2);
        String ho = nameParts.length > 1 ? nameParts[0] : "";
        String ten = nameParts.length > 1 ? nameParts[1] : nameParts[0];

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ho);
            stmt.setString(2, ten);
            stmt.setDate(3, Date.valueOf(patient.getNgaySinh()));
            stmt.setString(4, patient.getSoDienThoai());
            stmt.setString(5, patient.getGioiTinh());
            stmt.setString(6, patient.getMaBenhNhan());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật bệnh nhân: " + e.getMessage());
        }
        return false;
    }

    // DELETE
    public static boolean delete(String maBenhNhan) {
        String sql = "DELETE FROM \"BenhNhan\" WHERE \"MaBenhNhan\" = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maBenhNhan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa bệnh nhân: " + e.getMessage());
        }
        return false;
    }

    // READ ALL
    public static List<PatientModel> getAll() {
        List<PatientModel> list = new ArrayList<>();
        String sql = "SELECT * FROM \"BenhNhan\"";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String hoTen = rs.getString("Ho") + " " + rs.getString("Ten");
                list.add(new PatientModel(
                        rs.getString("MaBenhNhan"),
                        hoTen,
                        rs.getDate("NgaySinh").toLocalDate(),
                        rs.getString("SDT"),
                        rs.getString("GioiTinh") // Thêm giới tính
                ));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách bệnh nhân: " + e.getMessage());
        }
        return list;
    }

    // Method để lấy số lượng bệnh nhân (tùy chọn)
    public static int getPatientCount() {
        String sql = "SELECT COUNT(*) as total FROM \"BenhNhan\"";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm số lượng bệnh nhân: " + e.getMessage());
        }
        return 0;
    }
}
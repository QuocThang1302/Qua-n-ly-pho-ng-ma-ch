package com.example.DAO;

import com.example.model.PatientModel;
import com.example.utils.DatabaseConnector;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.time.LocalDate;

public class PatientDAO {
    // CREATE
    public static boolean insert(PatientModel patient) {
        String sql = """
            INSERT INTO BenhNhan (MaBenhNhan, Ho, Ten, NgaySinh, GioiTinh, SDT)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Tách họ tên thành Ho và Ten
            String[] nameParts = splitName(patient.getHoTen());
            String ho = nameParts[0];
            String ten = nameParts[1];

            stmt.setString(1, patient.getMaBenhNhan());
            stmt.setString(2, ho);
            stmt.setString(3, ten);
            stmt.setDate(4, Date.valueOf(patient.getNgaySinh()));
            stmt.setString(5, patient.getGioiTinh());
            stmt.setString(6, patient.getSoDienThoai());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // READ
    public static PatientModel getById(String maBenhNhan) {
        String sql = "SELECT * FROM BenhNhan WHERE MaBenhNhan = ?";

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
        String sql = """
            UPDATE BenhNhan
            SET Ho = ?, Ten = ?, NgaySinh = ?, GioiTinh = ?, SDT = ?
            WHERE MaBenhNhan = ?
        """;
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Tách họ tên thành Ho và Ten
            String[] nameParts = splitName(patient.getHoTen());
            String ho = nameParts[0];
            String ten = nameParts[1];

            stmt.setString(1, ho);
            stmt.setString(2, ten);
            stmt.setDate(3, Date.valueOf(patient.getNgaySinh()));
            stmt.setString(4, patient.getGioiTinh());
            stmt.setString(5, patient.getSoDienThoai());
            stmt.setString(6, patient.getMaBenhNhan());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE
    public static boolean delete(String maBenhNhan) {
        String sql = "DELETE FROM BenhNhan WHERE MaBenhNhan = ?";

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
        String sql = "SELECT * FROM BenhNhan";

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
                        rs.getString("GioiTinh")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách bệnh nhân: " + e.getMessage());
        }
        return list;
    }

    // COUNT
    public static int getPatientCount() {
        String sql = "SELECT COUNT(*) as total FROM BenhNhan";

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

    // Kiểm tra bệnh nhân đã tồn tại chưa
    public static boolean exists(String maBenhNhan) {
        String sql = "SELECT COUNT(*) FROM BenhNhan WHERE MaBenhNhan = ?";
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maBenhNhan);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy thông tin bệnh nhân theo mã
    public static PatientModel getByMaBenhNhan(String maBenhNhan) {
        String sql = """
            SELECT MaBenhNhan, CONCAT(Ho, ' ', Ten) as HoTen, NgaySinh, GioiTinh, SDT
            FROM BenhNhan
            WHERE MaBenhNhan = ?
        """;
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maBenhNhan);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PatientModel patient = new PatientModel();
                patient.setMaBenhNhan(rs.getString("MaBenhNhan"));
                patient.setHoTen(rs.getString("HoTen"));
                patient.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                patient.setGioiTinh(rs.getString("GioiTinh"));
                patient.setSoDienThoai(rs.getString("SDT"));
                return patient;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tạo mã bệnh nhân mới
    public static String generateNewMaBenhNhan() {
        String sql = "SELECT MAX(CAST(SUBSTRING(MaBenhNhan, 3) AS UNSIGNED)) FROM BenhNhan WHERE MaBenhNhan LIKE 'BN%'";
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int maxNumber = rs.getInt(1);
                return String.format("BN%03d", maxNumber + 1);
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in generateNewMaBenhNhan: " + e.getMessage());
            e.printStackTrace();
        }
        return "BN001"; // Mặc định nếu không có dữ liệu
    }

    // Tách họ tên thành Ho và Ten
    private static String[] splitName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new String[]{"", ""};
        }

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return new String[]{parts[0], ""};
        } else if (parts.length == 2) {
            return new String[]{parts[0], parts[1]};
        } else {
            // Nếu có nhiều từ, lấy từ cuối làm tên, còn lại làm họ
            String ten = parts[parts.length - 1];
            String ho = fullName.substring(0, fullName.lastIndexOf(ten)).trim();
            return new String[]{ho, ten};
        }
    }

    // Lấy tất cả bệnh nhân
    public static List<PatientModel> getAllPatients() {
        String sql = """
            SELECT MaBenhNhan, CONCAT(Ho, ' ', Ten) as HoTen, NgaySinh, GioiTinh, SDT
            FROM BenhNhan
            ORDER BY MaBenhNhan
        """;
        
        List<PatientModel> patients = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PatientModel patient = new PatientModel();
                patient.setMaBenhNhan(rs.getString("MaBenhNhan"));
                patient.setHoTen(rs.getString("HoTen"));
                patient.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                patient.setGioiTinh(rs.getString("GioiTinh"));
                patient.setSoDienThoai(rs.getString("SDT"));
                patients.add(patient);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return patients;
    }
}

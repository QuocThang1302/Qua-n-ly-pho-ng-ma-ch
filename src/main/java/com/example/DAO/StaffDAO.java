package com.example.DAO;

import com.example.utils.DatabaseConnector;
import com.example.model.StaffModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    public static boolean insertStaff(StaffModel staff) {
        String sql = """
            INSERT INTO NhanVien (MaNhanVien, Ho, Ten, RoleID, Luong, NgaySinh, GioiTinh, CCCD, DiaChi, SDT, Email, MatKhau)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getId());
            stmt.setString(2, staff.getLastname());
            stmt.setString(3, staff.getFirstname());
            stmt.setString(4, staff.getRole());
            stmt.setDouble(5, staff.getLuong());
            stmt.setDate(6, Date.valueOf(staff.getBirthday()));
            stmt.setString(7, staff.getGender());
            stmt.setString(8, staff.getCccd());
            stmt.setString(9, staff.getAddress());
            stmt.setString(10, staff.getPhone());
            stmt.setString(11, staff.getEmail());
            stmt.setString(12, staff.getPassword());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e); // Ném lại ngoại lệ để xử lý ở controller
        }
    }

    public static boolean updateStaff(StaffModel staff) {
        String sql = """
            UPDATE NhanVien
            SET Ho = ?, Ten = ?, RoleID = ?, Luong = ?, NgaySinh = ?, GioiTinh = ?, CCCD = ?, DiaChi = ?, SDT = ?, Email = ?, MatKhau = ?
            WHERE MaNhanVien = ?
        """;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getLastname());
            stmt.setString(2, staff.getFirstname());
            stmt.setString(3, staff.getRole());
            stmt.setDouble(4, staff.getLuong());
            stmt.setDate(5, Date.valueOf(staff.getBirthday()));
            stmt.setString(6, staff.getGender());
            stmt.setString(7, staff.getCccd());
            stmt.setString(8, staff.getAddress());
            stmt.setString(9, staff.getPhone());
            stmt.setString(10, staff.getEmail());
            stmt.setString(11, staff.getPassword());
            stmt.setString(12, staff.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e); // Ném lại ngoại lệ
        }
    }

    public static boolean deleteStaff(String id) {
        String sql = "DELETE FROM NhanVien WHERE MaNhanVien = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e); // Ném lại ngoại lệ
        }
    }

    public static StaffModel getById(String id) {
        String sql = "SELECT * FROM NhanVien WHERE MaNhanVien = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return parseStaff(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e); // Ném lại ngoại lệ
        }
        return null;
    }

    public static List<StaffModel> getAll() {
        List<StaffModel> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY MaNhanVien"; // Thêm ORDER BY
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(parseStaff(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e); // Ném lại ngoại lệ
        }
        return list;
    }

    private static StaffModel parseStaff(ResultSet rs) throws SQLException {
        StaffModel staff = new StaffModel();
        staff.setId(rs.getString("MaNhanVien"));
        staff.setLastname(rs.getString("Ho"));
        staff.setFirstname(rs.getString("Ten"));
        staff.setRole(rs.getString("RoleID"));
        staff.setLuong(rs.getDouble("Luong"));
        staff.setBirthday(rs.getDate("NgaySinh").toLocalDate());
        staff.setGender(rs.getString("GioiTinh"));
        staff.setCccd(rs.getString("CCCD"));
        staff.setAddress(rs.getString("DiaChi"));
        staff.setPhone(rs.getString("SDT"));
        staff.setEmail(rs.getString("Email"));
        staff.setPassword(rs.getString("MatKhau"));
        return staff;
    }
}
package com.example.DAO;

import com.example.model.PatientModel;
import com.example.model.DatabaseConnector;

import java.sql.*;
import java.time.LocalDate;

public class PatientDAO {

    public static PatientModel getById(String maBenhNhan) {
        String sql = "SELECT * FROM BenhNhan WHERE MaBenhNhan = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maBenhNhan);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new PatientModel(
                        rs.getString("MaBenhNhan"),
                        rs.getString("HoTen"),
                        rs.getDate("NgaySinh").toLocalDate(),
                        rs.getString("SoDienThoai")
                );
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin bệnh nhân: " + e.getMessage());
        }

        return null;
    }

}

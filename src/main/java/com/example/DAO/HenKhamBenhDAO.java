package com.example.DAO;

import com.example.model.AppointmentModel;
import com.example.model.DatabaseConnector;
import com.example.model.FilterDate;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HenKhamBenhDAO {

    // ✅ 1. Truy xuất lịch hẹn + bệnh nhân theo mã khám
    public static AppointmentModel getAppointmentWithPatient(String maKhamBenh) {
        String sql = """
            SELECT h.MaKhamBenh, h.MaBenhNhan, h.LyDoKham, h.NgayKham, h.NgayKetThuc,
                   h.MaBacSi, h.TinhTrang,
                   b.HoTen, b.NgaySinh, b.SoDienThoai, b.GioiTinh
            FROM HenKhamBenh h
            JOIN BenhNhan b ON h.MaBenhNhan = b.MaBenhNhan
            WHERE h.MaKhamBenh = ?
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maKhamBenh);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                AppointmentModel model = new AppointmentModel();
                model.setMaKhamBenh(rs.getString("MaKhamBenh"));
                model.setMaBenhNhan(rs.getString("MaBenhNhan"));
                model.setLyDoKham(rs.getString("LyDoKham"));
                model.setNgayKham(rs.getDate("NgayKham").toLocalDate());
                model.setNgayKetThuc(rs.getDate("NgayKetThuc").toLocalDate());
                model.setMaBacSi(rs.getString("MaBacSi"));
                model.setTinhTrang(rs.getString("TinhTrang"));

                model.setHoTen(rs.getString("HoTen"));
                model.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                model.setSoDienThoai(rs.getString("SoDienThoai"));
                model.setGioiTinh(rs.getString("GioiTinh"));
                return model;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ✅ 2. Thêm lịch hẹn mới
    public static boolean insert(AppointmentModel model) {
        String sql = """
            INSERT INTO HenKhamBenh (MaKhamBenh, MaBenhNhan, LyDoKham, NgayKham, NgayKetThuc, MaBacSi, TinhTrang)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, model.getMaKhamBenh());
            stmt.setString(2, model.getMaBenhNhan());
            stmt.setString(3, model.getLyDoKham());
            stmt.setDate(4, Date.valueOf(model.getNgayKham()));
            stmt.setDate(5, Date.valueOf(model.getNgayKetThuc()));
            stmt.setString(6, model.getMaBacSi());
            stmt.setString(7, model.getTinhTrang());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ 3. Cập nhật lịch hẹn
    public static boolean update(AppointmentModel model) {
        String sql = """
            UPDATE HenKhamBenh
            SET LyDoKham = ?, NgayKham = ?, NgayKetThuc = ?, MaBacSi = ?, TinhTrang = ?
            WHERE MaKhamBenh = ?
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, model.getLyDoKham());
            stmt.setDate(2, Date.valueOf(model.getNgayKham()));
            stmt.setDate(3, Date.valueOf(model.getNgayKetThuc()));
            stmt.setString(4, model.getMaBacSi());
            stmt.setString(5, model.getTinhTrang());
            stmt.setString(6, model.getMaKhamBenh());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ 4. Xóa lịch hẹn
    public static boolean delete(String maKhamBenh) {
        String sql = "DELETE FROM HenKhamBenh WHERE MaKhamBenh = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maKhamBenh);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ 5. Đếm số lượng bệnh nhân khác nhau theo ngày/tháng/năm
    public static int countDistinctPatientsByDate(FilterDate filter) {
        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(DISTINCT MaBenhNhan) AS SoBenhNhan
        FROM HenKhamBenh
        WHERE
    """);

        switch (filter.getMode()) {
            case "Năm" -> sql.append(" EXTRACT(YEAR FROM NgayKham) = ?");
            case "Tháng" -> sql.append(" EXTRACT(YEAR FROM NgayKham) = ? AND EXTRACT(MONTH FROM NgayKham) = ?");
            case "Ngày" -> sql.append(" NgayKham = ?");
            default -> throw new IllegalArgumentException("Chế độ lọc không hợp lệ: " + filter.getMode());
        }

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            switch (filter.getMode()) {
                case "Năm" -> stmt.setInt(1, filter.getYear().getValue());
                case "Tháng" -> {
                    stmt.setInt(1, filter.getYearMonth().getYear());
                    stmt.setInt(2, filter.getYearMonth().getMonthValue());
                }
                case "Ngày" -> stmt.setDate(1, Date.valueOf(filter.getLocalDate()));
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("SoBenhNhan");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}

package com.example.DAO;

import com.example.utils.DatabaseConnector;
import com.example.model.DutyShiftModel;
import com.example.model.Role;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DutyShiftDAO {

    // 🔍 Đọc toàn bộ lịch trực
    public static List<DutyShiftModel> getAllDutyShifts() {
        List<DutyShiftModel> list = new ArrayList<>();
        String sql = """
        SELECT 
            lt.MaLichTruc,
            lt.MaBacSi,
            nv.Ho || ' ' || nv.Ten AS TenNguoiTruc,
            r.TenRole,
            lt.NgayTruc,
            lt.CaTruc
        FROM LichTruc lt
        JOIN NhanVien nv ON lt.MaBacSi = nv.MaNhanVien
        JOIN Role r ON nv.RoleID = r.RoleID
    """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String maLichTruc = rs.getString("MaLichTruc");
                String maBacSi = rs.getString("MaBacSi");
                String tenNguoiTruc = rs.getString("TenNguoiTruc");
                String tenRole = rs.getString("TenRole");
                LocalDate ngayTruc = rs.getDate("NgayTruc").toLocalDate();
                String caTruc = rs.getString("CaTruc");

                Role role = Role.fromVietnamese(tenRole);
                DutyShiftModel dutyShift = new DutyShiftModel(maLichTruc, maBacSi, tenNguoiTruc, role, ngayTruc, caTruc);
                list.add(dutyShift);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }



    // ✅ Thêm mới 1 lịch trực
    public static boolean insertDutyShift(DutyShiftModel model, String maLichTruc ,String congViec, String trangThai) {
        String sql = """
        INSERT INTO LichTruc (MaLichTruc, MaBacSi, NgayTruc, CaTruc, CongViec, TrangThai)
        VALUES (?, ?, ?, ?, ?, ?)
    """;


        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maLichTruc);
            stmt.setString(2, model.getMaBacSi());
            stmt.setDate(3, Date.valueOf(model.getNgay()));
            stmt.setString(4, model.getCaTruc());
            stmt.setString(5, congViec);
            stmt.setString(6, trangThai);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    // ✏️ Cập nhật lịch trực
    public static boolean updateDutyShift(String maLichTruc, String congViec, String trangThai, DutyShiftModel model) {
        String sql = """
        UPDATE LichTruc
        SET MaBacSi = ?, NgayTruc = ?, CaTruc = ?, CongViec = ?, TrangThai = ?
        WHERE MaLichTruc = ?
    """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, model.getMaBacSi());
            stmt.setDate(2, Date.valueOf(model.getNgay()));
            stmt.setString(3, model.getCaTruc());
            stmt.setString(4, congViec);
            stmt.setString(5, trangThai);
            stmt.setString(6, maLichTruc);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }



    // ❌ Xoá lịch trực
    public static boolean deleteDutyShift(String maLichTruc) {
        String sql = "DELETE FROM LichTruc WHERE MaLichTruc = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maLichTruc);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}

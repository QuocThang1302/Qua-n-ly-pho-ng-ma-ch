package com.example.DAO;

import com.example.model.AppointmentModel;
import com.example.utils.DatabaseConnector;
import com.example.model.FilterDate;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class HenKhamBenhDAO {

    // ✅ 1. Truy xuất lịch hẹn + bệnh nhân theo mã khám
    public static AppointmentModel getAppointmentWithPatient(String maKhamBenh) {
        String sql = """
            SELECT h.MaKhamBenh, h.MaBenhNhan, h.LyDoKham, h.NgayKham, h.NgayKetThuc,
                   h.MaBacSi, h.TinhTrang,
                   CONCAT(b.Ho, ' ', b.Ten) as HoTen, b.NgaySinh, b.SDT as SoDienThoai, b.GioiTinh
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
            stmt.setString(2, model.getMaBenhNhan() != null ? model.getMaBenhNhan() : "");
            stmt.setString(3, model.getLyDoKham() != null ? model.getLyDoKham() : "");
            stmt.setDate(4, Date.valueOf(model.getNgayKham()));
            stmt.setDate(5, Date.valueOf(model.getNgayKetThuc()));
            stmt.setString(6, model.getMaBacSi() != null ? model.getMaBacSi() : "");
            stmt.setString(7, model.getTinhTrang() != null ? model.getTinhTrang() : "Chưa khám");

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
            SET MaBenhNhan = ?, LyDoKham = ?, NgayKham = ?, NgayKetThuc = ?, MaBacSi = ?, TinhTrang = ?
            WHERE MaKhamBenh = ?
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, model.getMaBenhNhan() != null ? model.getMaBenhNhan() : "");
            stmt.setString(2, model.getLyDoKham() != null ? model.getLyDoKham() : "");
            stmt.setDate(3, Date.valueOf(model.getNgayKham()));
            stmt.setDate(4, Date.valueOf(model.getNgayKetThuc()));
            stmt.setString(5, model.getMaBacSi() != null ? model.getMaBacSi() : "");
            stmt.setString(6, model.getTinhTrang() != null ? model.getTinhTrang() : "Chưa khám");
            stmt.setString(7, model.getMaKhamBenh());

            int result = stmt.executeUpdate();
            System.out.println("🔍 Update result: " + result + " rows affected for MaKhamBenh: " + model.getMaKhamBenh());
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in update: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ General Error in update: " + e.getMessage());
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

    // ✅ 6. Đếm số lượng bệnh nhân khác nhau
    public static List<Integer> getPatientCountsBetween(FilterDate from, FilterDate to) {
        List<Integer> counts = new ArrayList<>();

        LocalDate start = from.getLocalDate();
        LocalDate end = to.getLocalDate();

        if (start == null || end == null) {
            throw new IllegalArgumentException("FilterDate phải ở chế độ 'Ngày'");
        }

        long daysBetween = ChronoUnit.DAYS.between(start, end);

        if (daysBetween <= 31) {
            // Đếm theo ngày
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                FilterDate filter = new FilterDate("Ngày", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
                int count = countDistinctPatientsByDate(filter); // Không cần truyền connection
                counts.add(count);
            }
        } else {
            // Đếm theo tháng
            YearMonth startMonth = YearMonth.from(start);
            YearMonth endMonth = YearMonth.from(end);
            for (YearMonth ym = startMonth; !ym.isAfter(endMonth); ym = ym.plusMonths(1)) {
                FilterDate filter = new FilterDate("Tháng", 1, ym.getMonthValue(), ym.getYear());
                int count = countDistinctPatientsByDate(filter);
                counts.add(count);
            }
        }

        return counts;
    }

    // ✅ 7. Lấy tất cả lịch hẹn với thông tin bệnh nhân
    public static List<AppointmentModel> getAllAppointments() {
        String sql = """
            SELECT h.MaKhamBenh, h.MaBenhNhan, h.LyDoKham, h.NgayKham, h.NgayKetThuc,
                   h.MaBacSi, h.TinhTrang,
                   COALESCE(CONCAT(b.Ho, ' ', b.Ten), 'Chưa có tên') as HoTen, 
                   b.NgaySinh, 
                   COALESCE(b.SDT, '') as SoDienThoai, 
                   COALESCE(b.GioiTinh, 'Nam') as GioiTinh
            FROM HenKhamBenh h
            LEFT JOIN BenhNhan b ON h.MaBenhNhan = b.MaBenhNhan
            ORDER BY h.NgayKham, h.MaKhamBenh
        """;

        List<AppointmentModel> appointments = new ArrayList<>();

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AppointmentModel model = new AppointmentModel();
                model.setMaKhamBenh(rs.getString("MaKhamBenh"));
                model.setMaBenhNhan(rs.getString("MaBenhNhan"));
                model.setLyDoKham(rs.getString("LyDoKham"));
                model.setNgayKham(rs.getDate("NgayKham").toLocalDate());
                model.setNgayKetThuc(rs.getDate("NgayKetThuc").toLocalDate());
                model.setMaBacSi(rs.getString("MaBacSi"));
                model.setTinhTrang(rs.getString("TinhTrang"));

                model.setHoTen(rs.getString("HoTen"));
                
                // Xử lý trường hợp NgaySinh có thể null
                java.sql.Date ngaySinh = rs.getDate("NgaySinh");
                if (ngaySinh != null) {
                    model.setNgaySinh(ngaySinh.toLocalDate());
                } else {
                    model.setNgaySinh(LocalDate.now()); // Giá trị mặc định
                }
                
                model.setSoDienThoai(rs.getString("SoDienThoai"));
                model.setGioiTinh(rs.getString("GioiTinh"));
                
                appointments.add(model);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    // ✅ 8. Lấy lịch hẹn theo khoảng thời gian
    public static List<AppointmentModel> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT h.MaKhamBenh, h.MaBenhNhan, h.LyDoKham, h.NgayKham, h.NgayKetThuc,
                   h.MaBacSi, h.TinhTrang,
                   COALESCE(CONCAT(b.Ho, ' ', b.Ten), 'Chưa có tên') as HoTen, 
                   b.NgaySinh, 
                   COALESCE(b.SDT, '') as SoDienThoai, 
                   COALESCE(b.GioiTinh, 'Nam') as GioiTinh
            FROM HenKhamBenh h
            LEFT JOIN BenhNhan b ON h.MaBenhNhan = b.MaBenhNhan
            WHERE h.NgayKham BETWEEN ? AND ?
            ORDER BY h.NgayKham, h.MaKhamBenh
        """;

        List<AppointmentModel> appointments = new ArrayList<>();

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AppointmentModel model = new AppointmentModel();
                model.setMaKhamBenh(rs.getString("MaKhamBenh"));
                model.setMaBenhNhan(rs.getString("MaBenhNhan"));
                model.setLyDoKham(rs.getString("LyDoKham"));
                model.setNgayKham(rs.getDate("NgayKham").toLocalDate());
                model.setNgayKetThuc(rs.getDate("NgayKetThuc").toLocalDate());
                model.setMaBacSi(rs.getString("MaBacSi"));
                model.setTinhTrang(rs.getString("TinhTrang"));

                model.setHoTen(rs.getString("HoTen"));
                
                // Xử lý trường hợp NgaySinh có thể null
                java.sql.Date ngaySinh = rs.getDate("NgaySinh");
                if (ngaySinh != null) {
                    model.setNgaySinh(ngaySinh.toLocalDate());
                } else {
                    model.setNgaySinh(LocalDate.now()); // Giá trị mặc định
                }
                
                model.setSoDienThoai(rs.getString("SoDienThoai"));
                model.setGioiTinh(rs.getString("GioiTinh"));
                
                appointments.add(model);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    // ✅ 9. Test method để kiểm tra database
    public static void testDatabaseConnection() {
        System.out.println("🔍 Kiểm tra kết nối database...");
        
        try (Connection conn = DatabaseConnector.connect()) {
            System.out.println("✅ Kết nối database thành công");
            
            // Kiểm tra số lượng lịch hẹn
            String countSql = "SELECT COUNT(*) as total FROM HenKhamBenh";
            try (PreparedStatement stmt = conn.prepareStatement(countSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    System.out.println("📊 Tổng số lịch hẹn trong database: " + total);
                }
            }
            
            // Kiểm tra số lượng bệnh nhân
            String patientSql = "SELECT COUNT(*) as total FROM BenhNhan";
            try (PreparedStatement stmt = conn.prepareStatement(patientSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    System.out.println("👥 Tổng số bệnh nhân trong database: " + total);
                }
            }
            
            // Kiểm tra dữ liệu mẫu
            String sampleSql = """
                SELECT h.MaKhamBenh, h.MaBenhNhan, h.LyDoKham, h.NgayKham,
                       CONCAT(b.Ho, ' ', b.Ten) as HoTen
                FROM HenKhamBenh h
                LEFT JOIN BenhNhan b ON h.MaBenhNhan = b.MaBenhNhan
                LIMIT 5
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sampleSql);
                 ResultSet rs = stmt.executeQuery()) {
                System.out.println("📋 Dữ liệu mẫu từ database:");
                while (rs.next()) {
                    String maKham = rs.getString("MaKhamBenh");
                    String maBenhNhan = rs.getString("MaBenhNhan");
                    String lyDo = rs.getString("LyDoKham");
                    String ngayKham = rs.getDate("NgayKham").toString();
                    String hoTen = rs.getString("HoTen");
                    System.out.println("  - " + maKham + " | " + maBenhNhan + " | " + hoTen + " | " + lyDo + " | " + ngayKham);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // ✅ 10. Kiểm tra lịch hẹn có tồn tại không
    public static boolean exists(String maKhamBenh) {
        String sql = "SELECT COUNT(*) FROM HenKhamBenh WHERE MaKhamBenh = ?";
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maKhamBenh);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in exists check: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ✅ 11. Cập nhật hoặc thêm mới lịch hẹn
    public static boolean updateOrInsert(AppointmentModel model) {
        if (exists(model.getMaKhamBenh())) {
            System.out.println("🔄 Lịch hẹn đã tồn tại, thực hiện cập nhật: " + model.getMaKhamBenh());
            return update(model);
        } else {
            System.out.println("🆕 Lịch hẹn chưa tồn tại, thực hiện thêm mới: " + model.getMaKhamBenh());
            return insert(model);
        }
    }

    // ✅ 12. Tạo mã khám bệnh mới
    public static String generateNewMaKhamBenh() {
        String sql = "SELECT MAX(CAST(SUBSTRING(MaKhamBenh, 4) AS UNSIGNED)) FROM HenKhamBenh WHERE MaKhamBenh LIKE 'HKB%'";
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int maxNumber = rs.getInt(1);
                return String.format("HKB%03d", maxNumber + 1);
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in generateNewMaKhamBenh: " + e.getMessage());
            e.printStackTrace();
        }
        return "HKB001"; // Mặc định nếu không có dữ liệu
    }

    // ✅ 13. Test method để debug vấn đề lưu lịch hẹn
    public static void debugAppointmentSave(AppointmentModel model) {
        System.out.println("🔍 Debug Appointment Save:");
        System.out.println("  - MaKhamBenh: '" + model.getMaKhamBenh() + "'");
        System.out.println("  - MaBenhNhan: '" + model.getMaBenhNhan() + "'");
        System.out.println("  - LyDoKham: '" + model.getLyDoKham() + "'");
        System.out.println("  - NgayKham: " + model.getNgayKham());
        System.out.println("  - NgayKetThuc: " + model.getNgayKetThuc());
        System.out.println("  - MaBacSi: '" + model.getMaBacSi() + "'");
        System.out.println("  - TinhTrang: '" + model.getTinhTrang() + "'");
        
        // Kiểm tra lịch hẹn có tồn tại không
        boolean exists = exists(model.getMaKhamBenh());
        System.out.println("  - Exists in DB: " + exists);
        
        // Kiểm tra kết nối database
        try (Connection conn = DatabaseConnector.connect()) {
            System.out.println("  - Database connection: OK");
            
            // Kiểm tra cấu trúc bảng
            String checkTableSql = "DESCRIBE HenKhamBenh";
            try (PreparedStatement stmt = conn.prepareStatement(checkTableSql);
                 ResultSet rs = stmt.executeQuery()) {
                System.out.println("  - Table structure:");
                while (rs.next()) {
                    String field = rs.getString("Field");
                    String type = rs.getString("Type");
                    String nullFlag = rs.getString("Null");
                    String key = rs.getString("Key");
                    System.out.println("    * " + field + " (" + type + ") " + nullFlag + " " + key);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("  - Database connection: FAILED - " + e.getMessage());
            e.printStackTrace();
        }
    }
}

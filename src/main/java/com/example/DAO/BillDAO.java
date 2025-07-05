package com.example.DAO;

import com.example.model.BillModel;
import com.example.utils.DatabaseConnector;
import com.example.model.FilterDate;
import com.example.model.MedicineModel;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    // CREATE - Thêm hóa đơn mới
    public static boolean insertBill(BillModel bill, String tenHoaDon, LocalDateTime ngayLapHoaDon) {
        String sql = "INSERT INTO HoaDon (MaHoaDon, TenHoaDon, MaDonThuoc, MaPhieuKham, NgayLapHoaDon, GiaTien, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bill.getMaHoaDon());
            stmt.setString(2, tenHoaDon);
            stmt.setString(3, bill.getMaDonThuoc());
            stmt.setString(4, bill.getMaPhieuKham());
            stmt.setTimestamp(5, Timestamp.valueOf(ngayLapHoaDon));
            stmt.setDouble(6, bill.getTongTien());
            stmt.setString(7, bill.getTrangThai());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm hóa đơn: " + e.getMessage());
        }
        return false;
    }

    // READ - Lấy hóa đơn theo mã
    public static BillModel getBillById(String maHoaDon) {
        String sql = "SELECT h.MaHoaDon, h.MaPhieuKham, h.MaDonThuoc, pk.TienKham, h.TrangThai, h.NgayLapHoaDon as NgayLapDon " +
                "FROM HoaDon h JOIN PhieuKhamBenh pk ON h.MaPhieuKham = pk.MaPhieuKham " +
                "WHERE h.MaHoaDon = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BillModel bill = new BillModel();
                    bill.setMaHoaDon(rs.getString("MaHoaDon"));
                    bill.setMaDonThuoc(rs.getString("MaDonThuoc"));
                    bill.setMaPhieuKham(rs.getString("MaPhieuKham"));
                    bill.setTienKham(rs.getDouble("TienKham"));
                    bill.setTrangThai(rs.getString("TrangThai"));
                    bill.setNgayLapDon(rs.getTimestamp("NgayLapDon").toLocalDateTime());

                    // Lấy danh sách thuốc
                    List<MedicineModel> danhSachThuoc = getMedicinesByDonThuoc(bill.getMaDonThuoc());
                    bill.setDanhSachThuoc(danhSachThuoc);

                    // Tính tổng tiền
                    double tongTienThuoc = calculateTotalMedicinePrice(danhSachThuoc);
                    bill.setTongTien(bill.getTienKham() + tongTienThuoc);

                    return bill;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hóa đơn theo ID: " + e.getMessage());
        }
        return null;
    }

    // READ - Lấy tất cả hóa đơn
    public static List<BillModel> getAllBills() {
        List<BillModel> bills = new ArrayList<>();
        String sql = "SELECT h.MaHoaDon, h.MaDonThuoc, pk.TienKham, h.TrangThai, h.NgayLapHoaDon as NgayLapDon " +
                "FROM HoaDon h JOIN PhieuKhamBenh pk ON h.MaPhieuKham = pk.MaPhieuKham " +
                "ORDER BY h.NgayLapHoaDon DESC";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BillModel bill = new BillModel();
                bill.setMaHoaDon(rs.getString("MaHoaDon"));
                bill.setMaDonThuoc(rs.getString("MaDonThuoc"));
                bill.setTienKham(rs.getDouble("TienKham"));
                bill.setTrangThai(rs.getString("TrangThai"));
                bill.setNgayLapDon(rs.getTimestamp("NgayLapDon").toLocalDateTime());

                // Lấy danh sách thuốc
                List<MedicineModel> danhSachThuoc = getMedicinesByDonThuoc(bill.getMaDonThuoc());
                bill.setDanhSachThuoc(danhSachThuoc);

                // Tính tổng tiền
                double tongTienThuoc = calculateTotalMedicinePrice(danhSachThuoc);
                bill.setTongTien(bill.getTienKham() + tongTienThuoc);

                bills.add(bill);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
        }
        return bills;
    }

    // UPDATE - Cập nhật hóa đơn
    public static boolean updateBill(BillModel bill) {
        String sql = "UPDATE HoaDon SET MaDonThuoc = ?, MaPhieuKham = ?, GiaTien = ?, TrangThai = ? WHERE MaHoaDon = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bill.getMaDonThuoc());
            stmt.setString(2, bill.getMaPhieuKham());
            stmt.setDouble(3, bill.getTongTien());
            stmt.setString(4, bill.getTrangThai());
            stmt.setString(5, bill.getMaHoaDon());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật hóa đơn: " + e.getMessage());
        }
        return false;
    }

    // DELETE - Xóa hóa đơn
    public static boolean deleteBill(String maHoaDon) {
        String sql = "DELETE FROM HoaDon WHERE MaHoaDon = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maHoaDon);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa hóa đơn: " + e.getMessage());
        }
        return false;
    }

    // Phương thức hỗ trợ: Lấy danh sách thuốc theo mã đơn thuốc
    private static List<MedicineModel> getMedicinesByDonThuoc(String maDonThuoc) {
        List<MedicineModel> medicines = new ArrayList<>();
        String sql = "SELECT t.MaThuoc, t.TenThuoc, t.CongDung, ct.SoLuong, " +
                "ct.GiaTien, ct.HuongDanSuDung " +
                "FROM CTDonThuoc ct " +
                "JOIN Thuoc t ON ct.MaThuoc = t.MaThuoc " +
                "WHERE ct.MaDonThuoc = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maDonThuoc);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicineModel medicine = new MedicineModel();
                    medicine.setMaThuoc(rs.getString("MaThuoc"));
                    medicine.setTenThuoc(rs.getString("TenThuoc"));
                    medicine.setCongDung(rs.getString("CongDung"));
                    medicine.setSoLuong(rs.getInt("SoLuong"));
                    medicine.setGiaTien(rs.getDouble("GiaTien"));
                    medicine.setHuongDanSuDung(rs.getString("HuongDanSuDung"));
                    // Chưa có thông tin đơn vị trong database, có thể set mặc định


                    medicines.add(medicine);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách thuốc: " + e.getMessage());
        }
        return medicines;
    }

    // Phương thức hỗ trợ: Tính tổng tiền thuốc
    private static double calculateTotalMedicinePrice(List<MedicineModel> medicines) {
        double total = 0;
        for (MedicineModel medicine : medicines) {
            total += medicine.getGiaTien() * medicine.getSoLuong();
        }
        return total;
    }

    // Tính tổng hóa đơn
    public static double getTotalRevenue(FilterDate filterDate) {
        String sql = "";
        try (Connection conn = DatabaseConnector.connect()) {
            PreparedStatement stmt = null;

            switch (filterDate.getMode()) {
                case "Năm" -> {
                    sql = "SELECT SUM(GiaTien) AS TongTien FROM HoaDon WHERE EXTRACT(YEAR FROM NgayLapHoaDon) = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, filterDate.getYear().getValue());
                }
                case "Tháng" -> {
                    sql = "SELECT SUM(GiaTien) AS TongTien FROM HoaDon WHERE EXTRACT(MONTH FROM NgayLapHoaDon) = ? AND EXTRACT(YEAR FROM NgayLapHoaDon) = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, filterDate.getYearMonth().getMonthValue());
                    stmt.setInt(2, filterDate.getYearMonth().getYear());
                }
                case "Ngày" -> {
                    sql = "SELECT SUM(GiaTien) AS TongTien FROM HoaDon WHERE DATE(NgayLapHoaDon) = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setDate(1, Date.valueOf(filterDate.getLocalDate()));
                }
            }

            if (stmt != null) {
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return rs.getDouble("TongTien");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tính tổng doanh thu: " + e.getMessage());
        }
        return 0.0;

    }

    //Lọc từ ngày đến ngày
    public static double getTotalRevenue(Connection conn, FilterDate filterDate) {
        String sql = "";
        try {
            PreparedStatement stmt = null;

            switch (filterDate.getMode()) {
                case "Năm" -> {
                    sql = "SELECT SUM(GiaTien) AS TongTien FROM HoaDon WHERE EXTRACT(YEAR FROM NgayLapHoaDon) = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, filterDate.getYear().getValue());
                }
                case "Tháng" -> {
                    sql = "SELECT SUM(GiaTien) AS TongTien FROM HoaDon WHERE EXTRACT(MONTH FROM NgayLapHoaDon) = ? AND EXTRACT(YEAR FROM NgayLapHoaDon) = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, filterDate.getYearMonth().getMonthValue());
                    stmt.setInt(2, filterDate.getYearMonth().getYear());
                }
                case "Ngày" -> {
                    sql = "SELECT SUM(GiaTien) AS TongTien FROM HoaDon WHERE DATE(NgayLapHoaDon) = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setDate(1, Date.valueOf(filterDate.getLocalDate()));
                }
            }

            if (stmt != null) {
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return rs.getDouble("TongTien");
                }
                stmt.close(); // đóng sớm cho chắc
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tính tổng doanh thu (conn): " + e.getMessage());
        }
        return 0.0;
    }

    public static List<Double> getTotalRevenueBetween(FilterDate from, FilterDate to) {
        List<Double> revenueList = new ArrayList<>();

        LocalDate start = from.getLocalDate();
        LocalDate end = to.getLocalDate();

        if (start == null || end == null) {
            throw new IllegalArgumentException("FilterDate phải ở chế độ 'Ngày'");
        }

        try (Connection conn = DatabaseConnector.connect()) {
            long daysBetween = ChronoUnit.DAYS.between(start, end);

            if (daysBetween <= 31) {
                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    FilterDate filter = new FilterDate("Ngày", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
                    revenueList.add(getTotalRevenue(conn, filter));
                }
            } else {
                YearMonth startMonth = YearMonth.from(start);
                YearMonth endMonth = YearMonth.from(end);
                for (YearMonth ym = startMonth; !ym.isAfter(endMonth); ym = ym.plusMonths(1)) {
                    FilterDate filter = new FilterDate("Tháng", 1, ym.getMonthValue(), ym.getYear());
                    revenueList.add(getTotalRevenue(conn, filter));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy xuất danh sách doanh thu: " + e.getMessage());
        }

        return revenueList;
    }



}
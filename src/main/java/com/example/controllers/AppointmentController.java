package com.example.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import com.example.DAO.HenKhamBenhDAO;
import com.example.model.AppointmentEntry;
import com.example.model.AppointmentModel;
import com.example.model.UserContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class AppointmentController {

    @FXML
    private StackPane calendarContainer;

    private CalendarView calendarView;
    @FXML
    public void initialize() {
        // ✅ Test database connection trước
        HenKhamBenhDAO.testDatabaseConnection();
        
        calendarView = new CalendarView();
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.showDayPage();

        Calendar calendar = new Calendar("Lịch Khám");
        calendar.setStyle(Calendar.Style.STYLE1);
        
        // ✅ Load dữ liệu thực từ database
        loadAppointmentsFromDatabase(calendar);

        CalendarSource source = new CalendarSource("Phòng khám");
        source.getCalendars().add(calendar);
        calendarView.getCalendarSources().add(source);
        calendarContainer.getChildren().add(calendarView);
        
        // ✅ Đảm bảo calendar hiển thị ngày hiện tại
        calendarView.setToday(LocalDate.now());
        calendarView.setDate(LocalDate.now());
        
        System.out.println("🔄 Calendar đã được khởi tạo với ngày: " + LocalDate.now());

        calendarView.setEntryFactory(param -> {
            String title = "Khám Mới";
            AppointmentModel model = new AppointmentModel();
            model.setMaKhamBenh(HenKhamBenhDAO.generateNewMaKhamBenh());
            model.setMaBenhNhan(""); // Sẽ được cập nhật khi chọn bệnh nhân
            model.setHoTen(""); // Sẽ được cập nhật khi chọn bệnh nhân
            model.setLyDoKham(title);
            model.setNgayKham(param.getZonedDateTime().toLocalDate());
            model.setNgayKetThuc(param.getZonedDateTime().toLocalDate());
            model.setTinhTrang("Chưa khám");
            model.setMaBacSi("NV002"); // Mặc định bác sĩ
            
            // ✅ Tạo entry với title hiển thị họ tên bệnh nhân
            AppointmentEntry entry = new AppointmentEntry(title, model);
            entry.setInterval(param.getZonedDateTime());
            registerEntryChangeListeners(entry);
            
            // ✅ Lưu vào database
            try {
                boolean success = HenKhamBenhDAO.insert(model);
                if (success) {
                    System.out.println("✅ Đã tạo lịch hẹn mới: " + model.getMaKhamBenh());
                    
                    // ✅ Refresh calendar để hiển thị entry mới
                    Platform.runLater(this::refreshCalendarData);
                } else {
                    System.err.println("❌ Lỗi khi tạo lịch hẹn mới");
                }
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi lưu lịch hẹn mới: " + e.getMessage());
            }
            
            return entry;
        });

        calendarView.setEntryDetailsPopOverContentCallback(param -> {
            if (!(param.getEntry() instanceof AppointmentEntry entry)) return null;

            // Mở detail stage
            Platform.runLater(() -> openAppointmentDetailWindow(entry));

            return null;
        });

    }

    private void openAppointmentDetailWindow(AppointmentEntry entry) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/appointment_detail.fxml"));
            Parent root = loader.load();

            AppointmentDetailController controller = loader.getController();
            controller.setEntry(entry);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết lịch hẹn");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // ✅ Refresh calendar sau khi đóng dialog
            stage.setOnHidden(e -> refreshCalendarData());
            
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ Method để refresh calendar data
    private void refreshCalendarData() {
        System.out.println("🔄 Đang refresh calendar data...");
        
        // Lấy calendar hiện tại
        Calendar calendar = calendarView.getCalendarSources().get(0).getCalendars().get(0);
        
        // Xóa tất cả entries cũ
        calendar.clear();
        
        // Load lại dữ liệu từ database
        loadAppointmentsFromDatabase(calendar);
        
        System.out.println("✅ Đã refresh calendar data");
    }

    private void registerEntryChangeListeners(AppointmentEntry entry) {
        entry.titleProperty().addListener((obs, oldVal, newVal) -> updateEntry(entry));
        entry.intervalProperty().addListener((obs, oldVal, newVal) -> updateEntry(entry));
    }

    private void updateEntry(AppointmentEntry entry) {
        AppointmentModel model = entry.getModel();
        if (model == null) return;

        String title = entry.getTitle() != null ? entry.getTitle() : "Khám Mới";
        model.setLyDoKham(title);
        model.setNgayKham(entry.getStartDate());
        
        // ✅ Lưu thay đổi vào database
        try {
            boolean success = HenKhamBenhDAO.update(model);
            if (success) {
                System.out.println("✅ Đã cập nhật DB cho: " + model.getMaKhamBenh() + " - " + model.getLyDoKham());
                
                // ✅ Refresh calendar để hiển thị thay đổi
                Platform.runLater(this::refreshCalendarData);
            } else {
                System.err.println("❌ Lỗi khi cập nhật DB cho: " + model.getMaKhamBenh());
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi cập nhật DB: " + e.getMessage());
        }
    }
    
    private void loadAppointmentsFromDatabase(Calendar calendar) {
        try {
            // ✅ Lấy dữ liệu thực từ database
            List<AppointmentModel> danhSach = HenKhamBenhDAO.getAllAppointments();
            
            System.out.println("✅ Đã tải " + danhSach.size() + " lịch hẹn từ database");

            // Duyệt từng model để tạo Entry và thêm vào calendar
            for (AppointmentModel model : danhSach) {
                String hoTen = model.getHoTen() != null ? model.getHoTen() : "Chưa có tên";
                String lyDo = model.getLyDoKham() != null ? model.getLyDoKham() : "Chưa có lý do";
                String title = hoTen + " - " + lyDo;
                
                System.out.println("📅 Tạo entry: " + title + " - Ngày: " + model.getNgayKham());
                
                AppointmentEntry entry = new AppointmentEntry(title, model);
                
                // ✅ Sử dụng thời gian từ database nếu có, hoặc mặc định 9:00-9:30
                LocalTime startTime = LocalTime.of(9, 0); // Mặc định 9:00
                LocalTime endTime = LocalTime.of(9, 30);   // Mặc định 9:30
                
                // TODO: Có thể thêm trường GioBatDau và GioKetThuc vào database để lưu thời gian
                // Nếu có trường này, sẽ sử dụng thời gian từ database
                // startTime = model.getGioBatDau() != null ? model.getGioBatDau() : LocalTime.of(9, 0);
                // endTime = model.getGioKetThuc() != null ? model.getGioKetThuc() : LocalTime.of(9, 30);
                
                entry.setInterval(
                        model.getNgayKham().atTime(startTime),
                        model.getNgayKham().atTime(endTime)
                );
                registerEntryChangeListeners(entry);
                calendar.addEntry(entry);
                
                System.out.println("✅ Đã thêm lịch hẹn: " + title + " - " + model.getNgayKham());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tải dữ liệu từ database: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: sử dụng dữ liệu mẫu nếu có lỗi
            loadSampleData(calendar);
        }
    }
    
    private void loadSampleData(Calendar calendar) {
        System.out.println("⚠️ Sử dụng dữ liệu mẫu do lỗi kết nối database");
        
        // Dữ liệu mẫu (fallback)
        List<AppointmentModel> danhSach = List.of(
                new AppointmentModel(
                        "KB001",
                        "BN001",
                        "Nguyễn Văn A",
                        LocalDate.of(1990, 1, 1),
                        "0901234567",
                        "Nam",
                        "Đau đầu",
                        LocalDate.now(),
                        LocalDate.now(),
                        "BS001",
                        "Chưa khám"
                ),
                new AppointmentModel(
                        "KB002",
                        "BN002",
                        "Trần Thị B",
                        LocalDate.of(1985, 5, 20),
                        "0912345678",
                        "Nữ",
                        "Sốt nhẹ",
                        LocalDate.now(),
                        LocalDate.now(),
                        "BS002",
                        "Đã khám"
                )
        );

        // Duyệt từng model để tạo Entry và thêm vào calendar
        for (AppointmentModel model : danhSach) {
            String hoTen = model.getHoTen() != null ? model.getHoTen() : "Chưa có tên";
            String lyDo = model.getLyDoKham() != null ? model.getLyDoKham() : "Chưa có lý do";
            String title = hoTen + " - " + lyDo;
            AppointmentEntry entry = new AppointmentEntry(title, model);
            
            // ✅ Sử dụng thời gian từ database nếu có, hoặc mặc định 9:00-9:30
            LocalTime startTime = LocalTime.of(9, 0); // Mặc định 9:00
            LocalTime endTime = LocalTime.of(9, 30);   // Mặc định 9:30
            
            // TODO: Có thể thêm trường GioBatDau và GioKetThuc vào database để lưu thời gian
            // Nếu có trường này, sẽ sử dụng thời gian từ database
            // startTime = model.getGioBatDau() != null ? model.getGioBatDau() : LocalTime.of(9, 0);
            // endTime = model.getGioKetThuc() != null ? model.getGioKetThuc() : LocalTime.of(9, 30);
            
            entry.setInterval(
                    model.getNgayKham().atTime(startTime),
                    model.getNgayKham().atTime(endTime)
            );
            registerEntryChangeListeners(entry);
            calendar.addEntry(entry);
        }
    }
}

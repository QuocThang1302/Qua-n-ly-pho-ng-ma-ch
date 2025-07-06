package com.example.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import com.example.DAO.HenKhamBenhDAO;
import com.example.model.AppointmentEntry;
import com.example.model.AppointmentModel;
import com.example.model.Role;
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
    private Calendar calendar;

    @FXML
    public void initialize() {
        calendarView = new CalendarView();
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.showDayPage();

        calendar = new Calendar("Lịch Khám");
        calendar.setStyle(Calendar.Style.STYLE1);

        // Load dữ liệu từ DB
        loadAppointmentsFromDatabase();

        CalendarSource source = new CalendarSource("Phòng khám");
        source.getCalendars().add(calendar);
        calendarView.getCalendarSources().add(source);
        calendarContainer.getChildren().add(calendarView);

        // ✅ XỬ LÝ DOUBLE-CLICK ĐơN GIẢN
        calendarView.setEntryFactory(param -> {
            // Khi double click tạo Entry mới, ta chặn lại ở đây
            AppointmentModel model = new AppointmentModel();
            AppointmentEntry entry = new AppointmentEntry("", model);

            // Gán ngày giờ vào model nếu cần:
            model.setNgayKham(param.getZonedDateTime().toLocalDate());

            // Gọi form chi tiết để người dùng nhập
            Platform.runLater(() -> openAppointmentDetailWindow(entry));

            // Trả null để không thêm "New Entry" vào giao diện
            return null;
        });
        Role role = UserContext.getInstance().getRole();
        if(role.equals(Role.NURSE)){
            calendarView.setEntryFactory(param -> {
                String title = "Khám Mới";
                AppointmentModel model = new AppointmentModel();
                //TODO sua logic lay ma kham benh moi
                model.setMaKhamBenh(UUID.randomUUID().toString());
                model.setLyDoKham(title);
                model.setNgayKham(param.getZonedDateTime().toLocalDate());


                AppointmentEntry entry = new AppointmentEntry(title, model);
                entry.setInterval(param.getZonedDateTime());
                registerEntryChangeListeners(entry);
                return entry;

        });
        }
        else {
            calendarView.setEntryFactory(createEntryParameter -> null);
        }

        calendarView.setEntryDetailsPopOverContentCallback(param -> {
            if (!(param.getEntry() instanceof AppointmentEntry entry)) return null;

            // Mở cửa sổ chi tiết lịch hẹn
            Platform.runLater(() -> openAppointmentDetailWindow(entry));

            return null;
        });


    }

    private void registerEntryChangeListeners(AppointmentEntry entry) {
        entry.titleProperty().addListener((obs, oldVal, newVal) -> updateEntry(entry));
        entry.intervalProperty().addListener((obs, oldVal, newVal) -> updateEntry(entry));
    }

    private void updateEntry(AppointmentEntry entry) {
        AppointmentModel model = entry.getModel();
        if (model == null) return;

        model.setLyDoKham(entry.getTitle());
        model.setNgayKham(entry.getStartDate());
        //TODO cap nhat db
        System.out.println("Cập nhật DB cho: " + model.getMaKhamBenh() + " - " + model.getLyDoKham());
    }

    private void openAppointmentDetailWindow(AppointmentEntry entry) {
        try {
            System.out.println("🔧 Opening appointment detail window...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/appointment_detail.fxml"));
            Parent root = loader.load();

            AppointmentDetailController controller = loader.getController();
            controller.setEntry(entry);

            // Set callback để refresh calendar sau khi đóng window
            controller.setOnRefreshCallback(this::refreshCalendar);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết lịch hẹn");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("❌ Error opening appointment detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshCalendar() {
        System.out.println("🔄 Refreshing calendar...");

        // Xóa tất cả entries cũ
        calendar.clear();

        // Load lại từ DB
        loadAppointmentsFromDatabase();

        // Force refresh UI
        Platform.runLater(() -> {
            calendarView.refreshData();
        });
    }

    private void loadAppointmentsFromDatabase() {
        List<AppointmentModel> danhSach = HenKhamBenhDAO.getAll();
        System.out.println("📥 Loading " + danhSach.size() + " appointments from database...");

        for (AppointmentModel model : danhSach) {
            String title = model.getHoTen() != null ? model.getHoTen() : "Khám mới";
            AppointmentEntry entry = new AppointmentEntry(title, model);
            LocalDate ngay = model.getNgayKham();
            LocalTime batDau = model.getGioBatDau();
            LocalTime ketThuc = model.getGioKetThuc();

            if (ngay != null && batDau != null && ketThuc != null) {
                entry.setInterval(ngay.atTime(batDau), ngay.atTime(ketThuc));
            } else {
                // fallback nếu thiếu dữ liệu
                entry.setInterval(ngay.atTime(9, 0), ngay.atTime(9, 30));
            }
            calendar.addEntry(entry);
        }
    }

}
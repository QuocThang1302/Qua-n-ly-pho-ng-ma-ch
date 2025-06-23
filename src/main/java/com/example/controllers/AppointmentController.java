
package com.example.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import com.example.model.AppointmentEntry;
import com.example.model.AppointmentModel;
import com.example.model.UserContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class AppointmentController {

    @FXML
    private StackPane calendarContainer;

    private CalendarView calendarView;
    @FXML
    public void initialize() {
        calendarView = new CalendarView();
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.showDayPage();

        Calendar calendar = new Calendar("Lịch Khám");
        calendar.setStyle(Calendar.Style.STYLE3);
        // TODO : load  ✅ Load dữ liệu từ DB hàm ở cuối class
        loadAppointmentsFromDatabase(calendar);

        CalendarSource source = new CalendarSource("Phòng khám");
        source.getCalendars().add(calendar);
        calendarView.getCalendarSources().add(source);
        calendarContainer.getChildren().add(calendarView);

        calendarView.setEntryFactory(param -> {
            String title = "Khám Mới";
            AppointmentModel model = new AppointmentModel();
            model.setMaKhamBenh(UUID.randomUUID().toString());
            model.setLyDoKham(title);
            model.setNgayKham(param.getZonedDateTime().toLocalDate());
            model.setTinhTrang("Chưa khám");

            AppointmentEntry entry = new AppointmentEntry(title, model);
            entry.setInterval(param.getZonedDateTime());
            registerEntryChangeListeners(entry);
            return entry;
        });

        calendarView.setEntryDetailsPopOverContentCallback(param -> {
            if (!(param.getEntry() instanceof AppointmentEntry entry)) {
                return new VBox(new Label("Không thể hiển thị lịch hẹn."));
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/appointment_detail.fxml"));
                VBox detailBox = loader.load();
                AppointmentDetailController controller = loader.getController();
                controller.setEntry(entry);
                return detailBox;
            } catch (IOException e) {
                e.printStackTrace();
                return new VBox(new Label("Không thể tải giao diện chi tiết."));
            }
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
    private void loadAppointmentsFromDatabase(Calendar calendar) {
        // ⚠️ Giả định đã có DAO như sau:
        // List<AppointmentModel> danhSach = AppointmentDAO.getAllForToday();
        // hoặc AppointmentDAO.getAll()

        // TODO: thay dòng sau bằng dữ liệu thực từ DAO
        // List<AppointmentModel> danhSach = AppointmentDAO.getAll();

        System.out.println("Đang tải lịch hẹn từ DB...");

        // Giả lập danh sách (để test nếu chưa có DAO)
        // Xoá đoạn này khi dùng DAO thật
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
            AppointmentEntry entry = new AppointmentEntry(model.getHoTen(), model);
            entry.setInterval(
                    model.getNgayKham().atTime(LocalTime.of(9, 0)),
                    model.getNgayKham().atTime(LocalTime.of(9, 30))
            );
            calendar.addEntry(entry);
        }
    }

}

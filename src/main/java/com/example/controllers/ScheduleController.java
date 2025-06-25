package com.example.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DayViewBase;
import com.calendarfx.view.WeekView;
import com.calendarfx.view.page.WeekPage;
import com.example.model.DutyShiftModel;
import com.example.model.Role;
import com.example.model.ScheduleEntry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class ScheduleController {

    @FXML private DatePicker datePicker;
    @FXML private ChoiceBox<String> shiftChoice;
    @FXML private TextField nameField;
    @FXML private TextField roleField;
    @FXML private Label statusLabel;
    @FXML private StackPane calendarPane;

    private CalendarView calendarView;
    private CalendarSource calendarSource;
    private final Map<Role, Calendar> roleCalendars = new HashMap<>();

    @FXML
    public void initialize() {
        // Tạo nguồn lịch chung
        calendarSource = new CalendarSource("Nguồn lịch");

        // Khởi tạo CalendarView
        calendarView = new CalendarView();
        calendarView.getCalendarSources().add(calendarSource);
        calendarView.showWeekPage();
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowSourceTray(false);
        calendarView.setShowPageSwitcher(false);
        calendarView.setEntryFactory(param -> null);
        calendarView.setEntryDetailsPopOverContentCallback(param -> null);
        calendarPane.getChildren().add(calendarView);

        // Cấu hình WeekView
        WeekPage weekPage = calendarView.getWeekPage();
        WeekView weekView = weekPage.getDetailedWeekView().getWeekView();
        weekView.setStartTime(LocalTime.of(7, 0));
        weekView.setVisibleHours(14);
        weekView.setHourHeight(40);
        weekView.setEarlyLateHoursStrategy(DayViewBase.EarlyLateHoursStrategy.HIDE);

        // Chặn tạo Entry bằng chuột
        weekView.setEntryFactory(param -> null);
        weekView.setEntryDetailsPopOverContentCallback(param -> null);

        // Cài đặt lựa chọn ca trực
        shiftChoice.getItems().addAll(ScheduleEntry.SHIFTS.keySet());
        shiftChoice.getSelectionModel().selectFirst();

        // Tải dữ liệu mẫu
        loadSampleEntries();
    }

    @FXML
    private void handleAddShift() {
        LocalDate date = datePicker.getValue();
        String shift = shiftChoice.getValue();
        String name = nameField.getText().trim();
        String roleText = roleField.getText().trim();

        if (date == null || name.isEmpty() || roleText.isEmpty()) {
            showStatus("Vui lòng nhập đầy đủ thông tin.", true);
            return;
        }

        Role role;
        try {
            role = Role.fromVietnamese(roleText);
        } catch (IllegalArgumentException ex) {
            showStatus("Vai trò không hợp lệ.", true);
            return;
        }

        DutyShiftModel duty = new DutyShiftModel(name, role, date, shift);

        // Lấy hoặc tạo Calendar theo vai trò
        Calendar roleCalendar = roleCalendars.computeIfAbsent(role, r -> {
            Calendar c = new Calendar(r.toVietnamese());
            c.setStyle(getStyleForRole(r));
            calendarSource.getCalendars().add(c);
            return c;
        });

        // Tạo entry và đồng bộ từ model
        ScheduleEntry entry = new ScheduleEntry(name, duty);
        entry.syncFromModel();
        entry.setCalendar(roleCalendar);

        showStatus("Thêm lịch trực thành công.", false);
        nameField.clear();
        roleField.clear();
        calendarView.refreshData();
    }

    private void showStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: " + (isError ? "red;" : "green;"));
    }

    private void loadSampleEntries() {
        LocalDate today = LocalDate.now();
        addSampleEntry(new DutyShiftModel("Nguyen Van A", Role.DOCTOR, today, "Sáng"));
        addSampleEntry(new DutyShiftModel("Tran Thi B", Role.DOCTOR, today, "Sáng"));
        addSampleEntry(new DutyShiftModel("Le Van C", Role.NURSE, today, "Chiều"));
        addSampleEntry(new DutyShiftModel("Pham Thi D", Role.NURSE, today.plusDays(1), "Tối"));
    }

    private void addSampleEntry(DutyShiftModel duty) {
        Calendar roleCalendar = roleCalendars.computeIfAbsent(duty.getVaiTro(), r -> {
            Calendar c = new Calendar(r.toVietnamese());
            c.setStyle(getStyleForRole(r));
            calendarSource.getCalendars().add(c);
            return c;
        });

        ScheduleEntry entry = new ScheduleEntry(duty.getTenNguoiTruc(), duty);
        entry.syncFromModel();
        entry.setCalendar(roleCalendar);
    }

    private Calendar.Style getStyleForRole(Role role) {
        return switch (role) {
            case DOCTOR -> Calendar.Style.STYLE1;
            case NURSE -> Calendar.Style.STYLE2;
            case MANAGER -> Calendar.Style.STYLE3;
            case ADMIN -> Calendar.Style.STYLE4;
        };
    }
}

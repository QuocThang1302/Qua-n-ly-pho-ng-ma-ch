package com.example.controllers;

import com.example.model.FilterDate;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.time.YearMonth;
// TODO: FETCH DATA + CHART
public class DashboardController {
    @FXML
    private ChoiceBox<String> filterTypeChoice;
    @FXML
    private ChoiceBox<String> dayChoice;
    @FXML
    private ChoiceBox<String> monthChoice;
    @FXML
    private ChoiceBox<String> yearChoice;
    @FXML
    private Label resultLabel;

    @FXML
    private void initialize() {
        // Loại lọc
        filterTypeChoice.getItems().addAll("Năm", "Tháng", "Ngày");
        filterTypeChoice.setValue("Ngày");

        // Dữ liệu tháng và năm
        for (int i = 1; i <= 12; i++) {
            monthChoice.getItems().add(String.valueOf(i));
        }
        yearChoice.getItems().addAll("2023", "2024", "2025");

        // Thiết lập mặc định
        monthChoice.setValue("1");
        yearChoice.setValue("2024");

        // Gọi cập nhật ngày theo tháng/năm hiện tại
        updateValidDays();

        // Lắng nghe thay đổi tháng/năm để cập nhật lại ngày
        monthChoice.setOnAction(e -> updateValidDays());
        yearChoice.setOnAction(e -> updateValidDays());
    }

    private void updateValidDays() {
        int month = Integer.parseInt(monthChoice.getValue());
        int year = Integer.parseInt(yearChoice.getValue());

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        String previousSelectedDay = dayChoice.getValue();

        dayChoice.getItems().clear();
        for (int i = 1; i <= daysInMonth; i++) {
            dayChoice.getItems().add(String.valueOf(i));
        }

        // Nếu ngày trước đó còn hợp lệ thì giữ lại
        if (previousSelectedDay != null && Integer.parseInt(previousSelectedDay) <= daysInMonth) {
            dayChoice.setValue(previousSelectedDay);
        } else {
            dayChoice.setValue("1");
        }
    }

    @FXML
    private void handleFilter() {
        String mode = filterTypeChoice.getValue();
        int day = Integer.parseInt(dayChoice.getValue());
        int month = Integer.parseInt(monthChoice.getValue());
        int year = Integer.parseInt(yearChoice.getValue());

        FilterDate fd = new FilterDate(mode, day, month, year);
        resultLabel.setText("Đã chọn: " + fd.toString());
    }
}
package com.example.controllers;

import com.example.DAO.HenKhamBenhDAO;
import com.example.DAO.BillDAO;
import com.example.DAO.PatientDAO;
import com.example.DAO.QuiDinhDAO;
import com.example.model.FilterDate;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.Node;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardController {

    // FXML Components - theo file FXML mới
    @FXML
    private DatePicker dpFrom;
    @FXML
    private DatePicker dpTo;
    @FXML
    private Label patientCountLabel;
    @FXML
    private StackPane patientChartContainer;
    @FXML
    private StackPane revenueChartContainer;
    @FXML
    private Label revenueLabel;
    @FXML
    private TextField txtMaxPatients;
    @FXML
    private TextField txtExamFee;

    // Charts
    private BarChart<String, Number> patientBarChart;
    private AreaChart<String, Number> revenueAreaChart;

    // ExecutorService để xử lý tác vụ nền
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    // Biến để theo dõi task hiện tại
    private Task<Void> currentPatientChartTask;
    private Task<Void> currentRevenueChartTask;

    @FXML
    private void initialize() {
        // Khởi tạo DatePicker với giá trị mặc định
        initializeDatePickers();

        // Thiết lập các biểu đồ
        setupPatientChart();
        setupRevenueChart();

        // Cập nhật dữ liệu ban đầu
        updateChartsForDateRange();
    }

    /**
     * Thêm method handleUpdateConfig để khắc phục lỗi FXML
     */
    @FXML
    private void handleUpdateConfig() {
        System.out.println("Update config button clicked");

        boolean hasUpdate = false;
        StringBuilder message = new StringBuilder();

        // Cập nhật MAX_PATIENT_PER_DAY nếu txtMaxPatients không rỗng
        String maxPatientsText = txtMaxPatients.getText().trim();
        if (!maxPatientsText.isEmpty()) {
            try {
                int maxPatients = Integer.parseInt(maxPatientsText);
                boolean success = QuiDinhDAO.updateGiaTri("MAX_PATIENT_PER_DAY", BigDecimal.valueOf(maxPatients));
                if (success) {
                    message.append("✔ Cập nhật số bệnh nhân tối đa thành công.\n");
                    hasUpdate = true;
                } else {
                    message.append("❌ Cập nhật số bệnh nhân tối đa thất bại.\n");
                }
            } catch (NumberFormatException e) {
                message.append("⚠ Giá trị số bệnh nhân phải là số nguyên.\n");
            }
        }

        // Cập nhật DEFAULT_TIEN_KHAM nếu txtExamFee không rỗng
        String examFeeText = txtExamFee.getText().trim();
        if (!examFeeText.isEmpty()) {
            try {
                double examFee = Double.parseDouble(examFeeText);
                boolean success = QuiDinhDAO.updateGiaTri("DEFAULT_TIEN_KHAM", BigDecimal.valueOf(examFee));
                if (success) {
                    message.append("✔ Cập nhật tiền khám mặc định thành công.\n");
                    hasUpdate = true;
                } else {
                    message.append("❌ Cập nhật tiền khám mặc định thất bại.\n");
                }
            } catch (NumberFormatException e) {
                message.append("⚠ Giá trị tiền khám phải là số thực.\n");
            }
        }

        if (!hasUpdate && message.length() == 0) {
            message.append("⚠ Vui lòng nhập ít nhất một giá trị để cập nhật.");
        }

        // Hiển thị thông báo cho người dùng
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Kết quả cập nhật");
        alert.setHeaderText(null);
        alert.setContentText(message.toString());
        alert.showAndWait();
    }



    /**
     * Khởi tạo DatePicker với giá trị mặc định (tháng hiện tại)
     */
    private void initializeDatePickers() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();

        dpFrom.setValue(firstDayOfMonth);
        dpTo.setValue(lastDayOfMonth);
    }

    /**
     * Thiết lập biểu đồ bệnh nhân (Bar Chart)
     */
    private void setupPatientChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Thời gian");
        yAxis.setLabel("Số bệnh nhân");

        patientBarChart = new BarChart<>(xAxis, yAxis);
        patientBarChart.setTitle("Số lượng bệnh nhân theo ngày");
        patientBarChart.setLegendVisible(false);
        patientBarChart.setAnimated(false);

        // Style
        patientBarChart.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
        );

        patientChartContainer.getChildren().clear();
        patientChartContainer.getChildren().add(patientBarChart);
    }

    /**
     * Thiết lập biểu đồ doanh thu (Area Chart)
     */
    private void setupRevenueChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Thời gian");
        yAxis.setLabel("Doanh thu (VNĐ)");

        revenueAreaChart = new AreaChart<>(xAxis, yAxis);
        revenueAreaChart.setTitle("Doanh thu theo ngày");
        revenueAreaChart.setLegendVisible(false);
        revenueAreaChart.setAnimated(false);
        revenueAreaChart.setCreateSymbols(false);

        // Style
        revenueAreaChart.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
        );

        revenueChartContainer.getChildren().clear();
        revenueChartContainer.getChildren().add(revenueAreaChart);
    }

    @FXML
    private void handleFilter() {
        LocalDate fromDate = dpFrom.getValue();
        LocalDate toDate = dpTo.getValue();

        if (fromDate == null || toDate == null) {
            System.err.println("Vui lòng chọn đầy đủ ngày từ và ngày đến");
            return;
        }

        if (fromDate.isAfter(toDate)) {
            System.err.println("Ngày bắt đầu không thể sau ngày kết thúc");
            return;
        }

        updateChartsForDateRange();
    }

    /**
     * Cập nhật biểu đồ cho khoảng thời gian được chọn
     */
    private void updateChartsForDateRange() {
        LocalDate fromDate = dpFrom.getValue();
        LocalDate toDate = dpTo.getValue();

        if (fromDate == null || toDate == null) {
            return;
        }

        updatePatientChartAsync(fromDate, toDate);
        updateRevenueChartAsync(fromDate, toDate);
    }

    /**
     * Cập nhật biểu đồ bệnh nhân theo khoảng thời gian
     */
    private void updatePatientChartAsync(LocalDate fromDate, LocalDate toDate) {
        // Hủy task trước đó nếu đang chạy
        if (currentPatientChartTask != null && !currentPatientChartTask.isDone()) {
            currentPatientChartTask.cancel(true);
        }

        currentPatientChartTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Tạo FilterDate cho khoảng thời gian
                FilterDate fromFilter = new FilterDate("Ngày", fromDate.getDayOfMonth(),
                        fromDate.getMonthValue(), fromDate.getYear());
                FilterDate toFilter = new FilterDate("Ngày", toDate.getDayOfMonth(),
                        toDate.getMonthValue(), toDate.getYear());

                // Sử dụng hàm có sẵn để lấy dữ liệu
                List<Integer> patientCounts = HenKhamBenhDAO.getPatientCountsBetween(fromFilter, toFilter);

                if (isCancelled()) return null;

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Bệnh nhân");

                // Tạo labels cho biểu đồ
                long daysBetween = ChronoUnit.DAYS.between(fromDate, toDate);
                if (daysBetween <= 31) {
                    // Hiển thị theo ngày
                    LocalDate currentDate = fromDate;
                    for (int i = 0; i < patientCounts.size(); i++) {
                        String dateLabel = currentDate.getDayOfMonth() + "/" + currentDate.getMonthValue();
                        series.getData().add(new XYChart.Data<>(dateLabel, patientCounts.get(i)));
                        currentDate = currentDate.plusDays(1);
                    }
                } else {
                    // Hiển thị theo tháng
                    YearMonth startMonth = YearMonth.from(fromDate);
                    YearMonth currentMonth = startMonth;
                    for (int i = 0; i < patientCounts.size(); i++) {
                        String monthLabel = "T" + currentMonth.getMonthValue() + "/" + currentMonth.getYear();
                        series.getData().add(new XYChart.Data<>(monthLabel, patientCounts.get(i)));
                        currentMonth = currentMonth.plusMonths(1);
                    }
                }
                int totalPatients = patientCounts.stream().mapToInt(Integer::intValue).sum();

                // Cập nhật biểu đồ trên UI thread
                Platform.runLater(() -> {
                    patientBarChart.getData().clear();
                    patientBarChart.getData().add(series);
                    patientBarChart.setTitle("Bệnh nhân từ " + fromDate + " đến " + toDate);
                    patientCountLabel.setText(String.valueOf(totalPatients));

                    // Áp dụng style sau khi render
                    Platform.runLater(() -> stylePatientChart());
                });

                return null;
            }
        };

        currentPatientChartTask.setOnFailed(e -> {
            System.err.println("Lỗi cập nhật biểu đồ bệnh nhân: " +
                    currentPatientChartTask.getException().getMessage());
        });

        executorService.submit(currentPatientChartTask);
    }

    /**
     * Cập nhật biểu đồ doanh thu theo khoảng thời gian
     */
    private void updateRevenueChartAsync(LocalDate fromDate, LocalDate toDate) {
        // Hủy task trước đó nếu đang chạy
        if (currentRevenueChartTask != null && !currentRevenueChartTask.isDone()) {
            currentRevenueChartTask.cancel(true);
        }

        currentRevenueChartTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Tạo FilterDate cho khoảng thời gian
                FilterDate fromFilter = new FilterDate("Ngày", fromDate.getDayOfMonth(),
                        fromDate.getMonthValue(), fromDate.getYear());
                FilterDate toFilter = new FilterDate("Ngày", toDate.getDayOfMonth(),
                        toDate.getMonthValue(), toDate.getYear());

                // Sử dụng hàm có sẵn để lấy dữ liệu
                List<Double> revenueList = BillDAO.getTotalRevenueBetween(fromFilter, toFilter);

                if (isCancelled()) return null;

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Doanh thu");

                // Tạo labels cho biểu đồ
                long daysBetween = ChronoUnit.DAYS.between(fromDate, toDate);
                if (daysBetween <= 31) {
                    // Hiển thị theo ngày
                    LocalDate currentDate = fromDate;
                    for (int i = 0; i < revenueList.size(); i++) {
                        String dateLabel = currentDate.getDayOfMonth() + "/" + currentDate.getMonthValue();
                        series.getData().add(new XYChart.Data<>(dateLabel, revenueList.get(i)));
                        currentDate = currentDate.plusDays(1);
                    }
                } else {
                    // Hiển thị theo tháng
                    YearMonth startMonth = YearMonth.from(fromDate);
                    YearMonth currentMonth = startMonth;
                    for (int i = 0; i < revenueList.size(); i++) {
                        String monthLabel = "T" + currentMonth.getMonthValue() + "/" + currentMonth.getYear();
                        series.getData().add(new XYChart.Data<>(monthLabel, revenueList.get(i)));
                        currentMonth = currentMonth.plusMonths(1);
                    }
                }

                double totalRevenue = revenueList.stream().mapToDouble(Double::doubleValue).sum();
                String formatted = String.format("%,.0f₫", totalRevenue);

                // Cập nhật biểu đồ trên UI thread
                Platform.runLater(() -> {
                    revenueAreaChart.getData().clear();
                    revenueAreaChart.getData().add(series);
                    revenueAreaChart.setTitle("Doanh thu từ " + fromDate + " đến " + toDate);
                    revenueLabel.setText(formatted);

                    // Áp dụng style sau khi render
                    Platform.runLater(() -> styleRevenueChart());
                });

                return null;
            }
        };

        currentRevenueChartTask.setOnFailed(e -> {
            System.err.println("Lỗi cập nhật biểu đồ doanh thu: " +
                    currentRevenueChartTask.getException().getMessage());
        });

        executorService.submit(currentRevenueChartTask);
    }

    /**
     * Áp dụng style cho biểu đồ bệnh nhân
     */
    private void stylePatientChart() {
        patientBarChart.applyCss();
        patientBarChart.layout();

        int dataSize = 0;
        if (!patientBarChart.getData().isEmpty()) {
            dataSize = patientBarChart.getData().get(0).getData().size();
        }

        for (Node node : patientBarChart.lookupAll(".chart-bar")) {
            String style = """
        -fx-bar-fill: #4CAF50;
        -fx-background-radius: 3px;
        -fx-border-radius: 3px;
        """;

            // Chỉ scale nếu ít hơn 10 cột
            if (dataSize < 8) {
                style += "-fx-scale-x: 0.3;";
            }

            node.setStyle(style);
        }
    }

    /**
     * Áp dụng style cho biểu đồ doanh thu
     */
    private void styleRevenueChart() {
        revenueAreaChart.applyCss();
        revenueAreaChart.layout();

        for (Node node : revenueAreaChart.lookupAll(".chart-series-area-fill")) {
            node.setStyle("-fx-fill: linear-gradient(to bottom, #2196F340, #2196F310);");
        }

        for (Node node : revenueAreaChart.lookupAll(".chart-series-area-line")) {
            node.setStyle("-fx-stroke: #2196F3;" +
                    "-fx-stroke-width: 2px;");
        }
    }

    /**
     * Cleanup khi controller bị hủy
     */
    public void cleanup() {
        if (currentPatientChartTask != null && !currentPatientChartTask.isDone()) {
            currentPatientChartTask.cancel(true);
        }
        if (currentRevenueChartTask != null && !currentRevenueChartTask.isDone()) {
            currentRevenueChartTask.cancel(true);
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
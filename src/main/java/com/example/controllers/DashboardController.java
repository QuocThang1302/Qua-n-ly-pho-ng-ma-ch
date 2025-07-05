package com.example.controllers;

import com.example.DAO.HenKhamBenhDAO;
import com.example.DAO.BillDAO;
import com.example.DAO.PatientDAO;
import com.example.model.FilterDate;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.Node;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
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

    // Charts
    private BarChart<String, Number> patientBarChart;
    private AreaChart<String, Number> revenueAreaChart;

    // Cache để lưu trữ dữ liệu
    private Map<String, Integer> patientDataCache = new HashMap<>();
    private Map<String, Double> revenueDataCache = new HashMap<>();

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
        updateTotalPatientLabel();
        updateTodayRevenueLabel();
        updateChartsForDateRange();
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

    /**
     * Cập nhật tổng số bệnh nhân
     */
    private void updateTotalPatientLabel() {
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return PatientDAO.getPatientCount();
            }
        };

        task.setOnSucceeded(e -> {
            int count = task.getValue();
            Platform.runLater(() -> {
                patientCountLabel.setText(String.valueOf(count));
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                patientCountLabel.setText("N/A");
            });
            System.err.println("Lỗi khi lấy tổng bệnh nhân: " + task.getException().getMessage());
        });

        executorService.submit(task);
    }

    /**
     * Cập nhật doanh thu hôm nay
     */
    private void updateTodayRevenueLabel() {
        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                LocalDate today = LocalDate.now();
                FilterDate filterDate = new FilterDate("Ngày", today.getDayOfMonth(),
                        today.getMonthValue(), today.getYear());
                return BillDAO.getTotalRevenue(filterDate);
            }
        };

        task.setOnSucceeded(e -> {
            double revenue = task.getValue();
            Platform.runLater(() -> {
                String formatted = String.format("%,.0f₫", revenue);
                revenueLabel.setText(formatted);
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                revenueLabel.setText("N/A");
            });
            System.err.println("Lỗi lấy doanh thu hôm nay: " + task.getException().getMessage());
        });

        executorService.submit(task);
    }

    /**
     * Xử lý sự kiện lọc khi nhấn nút "Lọc"
     */
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
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Bệnh nhân");

                LocalDate currentDate = fromDate;
                while (!currentDate.isAfter(toDate)) {
                    if (isCancelled()) return null;

                    FilterDate fd = new FilterDate("Ngày", currentDate.getDayOfMonth(),
                            currentDate.getMonthValue(), currentDate.getYear());
                    String cacheKey = generateCacheKey(fd);

                    int count;
                    if (patientDataCache.containsKey(cacheKey)) {
                        count = patientDataCache.get(cacheKey);
                    } else {
                        count = HenKhamBenhDAO.countDistinctPatientsByDate(fd);
                        patientDataCache.put(cacheKey, count);
                    }

                    String dateLabel = currentDate.getDayOfMonth() + "/" + currentDate.getMonthValue();
                    series.getData().add(new XYChart.Data<>(dateLabel, count));

                    currentDate = currentDate.plusDays(1);
                }

                // Cập nhật biểu đồ trên UI thread
                Platform.runLater(() -> {
                    patientBarChart.getData().clear();
                    patientBarChart.getData().add(series);
                    patientBarChart.setTitle("Bệnh nhân từ " + fromDate + " đến " + toDate);

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
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Doanh thu");

                LocalDate currentDate = fromDate;
                while (!currentDate.isAfter(toDate)) {
                    if (isCancelled()) return null;

                    FilterDate fd = new FilterDate("Ngày", currentDate.getDayOfMonth(),
                            currentDate.getMonthValue(), currentDate.getYear());
                    String cacheKey = generateRevenueCacheKey(fd);

                    double revenue;
                    if (revenueDataCache.containsKey(cacheKey)) {
                        revenue = revenueDataCache.get(cacheKey);
                    } else {
                        revenue = BillDAO.getTotalRevenue(fd);
                        revenueDataCache.put(cacheKey, revenue);
                    }

                    String dateLabel = currentDate.getDayOfMonth() + "/" + currentDate.getMonthValue();
                    series.getData().add(new XYChart.Data<>(dateLabel, revenue));

                    currentDate = currentDate.plusDays(1);
                }

                // Cập nhật biểu đồ trên UI thread
                Platform.runLater(() -> {
                    revenueAreaChart.getData().clear();
                    revenueAreaChart.getData().add(series);
                    revenueAreaChart.setTitle("Doanh thu từ " + fromDate + " đến " + toDate);

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

        for (Node node : patientBarChart.lookupAll(".chart-bar")) {
            node.setStyle("-fx-bar-fill: #4CAF50;" +
                    "-fx-background-radius: 3px;" +
                    "-fx-border-radius: 3px;");
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
     * Tạo cache key cho dữ liệu bệnh nhân
     */
    private String generateCacheKey(FilterDate fd) {
        return "patient_" + fd.getMode() + "_" + fd.getLocalDate() + "_" + fd.getYearMonth() + "_" + fd.getYear();
    }

    /**
     * Tạo cache key cho dữ liệu doanh thu
     */
    private String generateRevenueCacheKey(FilterDate fd) {
        return "revenue_" + fd.getMode() + "_" + fd.getLocalDate() + "_" + fd.getYearMonth() + "_" + fd.getYear();
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
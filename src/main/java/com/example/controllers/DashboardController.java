package com.example.controllers;

import com.example.DAO.HenKhamBenhDAO;
import com.example.DAO.BillDAO;
import com.example.DAO.PatientDAO;
import com.example.model.FilterDate;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.Node;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private Label patientCountLabel;
    @FXML
    private StackPane patientChartContainer;
    @FXML
    private StackPane revenueChartContainer;
    @FXML
    private Label revenueLabel;

    private BarChart<String, Number> patientBarChart;
    private AreaChart<String, Number> revenueAreaChart;

    // Cache để lưu trữ dữ liệu đã tải
    private Map<String, Integer> dataCache = new HashMap<>();
    private Map<String, Double> revenueDataCache = new HashMap<>();

    // ExecutorService để xử lý tác vụ nền
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Biến để theo dõi task hiện tại
    private Task<Void> currentTask;
    private Task<Void> currentRevenueTask;

    @FXML
    private void initialize() {
        // Khởi tạo các ChoiceBox
        initializeChoiceBoxes();

        // Thiết lập biểu đồ
        setupBarChart();
        setupRevenueAreaChart();

        // Cập nhật dữ liệu ban đầu
        updatePatientChart();
        updateRevenueChart();
        updateSummaryCard();
        updateTodayRevenueLabel();
        updateTotalPatientLabel();

        // Thiết lập listeners
        setupListeners();
    }

    private void updateTodayRevenueLabel() {
        Task<Double> revenueTask = new Task<>() {
            @Override
            protected Double call() {
                LocalDate today = LocalDate.now();
                FilterDate filterDate = new FilterDate("Ngày", today.getDayOfMonth(), today.getMonthValue(), today.getYear());
                return BillDAO.getTotalRevenue(filterDate);
            }
        };

        revenueTask.setOnSucceeded(e -> {
            double revenue = revenueTask.getValue();

            // Format tiền (VD: 4.200.000₫)
            String formatted = String.format("%,.0f₫", revenue);
            revenueLabel.setText(formatted);
        });

        revenueTask.setOnFailed(e -> {
            revenueLabel.setText("Lỗi");
            System.err.println("Lỗi lấy doanh thu hôm nay: " + revenueTask.getException().getMessage());
        });

        executorService.submit(revenueTask);
    }

    private void updateTotalPatientLabel() {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() {
                return PatientDAO.getPatientCount();
            }
        };

        task.setOnSucceeded(e -> {
            int count = task.getValue();
            String patient = String.valueOf(count);
            patientCountLabel.setText(patient);
        });

        task.setOnFailed(e -> {
            patientCountLabel.setText("Lỗi lấy dữ liệu");
            System.err.println("Lỗi khi lấy tổng bệnh nhân: " + task.getException().getMessage());
        });

        executorService.submit(task);
    }


    private void initializeChoiceBoxes() {
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

        // Cập nhật ngày theo tháng/năm
        updateValidDays();
    }

    private void setupListeners() {
        // Listeners cho việc thay đổi lựa chọn với debounce
        filterTypeChoice.setOnAction(e -> {
            updateValidDays();
            updateChartAsync();
            updateRevenueChartAsync();
            updateSummaryCardAsync();
        });

        monthChoice.setOnAction(e -> {
            updateValidDays();
            updateChartAsync();
            updateRevenueChartAsync();
            updateSummaryCardAsync();
        });

        yearChoice.setOnAction(e -> {
            updateValidDays();
            updateChartAsync();
            updateRevenueChartAsync();
            updateSummaryCardAsync();
        });

        dayChoice.setOnAction(e -> {
            updateChartAsync();
            updateRevenueChartAsync();
            updateSummaryCardAsync();
        });
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

        // Giữ ngày đã chọn nếu hợp lệ
        if (previousSelectedDay != null &&
                Integer.parseInt(previousSelectedDay) <= daysInMonth) {
            dayChoice.setValue(previousSelectedDay);
        } else {
            dayChoice.setValue("1");
        }
    }

    @FXML
    private void handleFilter() {
        updateSingleDayCharts();
    }


    // Phương thức async cho update summary card
    private void updateSummaryCardAsync() {
        String mode = filterTypeChoice.getValue();
        int day = 0;
        if (dayChoice.getValue() != null && !dayChoice.getValue().isEmpty()) {
            day = Integer.parseInt(dayChoice.getValue());
        }

        int month = 0;
        if (monthChoice.getValue() != null && !monthChoice.getValue().isEmpty()) {
            month = Integer.parseInt(monthChoice.getValue());
        }

        int year = 0;
        if (yearChoice.getValue() != null && !yearChoice.getValue().isEmpty()) {
            year = Integer.parseInt(yearChoice.getValue());
        }

        FilterDate fd = new FilterDate(mode, day, month, year);
        String cacheKey = generateCacheKey(fd);

        // Kiểm tra cache trước
        if (dataCache.containsKey(cacheKey)) {
            int patientCount = dataCache.get(cacheKey);
            Platform.runLater(() -> {
                //patientCountLabel.setText(String.valueOf(patientCount));
                resultLabel.setText("Đã chọn: " + fd.toString() + " - Số bệnh nhân: " + patientCount);
            });
            return;
        }

        // Nếu không có trong cache, tải async
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return HenKhamBenhDAO.countDistinctPatientsByDate(fd);
            }
        };

        task.setOnSucceeded(e -> {
            int patientCount = task.getValue();
            dataCache.put(cacheKey, patientCount); // Lưu vào cache

            Platform.runLater(() -> {
                //patientCountLabel.setText(String.valueOf(patientCount));
                resultLabel.setText("Đã chọn: " + fd.toString() + " - Số bệnh nhân: " + patientCount);
            });
        });

        executorService.submit(task);
    }

    private void setupBarChart() {
        // Thiết lập biểu đồ bệnh nhân
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Thời gian");
        yAxis.setLabel("Số bệnh nhân");

        patientBarChart = new BarChart<>(xAxis, yAxis);
        patientBarChart.setTitle("Số lượng bệnh nhân");
        patientBarChart.setLegendVisible(false);
        patientBarChart.setAnimated(false); // Tắt animation để nhanh hơn

        // Thiết lập style cho biểu đồ bệnh nhân
        patientBarChart.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
        );

        patientChartContainer.getChildren().clear();
        patientChartContainer.getChildren().add(patientBarChart);
    }

    private void setupRevenueAreaChart() {
        // Thiết lập biểu đồ doanh thu
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Thời gian");
        yAxis.setLabel("Doanh thu (VNĐ)");

        revenueAreaChart = new AreaChart<>(xAxis, yAxis);
        revenueAreaChart.setTitle("Doanh thu");
        revenueAreaChart.setLegendVisible(false);
        revenueAreaChart.setAnimated(false); // Tắt animation để nhanh hơn
        revenueAreaChart.setCreateSymbols(false); // Ẩn các điểm trên đường

        // Thiết lập style cho biểu đồ doanh thu
        revenueAreaChart.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
        );

        revenueChartContainer.getChildren().clear();
        revenueChartContainer.getChildren().add(revenueAreaChart);
    }


    // Phương thức async cho update chart
    private void updateChartAsync() {
        // Hủy task trước đó nếu đang chạy
        if (currentTask != null && !currentTask.isDone()) {
            currentTask.cancel(true);
        }

        String mode = filterTypeChoice.getValue();
        int month = Integer.parseInt(monthChoice.getValue());
        int year = Integer.parseInt(yearChoice.getValue());

        currentTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Bệnh nhân");


                switch (mode) {
                    case "Ngày" -> {
                        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
                        for (int day = 1; day <= daysInMonth; day++) {
                            if (isCancelled()) return null;

                            FilterDate fd = new FilterDate("Ngày", day, month, year);
                            String cacheKey = generateCacheKey(fd);

                            int count;
                            if (dataCache.containsKey(cacheKey)) {
                                count = dataCache.get(cacheKey);
                            } else {
                                count = HenKhamBenhDAO.countDistinctPatientsByDate(fd);
                                dataCache.put(cacheKey, count);
                            }

                            series.getData().add(new XYChart.Data<>(String.valueOf(day), count));
                        }
                        Platform.runLater(() -> patientBarChart.getXAxis().setLabel("Ngày trong tháng " + month));
                    }
                    case "Tháng" -> {
                        for (int m = 1; m <= 12; m++) {
                            if (isCancelled()) return null;

                            FilterDate fd = new FilterDate("Tháng", 1, m, year);
                            String cacheKey = generateCacheKey(fd);

                            int count;
                            if (dataCache.containsKey(cacheKey)) {
                                count = dataCache.get(cacheKey);
                            } else {
                                count = HenKhamBenhDAO.countDistinctPatientsByDate(fd);
                                dataCache.put(cacheKey, count);
                            }

                            series.getData().add(new XYChart.Data<>("T" + m, count));
                        }
                        Platform.runLater(() -> patientBarChart.getXAxis().setLabel("Tháng trong năm " + year));
                    }
                    case "Năm" -> {
                        for (int y = 2023; y <= 2025; y++) {
                            if (isCancelled()) return null;

                            FilterDate fd = new FilterDate("Năm", 1, 1, y);
                            String cacheKey = generateCacheKey(fd);

                            int count;
                            if (dataCache.containsKey(cacheKey)) {
                                count = dataCache.get(cacheKey);
                            } else {
                                count = HenKhamBenhDAO.countDistinctPatientsByDate(fd);
                                dataCache.put(cacheKey, count);
                            }

                            series.getData().add(new XYChart.Data<>(String.valueOf(y), count));
                        }
                        Platform.runLater(() -> patientBarChart.getXAxis().setLabel("Năm"));
                    }
                }

                // Cập nhật chart trên UI thread
                Platform.runLater(() -> {
                    patientBarChart.getData().clear();
                    patientBarChart.getData().add(series);

                    // Style chart sau khi data đã được thêm
                    Platform.runLater(() -> styleChartBars(patientBarChart, "#4CAF50"));
                });

                return null;
            }
        };

        currentTask.setOnSucceeded(e -> {
            // Task đã hoàn thành, chart đã được cập nhật trong Platform.runLater
        });

        currentTask.setOnFailed(e -> {
            System.err.println("Error updating chart: " + currentTask.getException().getMessage());
        });

        executorService.submit(currentTask);
    }

    // Thêm phương thức async cho update revenue chart
    private void updateRevenueChartAsync() {
        // Hủy task trước đó nếu đang chạy
        if (currentRevenueTask != null && !currentRevenueTask.isDone()) {
            currentRevenueTask.cancel(true);
        }

        String mode = filterTypeChoice.getValue();
        int month = Integer.parseInt(monthChoice.getValue());
        int year = Integer.parseInt(yearChoice.getValue());

        currentRevenueTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Doanh thu");

                switch (mode) {
                    case "Ngày" -> {
                        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
                        for (int day = 1; day <= daysInMonth; day++) {
                            if (isCancelled()) return null;

                            FilterDate fd = new FilterDate("Ngày", day, month, year);
                            String cacheKey = generateRevenueCacheKey(fd);

                            double revenue;
                            if (revenueDataCache.containsKey(cacheKey)) {
                                revenue = revenueDataCache.get(cacheKey);
                            } else {
                                revenue = BillDAO.getTotalRevenue(fd);
                                revenueDataCache.put(cacheKey, revenue);
                            }

                            series.getData().add(new XYChart.Data<>(String.valueOf(day), revenue));
                        }
                        Platform.runLater(() -> revenueAreaChart.getXAxis().setLabel("Ngày trong tháng " + month));
                    }
                    case "Tháng" -> {
                        for (int m = 1; m <= 12; m++) {
                            if (isCancelled()) return null;

                            FilterDate fd = new FilterDate("Tháng", 1, m, year);
                            String cacheKey = generateRevenueCacheKey(fd);

                            double revenue;
                            if (revenueDataCache.containsKey(cacheKey)) {
                                revenue = revenueDataCache.get(cacheKey);
                            } else {
                                revenue = BillDAO.getTotalRevenue(fd);
                                revenueDataCache.put(cacheKey, revenue);
                            }

                            series.getData().add(new XYChart.Data<>("T" + m, revenue));
                        }
                        Platform.runLater(() -> revenueAreaChart.getXAxis().setLabel("Tháng trong năm " + year));
                    }
                    case "Năm" -> {
                        for (int y = 2023; y <= 2025; y++) {
                            if (isCancelled()) return null;

                            FilterDate fd = new FilterDate("Năm", 1, 1, y);
                            String cacheKey = generateRevenueCacheKey(fd);

                            double revenue;
                            if (revenueDataCache.containsKey(cacheKey)) {
                                revenue = revenueDataCache.get(cacheKey);
                            } else {
                                revenue = BillDAO.getTotalRevenue(fd);
                                revenueDataCache.put(cacheKey, revenue);
                            }

                            series.getData().add(new XYChart.Data<>(String.valueOf(y), revenue));
                        }
                        Platform.runLater(() -> revenueAreaChart.getXAxis().setLabel("Năm"));
                    }
                }

                // Cập nhật chart trên UI thread
                Platform.runLater(() -> {
                    revenueAreaChart.getData().clear();
                    revenueAreaChart.getData().add(series);

                    // Style chart sau khi data đã được thêm - màu khác cho doanh thu
                    Platform.runLater(() -> styleAreaChart(revenueAreaChart, "#2196F3"));
                });

                return null;
            }
        };

        currentRevenueTask.setOnSucceeded(e -> {
            // Task đã hoàn thành, chart đã được cập nhật trong Platform.runLater
        });

        currentRevenueTask.setOnFailed(e -> {
            System.err.println("Error updating revenue chart: " + currentRevenueTask.getException().getMessage());
        });

        executorService.submit(currentRevenueTask);
    }

    // Phương thức cũ để tương thích
    private void updatePatientChart() {
        updateChartAsync();
    }

    private void updateSummaryCard() {
        updateSummaryCardAsync();
    }

    private void updateRevenueChart() {
        updateRevenueChartAsync();
    }

    private String generateCacheKey(FilterDate fd) {
        return fd.getMode() + "_" + fd.getLocalDate() + "_" + fd.getYearMonth() + "_" + fd.getYear();
    }

    private String generateRevenueCacheKey(FilterDate fd) {
        return "revenue_" + fd.getMode() + "_" + fd.getLocalDate() + "_" + fd.getYearMonth() + "_" + fd.getYear();
    }

    private void styleChartBars(BarChart<String, Number> chart, String color) {
        chart.applyCss();
        chart.layout();

        for (Node node : chart.lookupAll(".chart-bar")) {
            node.setStyle("-fx-bar-fill: " + color + ";" +
                    "-fx-background-radius: 3px;" +
                    "-fx-border-radius: 3px;");

            double actualWidth = node.getBoundsInParent().getWidth();

            // ✅ Scale lại nếu lớn hơn 40px
            if (actualWidth > 40) {
                double scale = 40.0 / actualWidth;
                node.setScaleX(scale);
            } else {
                node.setScaleX(1.0); // Giữ nguyên nếu nhỏ hơn
            }
        }
    }


    private void styleAreaChart(AreaChart<String, Number> chart, String color) {
        // Đợi chart render xong rồi mới áp dụng style
        chart.applyCss();
        chart.layout();

        // Style cho area chart
        for (Node node : chart.lookupAll(".chart-series-area-fill")) {
            node.setStyle("-fx-fill: linear-gradient(to bottom, " + color + "40, " + color + "10);");
        }

        for (Node node : chart.lookupAll(".chart-series-area-line")) {
            node.setStyle("-fx-stroke: " + color + ";" +
                    "-fx-stroke-width: 2px;");
        }
    }

    // Cleanup khi controller bị hủy
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    private void updateSingleDayCharts() {
        int day = Integer.parseInt(dayChoice.getValue());
        int month = Integer.parseInt(monthChoice.getValue());
        int year = Integer.parseInt(yearChoice.getValue());

        FilterDate fd = new FilterDate("Ngày", day, month, year);
        String cacheKey = generateCacheKey(fd);
        String revenueCacheKey = generateRevenueCacheKey(fd);

        // Tạo task xử lý biểu đồ bệnh nhân
        Task<Void> patientTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                int count;
                if (dataCache.containsKey(cacheKey)) {
                    count = dataCache.get(cacheKey);
                } else {
                    count = HenKhamBenhDAO.countDistinctPatientsByDate(fd);
                    dataCache.put(cacheKey, count);
                }

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.getData().add(new XYChart.Data<>(day + "/" + month, count));

                Platform.runLater(() -> {
                    patientBarChart.getData().clear();
                    patientBarChart.getData().add(series);
                    patientBarChart.setTitle("Bệnh nhân ngày " + day + "/" + month + "/" + year);
                    patientBarChart.getXAxis().setLabel("Ngày");
                    styleChartBars(patientBarChart, "#4CAF50");
                });

                return null;
            }
        };

        // Tạo task xử lý biểu đồ doanh thu
        Task<Void> revenueTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                double revenue;
                if (revenueDataCache.containsKey(revenueCacheKey)) {
                    revenue = revenueDataCache.get(revenueCacheKey);
                } else {
                    revenue = BillDAO.getTotalRevenue(fd);
                    revenueDataCache.put(revenueCacheKey, revenue);
                }

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.getData().add(new XYChart.Data<>(day + "/" + month, revenue));

                Platform.runLater(() -> {
                    revenueAreaChart.getData().clear();
                    revenueAreaChart.getData().add(series);
                    revenueAreaChart.setTitle("Doanh thu ngày " + day + "/" + month + "/" + year);
                    revenueAreaChart.getXAxis().setLabel("Ngày");
                    styleAreaChart(revenueAreaChart, "#2196F3");
                });

                return null;
            }
        };

        // Chạy cả hai task
        executorService.submit(patientTask);
        executorService.submit(revenueTask);
    }

}
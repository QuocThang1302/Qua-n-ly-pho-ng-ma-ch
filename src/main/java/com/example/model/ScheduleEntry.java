package com.example.model;

import com.calendarfx.model.Entry;

import java.time.LocalDateTime;

/**
 * Entry dùng cho lịch trực, gắn với DutyShiftModel.
 */
public class ScheduleEntry extends Entry<String> {

    private DutyShiftModel model;

    public ScheduleEntry(String title, DutyShiftModel model) {
        super(title);
        this.model = model;
    }

    public DutyShiftModel getModel() {
        return model;
    }

    public void setModel(DutyShiftModel model) {
        this.model = model;
    }

    // Optional: override title / tooltip
    public void syncFromModel() {
        setTitle(model.getTenNguoiTruc() + " - " + model.getVaiTro().toVietnamese());
        setLocation(model.getCaTruc());
        setInterval(
                LocalDateTime.of(model.getNgay(), ScheduleEntry.SHIFTS.get(model.getCaTruc())[0]),
                LocalDateTime.of(model.getNgay(), ScheduleEntry.SHIFTS.get(model.getCaTruc())[1])
        );
    }

    // Optional: bạn có thể đặt lại giờ trực ở đây nếu muốn tuỳ chỉnh thêm
    public static final java.util.Map<String, java.time.LocalTime[]> SHIFTS = java.util.Map.of(
            "Sáng", new java.time.LocalTime[]{java.time.LocalTime.of(7, 30), java.time.LocalTime.of(11, 30)},
            "Chiều", new java.time.LocalTime[]{java.time.LocalTime.of(13, 0), java.time.LocalTime.of(17, 0)},
            "Tối", new java.time.LocalTime[]{java.time.LocalTime.of(17, 0), java.time.LocalTime.of(21, 0)}
    );
}

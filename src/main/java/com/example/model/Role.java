package com.example.model;

public enum Role {
    ADMIN,
    DOCTOR,
    MANAGER,
    NURSE;

    public static Role fromVietnamese(String name) {
        return switch (name.toLowerCase()) {
            case "bác sĩ" -> DOCTOR;
            case "y tá" -> NURSE;
            case "quản lý" -> MANAGER;
            case "quản trị" -> ADMIN;
            default -> throw new IllegalArgumentException("Vai trò không hợp lệ: " + name);
        };
    }

    public String toVietnamese() {
        return switch (this) {
            case DOCTOR -> "Bác sĩ";
            case NURSE -> "Y tá";
            case MANAGER -> "Quản lý";
            case ADMIN -> "Quản trị";
        };
    }
}

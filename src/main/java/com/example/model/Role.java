package com.example.model;

public enum Role {
    ADMIN,
    DOCTOR,
    MANAGER,
    NURSE;

    public static Role fromVietnamese(String name) {
        return switch (name.toLowerCase()) {
            case "Bác sĩ" -> DOCTOR;
            case "Y tá" -> NURSE;
            case "Quản lý" -> MANAGER;
            case "Quản trị" -> ADMIN;
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

package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model;
public class TelemetryData {
    public int speed;
    public int batteryLevel;
    public TelemetryData(int speed, int batteryLevel) {
        this.speed = speed;
        this.batteryLevel = batteryLevel;
    }
}

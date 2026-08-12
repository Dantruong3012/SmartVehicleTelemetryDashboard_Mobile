package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model;
public class TelemetryData {
    public int speed;
    public int batteryLevel;
    public int engineTemperature;

    public TelemetryData(int speed, int batteryLevel) {
        this(speed, batteryLevel, 70);
    }

    public TelemetryData(int speed, int batteryLevel, int engineTemperature) {
        this.speed = speed;
        this.batteryLevel = batteryLevel;
        this.engineTemperature = engineTemperature;
    }
}

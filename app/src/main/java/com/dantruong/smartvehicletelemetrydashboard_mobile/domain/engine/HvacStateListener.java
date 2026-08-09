package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine;
public interface HvacStateListener {
    void onTemperatureChanged(int temp);
    void onHvacStateChanged(boolean isOn);
}

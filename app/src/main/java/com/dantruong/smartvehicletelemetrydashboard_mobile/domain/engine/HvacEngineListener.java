package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine;

public interface HvacEngineListener {
    void onUpdateTemp(int temp);
    void onChangeHvacStatus(boolean isOn);
}

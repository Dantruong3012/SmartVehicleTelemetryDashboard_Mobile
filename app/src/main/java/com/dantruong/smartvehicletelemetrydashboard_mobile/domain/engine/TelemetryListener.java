package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine;

import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.TelemetryData;

public interface TelemetryListener {
    void onTelemetryUpdated(TelemetryData data);
}

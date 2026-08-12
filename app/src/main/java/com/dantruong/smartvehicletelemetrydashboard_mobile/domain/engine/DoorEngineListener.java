package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine;

public interface DoorEngineListener {
    void onDoorStateChanged(int doorId, boolean isOpen);
    void onEmergencyBrakeTriggered(boolean isBraking);
}

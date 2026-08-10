package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine;

public interface DoorStateListener {
    void onDoorStateChanged(int doorId, boolean isOpen);
}

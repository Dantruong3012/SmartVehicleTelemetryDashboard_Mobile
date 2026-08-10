package com.dantruong.smartvehicletelemetrydashboard_mobile;

interface IDoorControlCallback {
    void onDoorStateChanged(int doorId, boolean isOpen);
    void onDoorError(int doorId, int errorCode);
}
package com.dantruong.smartvehicletelemetrydashboard_mobile;

import com.dantruong.smartvehicletelemetrydashboard_mobile.IDoorControlCallback;

interface IDoorControlInterface {
    void requestOpenDoor(int doorId);
    void requestCloseDoor(int doorId);
    boolean isDoorOpen(int doorId);
    void registerCallback(IDoorControlCallback callback);
    void unregisterCallback(IDoorControlCallback callback);
}
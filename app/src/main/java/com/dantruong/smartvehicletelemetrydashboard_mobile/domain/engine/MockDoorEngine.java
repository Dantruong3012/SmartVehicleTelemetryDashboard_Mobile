package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.HashMap;
import java.util.Map;

public class MockDoorEngine {
    private final HandlerThread engineThread;
    private final Handler handler;
    public MockDoorEngine() {
        engineThread = new HandlerThread("DoorEngineThread");
        engineThread.start();
        handler = new Handler(engineThread.getLooper());
    }
    private final Map<Integer, Boolean> doorStates = new HashMap<>();
    private DoorEngineListener listener;

    public void setListener(DoorEngineListener listener) {
        this.listener = listener;
    }

    public void requestOpenDoor(int doorId) {
        if (Boolean.TRUE.equals(doorStates.get(doorId))) return;
        if (listener != null) {
            listener.onEmergencyBrakeTriggered(true);
        }
        handler.postDelayed(() -> {
            doorStates.put(doorId, true);
            if (listener != null) {
                listener.onDoorStateChanged(doorId, true);
            }
        }, 5000);
    }

    public void requestCloseDoor(int doorId) {
        doorStates.put(doorId, false);
        if (listener != null) {
            listener.onDoorStateChanged(doorId, false);
        }
        boolean allClosed = true;
        for (Boolean state : doorStates.values()) {
            if (Boolean.TRUE.equals(state)) {
                allClosed = false;
                break;
            }
        }
        if (allClosed && listener != null) {
            listener.onEmergencyBrakeTriggered(false);
        }
    }

    public boolean isDoorOpen(int doorId) {
        return Boolean.TRUE.equals(doorStates.get(doorId));
    }


    public void destroy() {
        engineThread.quit();
    }

}

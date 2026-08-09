package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine;

public class MockHvacEngine {
    private HvacEngineListener hvacEngineListener;
    private boolean isHvacOn = false;
    private Thread hvacThread;
    private int currentTemp;
    private int targetTemp;

    public MockHvacEngine(int initialTemp) {
        this.currentTemp = initialTemp;
        this.targetTemp = initialTemp;
    }

    public void setTargetTemp(int targetTemp) {
        this.targetTemp = targetTemp;
        if (!isHvacOn) {
            this.currentTemp = targetTemp;
            if (hvacEngineListener != null) {
                hvacEngineListener.onUpdateTemp(this.currentTemp);
            }
        }
    }

    public void setHvacEngineListener(HvacEngineListener hvacEngineListener) {
        this.hvacEngineListener = hvacEngineListener;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public boolean isHvacOn() {
        return isHvacOn;
    }

    public void turnOn() {
        if (isHvacOn) return;
        isHvacOn = true;

        if (hvacEngineListener != null) {
            hvacEngineListener.onChangeHvacStatus(true);
        }

        hvacThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                while (isHvacOn) {
                    int oldTemp = currentTemp;
                    Thread.sleep(1000);

                    if (targetTemp > currentTemp) {
                        currentTemp++;
                    } else if (targetTemp < currentTemp) {
                        currentTemp--;
                    }

                    if (currentTemp != oldTemp && hvacEngineListener != null) {
                        hvacEngineListener.onUpdateTemp(currentTemp);
                    }
                }
            } catch (InterruptedException e) {
                if (hvacThread != null) {
                    hvacThread.interrupt();
                }
            } finally {
                isHvacOn = false;
                if (hvacEngineListener != null) {
                    hvacEngineListener.onChangeHvacStatus(false);
                }
            }
        });
        hvacThread.start();
    }

    public void turnOff() {
        isHvacOn = false;
        if (hvacThread != null) {
            hvacThread.interrupt();
        }
    }
}

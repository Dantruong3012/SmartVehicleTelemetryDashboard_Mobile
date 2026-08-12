package com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import androidx.annotation.Nullable;
import androidx.room.Room;

import com.dantruong.smartvehicletelemetrydashboard_mobile.ICanbusCallBack;
import com.dantruong.smartvehicletelemetrydashboard_mobile.ICanbusInterface;
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.AppDatabase;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.HvacConfig;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.HvacEngineListener;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.MockHvacEngine;

public class HvacEngineService extends Service {
    private ICanbusCallBack clientCallback;
    private MockHvacEngine mockHvacEngine;
    AppDatabase appDatabase;

    private HandlerThread hvacHandlerThread;
    private Handler hvacHandler;


    private static final int MSG_TEMP_UPDATE = 1;

    private final ICanbusInterface.Stub binder = new ICanbusInterface.Stub() {
        @Override
        public void registerCallBack(ICanbusCallBack iCanbusCallBack) throws RemoteException {
            clientCallback = iCanbusCallBack;
        }

        @Override
        public void unRegisterCallBack(ICanbusCallBack iCanbusCallBack) throws RemoteException {
            if (clientCallback != null && iCanbusCallBack != null &&
                clientCallback.asBinder() == iCanbusCallBack.asBinder()) {
                clientCallback = null;
            }
        }

        @Override
        public void setTargetTemperature(int temp) throws RemoteException {
            if (mockHvacEngine != null){
                hvacHandler.post(() -> mockHvacEngine.setTargetTemp(temp));
            }
        }

        @Override
        public int getCurrentTemp() throws RemoteException {
            if (mockHvacEngine != null){
                return mockHvacEngine.getCurrentTemp();
            }
            return HvacConfig.DEFAULT_TEMPERATURE;
        }

        @Override
        public void setHvacEnabled(boolean isEnabled) throws RemoteException {
            if (mockHvacEngine != null) {
                if (isEnabled) {
                    hvacHandler.post(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        int savedTemp = HvacConfig.DEFAULT_TEMPERATURE;
                        if (appDatabase != null) {
                            Integer dbTemp = appDatabase.temperatureDao().getCurrentTempBlocking();
                            if (dbTemp != null) savedTemp = dbTemp;
                        }
                        mockHvacEngine.setTargetTemp(savedTemp);
                        mockHvacEngine.turnOn();
                    });
                } else {
                    hvacHandler.post(() -> mockHvacEngine.turnOff());
                }
            }
        }

        @Override
        public boolean isHvacEnabled() throws RemoteException {
            if (mockHvacEngine != null) {
                return mockHvacEngine.isHvacOn();
            }
            return false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = Room.databaseBuilder(
                        getApplicationContext(),
                        AppDatabase.class,
                        "smart-vehicle"
                )
                .enableMultiInstanceInvalidation()
                .fallbackToDestructiveMigration()
                .build();
        hvacHandlerThread = new HandlerThread("hvacHandlerThread");
        hvacHandlerThread.start();
        hvacHandler = new Handler(hvacHandlerThread.getLooper(), msg -> {
            if (msg.what == MSG_TEMP_UPDATE){
                int temp = msg.arg1;
                if (clientCallback != null){
                    try {
                        clientCallback.onTemperatureChanged(temp);
                    }catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
            return false;
        });

        mockHvacEngine = new MockHvacEngine(HvacConfig.DEFAULT_TEMPERATURE);
        mockHvacEngine.setHvacEngineListener(new HvacEngineListener() {
            @Override
            public void onUpdateTemp(int temp) {
                hvacHandler.removeMessages(MSG_TEMP_UPDATE);
                Message msg = hvacHandler.obtainMessage(MSG_TEMP_UPDATE);
                msg.arg1 = temp;
                hvacHandler.sendMessage(msg);
            }

            @Override
            public void onChangeHvacStatus(boolean isOn) {
                if (clientCallback != null) {
                    try {
                        clientCallback.onTurnHvacEngine(isOn);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mockHvacEngine != null) mockHvacEngine.turnOff();
        if (hvacHandlerThread != null) hvacHandlerThread.quitSafely();
        if (appDatabase != null) appDatabase.close();
    }
}

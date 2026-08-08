
package com.dantruong.smartvehicletelemetrydashboard_mobile;

interface ICanbusCallBack {
   void onTemperatureChanged(int temp);
   void onTurnHvacEngine(boolean isOn);
}
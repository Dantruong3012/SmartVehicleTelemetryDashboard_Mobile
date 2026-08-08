package com.dantruong.smartvehicletelemetrydashboard_mobile;

import com.dantruong.smartvehicletelemetrydashboard_mobile.ICanbusCallBack;
interface ICanbusInterface {
   void registerCallBack(ICanbusCallBack iCanbusCallBack);
   void unRegisterCallBack (ICanbusCallBack iCanbusCallBack);
   void setTargetTemperature(int temp);
   int getCurrentTemp();
}
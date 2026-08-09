package com.dantruong.smartvehicletelemetrydashboard_mobile;

import com.dantruong.smartvehicletelemetrydashboard_mobile.ICanbusCallBack;
interface ICanbusInterface {

    // đk và huỷ đk callback
   void registerCallBack(ICanbusCallBack iCanbusCallBack);
   void unRegisterCallBack (ICanbusCallBack iCanbusCallBack);

    // điều chỉnh nhiệt độ điều hoà
   void setTargetTemperature(int temp);
   int getCurrentTemp();

    // bật/tắt đh
   void setHvacEnabled(boolean isEnabled);

   boolean isHvacEnabled();
}
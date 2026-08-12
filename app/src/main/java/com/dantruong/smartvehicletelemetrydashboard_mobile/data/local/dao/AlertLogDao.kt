package com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity.AlertLog

@Dao
interface AlertLogDao {
    @Insert
    fun insertAlertLog(alertLog: AlertLog)

    @Query("SELECT * FROM alert_logs ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getLatestAlertLogs(limit: Int): List<AlertLog>
}

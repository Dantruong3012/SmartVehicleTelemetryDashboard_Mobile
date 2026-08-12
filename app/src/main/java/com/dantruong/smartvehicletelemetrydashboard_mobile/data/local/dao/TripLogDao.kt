package com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity.TripLog

@Dao
interface TripLogDao {
    @Insert
    fun insertTripLog(tripLog: TripLog)
    @Query("SELECT * FROM trip_logs ORDER BY loggedAt DESC LIMIT :limit")
    suspend fun getLatestTripLogs(limit: Int): List<TripLog>
}

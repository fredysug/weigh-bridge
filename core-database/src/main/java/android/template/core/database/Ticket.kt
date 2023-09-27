/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.core.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Entity
data class Ticket(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "license_number") val licenseNumber: String,
    @ColumnInfo(name = "driver_name") val driverName: String,
    @ColumnInfo(name = "inbound_weight") val inboundWeight: Double,
    @ColumnInfo(name = "outbound_weight") val outboundWeight: Double,
)

@Dao
interface TicketDao {
    @Query("SELECT * FROM ticket")
    fun getTickets(): Flow<List<Ticket>>

    @Query("SELECT * FROM ticket where uid = :uid")
    suspend fun getTicket(uid: Int): Ticket

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(item: Ticket) : Long
}

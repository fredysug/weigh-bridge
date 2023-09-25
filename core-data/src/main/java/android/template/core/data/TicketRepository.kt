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

package android.template.core.data

import kotlinx.coroutines.flow.Flow
import android.template.core.database.TicketDao
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

interface TicketRepository {
    val tickets: Flow<List<Ticket>>

    suspend fun add(
        date: Date,
        licenseNumber: String,
        driverName: String,
        inboundWeight: Double,
        outboundWeight: Double,
    )
}

class TicketRepositoryImpl @Inject constructor(
    private val ticketDao: TicketDao,
) : TicketRepository {

    override val tickets: Flow<List<Ticket>> =
        ticketDao.getTickets().map { items ->
            items.map { Ticket(it) }
        }

    override suspend fun add(
        date: Date,
        licenseNumber: String,
        driverName: String,
        inboundWeight: Double,
        outboundWeight: Double,
    ) {
        ticketDao.insertTicket(
            android.template.core.database.Ticket(
                licenseNumber = licenseNumber,
                driverName = driverName,
                inboundWeight = inboundWeight,
                outboundWeight = outboundWeight,
                date = date,
            )
        )
    }
}

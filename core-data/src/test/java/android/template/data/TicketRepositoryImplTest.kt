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

package android.template.data

import android.template.core.data.TicketRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import android.template.core.data.TicketRepositoryImpl
import android.template.core.database.Ticket
import android.template.core.database.TicketDao
import org.junit.Assert.assertTrue
import org.junit.Before
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class TicketRepositoryImplTest {

    private lateinit var dao: FakeTicketDao
    private lateinit var repository: TicketRepository

    @Before
    fun setup() {
        dao = FakeTicketDao()
        repository = TicketRepositoryImpl(dao)
    }

    @Test
    fun `when add, should add ticket to dao`() = runTest {
        repository.add(
            Date(1695643100439),
            "license",
            "driverName",
            0.0,
            1.0,
        )

        assertTrue(dao.dataAdded)
    }


    @Test
    fun `when get ticket from Dao, should return ticket as entity`() = runTest {
        val date = Date(1695643100439)
        val licenseNumber = "license"
        val driverName = "driverName"
        val inboundWeight = 0.0
        val outboundWeight = 1.0

        repository.add(date, licenseNumber, driverName, inboundWeight, outboundWeight)

        assertEquals(repository.tickets.first().size, 1)
        assertEquals(
            repository.tickets.first().first(),
            android.template.core.data.Ticket(
                uid = 0,
                date = date,
                licenseNumber = licenseNumber,
                driverName = driverName,
                inboundWeight = inboundWeight,
                outboundWeight = outboundWeight,
            )
        )
    }

    @Test
    fun `when get by uid, should add ticket to return ticket as entity`() = runTest {
        val date = Date(1695643100439)
        val licenseNumber = "license"
        val driverName = "driverName"
        val inboundWeight = 0.0
        val outboundWeight = 1.0

        repository.add(
            date = date,
            licenseNumber = licenseNumber,
            driverName = driverName,
            inboundWeight = inboundWeight,
            outboundWeight = outboundWeight,
        )

        val ticket = repository.getTicket(0)

        assertEquals(date, ticket.date)
        assertEquals(licenseNumber, ticket.licenseNumber)
        assertEquals(driverName, ticket.driverName)
        assertEquals(inboundWeight, ticket.inboundWeight, 0.0)
        assertEquals(outboundWeight, ticket.outboundWeight, 0.0)
    }

    @Test
    fun `when update ticket, should re-add ticket again to the dao `() = runTest {
        val date = Date(1695643100439)
        val licenseNumber = "license"
        val driverName = "driverName"
        val inboundWeight = 0.0
        val outboundWeight = 1.0

        repository.updateTicket(
            android.template.core.data.Ticket(
                uid = 0,
                date = date,
                licenseNumber = licenseNumber,
                driverName = driverName,
                inboundWeight = inboundWeight,
                outboundWeight = outboundWeight,
            )
        )

        assertTrue(dao.dataAdded)
    }

}

private class FakeTicketDao : TicketDao {

    private val data = mutableListOf<Ticket>()
    var dataAdded = false

    override fun getTickets(): Flow<List<Ticket>> = flow {
        emit(data)
    }

    override suspend fun getTicket(uid: Int): Ticket {
        return data.first { it.uid == uid }
    }

    override suspend fun insertTicket(ticket: Ticket) {
        dataAdded = true
        data.add(0, ticket)
    }
}

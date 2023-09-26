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

package android.template.test.app.testdi

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import android.template.core.data.TicketRepository
import android.template.core.data.di.DataModule
import android.template.core.data.di.FakeTicketRepository
import android.template.core.database.AppDatabase
import android.template.core.database.Ticket
import android.template.core.database.TicketDao
import android.template.core.database.di.DatabaseModule
import dagger.Provides
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
interface FakeDataModule {

    @Binds
    abstract fun bindRepository(
        fakeRepository: FakeTicketRepository
    ): TicketRepository
}
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
interface FakeDatabaseModule2 {

    @Provides
    fun provideTicketDao(): TicketDao {
        return FakeTicketDao()
    }

    private class FakeTicketDao : TicketDao {

        private val data = mutableListOf<Ticket>()
        var dataAdded = false

        override fun getTickets(): Flow<List<Ticket>> = flow {
            emit(data)
        }

        override suspend fun insertTicket(ticket: Ticket) {
            dataAdded = true
            data.add(0, ticket)
        }
    }

}

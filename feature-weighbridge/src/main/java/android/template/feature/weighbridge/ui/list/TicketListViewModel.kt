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

package android.template.feature.weighbridge.ui.list

import android.template.core.data.Ticket
import android.template.core.data.TicketRepository
import android.template.feature.weighbridge.ui.list.UiState.Error
import android.template.feature.weighbridge.ui.list.UiState.Loading
import android.template.feature.weighbridge.ui.list.UiState.Success
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter.Field
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter.FilterDateRange
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter.FilterDriver
import android.template.feature.weighbridge.ui.list.UiState.Success.Sort
import android.template.feature.weighbridge.ui.list.UiState.Success.Sort.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TicketListViewModel @Inject constructor(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    val availableFilter = listOf(Field.Date, Field.DriverName, Field.LicenseNumber)
    val availableSort =
        listOf(DateAsc, DateDesc, LicenseNumberAsc, LicenseNumberDesc, DriverAsc, DriverDesc)

    private val filter = MutableStateFlow<Filter?>(null)
    private val sort = MutableStateFlow<Sort?>(null)
    private val expandedIds = MutableStateFlow<List<Int>>(emptyList())

    val uiState: StateFlow<UiState> = ticketRepository
        .tickets
        .mapViewObjectAndAssignExpandState()
        .filter()
        .sort()
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Loading)

    private fun Flow<List<Ticket>>.mapViewObjectAndAssignExpandState() =
        combine(expandedIds) { tickets, expandedIds ->
            tickets.map { TicketViewObject(it, expandedIds.contains(it.uid)) }
        }

    private fun Flow<List<TicketViewObject>>.filter(): Flow<UiState> =
        combine(filter) { tickets, filter ->
            if (filter != null) {
                val filteredTicket = tickets
                    .filter { ticket ->
                        when (filter) {
                            is FilterDateRange ->
                                ticket.date.after(filter.dateStart) && ticket.date.before(filter.dateEnd)

                            is FilterDriver ->
                                ticket.driverName.contains(filter.value, false)

                            is Filter.FilterLicenseNumber ->
                                ticket.licenseNumber.contains(filter.value, false)
                        }
                    }
                Success(data = filteredTicket, filter = filter)
            } else {
                Success(data = tickets)
            }
        }

    private fun Flow<UiState>.sort(): Flow<UiState> = combine(sort) { uiState, sort ->
        if (sort != null && uiState is Success) {
            val sortedTicket = uiState.data
                .sortedWith(
                    when (sort) {
                        DateAsc -> compareBy(TicketViewObject::date)
                        DateDesc -> compareByDescending(TicketViewObject::date)
                        LicenseNumberAsc -> compareBy(TicketViewObject::licenseNumber)
                        LicenseNumberDesc -> compareByDescending(TicketViewObject::licenseNumber)
                        DriverAsc -> compareBy(TicketViewObject::driverName)
                        DriverDesc -> compareByDescending(TicketViewObject::driverName)
                    }
                )
            uiState.copy(data = sortedTicket, sort = sort)
        } else {
            uiState
        }
    }

    fun onExpand(uid: Int, expanded: Boolean) {
        if (expanded)
            expandedIds.value = expandedIds.value + uid
        else
            expandedIds.value = expandedIds.value - uid
    }

    fun filter(newFilter: Filter) {
        filter.value = newFilter
    }

    fun sort(newSort: Sort) {
        sort.value = newSort
    }
}

sealed interface UiState {
    object Loading : UiState
    data class Error(val throwable: Throwable) : UiState
    data class Success(
        val data: List<TicketViewObject>,
        val filter: Filter? = null,
        val sort: Sort? = null
    ) : UiState {
        sealed interface Filter {
            data class FilterDateRange(val dateStart: Date, val dateEnd: Date) : Filter
            data class FilterLicenseNumber(val value: String) : Filter
            data class FilterDriver(val value: String) : Filter

            enum class Field { Date, DriverName, LicenseNumber }
        }

        enum class Sort { DateAsc, DateDesc, LicenseNumberAsc, LicenseNumberDesc, DriverAsc, DriverDesc }
    }
}

data class TicketViewObject(
    val uid: Int,
    val date: Date,
    val licenseNumber: String,
    val driverName: String,
    val inboundWeight: String,
    val outboundWeight: String,
    val netWeight: String,
    val expanded: Boolean,
) {
    constructor(ticket: Ticket, expanded: Boolean) : this(
        uid = ticket.uid,
        date = ticket.date,
        licenseNumber = ticket.licenseNumber,
        driverName = ticket.driverName,
        inboundWeight = ticket.inboundWeight.toString(),
        outboundWeight = ticket.outboundWeight.toString(),
        netWeight = ticket.netWeight.toString(),
        expanded = expanded,
    )
}

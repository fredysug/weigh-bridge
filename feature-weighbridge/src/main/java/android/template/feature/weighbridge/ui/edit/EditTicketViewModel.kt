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

package android.template.feature.weighbridge.ui.edit

import android.template.core.data.Ticket
import android.template.core.data.util.Logger
import android.template.core.data.TicketRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditTicketViewModel @Inject constructor(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _editTicketForm = MutableStateFlow(EditTicketForm.default)
    val editTicketForm = _editTicketForm.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _editingResult = MutableSharedFlow<EditingState>()
    val editingResult = _editingResult.asSharedFlow()

    fun load(ticketUid: Int) {
        viewModelScope.launch {
            val ticket = ticketRepository.getTicket(ticketUid)
            with(ticket) {
                _editTicketForm.update(
                    uid = uid,
                    date = date,
                    licenseNumber = licenseNumber,
                    driverName = driverName,
                    inboundWeight = inboundWeight.toString(),
                    outboundWeight = outboundWeight.toString(),
                )
            }
        }
    }

    fun editTicket() {
        viewModelScope.launch {
            _isLoading.value = true
            if (validateForm()) {
                try {
                    with(_editTicketForm.value) {
                        ticketRepository.updateTicket(
                            Ticket(
                                uid = uid,
                                date = date,
                                licenseNumber = licenseNumber.trim(),
                                driverName = driverName.trim(),
                                inboundWeight = inboundWeight.toDouble(),
                                outboundWeight = outboundWeight.toDouble(),
                            )
                        )
                    }
                    _editingResult.emit(EditingState.Success)
                } catch (e: Exception) {
                    Logger.e("Error when edit ticket ${editTicketForm.value} because $e")
                    _editingResult.emit(EditingState.Error)
                }
            } else {
                _editingResult.emit(EditingState.IncompleteForm)
            }
            _isLoading.value = false
        }
    }

    private fun validateForm(): Boolean {
        with(editTicketForm.value) {
            _editTicketForm.update(
                inboundWeightError = inboundWeight.isEmpty(),
                outboundWeightError = outboundWeight.isEmpty(),
            )
        }
        return with(editTicketForm.value) {
            !inboundWeightError && !outboundWeightError
        }
    }

    fun onInboundWeightUpdate(newValue: String) {
        val newInboundWeight = newValue.toDoubleOrNull()
        if (newInboundWeight != null || newValue.isEmpty())
            _editTicketForm.update(
                inboundWeight = newValue,
                inboundWeightError = newValue.isEmpty()
            )
    }

    fun onOutboundWeightUpdate(newValue: String) {
        val newOutboundWeight = newValue.toDoubleOrNull()
        if (newOutboundWeight != null || newValue.isEmpty())
            _editTicketForm.update(
                outboundWeight = newValue,
                outboundWeightError = newValue.isEmpty()
            )
    }

    private fun MutableStateFlow<EditTicketForm>.update(
        uid: Int = this.value.uid,
        date: Date = this.value.date,
        licenseNumber: String = this.value.licenseNumber,
        driverName: String = this.value.driverName,
        inboundWeight: String = this.value.inboundWeight,
        outboundWeight: String = this.value.outboundWeight,
        inboundWeightError: Boolean = this.value.inboundWeightError,
        outboundWeightError: Boolean = this.value.outboundWeightError,
    ) {
        this.value = editTicketForm.value.copy(
            uid = uid,
            date = date,
            licenseNumber = licenseNumber,
            driverName = driverName,
            inboundWeight = inboundWeight,
            outboundWeight = outboundWeight,
            inboundWeightError = inboundWeightError,
            outboundWeightError = outboundWeightError,
        )
    }
}

data class EditTicketForm(
    val uid: Int,
    val date: Date,
    val licenseNumber: String,
    val driverName: String,
    val inboundWeight: String,
    val outboundWeight: String,
    val inboundWeightError: Boolean,
    val outboundWeightError: Boolean,
) {
    companion object {
        val default = EditTicketForm(
            uid = 0,
            date = Date(),
            licenseNumber = "",
            driverName = "",
            inboundWeight = "",
            outboundWeight = "",
            inboundWeightError = false,
            outboundWeightError = false
        )
    }
}

enum class EditingState { IncompleteForm, Error, Success }

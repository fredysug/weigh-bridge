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

package android.template.feature.weighbridge.ui.add

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
class AddTicketViewModel @Inject constructor(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _addTicketForm = MutableStateFlow(AddTicketForm.default)
    val addTicketForm = _addTicketForm.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _addingResult = MutableSharedFlow<AddingState>()
    val addingResult = _addingResult.asSharedFlow()

    fun addTicket() {
        viewModelScope.launch {
            _isLoading.value = true
            if (validateForm()) {
                try {
                    with(_addTicketForm.value) {
                        ticketRepository.add(
                            date = date,
                            licenseNumber = licenseNumber,
                            driverName = driverName,
                            inboundWeight = inboundWeight.toDouble(),
                            outboundWeight = outboundWeight.toDouble(),
                        )
                    }
                    _addingResult.emit(AddingState.Success)
                } catch (e: Exception) {
                    Logger.e("Error when add ticket ${addTicketForm.value} because $e")
                    _addingResult.emit(AddingState.Error)
                }
            } else {
                _addingResult.emit(AddingState.IncompleteForm)
            }
            _isLoading.value = false
        }
    }

    private fun validateForm(): Boolean {
        with(addTicketForm.value) {
            _addTicketForm.update(
                licenseNumberError = licenseNumber.isBlank(),
                driverNameError = driverName.isBlank(),
                inboundWeightError = inboundWeight.isEmpty(),
                outboundWeightError = outboundWeight.isEmpty(),
            )
        }
        return with(addTicketForm.value) {
            !licenseNumberError && !driverNameError && !inboundWeightError && !outboundWeightError
        }
    }

    fun onDateUpdate(newValue: Date) {
        _addTicketForm.update(date = newValue)
    }

    fun onLicenseNumberUpdate(newValue: String) {
        _addTicketForm.update(licenseNumber = newValue, licenseNumberError = newValue.isBlank())
    }

    fun onDriverNameUpdate(newValue: String) {
        _addTicketForm.update(driverName = newValue, driverNameError = newValue.isBlank())
    }

    fun onInboundWeightUpdate(newValue: String) {
        val newInboundWeight = newValue.toDoubleOrNull()
        if (newInboundWeight != null || newValue.isEmpty())
            _addTicketForm.update(inboundWeight = newValue, inboundWeightError = newValue.isEmpty())
    }

    fun onOutboundWeightUpdate(newValue: String) {
        val newOutboundWeight = newValue.toDoubleOrNull()
        if (newOutboundWeight != null || newValue.isEmpty())
            _addTicketForm.update(outboundWeight = newValue, outboundWeightError = newValue.isEmpty())
    }

    private fun MutableStateFlow<AddTicketForm>.update(
        date: Date = this.value.date,
        licenseNumber: String = this.value.licenseNumber,
        driverName: String = this.value.driverName,
        inboundWeight: String = this.value.inboundWeight,
        outboundWeight: String = this.value.outboundWeight,
        licenseNumberError: Boolean = this.value.licenseNumberError,
        driverNameError: Boolean = this.value.driverNameError,
        inboundWeightError: Boolean = this.value.inboundWeightError,
        outboundWeightError: Boolean = this.value.outboundWeightError,
    ) {
        this.value = addTicketForm.value.copy(
            date = date,
            licenseNumber = licenseNumber,
            driverName = driverName,
            inboundWeight = inboundWeight,
            outboundWeight = outboundWeight,
            licenseNumberError = licenseNumberError,
            driverNameError = driverNameError,
            inboundWeightError = inboundWeightError,
            outboundWeightError = outboundWeightError,
        )
    }
}

data class AddTicketForm(
    val date: Date,
    val licenseNumber: String,
    val driverName: String,
    val inboundWeight: String,
    val outboundWeight: String,
    val licenseNumberError: Boolean,
    val driverNameError: Boolean,
    val inboundWeightError: Boolean,
    val outboundWeightError: Boolean,
) {
    companion object {
        val default = AddTicketForm(
            date = Date(),
            licenseNumber = "",
            driverName = "",
            inboundWeight = "",
            outboundWeight = "",
            licenseNumberError = false,
            driverNameError = false,
            inboundWeightError = false,
            outboundWeightError = false
        )
    }
}

enum class AddingState { IncompleteForm, Error, Success }

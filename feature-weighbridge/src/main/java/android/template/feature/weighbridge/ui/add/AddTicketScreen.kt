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

import android.template.core.ui.DateTimePicker
import android.template.core.ui.WeighBridgeApplicationTheme
import android.template.core.ui.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection.Companion.Down
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeAction.Companion.Next
import androidx.compose.ui.text.input.KeyboardType.Companion.Number
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTicketScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AddTicketViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = STARTED) {
            viewModel.addingResult.collect {
                when (it) {
                    AddingState.IncompleteForm -> snackBarHostState.showSnackbar("Please complete the form")
                    AddingState.Error -> snackBarHostState.showSnackbar("Oops, something went wrong. Please try again")
                    AddingState.Success -> navController.navigateUp()
                }
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add Ticket") },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
    ) {
        if (viewModel.isLoading.collectAsState().value)
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(modifier = Modifier.wrapContentSize())
                Text(text = "Please wait..")
            }
        else
            TicketForm(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it),
                viewModel = viewModel,
            )
    }
}

@Composable
private fun TicketForm(
    modifier: Modifier,
    viewModel: AddTicketViewModel,
) {
    val ticketForm = viewModel.addTicketForm.collectAsState().value
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val errorRequired: @Composable (() -> Unit) = {
            Text(text = "Please fill this field", color = MaterialTheme.colorScheme.error)
        }

        DateTimePicker(
            modifier = Modifier.fillMaxWidth(),
            value = ticketForm.date,
            onValueChange = { viewModel.onDateUpdate(it) }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = ticketForm.licenseNumber,
            onValueChange = { viewModel.onLicenseNumberUpdate(it) },
            singleLine = true,
            enabled = true,
            label = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.MailOutline, "")
                    Text("License Number")
                }
            },
            isError = ticketForm.licenseNumberError,
            supportingText = if (ticketForm.licenseNumberError) errorRequired else null,
            keyboardOptions = KeyboardOptions(imeAction = Next),
            keyboardActions = KeyboardActions { focusManager.moveFocus(Down) },
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = ticketForm.driverName,
            onValueChange = { viewModel.onDriverNameUpdate(it) },
            singleLine = true,
            enabled = true,
            label = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Person, "")
                    Text("Driver Name")
                }
            },
            isError = ticketForm.driverNameError,
            supportingText = if (ticketForm.driverNameError) errorRequired else null,
            keyboardOptions = KeyboardOptions(imeAction = Next),
            keyboardActions = KeyboardActions { focusManager.moveFocus(Down) },
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = ticketForm.inboundWeight,
            onValueChange = { viewModel.onInboundWeightUpdate(it) },
            singleLine = true,
            enabled = true,
            label = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(painter = painterResource(id = R.drawable.ic_input), "")
                    Text("Inbound Weight (TON)")
                }
            },
            isError = ticketForm.inboundWeightError,
            supportingText = if (ticketForm.inboundWeightError) errorRequired else null,
            keyboardActions = KeyboardActions { focusManager.moveFocus(Down) },
            keyboardOptions = KeyboardOptions(keyboardType = Number, imeAction = Next),
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = ticketForm.outboundWeight,
            onValueChange = { viewModel.onOutboundWeightUpdate(it) },
            singleLine = true,
            enabled = true,
            label = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(painter = painterResource(id = R.drawable.ic_output), "")
                    Text("Outbound Weight (TON)")
                }
            },
            isError = ticketForm.outboundWeightError,
            supportingText = if (ticketForm.outboundWeightError) errorRequired else null,
            keyboardActions = KeyboardActions { viewModel.addTicket() },
            keyboardOptions = KeyboardOptions(
                keyboardType = Number,
                imeAction = ImeAction.Done
            ),
        )

        Button(modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.addTicket() }) {
            Text("Save")
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    WeighBridgeApplicationTheme {
//        AddEditTicketScreen(listOf("Compose", "Room", "Kotlin"), onSave = {})
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    WeighBridgeApplicationTheme {
//        AddEditTicketScreen(listOf("Compose", "Room", "Kotlin"), onSave = {})
    }
}

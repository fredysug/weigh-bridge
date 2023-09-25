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

import android.template.core.ui.DateTimePicker
import android.template.core.ui.R
import android.template.core.ui.Screens
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter.Field
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter.Field.*
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter.FilterDateRange
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter.FilterDriver
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter.FilterLicenseNumber
import android.template.feature.weighbridge.ui.list.UiState.Success.Sort
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: TicketListViewModel = hiltViewModel()
) {
    var openFilterSection by rememberSaveable { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Weigh Bridge") }, actions = {
            var sortMenuExpandedState by remember { mutableStateOf(false) }
            IconButton(onClick = { sortMenuExpandedState = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = "Open Options"
                )
            }
            SortDropDown(expanded = sortMenuExpandedState,
                availableSort = viewModel.availableSort,
                onSelected = viewModel::sort,
                onDismiss = { sortMenuExpandedState = false })

            IconButton(onClick = { openFilterSection = !openFilterSection }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tune),
                    contentDescription = "Open Filter"
                )
            }

        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate(Screens.AddTicket.route) }) {
            Icon(Icons.Filled.Add, "Add log")
        }
    }) {
        when (val uiState = viewModel.uiState.collectAsState().value) {
            is UiState.Error -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                ) {
                    Text(text = "Oops, Something went wrong")
                }
            }

            UiState.Loading -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                ) {
                    CircularProgressIndicator(modifier = Modifier.wrapContentSize())
                    Text(text = "Please wait..")
                }
            }

            is UiState.Success -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    if (openFilterSection) {
                        FilterArea(
                            availableField = viewModel.availableFilter,
                            selectedFilter = uiState.filter,
                            onFilterChange = viewModel::filter,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (uiState.data.isNotEmpty())
                        Tickets(data = uiState.data, expand = viewModel::onExpand)
                    else Box(
                        modifier = modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) { Text(text = "No Ticket..") }
                }
            }
        }
    }
}

@Composable
private fun SortDropDown(
    availableSort: List<Sort>,
    expanded: Boolean,
    onSelected: (Sort) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        modifier = Modifier.width(width = 150.dp),
        expanded = expanded,
        onDismissRequest = { onDismiss() },
        properties = PopupProperties()
    ) {
        availableSort.forEach { sort ->
            DropdownMenuItem(onClick = {
                onSelected(sort)
                onDismiss()
            }, enabled = true, text = { Text(text = sort.getLabel()) })
        }
    }
}

private fun Sort.getLabel() = when (this) {
    Sort.DateAsc -> "Date asc"
    Sort.DateDesc -> "Date desc"
    Sort.LicenseNumberAsc -> "License number asc"
    Sort.LicenseNumberDesc -> "License number desc"
    Sort.DriverAsc -> "Driver asc"
    Sort.DriverDesc -> "Driver desc"
}

@Composable
fun FilterArea(
    modifier: Modifier = Modifier,
    availableField: List<Field>,
    selectedFilter: Filter?,
    onFilterChange: (Filter) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterDropdown(
            modifier = Modifier.width(150.dp),
            availableField = availableField,
            onFilterChange = onFilterChange,
        )

        Spacer(modifier = Modifier.width(4.dp))

        when (selectedFilter) {
            is FilterDateRange -> {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DateTimePicker(modifier = Modifier.fillMaxWidth(),
                        onValueChange = { onFilterChange(selectedFilter.copy(dateStart = it)) },
                        label = { Text(text = "Start Date") })
                    DateTimePicker(modifier = Modifier.fillMaxWidth(),
                        onValueChange = { onFilterChange(selectedFilter.copy(dateEnd = it)) },
                        label = { Text(text = "End Date") })
                }
            }

            is FilterDriver -> {
                OutlinedTextField(
                    value = selectedFilter.value,
                    onValueChange = { onFilterChange(selectedFilter.copy(value = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }

            is FilterLicenseNumber -> {
                OutlinedTextField(
                    value = selectedFilter.value,
                    onValueChange = { onFilterChange(selectedFilter.copy(value = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }

            null -> Unit
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FilterDropdown(
    modifier: Modifier, availableField: List<Field>, onFilterChange: (Filter) -> Unit
) {
    var filterTypeDropDownExpanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Filter Type") }

    ExposedDropdownMenuBox(
        expanded = filterTypeDropDownExpanded,
        onExpandedChange = { filterTypeDropDownExpanded = !filterTypeDropDownExpanded },
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { TrailingIcon(expanded = filterTypeDropDownExpanded) },
            modifier = modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = filterTypeDropDownExpanded,
            onDismissRequest = { filterTypeDropDownExpanded = false },
        ) {
            availableField.forEach { field ->
                DropdownMenuItem(text = { Text(text = field.getLabel()) }, onClick = {
                    selectedText = field.getLabel()
                    filterTypeDropDownExpanded = false
                    onFilterChange(
                        when (field) {
                            Date -> FilterDateRange(Date(), Date())
                            DriverName -> FilterDriver("")
                            LicenseNumber -> FilterLicenseNumber("")
                        }
                    )
                })
            }
        }
    }
}

fun Field.getLabel() = when (this) {
    Date -> "Date"
    DriverName -> "Driver"
    LicenseNumber -> "License No."
}

@Composable
private fun Tickets(
    modifier: Modifier = Modifier, data: List<TicketViewObject>,
    expand: (Int, Boolean) -> Unit,
) {
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp), content = {
        items(data) { ticket ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expand(ticket.uid, !ticket.expanded) },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.wrapContentWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = ticket.licenseNumber,
                            fontWeight = Bold,
                            modifier = Modifier
                                .border(
                                    border = BorderStroke(
                                        1.dp, colorScheme.outline
                                    ), shape = RoundedCornerShape(4.dp)
                                )
                                .padding(8.dp),
                        )
                        Text(text = "Driver:${ticket.driverName}")
                        Text(text = dateFormatter.format(ticket.date))

                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_weight), ""
                        )
                        Text(text = ticket.netWeight, fontWeight = Bold)
                    }
                }

                if (ticket.expanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(painter = painterResource(id = R.drawable.ic_input), "")
                            Text(text = ticket.inboundWeight)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(painter = painterResource(id = R.drawable.ic_output), "")
                            Text(text = ticket.outboundWeight)
                        }
                    }
                }

                Icon(
                    modifier = Modifier.fillMaxWidth(),
                    imageVector = if (ticket.expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = ""
                )

            }
        }
    })
}

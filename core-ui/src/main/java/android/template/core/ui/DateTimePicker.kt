package android.template.core.ui

import android.template.core.ui.PickerState.Hidden
import android.template.core.ui.PickerState.PickDate
import android.template.core.ui.PickerState.PickTime
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


enum class PickerState { Hidden, PickDate, PickTime }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    modifier: Modifier = Modifier,
    value: Date = Date(),
    onValueChange: (Date) -> Unit,
) {
    val calendar by remember {
        mutableStateOf(Calendar.getInstance().apply { time = value })
    }

    var pickerState by remember { mutableStateOf(Hidden) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true,
    )

    val dateFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    val source = remember { MutableInteractionSource() }
    if (source.collectIsPressedAsState().value) {
        pickerState = PickDate
    }
    OutlinedTextField(
        modifier = modifier,
        interactionSource = source,
        value = dateFormatter.format(calendar.time),
        singleLine = true,
        readOnly = true,
        enabled = true,
        onValueChange = { },
        label = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.DateRange, "")
                Text("Date and Time")
            }
        },
    )

    if (pickerState != Hidden) {
        DatePickerDialog(onDismissRequest = { pickerState = Hidden }, confirmButton = {
            if (pickerState == PickDate) {
                TextButton(onClick = { pickerState = PickTime }) { Text(text = "Next") }
            } else {
                TextButton(onClick = {
                    pickerState = Hidden
                    calendar.apply {
                        this.time = Date(datePickerState.selectedDateMillis!!)
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                    }
                    onValueChange(calendar.time)
                }) {
                    Text(text = "Confirm")
                }
            }
        }, dismissButton = {
            if (pickerState == PickDate) {
                TextButton(onClick = { pickerState = Hidden }) { Text(text = "Cancel") }
            } else {
                TextButton(onClick = { pickerState = PickDate }) { Text(text = "Back") }
            }
        }) {
            Spacer(modifier = Modifier.height(24.dp))
            if (pickerState == PickDate) {
                DatePicker(state = datePickerState, title = null, showModeToggle = false)
            } else if (pickerState == PickTime) {
                TimeInput(state = timePickerState, modifier = Modifier.align(CenterHorizontally))
            }
        }
    }
}

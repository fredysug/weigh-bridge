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

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class AddTicketScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val isLoading = MutableStateFlow<Boolean>(false)
    private val addTicketForm = MutableStateFlow(AddTicketForm.default)
    private val addingResult = MutableSharedFlow<AddingState>()

    private val viewModel = mockk<AddTicketViewModel>(relaxed = true) {
        every { isLoading } returns this@AddTicketScreenTest.isLoading
        every { addTicketForm } returns this@AddTicketScreenTest.addTicketForm
        every { addingResult } returns this@AddTicketScreenTest.addingResult
    }


    @Before
    fun setup() {
        composeTestRule.setContent {
            AddTicketScreen(
                navController = NavHostController(composeTestRule.activity),
                viewModel = viewModel
            )
        }
    }

    @Test
    fun when_is_loading_true_should_show_loading_and_hide_ticket_form() {
        isLoading.value = true

        assertDoesNotExist("Date and Time")
        assertDoesNotExist("License Number")
        assertDoesNotExist("Driver Name")
        assertDoesNotExist("Inbound Weight (TON)")
        assertDoesNotExist("Outbound Weight (TON)")
    }

    @Test
    fun when_is_loading_false_should_show_ticket_form() {
        isLoading.value = false

        assertIsDisplayed("Date and Time")
        assertIsDisplayed("License Number")
        assertIsDisplayed("Driver Name")
        assertIsDisplayed("Inbound Weight (TON)")
        assertIsDisplayed("Outbound Weight (TON)")
    }

    @Test
    fun should_show_ticket_value_in_the_form() {
        val date = Date(1695756317792)
        val licenseNumber = "B123KZL"
        val driverName = "Fredy"
        val inBoundWeight = "1.1"
        val outBoundWeight = "1.2"

        addTicketForm.value =
            AddTicketForm(
                date,
                licenseNumber,
                driverName,
                inBoundWeight,
                outBoundWeight,
                licenseNumberError = false,
                driverNameError = false,
                inboundWeightError = false,
                outboundWeightError = false
            )

//        assertIsDisplayed("27 September 2023, 02:29")
        assertIsDisplayed(licenseNumber)
        assertIsDisplayed(driverName)
        assertIsDisplayed(inBoundWeight)
        assertIsDisplayed(outBoundWeight)
    }

    private fun assertIsDisplayed(withText: String) = composeTestRule
        .onNodeWithText(withText)
        .assertIsDisplayed()

    private fun assertDoesNotExist(withText: String) = composeTestRule
        .onNodeWithText(withText)
        .assertDoesNotExist()

}

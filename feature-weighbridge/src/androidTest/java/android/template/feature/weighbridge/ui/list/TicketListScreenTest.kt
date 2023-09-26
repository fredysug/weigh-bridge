package android.template.feature.weighbridge.ui.list

import android.os.SystemClock
import android.template.feature.weighbridge.ui.add.AddTicketForm
import android.template.feature.weighbridge.ui.add.AddTicketScreen
import android.template.feature.weighbridge.ui.add.AddTicketViewModel
import android.template.feature.weighbridge.ui.add.AddingState
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

class TicketListScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val uiState = MutableStateFlow<UiState>(UiState.Loading)

    private val viewModel = mockk<TicketListViewModel>(relaxed = true) {
        every { uiState } returns this@TicketListScreenTest.uiState
    }

    @Before
    fun setup() {
        composeTestRule.setContent {
            TicketListScreen(
                navController = NavHostController(composeTestRule.activity),
                viewModel = viewModel
            )
        }
    }

    @Test
    fun when_uiState_is_Loading_should_show_loading() {
        uiState.value = UiState.Loading

        assertIsDisplayed("Please wait..")
    }

    @Test
    fun when_uiState_is_Error_should_show_error() {
        uiState.value = UiState.Error(Exception())

        assertIsDisplayed("Oops, Something went wrong")
    }

    @Test
    fun when_is_success_but_tickets_is_empty_should_show_no_data_message() {
        uiState.value = UiState.Success(emptyList())

        assertIsDisplayed("No Ticket..")
    }

    @Test
    fun when_is_success_and_tickets_is_not_empty_should_show_tickets() {
        val ticket1 = TicketViewObject(
            0,
            Date(1695756317792),
            "B2223KK",
            "Fredy",
            "1.2",
            "1.3",
            "0.1",
            false
        )

        val ticket2 = TicketViewObject(
            1,
            Date(1695756317792),
            "A7223KL",
            "Sugiarto",
            "1.0",
            "1.4",
            "0.4",
            true
        )

        uiState.value = UiState.Success(listOf(ticket1, ticket2))

        assertIsDisplayed(ticket1.licenseNumber)
        assertIsDisplayed("Driver: ${ticket1.driverName}")
        assertIsDisplayed(ticket1.netWeight)
        assertDoesNotExist(ticket1.inboundWeight)
        assertDoesNotExist(ticket1.outboundWeight)

        assertIsDisplayed(ticket2.licenseNumber)
        assertIsDisplayed("Driver: ${ticket2.driverName}")
        assertIsDisplayed(ticket2.netWeight)
        assertIsDisplayed(ticket2.inboundWeight)
        assertIsDisplayed(ticket2.outboundWeight)
    }

    private fun assertIsDisplayed(withText: String) = composeTestRule
        .onNodeWithText(withText)
        .assertIsDisplayed()

    private fun assertDoesNotExist(withText: String) = composeTestRule
        .onNodeWithText(withText)
        .assertDoesNotExist()
}
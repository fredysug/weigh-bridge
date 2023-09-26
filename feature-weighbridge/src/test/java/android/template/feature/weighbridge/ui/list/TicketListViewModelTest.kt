package android.template.feature.weighbridge.ui.list

import android.template.core.data.Ticket
import android.template.core.data.TicketRepository
import android.template.core.testing.CoroutinesTestRule
import android.template.feature.weighbridge.ui.add.AddTicketViewModel
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter.FilterDateRange
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter.FilterDriver
import android.template.feature.weighbridge.ui.list.UiState.Success.Filter.FilterLicenseNumber
import android.template.feature.weighbridge.ui.list.UiState.Success.Sort
import android.template.feature.weighbridge.ui.list.UiState.Success.Sort.DateAsc
import android.template.feature.weighbridge.ui.list.UiState.Success.Sort.DateDesc
import android.template.feature.weighbridge.ui.list.UiState.Success.Sort.DriverAsc
import android.template.feature.weighbridge.ui.list.UiState.Success.Sort.DriverDesc
import android.template.feature.weighbridge.ui.list.UiState.Success.Sort.LicenseNumberAsc
import android.template.feature.weighbridge.ui.list.UiState.Success.Sort.LicenseNumberDesc
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Exception
import java.util.Date


@OptIn(ExperimentalCoroutinesApi::class)
class TicketListViewModelTest {

    @get:Rule
    val rule = CoroutinesTestRule()

    private lateinit var repository: TicketRepository
    private lateinit var viewModel: TicketListViewModel

    val ticket1 = Ticket(
        0,
        Date(1),
        "B2223KK",
        "Fredy",
        1.2,
        1.3,
    )

    val ticket2 = Ticket(
        1,
        Date(1),
        "A7223KL",
        "Fredy",
        1.0,
        1.4,
    )

    val ticket3 = Ticket(
        3,
        Date(5),
        "A7223KL",
        "Budi",
        1.0,
        1.4,
    )

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) {
            every { tickets } returns flowOf(listOf(ticket1, ticket2, ticket3))
        }
        viewModel = TicketListViewModel(repository)
    }

    @Test
    fun `should start with uiState loading`() {
        runBlocking {
            repository = mockk(relaxed = true) {
                every { tickets } returns flow { }
            }

            viewModel = TicketListViewModel(repository)

            assertEquals(UiState.Loading, viewModel.uiState.value)
        }
    }

    @Test
    fun `when repository return data successfully, should update uiState to success`() {
        runBlocking {
            repository = mockk(relaxed = true) {
                every { tickets } returns flowOf(listOf(ticket1, ticket2, ticket3))
            }

            viewModel = TicketListViewModel(repository)

            assertEquals(
                UiState.Success(
                    data = listOf(
                        TicketViewObject(ticket1, false),
                        TicketViewObject(ticket2, false),
                        TicketViewObject(ticket3, false),
                    )
                ), viewModel.uiState.value
            )
        }
    }

    @Test
    fun `when repository return error, should update uiState to error`() {
        runBlocking {
            repository = mockk(relaxed = true) {
                every { tickets } returns flow { throw Exception() }
            }

            viewModel = TicketListViewModel(repository)

            assertTrue(viewModel.uiState.value is UiState.Error)
        }
    }

    @Test
    fun `when filter, should filter tickets based on filter area`() {
        fun test(filter: Filter, remainingTickets: List<TicketViewObject>) {
            every { repository.tickets } returns flowOf(listOf(ticket1, ticket2, ticket3))

            viewModel.filter(filter)

            assertEquals(
                UiState.Success(data = remainingTickets, filter = filter), viewModel.uiState.value
            )
        }

        runBlocking {
            val ticketVO1 = TicketViewObject(ticket1, false)
            val ticketVO2 = TicketViewObject(ticket2, false)
            val ticketVO3 = TicketViewObject(ticket3, false)

            test(FilterDriver(""), listOf(ticketVO1, ticketVO2, ticketVO3))
            test(FilterDriver("Fre"), listOf(ticketVO1, ticketVO2))

            test(FilterLicenseNumber("B"), listOf(ticketVO1))
            test(FilterLicenseNumber("2"), listOf(ticketVO1, ticketVO2, ticketVO3))

            test(FilterDateRange(Date(0), Date(4)), listOf(ticketVO1, ticketVO2))
            test(FilterDateRange(Date(3), Date(6)), listOf(ticketVO3))
        }
    }

    @Test
    fun `when sort, should sort tickets based on sort options`() {
        fun test(sort: Sort, orderOfTickets: List<TicketViewObject>) {
            every { repository.tickets } returns flowOf(listOf(ticket1, ticket2, ticket3))

            viewModel.sort(sort)

            assertEquals(
                UiState.Success(data = orderOfTickets, sort = sort), viewModel.uiState.value
            )
        }

        runBlocking {
            val ticketVO1 = TicketViewObject(ticket1, false)
            val ticketVO2 = TicketViewObject(ticket2, false)
            val ticketVO3 = TicketViewObject(ticket3, false)

            test(DateAsc, listOf(ticketVO1, ticketVO2, ticketVO3))
            test(DateDesc, listOf(ticketVO3, ticketVO1, ticketVO2))

            test(LicenseNumberAsc, listOf(ticketVO2, ticketVO3, ticketVO1))
            test(LicenseNumberDesc, listOf(ticketVO1, ticketVO2, ticketVO3))

            test(DriverAsc, listOf(ticketVO3, ticketVO1, ticketVO2))
            test(DriverDesc, listOf(ticketVO1, ticketVO2, ticketVO3))
        }
    }
}

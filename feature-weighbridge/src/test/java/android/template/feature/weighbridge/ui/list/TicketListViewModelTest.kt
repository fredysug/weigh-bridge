package android.template.feature.weighbridge.ui.list

import android.template.core.data.Ticket
import android.template.core.data.TicketRepository
import android.template.feature.weighbridge.ui.add.AddTicketViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.Date


class TicketListViewModelTest {
    @Test
    fun uiState_initiallyLoading() = runTest {
        val viewModel = AddTicketViewModel(FakeTicketRepository())
//        Assert.assertEquals(viewModel.uiState.first(), MyModelUiState.Loading)
    }

    @Test
    fun uiState_onItemSaved_isDisplayed() = runTest {
        val viewModel = AddTicketViewModel(FakeTicketRepository())
//        Assert.assertEquals(viewModel.uiState.first(), MyModelUiState.Loading)
    }

    @Test
    fun testSort() {
        val list = listOf(
            Ticket(1, Date(1695643100439), "B", "Fredy", 1.0, 1.0),
            Ticket(2, Date(1695643100410), "A", "Fredy", 1.0, 1.0),
            Ticket(3, Date(1695643100410), "A", "Melita", 1.0, 1.0),
        )

        val sorted = list
            .sortedWith(compareBy({ it.date }, { it.licenseNumber }))

        println(sorted)
    }
}

private class FakeTicketRepository : TicketRepository {

    private val data = mutableListOf<Ticket>()

    override val tickets: Flow<List<Ticket>>
        get() = flow { emit(data.toList()) }

    override suspend fun add(
        date: Date,
        licenseNumber: String,
        driverName: String,
        inboundWeight: Double,
        outboundWeight: Double
    ) {
        data.add(
            0, Ticket(
                uid = 1,
                date = date,
                licenseNumber = licenseNumber,
                driverName = driverName,
                inboundWeight = inboundWeight,
                outboundWeight = outboundWeight,
            )
        )
    }
}

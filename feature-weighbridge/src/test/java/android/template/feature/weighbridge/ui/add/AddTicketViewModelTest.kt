package android.template.feature.weighbridge.ui.add

import android.template.core.data.repository.TicketRepository
import android.template.core.testing.CoroutinesTestRule
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

@ExperimentalCoroutinesApi
class AddTicketViewModelTest {

    @get:Rule
    val rule = CoroutinesTestRule()

    private lateinit var repository: TicketRepository
    private lateinit var viewModel: AddTicketViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        viewModel = AddTicketViewModel(repository)
    }

    @Test
    fun `when date update, should update the value on the ticket form`() {
        runBlocking {
            val date = Date()

            viewModel.onDateUpdate(date)

            assertEquals(date, viewModel.addTicketForm.value.date)
        }
    }

    @Test
    fun `when license number update with new value, should update the value on the ticket form and should not error`() {
        runBlocking {
            val licenseNumber = "B3321KJL"

            viewModel.onLicenseNumberUpdate(licenseNumber)

            assertEquals(licenseNumber, viewModel.addTicketForm.value.licenseNumber)
            assertFalse(viewModel.addTicketForm.value.licenseNumberError)
        }
    }

    @Test
    fun `when license number update with empty string, should update the value on the ticket form and error`() {
        runBlocking {
            val licenseNumber = ""

            viewModel.onLicenseNumberUpdate(licenseNumber)

            assertEquals(licenseNumber, viewModel.addTicketForm.value.licenseNumber)
            assertTrue(viewModel.addTicketForm.value.licenseNumberError)
        }
    }

    @Test
    fun `when driver name update with new value, should update the value on the ticket form and should not error`() {
        runBlocking {
            val driverName = "B3321KJL"

            viewModel.onDriverNameUpdate(driverName)

            assertEquals(driverName, viewModel.addTicketForm.value.driverName)
            assertFalse(viewModel.addTicketForm.value.driverNameError)
        }
    }

    @Test
    fun `when driver name update with empty string, should update the value on the ticket form and error`() {
        runBlocking {
            val driverName = ""

            viewModel.onDriverNameUpdate(driverName)

            assertEquals(driverName, viewModel.addTicketForm.value.driverName)
            assertTrue(viewModel.addTicketForm.value.driverNameError)
        }
    }

    @Test
    fun `when inbound weight update with new value, should update the value on the ticket form and should not error`() {
        runBlocking {
            val inboundWeight = "1.23"

            viewModel.onInboundWeightUpdate(inboundWeight)

            assertEquals(inboundWeight, viewModel.addTicketForm.value.inboundWeight)
            assertFalse(viewModel.addTicketForm.value.inboundWeightError)
        }
    }

    @Test
    fun `when inbound weight new value is not digit, should not update the value on the ticket form`() {
        runBlocking {
            val inboundWeight = "BBB"

            viewModel.onInboundWeightUpdate(inboundWeight)

            assertEquals("", viewModel.addTicketForm.value.inboundWeight)
        }
    }

    @Test
    fun `when inbound weight update with empty string, should update the value on the ticket form and error`() {
        runBlocking {
            val inboundWeight = ""

            viewModel.onInboundWeightUpdate(inboundWeight)

            assertEquals(inboundWeight, viewModel.addTicketForm.value.inboundWeight)
            assertTrue(viewModel.addTicketForm.value.inboundWeightError)
        }
    }

    @Test
    fun `when outbound weight update with new value, should update the value on the ticket form and should not error`() {
        runBlocking {
            val outboundWeight = "1.23"

            viewModel.onOutboundWeightUpdate(outboundWeight)

            assertEquals(outboundWeight, viewModel.addTicketForm.value.outboundWeight)
            assertFalse(viewModel.addTicketForm.value.outboundWeightError)
        }
    }

    @Test
    fun `when outbound weight new value is not digit, should not update the value on the ticket form`() {
        runBlocking {
            val outboundWeight = "BBB"

            viewModel.onOutboundWeightUpdate(outboundWeight)

            assertEquals("", viewModel.addTicketForm.value.outboundWeight)
        }
    }

    @Test
    fun `when outbound weight update with empty string, should update the value on the ticket form and error`() {
        runBlocking {
            val outboundWeight = ""

            viewModel.onOutboundWeightUpdate(outboundWeight)

            assertEquals(outboundWeight, viewModel.addTicketForm.value.outboundWeight)
            assertTrue(viewModel.addTicketForm.value.outboundWeightError)
        }
    }

    @Test
    fun `when add ticket and form is empty, should not add the ticket to the repo and error`() {
        viewModel.addTicket()

        assertTrue(viewModel.addTicketForm.value.licenseNumberError)
        assertTrue(viewModel.addTicketForm.value.driverNameError)
        assertTrue(viewModel.addTicketForm.value.inboundWeightError)
        assertTrue(viewModel.addTicketForm.value.outboundWeightError)

        coVerify(exactly = 0) { repository.add(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `when add ticket and form is not empty, should add the ticket to the repo and no error`() {
        val date = Date()
        val licenseNumber = "B3321KJL"
        val driverName = "B3321KJL"
        val inboundWeight = "1.23"
        val outboundWeight = "1.24"

        viewModel.onDateUpdate(date)
        viewModel.onDriverNameUpdate(driverName)
        viewModel.onLicenseNumberUpdate(licenseNumber)
        viewModel.onInboundWeightUpdate(inboundWeight)
        viewModel.onOutboundWeightUpdate(outboundWeight)

        viewModel.addTicket()

        assertEquals(licenseNumber, viewModel.addTicketForm.value.licenseNumber)
        assertEquals(driverName, viewModel.addTicketForm.value.driverName)
        assertEquals(inboundWeight, viewModel.addTicketForm.value.inboundWeight)
        assertEquals(outboundWeight, viewModel.addTicketForm.value.outboundWeight)

        assertFalse(viewModel.addTicketForm.value.licenseNumberError)
        assertFalse(viewModel.addTicketForm.value.driverNameError)
        assertFalse(viewModel.addTicketForm.value.inboundWeightError)
        assertFalse(viewModel.addTicketForm.value.outboundWeightError)

        coVerify(exactly = 1) {
            repository.add(
                any(),
                date,
                licenseNumber,
                driverName,
                inboundWeight.toDouble(),
                outboundWeight.toDouble()
            )
        }
    }
}
package android.template.feature.weighbridge.ui.edit

import android.template.core.data.Ticket
import android.template.core.data.TicketRepository
import android.template.core.testing.CoroutinesTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

class EditTicketViewModelTest {

    @get:Rule
    val rule = CoroutinesTestRule()

    private val uid = 0
    private val date = Date()
    private val licenseNumber = "B3321KJL"
    private val driverName = "B3321KJL"
    private val inboundWeight = 1.23
    private val outboundWeight = 1.24

    private lateinit var repository: TicketRepository
    private lateinit var viewModel: EditTicketViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) {
            coEvery { getTicket(any()) } returns Ticket(
                uid,
                date,
                licenseNumber,
                driverName,
                inboundWeight,
                outboundWeight
            )
        }
        viewModel = EditTicketViewModel(repository)
    }

    @Test
    fun `when inbound weight update with new value, should update the value on the ticket form and should not error`() {
        runBlocking {
            val inboundWeight = "1.23"

            viewModel.onInboundWeightUpdate(inboundWeight)

            Assert.assertEquals(inboundWeight, viewModel.editTicketForm.value.inboundWeight)
            Assert.assertFalse(viewModel.editTicketForm.value.inboundWeightError)
        }
    }

    @Test
    fun `when inbound weight new value is not digit, should not update the value on the ticket form`() {
        runBlocking {
            val inboundWeight = "BBB"

            viewModel.onInboundWeightUpdate(inboundWeight)

            Assert.assertEquals("", viewModel.editTicketForm.value.inboundWeight)
        }
    }

    @Test
    fun `when inbound weight update with empty string, should update the value on the ticket form and error`() {
        runBlocking {
            val inboundWeight = ""

            viewModel.onInboundWeightUpdate(inboundWeight)

            Assert.assertEquals(inboundWeight, viewModel.editTicketForm.value.inboundWeight)
            Assert.assertTrue(viewModel.editTicketForm.value.inboundWeightError)
        }
    }

    @Test
    fun `when outbound weight update with new value, should update the value on the ticket form and should not error`() {
        runBlocking {
            val outboundWeight = "1.23"

            viewModel.onOutboundWeightUpdate(outboundWeight)

            Assert.assertEquals(outboundWeight, viewModel.editTicketForm.value.outboundWeight)
            Assert.assertFalse(viewModel.editTicketForm.value.outboundWeightError)
        }
    }

    @Test
    fun `when outbound weight new value is not digit, should not update the value on the ticket form`() {
        runBlocking {
            val outboundWeight = "BBB"

            viewModel.onOutboundWeightUpdate(outboundWeight)

            Assert.assertEquals("", viewModel.editTicketForm.value.outboundWeight)
        }
    }

    @Test
    fun `when outbound weight update with empty string, should update the value on the ticket form and error`() {
        runBlocking {
            val outboundWeight = ""

            viewModel.onOutboundWeightUpdate(outboundWeight)

            Assert.assertEquals(outboundWeight, viewModel.editTicketForm.value.outboundWeight)
            Assert.assertTrue(viewModel.editTicketForm.value.outboundWeightError)
        }
    }

    @Test
    fun `when edit ticket and form is empty, should not edit the ticket to the repo and error`() {
        viewModel.editTicket()

        Assert.assertTrue(viewModel.editTicketForm.value.inboundWeightError)
        Assert.assertTrue(viewModel.editTicketForm.value.outboundWeightError)

        coVerify(exactly = 0) { repository.updateTicket(any()) }
    }

    @Test
    fun `when edit ticket and form is not empty, should edit the ticket to the repo and no error`() {
        viewModel.load(uid)

        viewModel.onInboundWeightUpdate(inboundWeight.toString())
        viewModel.onOutboundWeightUpdate(outboundWeight.toString())

        viewModel.editTicket()

        Assert.assertFalse(viewModel.editTicketForm.value.inboundWeightError)
        Assert.assertFalse(viewModel.editTicketForm.value.outboundWeightError)

        coVerify(exactly = 1) {
            repository.updateTicket(
                Ticket(
                    uid,
                    date,
                    licenseNumber,
                    driverName,
                    inboundWeight.toDouble(),
                    outboundWeight.toDouble()
                )
            )
        }
    }
}
package android.template.data

import android.template.core.data.Ticket
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.Date
import kotlin.random.Random

class TicketTest {
    @Test
    fun `weight id should be outbound minus inbound weight`() {
        val inboundWeight = Random.nextDouble()
        val outBoundWeight = Random.nextDouble()

        val ticket = generateTicket(inboundWeight = inboundWeight, outboundWeight = outBoundWeight)

        assertEquals(outBoundWeight - inboundWeight, ticket.netWeight)
    }

    companion object {
        fun generateTicket(
            uid: Int = 0,
            date: Date = Date(1695643100439),
            licenseNumber: String = "license number",
            driverName: String = "driver name",
            inboundWeight: Double = 0.0,
            outboundWeight: Double = 0.0,
        ) = Ticket(
            uid = uid,
            date = date,
            licenseNumber = licenseNumber,
            driverName = driverName,
            inboundWeight = inboundWeight,
            outboundWeight = outboundWeight,
        )
    }
}

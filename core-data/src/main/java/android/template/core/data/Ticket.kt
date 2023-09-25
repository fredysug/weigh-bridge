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

package android.template.core.data

import android.template.core.database.Ticket
import java.util.Date

data class Ticket(
    val uid: Int,
    val date: Date,
    val licenseNumber: String,
    val driverName: String,
    val inboundWeight: Double,
    val outboundWeight: Double,
){
    val netWeight = outboundWeight - inboundWeight

    constructor(databaseTicket: Ticket) : this(
        uid = databaseTicket.uid,
        date = databaseTicket.date,
        licenseNumber = databaseTicket.licenseNumber,
        driverName = databaseTicket.driverName,
        inboundWeight = databaseTicket.inboundWeight,
        outboundWeight = databaseTicket.outboundWeight,
    )
}

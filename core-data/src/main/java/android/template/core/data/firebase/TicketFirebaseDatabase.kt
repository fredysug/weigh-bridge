package android.template.core.data.firebase


//class TicketFirebaseDatabaseImpl(
//    private val database: FirebaseDatabase
//) : TicketRepository {
//
//
//    fun set(ticket: Ticket) {
//        val myRef = database.getReference("ticket/${ticket.uid}")
//
//        val gson = Gson()
//        val ticketString = gson.toJson(ticket)
//
//        myRef.setValue(ticket)
//        myRef.push()
//
//        Log.e("ketai", "ke set nih harusnya ${myRef.parent?.parent?.parent?.path}")
//        Log.e("ketai", "ga tau apaan ${myRef.path}")
//
////        Log.e("Ketai", "kekekeekek $ticketString")
////        val newTicker = gson.fromJson(ticketString, Ticket::class.java)
////        Log.e("Ketai", "newnenew $newTicker")
////
////        myRef.setValue(ticketString)
////        myRef.push()
//    }
//
//    fun get(): Flow<Ticket> {
//        val myRef = database.getReference("ticket")
//        Log.e("ketai", "mencoba gett dari ${myRef}")
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                Log.e("ketai", "akan dapat data")
//                val value = dataSnapshot.getValue<List<Ticket>>()
//                Log.e("ketai", "dapet data $value")
//
////                    send()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w("ketai", "Failed to read value.", error.toException())
//            }
//        })
//        return callbackFlow {
//            Ticket(0, Date(), "", "", 1.1, 1.1)
//        }
//
//    }
//
//    override val tickets: Flow<List<Ticket>> = callbackFlow {
//        val reference = database.getReference("ticker")
//        reference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val value = dataSnapshot.getValue<List<Ticket>>()
//                this@callbackFlow.send(value ?: emptyList())
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Logger.e("Failed to read value from firebase database ${error.toException()}")
//                this@callbackFlow.send(emptyList())
//            }
//        })
//    }
//
//
//    override suspend fun getTicket(uid: Int): Ticket = suspendCoroutine { emitter ->
//        database.getReference("ticket").child(uid.toString()).get()
//            .addOnSuccessListener {
//                emitter.resume(it.value!!)
//            }.addOnFailureListener {
//                emitter.resumeWithException(it.cause)
//            }
//    }
//
//    override suspend fun updateTicket(ticket: Ticket) {
//        val reference = database.getReference("ticket/${ticket.uid}")
//        reference.setValue(ticket)
//        reference.push()
//    }
//
//    override suspend fun add(
//        uid: Int,
//        date: Date,
//        licenseNumber: String,
//        driverName: String,
//        inboundWeight: Double,
//        outboundWeight: Double
//    ): Long {
//        updateTicket(
//            Ticket(
//                uid = uid,
//                licenseNumber = licenseNumber,
//                driverName = driverName,
//                inboundWeight = inboundWeight,
//                outboundWeight = outboundWeight,
//                date = date,
//            )
//        )
//        return uid.toLong()
//    }
//}
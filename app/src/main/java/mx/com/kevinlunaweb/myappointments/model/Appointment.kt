package mx.com.kevinlunaweb.myappointments.model

data class Appointment(
    val id: Int,
    val doctorName: String,
    val scheduledDate: String,
    val scheduledTime: String
)

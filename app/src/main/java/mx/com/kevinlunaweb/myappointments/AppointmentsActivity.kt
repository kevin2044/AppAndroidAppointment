package mx.com.kevinlunaweb.myappointments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_appointments.*
import mx.com.kevinlunaweb.myappointments.model.Appointment
import java.util.ArrayList

class AppointmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        val appointments = ArrayList<Appointment>()
        appointments.add(
            Appointment(1, "Medico Test", "12/12/2020", "3:00 PM")
        )
        appointments.add(
            Appointment(2, "Medico 2", "13/12/2020", "3:30 PM")
        )
        appointments.add(
            Appointment(3, "Medico 3", "14/12/2020", "4:00 PM")
        )

        rvAppointments.layoutManager = LinearLayoutManager(this) //GridLayoutManager
        rvAppointments.adapter = AppointmentAdapter(appointments)
    }
}

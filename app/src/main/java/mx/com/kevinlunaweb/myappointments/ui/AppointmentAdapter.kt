package mx.com.kevinlunaweb.myappointments.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_appointment.view.*
import mx.com.kevinlunaweb.myappointments.R
import mx.com.kevinlunaweb.myappointments.model.Appointment
import java.util.ArrayList

class AppointmentAdapter(val appointments: ArrayList<Appointment>): RecyclerView.Adapter<AppointmentAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(appointment: Appointment){
            with(itemView){
                tvAppointmentId.text = itemView.context.getString(R.string.item_appointment_id, appointment.id)
                tvDoctorName.text = appointment.doctorName
                tvScheduledDate.text = itemView.context.getString(R.string.item_appointment_date, appointment.scheduledDate)
                tvScheduledTime.text = itemView.context.getString(R.string.item_appointment_time, appointment.scheduledTime)
            }
        }
    }

    // Inflar los items XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_appointment,
                parent,
                false
            )
        )
    }
    // Devolver la cantidad de elementos
    override fun getItemCount(): Int {
        return appointments.size
    }
    // asociar la data al XML
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
    }
}
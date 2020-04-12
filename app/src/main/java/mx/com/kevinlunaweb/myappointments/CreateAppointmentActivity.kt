package mx.com.kevinlunaweb.myappointments

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_appointment.*
import java.util.*

class CreateAppointmentActivity : AppCompatActivity() {

    private var selectedCalendar = Calendar.getInstance()
    private var selectedRadioButton: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        btnNext.setOnClickListener {
            cvStepOne.visibility = View.GONE
            cvStepTwo.visibility = View.VISIBLE
        }

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "Cita Registrada Correctamente", Toast.LENGTH_LONG).show()
            finish()
        }

        val optionsSpecialty = arrayOf("Specialty A", "Specialty B", "Specialty C")

        spinnerSpecialties.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionsSpecialty)

        val optionsDoctors = arrayOf("Doctor A", "Doctor B", "Doctor C")

        spinnerDoctors.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionsDoctors)
    }

    fun onClickScheduledDate(v: View?){

        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)

        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        println("este el numero de mes $month")

        val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
            //Toast.makeText(this, "$y-$m-$d", Toast.LENGTH_LONG).show()
            selectedCalendar.set(y, m, d)
            etScheduledDate.setText(resources.getString(R.string.date_format,
                    y,
                    m.twoDigits(),
                    d.twoDigits()
                )
            )
            displayRadioButtons()
        }
        // min date
        // max date
        //set limits
        val datePickerDialog = DatePickerDialog(this,listener, year, month, dayOfMonth )
        val datePicker = datePickerDialog.datePicker
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        datePicker.minDate = calendar.timeInMillis  // 1 now
        calendar.add(Calendar.DAY_OF_MONTH, 29)
        datePicker.maxDate = calendar.timeInMillis //30+ now

        //show dialog
        datePickerDialog.show()
    }

    private fun displayRadioButtons(){
        //radioGroup.clearCheck()
        selectedRadioButton = null
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()



        val hours = arrayOf("3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM")
        var goToLeft  = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it
            radioButton.setOnClickListener {view ->
                selectedRadioButton?.isChecked = false
                selectedRadioButton = view as RadioButton?
                selectedRadioButton?.isChecked = true
            }
            if(goToLeft){
                radioGroupLeft.addView(radioButton)
            }else{
                radioGroupRight.addView(radioButton)
            }
            goToLeft = !goToLeft
        }

    }

    private fun Int.twoDigits() = if (this>=10) this.toString() else "0$this"

}

package mx.com.kevinlunaweb.myappointments.ui

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_appointment.*
import kotlinx.android.synthetic.main.card_view_step_one.*
import kotlinx.android.synthetic.main.card_view_step_three.*
import kotlinx.android.synthetic.main.card_view_step_two.*
import mx.com.kevinlunaweb.myappointments.R
import mx.com.kevinlunaweb.myappointments.io.ApiService
import mx.com.kevinlunaweb.myappointments.model.Specialty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CreateAppointmentActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }
    private var selectedCalendar = Calendar.getInstance()
    private var selectedTimeRadioButton: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        btnNext.setOnClickListener {
            if(etDescription.text.toString().length < 3){
                etDescription.error = "La descripción es demasiado corta"
            }else{
                cvStepOne.visibility = View.GONE
                cvStepTwo.visibility = View.VISIBLE
            }
        }

        btnNexttwo.setOnClickListener {
            when {
                etScheduledDate.text.toString().isEmpty() -> {
                    etScheduledDate.error = "Es necesario seleccionar una fecha"
                }
                selectedTimeRadioButton == null -> {
                    Snackbar.make(createAppointmentLinearLayout,"Es necesario definir una hora para la cita", Snackbar.LENGTH_LONG).show()
                }
                else -> {
                    showAppointmentDataToConfirm()
                    cvStepTwo.visibility = View.GONE
                    cvStepThree.visibility = View.VISIBLE
                }
            }
        }

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "Cita Registrada Correctamente", Toast.LENGTH_LONG).show()
            finish()
        }

        loadSpecialties()

        val optionsDoctors = arrayOf("Doctor A", "Doctor B", "Doctor C")

        spinnerDoctors.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionsDoctors)
    }

    private fun loadSpecialties() {
        val call = apiService.getSpecialties()
        call.enqueue(object: retrofit2.Callback<ArrayList<Specialty>>{
            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, "Ocurrió un problema al cargar las especialidades",Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onResponse(call: Call<ArrayList<Specialty>>, response: Response<ArrayList<Specialty>>) {
                if(response.isSuccessful){ // 200...300
                    val specialties = response.body()
                    val specialtyOptions = ArrayList<String>()
                    specialties?.forEach {
                        specialtyOptions.add(it.name)
                    }

                    spinnerSpecialties.adapter = ArrayAdapter<String>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1, specialtyOptions)
                }
            }
        })

    }

    private fun showAppointmentDataToConfirm() {
        tvConfirmDescription.text = etDescription.text.toString()
        tvConfirmSpecialty.text = spinnerSpecialties.selectedItem.toString()
        val selectedRadioBtnId = radioGroupType.checkedRadioButtonId
        val selectedRadioType = radioGroupType.findViewById<RadioButton>(selectedRadioBtnId)
        tvConfirmType.text = selectedRadioType.text.toString()
        tvConfirmDoctorName.text = spinnerDoctors.selectedItem.toString()
        tvConfirmDate.text = etScheduledDate.text.toString()
        tvConfirmTime.text = selectedTimeRadioButton?.text.toString()
    }

    fun onClickScheduledDate(v: View?){

        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)

        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        println("este el numero de mes $month")

        val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
            //Toast.makeText(this, "$y-$m-$d", Toast.LENGTH_LONG).show()
            selectedCalendar.set(y, m, d)
            etScheduledDate.setText(resources.getString(
                R.string.date_format,
                    y,
                    m.twoDigits(),
                    d.twoDigits()
                )
            )
            etScheduledDate.error = null
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
        selectedTimeRadioButton = null
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()



        val hours = arrayOf("3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM")
        var goToLeft  = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it
            radioButton.setOnClickListener {view ->
                selectedTimeRadioButton?.isChecked = false
                selectedTimeRadioButton = view as RadioButton?
                selectedTimeRadioButton?.isChecked = true
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

    override fun onBackPressed() {
        when {
            cvStepThree.visibility == View.VISIBLE -> {
                cvStepThree.visibility = View.GONE
                cvStepTwo.visibility = View.VISIBLE
            }
            cvStepTwo.visibility == View.VISIBLE -> {
                cvStepTwo.visibility = View.GONE
                cvStepOne.visibility = View.VISIBLE
            }
            cvStepOne.visibility == View.VISIBLE -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("¿Estás seguro que deseas salir?")
                builder.setMessage("Siabandonas el registro, los datos que habias ingresado se perderán.")
                builder.setPositiveButton("Si, salir") { _, _ ->
                    finish()
                }

                builder.setNegativeButton("Continuar registro") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            }
        }
    }
}

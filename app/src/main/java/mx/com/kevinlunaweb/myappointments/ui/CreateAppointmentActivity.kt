package mx.com.kevinlunaweb.myappointments.ui

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
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
import mx.com.kevinlunaweb.myappointments.model.Doctor
import mx.com.kevinlunaweb.myappointments.model.Schedule
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
        listenSpecialtyChanges()
        listenDoctorAndDateChanges()
    }

    private fun listenDoctorAndDateChanges() {
        //doctors
        spinnerDoctors.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val doctor = adapter?.getItemAtPosition(position) as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
                //Toast.makeText(this@CreateAppointmentActivity, "id: ${specialty.id}", Toast.LENGTH_SHORT).show()
            }
        }
        //scheduled date
        etScheduledDate.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val doctor = spinnerDoctors.selectedItem as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
            }

        })
    }

    private fun loadHours(doctorId: Int, date: String) {
        val call = apiService.getHours(doctorId, date)
        call.enqueue(object: Callback<Schedule> {
            override fun onFailure(call: Call<Schedule>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, "No se han podido cargar las horas", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                if(response.isSuccessful){ // 200...300
                    val schedule = response.body()
                    Toast.makeText(this@CreateAppointmentActivity, "morning: ${schedule?.morning?.size} , afternoon: ${schedule?.afternoon?.size}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun listenSpecialtyChanges() {
        spinnerSpecialties.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val specialty = adapter?.getItemAtPosition(position) as Specialty
                loadDoctors(specialty.id)
                //Toast.makeText(this@CreateAppointmentActivity, "id: ${specialty.id}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSpecialties() {
        val call = apiService.getSpecialties()
        call.enqueue(object: retrofit2.Callback<ArrayList<Specialty>>{
            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, "Ocurrió un problema al cargar las especialidades",Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ArrayList<Specialty>>, response: Response<ArrayList<Specialty>>) {
                if(response.isSuccessful){ // 200...300
                    response.body()?.let {
                        val specialties = it.toMutableList()
                        spinnerSpecialties.adapter = ArrayAdapter<Specialty>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1, specialties)
                    }
                }
            }
        })
    }

    private fun loadDoctors(specialtyId: Int){
        val call = apiService.getDoctors(specialtyId)
        call.enqueue(object: Callback<ArrayList<Doctor>>{
            override fun onFailure(call: Call<ArrayList<Doctor>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, "Ocurrió un problema al cargar los doctores",Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onResponse(call: Call<ArrayList<Doctor>>, response: Response<ArrayList<Doctor>>) {
                if(response.isSuccessful){ // 200...300
                    response.body()?.let {
                        val doctors = it.toMutableList()
                        spinnerDoctors.adapter = ArrayAdapter<Doctor>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1, doctors)
                    }
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
                    (m+1).twoDigits(),
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

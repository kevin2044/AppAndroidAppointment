package mx.com.kevinlunaweb.myappointments.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import mx.com.kevinlunaweb.myappointments.R

class MainActivity : AppCompatActivity() {

    private val snackBar by lazy {
        Snackbar.make(mainLayout,
            R.string.press_back_again, Snackbar.LENGTH_LONG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE);
        val session = preferences.getBoolean("session", false)

        if(session){
            goToMenuActivity()
        }

        createHere.setOnClickListener {
            Toast.makeText(this,"Que onda",Toast.LENGTH_LONG).show()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            // validates
            createSessionPreference()
            goToMenuActivity()
        }
    }

    private fun createSessionPreference() {
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", true)
        editor.apply()
    }

    private fun goToMenuActivity(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if(snackBar.isShown){
            super.onBackPressed()
        }else{
            snackBar.show()
        }
    }
}

package mx.com.kevinlunaweb.myappointments.io

import mx.com.kevinlunaweb.myappointments.model.Specialty
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.ArrayList

interface ApiService {
    @GET("specialties/")
    abstract fun getSpecialties(): Call<ArrayList<Specialty>>

    companion object Factory {
        private const val BASE_URL = "https://kevinlunaweb.com.mx/api/"

        fun create(): ApiService{
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
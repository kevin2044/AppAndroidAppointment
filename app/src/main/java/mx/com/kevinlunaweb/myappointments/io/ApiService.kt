package mx.com.kevinlunaweb.myappointments.io

import mx.com.kevinlunaweb.myappointments.model.Doctor
import mx.com.kevinlunaweb.myappointments.model.Schedule
import mx.com.kevinlunaweb.myappointments.model.Specialty
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*


interface ApiService {
    @GET("specialties/")
    fun getSpecialties(): Call<ArrayList<Specialty>>

    @GET("specialties/{specialty}/doctors/")
    fun getDoctors(@Path("specialty") specialtyId: Int): Call<ArrayList<Doctor>>

    @GET("schedule/hours/")
    fun getHours(@Query("doctor_id") doctorId: Int, @Query("date") date: String): Call<Schedule>

    companion object Factory {
        private const val BASE_URL = "https://kevinlunaweb.com.mx/api/"

        fun create(): ApiService{
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
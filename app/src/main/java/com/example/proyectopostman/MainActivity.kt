package com.example.proyectopostman


import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectomodificaciones.SamirResponse
import com.example.proyectomodificaciones.SamirService
import com.example.proyectopostman.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var allInmuebles = mutableListOf<SamirResponse>()
    private lateinit var btnPOST: Button
    private lateinit var btnDELETE: Button
    private lateinit var btnPUT: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnPOST = binding.btnPOST
        btnDELETE = binding.btnDELETE
        btnPUT = binding.btnPUT


        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.10.30.160:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val myApi = retrofit.create(SamirService::class.java)

        myApi.getInmuebles().enqueue(object : Callback<List<SamirResponse>> {
            override fun onResponse(
                call: Call<List<SamirResponse>>,
                response: Response<List<SamirResponse>>
            ) {
                val inmueble = response.body()
                allInmuebles.addAll(inmueble!!)

                if (response.isSuccessful) {}
            }

            override fun onFailure(call: Call<List<SamirResponse>>, t: Throwable) {
                println(call.toString())
                println(t.toString())
            }
        })

        btnPOST.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://10.10.30.160:8080/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val myApi = retrofit.create(SamirService::class.java)

                val myData = SamirResponse(
                    "Iglu en la Antartida", 300.99f, "Un palacio de nieve en una zona muy fria", 60, 40, "Antartida",
                    "Costal", "2022-02-23", 8, 4, 40
                )

                myApi.postMyData(myData).enqueue(object : Callback<SamirResponse> {

                    override fun onFailure(call: Call<SamirResponse>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<SamirResponse>,
                        response: Response<SamirResponse>
                    ) {
                        if (response.isSuccessful) {
                            val myResponse = response.body()
                            Log.i("POST", "Se ha introducido un objeto con el id: " + myResponse?.idInmueble.toString())
                        } else {
                            System.err.println(response.errorBody()?.string())
                        }
                    }
                })
            }
        }

        btnPUT.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://10.10.30.160:8080/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val myApi = retrofit.create(SamirService::class.java)

                val myData = SamirResponse(
                    "Iglu en la Antartida", 300.99f, "Un palacio de nieve en una zona muy fria", 60, 40, "Antartida",
                    "Costal", "2022-02-23", 8, 4, 2
                )

                myApi.postMyData(myData).enqueue(object : Callback<SamirResponse> {

                    override fun onFailure(call: Call<SamirResponse>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<SamirResponse>,
                        response: Response<SamirResponse>
                    ) {
                        if (response.isSuccessful) {
                            val myResponse = response.body()
                            Log.i("POST", "Cambiado el objeto con id: " + myResponse?.idInmueble.toString())
                        } else {
                            System.err.println(response.errorBody()?.string())
                        }
                    }
                })
            }
        }
        btnDELETE.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Introduce el id del objeto que se borrará:")


            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(input)


            builder.setPositiveButton("Aceptar") { dialog, which ->
                val number = input.text.toString().toInt()

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://10.10.30.160:8080/api/inmuebles/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val myApi = retrofit.create(SamirService::class.java)

                myApi.deleteInmueble(input.text.toString()).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.i("delete", "Se ha borrado exitosamente")
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.i("delete", "Se ha producido un error a la hora de borrar")
                    }
                })
            }
            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.cancel()
            }
            builder.show()
        }
    }
}
package com.example.proyectopostman

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopostman.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var rvMain: RecyclerView

    private lateinit var binding: ActivityMainBinding

    private var lista = mutableListOf<PaisCard>()
    private lateinit var miAdapter: PaisAdapter


    private lateinit var searchView: androidx.appcompat.widget.SearchView

    private lateinit var listaCopia: MutableList<Pais>

    private lateinit var context: Context

    private lateinit var PaisKey: PaisKey
    private var pageNumber = 0
    private var totalPages = 0
    private var allCards = mutableListOf<PaisCard>()

    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        spinner = binding.numPag
        binding.tvNo.text = "Cargando ..."

        cargarSpinner()

        rvMain = findViewById(R.id.rvMain)
        totalPages = 1

        searchView = binding.searchview

        miAdapter = PaisAdapter(lista)

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                buscarPais(newText)
                return true
            }
        })

        val retrofit = Retrofit.Builder()
            .baseUrl("https://apis.thatapicompany.com/9diyyzzwr4w8bf4r/countries")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val PaisKey = retrofit.create(PaisKey::class.java)

        binding.tvNo.visibility = View.VISIBLE
// Se crea un Listener para la respuesta de la llamada a la API
        PaisKey.getCards("d7t8frAA0E9DUTPZzYKNtNZNXiH1wcR1", 1)
            .enqueue(object : Callback<PaisResponse> {
                // Se define la acción a realizar en caso de éxito en la llamada
                override fun onResponse(
                    call: Call<PaisResponse>,
                    response: Response<PaisResponse>
                ) {
                    val paises = response.body()?.cards
                    allCards.addAll(paises!!)




                    if (response.isSuccessful) {
                        binding.tvNo.visibility = View.INVISIBLE
                        miAdapter.setList(allCards)
                        val layoutManager = LinearLayoutManager(applicationContext)
                        binding.rvMain.layoutManager = layoutManager
                        binding.rvMain.adapter = miAdapter

                    }


                }

                override fun onFailure(call: Call<PaisResponse>, t: Throwable) {
                    binding.tvNo.visibility = View.VISIBLE
                    binding.tvNo.text = "No se han encontrado países"
                }
            })


        spinnerListener()
        // Se define la acción a realizar en caso de fallo en la llamada


    }

    fun spinnerListener(){

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.rvMain.visibility = View.INVISIBLE
                binding.tvNo.visibility = View.VISIBLE
                binding.tvNo.text = "Cargando ..."
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://apis.thatapicompany.com/9diyyzzwr4w8bf4r/countries")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val PaisKey = retrofit.create(PaisKey::class.java)
                val selectedItem = spinner.getItemAtPosition(position) as Int
                PaisKey.getCards("d7t8frAA0E9DUTPZzYKNtNZNXiH1wcR1", selectedItem)
                    .enqueue(object : Callback<PaisResponse> {
                        override fun onResponse(
                            call: Call<PaisKey>,
                            response: Response<PaisKey>
                        ) {
                            allCards.clear()
                            miAdapter.setList(allCards)
                            val layoutManager = LinearLayoutManager(applicationContext)
                            binding.rvMain.layoutManager = layoutManager
                            binding.rvMain.adapter = miAdapter
                            val paises = response.body()?.cards
                            allCards.addAll(paises!!)

                            if (response.isSuccessful) {
                                binding.rvMain.visibility = View.VISIBLE
                                binding.tvNo.visibility = View.INVISIBLE
                                miAdapter.setList(allCards)
                                val layoutManager = LinearLayoutManager(applicationContext)
                                binding.rvMain.layoutManager = layoutManager
                                binding.rvMain.adapter = miAdapter
                            }
                        }

                        override fun onFailure(call: Call<PaisKey>, t: Throwable) {
                            binding.tvNo.visibility = View.VISIBLE
                            binding.tvNo.text = "No se han encontrado países"
                        }

                        override fun onResponse(
                            call: Call<PaisResponse>,
                            response: Response<PaisResponse>
                        ) {
                        }
                    })
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // no se realiza ninguna acción
            }
        }


    }

    private fun cargarSpinner() {

        val numbers = ArrayList<Int>()
        for (i in 1..137) {
            numbers.add(i)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, numbers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

    }

    private fun buscarPais(texto: String?) {
        val resultados = allCards.filter {
            it.name.contains(texto!!, true)
        }
        miAdapter.setList((resultados as MutableList<PaisCard>))

    }

    private fun cargarTodosLosPaises() {
        while (pageNumber <= totalPages) {
            PaisKey.getCards("d7t8frAA0E9DUTPZzYKNtNZNXiH1wcR1", pageNumber)
                .enqueue(object : Callback<PaisKey> {
                    // Se define la acción a realizar en caso de éxito en la llamada
                    override fun onResponse(
                        call: Call<PaisKey>,
                        response: Response<PaisKey>
                    ) {
                        // Se obtiene la lista de paises
                        val paises = response.body()?.cards
                        allCards.addAll(paises!!)
                        pageNumber++
                    }

                    override fun onFailure(call: Call<PaisKey>, t: Throwable) {

                    }

                    // Se define la acción a realizar en caso de fallo en la llamada

                })
        }
        miAdapter.setList(allCards)
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.rvMain.layoutManager = layoutManager
        binding.rvMain.adapter = miAdapter
    }

}
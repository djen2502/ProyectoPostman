package com.example.proyectopostman

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var rvMain: RecyclerView

    private lateinit var binding: ActivityMainBinding

    private var lista = mutableListOf<PaisCard>()
    private lateinit var miAdapter: PaisAdapter


    private lateinit var searchView: androidx.appcompat.widget.SearchView

    private lateinit var listaCopia: MutableList<Pais>

    private lateinit var context: Context

    //private lateinit var PaisKey: PaisKey
    private var pageNumber = 0
    private var totalPages = 0
    private var allCards = mutableListOf<PaisCard>()

    private lateinit var spinner: Spinner

    private var isLoading = false
    private var isLastPage = false

    private lateinit var layoutManager: LinearLayoutManager
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

        miAdapter = PaisAdapter(allCards)


        layoutManager = LinearLayoutManager(applicationContext)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.magicthegathering.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val PaisKey = retrofit.create(PaisKey::class.java)
        binding.tvNo.visibility = View.VISIBLE
// Se crea un Listener para la respuesta de la llamada a la API
        PaisKey.getCards()
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

                        binding.rvMain.layoutManager = layoutManager
                        binding.rvMain.adapter = miAdapter

                    }


                }

                override fun onFailure(call: Call<PaisResponse>, t: Throwable) {
                    binding.tvNo.visibility = View.VISIBLE
                    binding.tvNo.text = "No se encontraron paises"
                }
            })


        spinnerListener()
        // Se define la acción a realizar en caso de fallo en la llamada
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                PaisKey.getCarta(newText.toString())
                    .enqueue(object : Callback<PaisResponse> {
                        override fun onResponse(
                            call: Call<PaisResponse>,
                            response: Response<PaisResponse>
                        ) {
                            if (response.isSuccessful) {
                                val result = response.body()!!.cards
                                miAdapter.setList(result as MutableList<PaisCard>)
                            }
                        }

                        override fun onFailure(call: Call<PaisResponse>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                "No se pudo realizar la búsqueda",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                return true
            }
        })

        setUpScrollListener()

    }

    private fun setUpScrollListener() {

        binding.rvMain.setOnScrollChangeListener { _, _, _, _, _ ->
            val totalItemCount = binding.rvMain.computeVerticalScrollRange()
            val visibleItemCount = binding.rvMain.computeVerticalScrollExtent()
            val pastVisibleItems = binding.rvMain.computeVerticalScrollOffset()

            if (pastVisibleItems + visibleItemCount >= totalItemCount * 0.60) {
                addNextN()
            }
        }


    }

    fun addNextN() {

        if (pageNumber < 137) {


            CoroutineScope(Dispatchers.IO).launch {

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.magicthegathering.io/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val call = retrofit.create(PaisKey::class.java)
                    .getCards()
                    .enqueue(object : Callback<PaisResponse> {
                        override fun onResponse(
                            call: Call<PaisResponse>,
                            response: Response<PaisResponse>
                        ) {
                            runOnUiThread {

                                if (call.isExecuted) {
                                    val pais = response.body()?.cards
                                    allCards.addAll(pais!!)
                                    miAdapter.notifyDataSetChanged()
                                }else{
                                    println("error")
                                }

                            }
                        }

                        override fun onFailure(call: Call<PaisResponse>, t: Throwable) {
                            println("error")
                        }

                    })



            }

        }
    }


    fun spinnerListener() {

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.rvMain.visibility = View.INVISIBLE
                binding.tvNo.visibility = View.VISIBLE
                binding.tvNo.text = "Cargando ..."
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.magicthegathering.io/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val PaisKey = retrofit.create(PaisKey::class.java)
                val selectedItem = spinner.getItemAtPosition(position) as Int
                PaisKey.getCards()
                    .enqueue(object : Callback<PaisResponse> {
                        override fun onResponse(
                            call: Call<PaisResponse>,
                            response: Response<PaisResponse>
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

                        override fun onFailure(call: Call<PaisResponse>, t: Throwable) {
                            binding.tvNo.visibility = View.VISIBLE
                            binding.tvNo.text = "No se ha encontrado paises"
                        }
                    })
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
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

}
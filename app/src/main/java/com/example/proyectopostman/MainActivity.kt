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

    private lateinit var pokemonService: PaisKey
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
                buscarPokemon(newText)
                return true
            }
        })

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.pokemontcg.io/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val pokemonService = retrofit.create(PaisKey::class.java)

        binding.tvNo.visibility = View.VISIBLE
// Se crea un Listener para la respuesta de la llamada a la API
        pokemonService.getCards("c099e29e-2bde-4974-a532-cb7e2cf90072", 1)
            .enqueue(object : Callback<PaisResponse> {
                // Se define la acción a realizar en caso de éxito en la llamada
                override fun onResponse(
                    call: Call<PaisResponse>,
                    response: Response<PaisResponse>
                ) {
                    val pokemons = response.body()?.cards
                    allCards.addAll(pokemons!!)




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
                    binding.tvNo.text = "No hay pokemon"
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
                    .baseUrl("https://api.pokemontcg.io/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val pokemonService = retrofit.create(PaisKey::class.java)
                val selectedItem = spinner.getItemAtPosition(position) as Int
                pokemonService.getCards("c099e29e-2bde-4974-a532-cb7e2cf90072", selectedItem)
                    .enqueue(object : Callback<PaisKey> {
                        override fun onResponse(
                            call: Call<PaisKey>,
                            response: Response<PaisKey>
                        ) {
                            allCards.clear()
                            miAdapter.setList(allCards)
                            val layoutManager = LinearLayoutManager(applicationContext)
                            binding.rvMain.layoutManager = layoutManager
                            binding.rvMain.adapter = miAdapter
                            val pokemons = response.body()?.cards
                            allCards.addAll(pokemons!!)

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
                            binding.tvNo.text = "No hay pokemon"
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

    private fun buscarPokemon(texto: String?) {
        val resultados = allCards.filter {
            it.name.contains(texto!!, true)
        }
        miAdapter.setList((resultados as MutableList<PaisCard>))

    }

    private fun cargarTodosLosPokemon() {
        while (pageNumber <= totalPages) {
            pokemonService.getCards("c099e29e-2bde-4974-a532-cb7e2cf90072", pageNumber)
                .enqueue(object : Callback<PaisKey> {
                    // Se define la acción a realizar en caso de éxito en la llamada
                    override fun onResponse(
                        call: Call<PaisKey>,
                        response: Response<PaisKey>
                    ) {
                        // Se obtiene la lista de pokemones
                        val pokemons = response.body()?.cards
                        allCards.addAll(pokemons!!)
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
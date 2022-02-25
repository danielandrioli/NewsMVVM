package com.dboy.newsmvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dboy.newsmvvm.R
import com.dboy.newsmvvm.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/*
* APP BASEADO NA PLAYLIST DO PHILIP LACKNER. ACRESCENTAREI MODIFICAÇÕES: COIL NO LUGAR DO GLIDE, E O USO DO DAGGER-HILT
* */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private var binding : ActivityMainBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.breakingNewsFragment, R.id.favoriteNewsFragment, R.id.searchNewsFragment))
        //com esse appBarConfiguration não haverá botão de voltar. O botão não é necessário, visto que todos fragments são top level.
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding?.bottomNavigationView?.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
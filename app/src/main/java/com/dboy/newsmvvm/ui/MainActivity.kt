package com.dboy.newsmvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dboy.newsmvvm.R
import com.dboy.newsmvvm.databinding.ActivityMainBinding
import com.dboy.newsmvvm.util.CountryCode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/*
* APP BASEADO NA PLAYLIST DO PHILIP LACKNER. ACRESCENTAREI MODIFICAÇÕES: COIL NO LUGAR DO GLIDE, E O USO DO DAGGER-HILT
* */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private var binding : ActivityMainBinding? = null
    private val newsViewModel: NewsViewModel by viewModels()
//    private val dataStore: DataStore<Preferences> by preferencesDataStore("settings")

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

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || navController.navigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_dropdown_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val selectedCountry: CountryCode = when (item.itemId){
            R.id.it_Argentina -> CountryCode.ar
            R.id.it_Brasil -> CountryCode.br
            R.id.it_France -> CountryCode.fr
            R.id.it_Mexico -> CountryCode.mx
            R.id.it_USA -> CountryCode.us
            else -> CountryCode.us
        }
        newsViewModel.changeCountry(selectedCountry)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
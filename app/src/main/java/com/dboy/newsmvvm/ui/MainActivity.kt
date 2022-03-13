package com.dboy.newsmvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.forEach
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

/*
* APP BASEADO NA PLAYLIST DO PHILIP LACKNER. ACRESCENTAREI MODIFICAÇÕES: COIL NO LUGAR DO GLIDE, E O USO DO DAGGER-HILT
* */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private var binding : ActivityMainBinding? = null
    private val newsViewModel: NewsViewModel by viewModels()

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
        menuInflater.inflate(R.menu.top_dropdown_menu_withflag, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        menu.forEach {
            it.isCheckable = false
            it.isChecked = false
        }
        val selectedCountryId = when(newsViewModel.countryCode.value){
            CountryCode.ar -> R.id.it_Argentina
            CountryCode.br -> R.id.it_Brasil
            CountryCode.fr -> R.id.it_France
            CountryCode.mx -> R.id.it_Mexico
            CountryCode.us -> R.id.it_USA
            else -> R.id.it_USA
        }
        menu.findItem(selectedCountryId).setCheckable(true).isChecked = true
        return super.onMenuOpened(featureId, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val selectedCountry: CountryCode = when (item.itemId){
            R.id.it_Argentina -> CountryCode.ar
            R.id.it_Brasil -> CountryCode.br
            R.id.it_France -> CountryCode.fr
            R.id.it_Mexico -> CountryCode.mx
            R.id.it_USA -> CountryCode.us
            else -> return false  //return false here, otherwise the upper backbutton won't work.
        }
        newsViewModel.changeCountry(selectedCountry)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
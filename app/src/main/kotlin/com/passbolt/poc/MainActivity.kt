package com.passbolt.poc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var navController: NavController

  private val navDestinationChangedListener =
    NavController.OnDestinationChangedListener { _, destination, _ ->
      supportActionBar?.title = destination.label ?: getString(R.string.app_name)
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val navHostFragment =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    navController = navHostFragment.navController

    setupNavigation()
  }

  override fun onSupportNavigateUp(): Boolean {
    return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
  }

  override fun onDestroy() {
    navController.removeOnDestinationChangedListener(
        navDestinationChangedListener
    )
    super.onDestroy()
  }

  private fun setupNavigation() {
    navController.apply {
      addOnDestinationChangedListener(navDestinationChangedListener)
      appBarConfiguration = AppBarConfiguration(graph)
      setupActionBarWithNavController(this, appBarConfiguration)
    }
  }
}

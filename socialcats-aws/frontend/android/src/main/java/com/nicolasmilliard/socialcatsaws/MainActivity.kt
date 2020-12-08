package com.nicolasmilliard.socialcatsaws

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.nicolasmilliard.socialcatsaws.home.Home
import com.nicolasmilliard.socialcatsaws.profile.Profile
import com.nicolasmilliard.socialcatsaws.profile.ProfilePresenter
import com.nicolasmilliard.socialcatsaws.ui.SocialCatsAwsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

  private val profilePresenter: ProfilePresenter by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SocialCatsAwsTheme {
        nav()
      }
    }
  }

  @Composable
  fun nav() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
      composable("home") { Home { userId -> navController.navigate("profile/$userId") } }
      composable(
        "profile/{userId}",
        arguments = listOf(navArgument("userId") { type = NavType.StringType })
      ) { backStackEntry ->
        val userId = backStackEntry.arguments!!.getString("userId")!!
        profilePresenter.loadProfile(userId)
        Profile(userId, profilePresenter) { navController.navigateUp() }
      }
    }
  }
}

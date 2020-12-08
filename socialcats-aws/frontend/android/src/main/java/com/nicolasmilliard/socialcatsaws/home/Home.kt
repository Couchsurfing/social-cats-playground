package com.nicolasmilliard.socialcatsaws.home

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalAirport
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nicolasmilliard.socialcatsaws.R
import com.nicolasmilliard.socialcatsaws.ui.SocialCatsAwsTheme

@Composable
fun Home(onProfileClicked: (userId: String) -> Unit = { }) =
  Scaffold(topBar = { AppBar() }) { innerPadding ->
    ScrollableColumn(
      contentPadding = innerPadding,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Button(onClick = { onProfileClicked("1") }) {
        Text(text = "User 1")
      }
    }
  }

@Composable
private fun AppBar() {
  TopAppBar(
    navigationIcon = {
      Icon(Icons.Rounded.LocalAirport)
    },
    title = {
      Text(text = stringResource(R.string.app_name))
    },
    backgroundColor = MaterialTheme.colors.primarySurface
  )
}

@Preview(name = "Home")
@Composable
fun DefaultPreview() {
  SocialCatsAwsTheme {
    Home()
  }
}

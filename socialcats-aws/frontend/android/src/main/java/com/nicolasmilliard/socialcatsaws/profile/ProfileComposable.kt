package com.nicolasmilliard.socialcatsaws.profile

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.viewModel
import com.nicolasmilliard.socialcatsaws.ui.SocialCatsAwsTheme

@Composable
fun Profile(userId: String, presenter: ProfilePresenter = viewModel(), onUp: () -> Unit = { }) {
  Scaffold(
    topBar = { AppBar(presenter, onUp) }
  ) { innerPadding ->
    val model: ProfilePresenter.Model by presenter.models.collectAsState()
    ScrollableColumn(contentPadding = innerPadding) {
      Text(text = "Test ${model.isLoading}, ${model.userId}")
    }
  }
}

@Composable
private fun AppBar(presenter: ProfilePresenter, onUp: () -> Unit) {
  TopAppBar(
    navigationIcon = {
      IconButton(onClick = onUp) {
        Icon(Icons.Rounded.ArrowBack)
      }
    },
    title = {
      val model: ProfilePresenter.Model by presenter.models.collectAsState()
      Text(text = "${model.name}")
    },
    backgroundColor = MaterialTheme.colors.primarySurface
  )
}

@Preview(name = "Home")
@Composable
fun DefaultPreview() {
  SocialCatsAwsTheme {
    Profile("1")
  }
}

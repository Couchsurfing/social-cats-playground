package com.nicolasmilliard.socialcats.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nicolasmilliard.socialcats.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {

  private lateinit var textMessage: TextView
  private lateinit var nameEditText: EditText
  private lateinit var createButton: Button
  private lateinit var nameText: TextView

  private lateinit var auth: FirebaseAuth
  private lateinit var db: FirebaseFirestore

  private val onNavigationItemSelectedListener =
    BottomNavigationView.OnNavigationItemSelectedListener { item ->
      when (item.itemId) {
        R.id.navigation_home -> {
          textMessage.setText(R.string.title_home)
          return@OnNavigationItemSelectedListener true
        }
        R.id.navigation_dashboard -> {
          textMessage.setText(R.string.title_dashboard)
          return@OnNavigationItemSelectedListener true
        }
        R.id.navigation_notifications -> {
          textMessage.setText(R.string.title_notifications)
          return@OnNavigationItemSelectedListener true
        }
      }
      false
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    auth = FirebaseAuth.getInstance()
    db = Firebase.firestore

    setContentView(R.layout.activity_main)
    val navView: BottomNavigationView = findViewById(R.id.nav_view)

    textMessage = findViewById(R.id.message)
    nameEditText = findViewById(R.id.name_input)
    createButton = findViewById(R.id.button)
    nameText = findViewById(R.id.name_text)

    navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

    createButton.setOnClickListener { _ ->
      val city = hashMapOf(
        "name" to nameEditText.text.toString()
      )
      db.collection("users").document(auth.currentUser!!.uid)
        .set(city)
        .addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Timber.w(e, "Error writing document") }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 123) {
      val response = IdpResponse.fromResultIntent(data)

      // Successfully signed in
      if (resultCode == RESULT_OK) {
        Snackbar.make(findViewById(android.R.id.content), "Success", Snackbar.LENGTH_LONG)
      } else {
        // Sign in failed
        if (response == null) {
          // User pressed back button
          Snackbar.make(findViewById(android.R.id.content), "Cancelled", Snackbar.LENGTH_LONG)
          return
        }

        if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
          Snackbar.make(
            findViewById(android.R.id.content),
            "No internet connection",
            Snackbar.LENGTH_LONG
          )
          return
        }

        Snackbar.make(findViewById(android.R.id.content), "Unknown error", Snackbar.LENGTH_LONG)
      }
    }
  }

  var addSnapshotListener: ListenerRegistration? = null
  override fun onResume() {
    super.onResume()
    if (auth.currentUser != null) {
      // already signed in
      Timber.i("${auth.currentUser!!.phoneNumber}")
      val db = FirebaseFirestore.getInstance()
      val docRef = db.collection("users").document(auth.currentUser!!.uid)
      addSnapshotListener = docRef.addSnapshotListener { snapshot, e ->
        if (e != null) {
          Timber.w(e, "Listen failed.")
          return@addSnapshotListener
        }

        if (snapshot != null && snapshot.exists()) {
          Timber.d("Current data: ${snapshot.data}")
          nameText.text = "Current data: ${snapshot.data!!["name"]}"
        } else {
          Timber.d("Current data: null")
        }
      }
    } else {
      // not signed in
      startActivityForResult(
        // Get an instance of AuthUI based on the default app
        AuthUI.getInstance()
          .createSignInIntentBuilder()
          .setAvailableProviders(listOf(AuthUI.IdpConfig.PhoneBuilder().build()))
          .build(),
        123
      )
    }
  }

  override fun onPause() {
    super.onPause()
    addSnapshotListener?.remove()
  }
}

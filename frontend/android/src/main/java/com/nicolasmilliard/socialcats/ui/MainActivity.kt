package com.nicolasmilliard.socialcats.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.byteunits.BinaryByteUnit.MEBIBYTES
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nicolasmilliard.presentation.Presentation
import com.nicolasmilliard.presentation.startPresentation
import com.nicolasmilliard.presentation.bindTo
import com.nicolasmilliard.socialcats.R
import com.nicolasmilliard.socialcats.auth.Auth
import com.nicolasmilliard.socialcats.search.OpenProfileUserHandler
import com.nicolasmilliard.socialcats.search.SearchLoader
import com.nicolasmilliard.socialcats.search.SearchPresenter
import com.nicolasmilliard.socialcats.search.SearchUiBinder
import com.nicolasmilliard.socialcats.search.SocialCatsApiModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KotlinLogging
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

private val logger = KotlinLogging.logger {}
class MainActivity : AppCompatActivity() {

    private lateinit var auth: Auth
    private lateinit var db: FirebaseFirestore

    private val binderJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + binderJob)

    lateinit var presentation: Presentation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Auth(FirebaseAuth.getInstance())
        db = Firebase.firestore

        val client = OkHttpClient.Builder()
            .cache(Cache(cacheDir, MEBIBYTES.toBytes(10)))
            .addNetworkInterceptor(
                HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) = logger.info { message }
                }).apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
        val searchService = SocialCatsApiModule.searchService(client)
        val searchLoader = SearchLoader(searchService)
        presentation = lastNonConfigurationInstance as Presentation?
            ?: SearchPresenter(auth, searchLoader).startPresentation(Dispatchers.Main)

        val presenter = presentation.presenter as SearchPresenter

        val onClick = OpenProfileUserHandler(this)

        setContentView(R.layout.search)

        scope.launch {
            val binder = SearchUiBinder(window.decorView, presenter.events, onClick)

            binder.bindTo(presenter)
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
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Cancelled",
                        Snackbar.LENGTH_LONG
                    )
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

                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Unknown error",
                    Snackbar.LENGTH_LONG
                )
            }
        }
    }

    var addSnapshotListener: ListenerRegistration? = null
    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            // already signed in
            Timber.i("${auth.currentUser!!.phoneNumber}")
//            val db = FirebaseFirestore.getInstance()
//            val docRef = db.collection("users").document(auth.currentUser!!.uid)
//            addSnapshotListener = docRef.addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    Timber.w(e, "Listen failed.")
//                    return@addSnapshotListener
//                }
//
//                if (snapshot != null && snapshot.exists()) {
//                    Timber.d("Current data: ${snapshot.data}")
//                    nameText.text = "Current data: ${snapshot.data!!["name"]}"
//                } else {
//                    Timber.d("Current data: null")
//                }
//            }
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

    override fun onDestroy() {
        super.onDestroy()
        binderJob.cancel()

        if (!isChangingConfigurations) {
            presentation.stop()
        }
    }
}

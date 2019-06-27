@file:JsModule("firebase/app")

package firebase

import firebase.auth.FirebaseAuth

external fun initializeApp(config: dynamic)

external fun auth(): FirebaseAuth

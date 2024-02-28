package com.crest.firebaseapp

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("TAG", message.data.toString())

        super.onMessageReceived(message)
    }

    override fun onNewToken(token: String) {
        Log.d("TAG", token)
        super.onNewToken(token)
    }
}
package com.alejandrolopez.connecta4game

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import com.alejandrolopez.connecta4game.classes.WSClient
import java.net.URI

class MainActivity : AppCompatActivity() {

    companion object {
        public lateinit var wsClient : WSClient
        public lateinit var clientName : String

        public fun connectWS(protocol : String, serverIP : String, port : String) {
            var uri : URI = URI(protocol + "://" + serverIP + ":" + port)
            wsClient = WSClient(uri)
            wsClient.connect()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, ConnectionActivity::class.java)
        startActivity(intent)
    }
}
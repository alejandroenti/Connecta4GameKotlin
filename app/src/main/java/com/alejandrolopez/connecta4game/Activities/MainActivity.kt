package com.alejandrolopez.connecta4game.Activities

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alejandrolopez.connecta4game.classes.ActivityTracker
import com.alejandrolopez.connecta4game.classes.ClientData
import com.alejandrolopez.connecta4game.classes.GameObject
import com.alejandrolopez.connecta4game.classes.WSClient
import org.json.JSONArray
import java.net.URI


class MainActivity : AppCompatActivity() {

    companion object {
        public lateinit var wsClient : WSClient
        public lateinit var clientName : String
        public lateinit var opponentName : String
        public lateinit var myColor : String
        public lateinit var winner : String

        public var board: Array<Array<Char>>? = null
        public var clients : MutableList<ClientData> = ArrayList<ClientData>()
        public var objects: MutableList<GameObject> = ArrayList<GameObject>()

        public var currentActivityRef: AppCompatActivity? = null

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
package com.alejandrolopez.connecta4game

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alejandrolopez.connecta4game.classes.ActivityTracker
import com.alejandrolopez.connecta4game.classes.ClientData
import com.alejandrolopez.connecta4game.classes.GameObject
import com.alejandrolopez.connecta4game.classes.WSClient
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI


class MainActivity : AppCompatActivity() {

    companion object {
        public lateinit var wsClient : WSClient
        public lateinit var clientName : String
        public lateinit var myColor : String

        public var clients : MutableList<ClientData> = ArrayList<ClientData>()
        public var objects: MutableList<GameObject> = ArrayList<GameObject>()

        public var tracker : ActivityTracker = ActivityTracker()

        public fun connectWS(protocol : String, serverIP : String, port : String) {
            var uri : URI = URI(protocol + "://" + serverIP + ":" + port)
            wsClient = WSClient(uri)
            wsClient.connect()
        }

        fun jsonArrayToList(array: JSONArray): List<Any?> {
            val list = mutableListOf<Any?>()
            for (i in 0 until array.length()) {
                list.add(array.get(i))
            }
            return list
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, ConnectionActivity::class.java)
        startActivity(intent)
    }
}
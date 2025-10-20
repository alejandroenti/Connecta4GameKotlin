package com.alejandrolopez.connecta4game

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class ConnectionActivity : AppCompatActivity() {

    private lateinit var playerNameInput : TextInputEditText
    private lateinit var protocolInput : TextInputEditText
    private lateinit var serverIPInput : TextInputEditText
    private lateinit var portInput : TextInputEditText
    private lateinit var btnConnect : Button
    private lateinit var btnLocal : Button
    private lateinit var btnProxmox : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_connection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.connection)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playerNameInput = findViewById<TextInputEditText>(R.id.playerNameInputText)
        protocolInput = findViewById<TextInputEditText>(R.id.protocolInputText)
        serverIPInput = findViewById<TextInputEditText>(R.id.serverIPInputText)
        portInput = findViewById<TextInputEditText>(R.id.portInputText)
        btnConnect = findViewById<Button>(R.id.btnConnectionConnect)
        btnLocal = findViewById<Button>(R.id.btnConnectionLocal)
        btnProxmox = findViewById<Button>(R.id.btnConnectionProxmox)

        btnConnect.setOnClickListener {
            connectoToServer()
        }

        btnLocal.setOnClickListener {
            setLocalConfiguration()
        }

        btnProxmox.setOnClickListener {
            setProxmoxConfiguration()
        }

        setLocalConfiguration()
    }

    private fun setLocalConfiguration() {
        protocolInput.setText("ws")
        serverIPInput.setText("localhost")
        portInput.setText("3000")
    }

    private fun setProxmoxConfiguration() {
        protocolInput.setText("wss")
        serverIPInput.setText("vasensiobermudez.ieti.site")
        portInput.setText("443")
    }

    private fun connectoToServer() {

        var name : String = playerNameInput.text.toString()
        var protocol : String = protocolInput.text.toString()
        var serverIP : String = serverIPInput.text.toString()
        var port : String = portInput.text.toString()

        if (name.equals("")) {
            Toast.makeText(this, "Name has not been provided!", Toast.LENGTH_SHORT).show()
            return
        }

        if (protocol.equals("")) {
            Toast.makeText(this, "Protocol has not been provided!", Toast.LENGTH_SHORT).show()
            return
        }

        if (serverIP.equals("")) {
            Toast.makeText(this, "Server IP has not been provided!", Toast.LENGTH_SHORT).show()
            return
        }

        if (port.equals("")) {
            Toast.makeText(this, "Port has not been provided!", Toast.LENGTH_SHORT).show()
            return
        }

        MainActivity.clientName = name
        MainActivity.connectWS(protocol, serverIP, port)
    }
}
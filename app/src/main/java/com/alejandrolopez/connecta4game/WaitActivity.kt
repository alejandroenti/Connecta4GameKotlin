package com.alejandrolopez.connecta4game

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alejandrolopez.connecta4game.MainActivity.Companion.clientName
import com.alejandrolopez.connecta4game.MainActivity.Companion.opponentName

class WaitActivity: AppCompatActivity() {

    private lateinit var counter : TextView
    private lateinit var player1 : TextView
    private lateinit var player2 : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wait)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.wait)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        counter = findViewById<TextView>(R.id.counterText)
        player1 = findViewById<TextView>(R.id.player1Text)
        player2 = findViewById<TextView>(R.id.player2Text)

        counter.setText("3")
        player1.setText(clientName)
        player2.setText(opponentName)

        MainActivity.currentActivityRef = this
    }

    public fun setCounter(value : Int) {
        runOnUiThread {
            counter.text = value.toString()
        }
    }
}
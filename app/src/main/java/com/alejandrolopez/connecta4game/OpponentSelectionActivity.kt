package com.alejandrolopez.connecta4game

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alejandrolopez.connecta4game.MainActivity.Companion.clients
import com.alejandrolopez.connecta4game.fragments.SendInvitationFragment

class OpponentSelectionActivity : AppCompatActivity() {

    private lateinit var panel : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_connection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.connection)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        panel = this.findViewById<LinearLayout>(R.id.sendInvitationPanel)
    }

    public fun createSendList() {
        panel.removeAllViews()

        for (client in clients) {
            val fragment = SendInvitationFragment()
            fragment.setName(client.name!!)

            supportFragmentManager.beginTransaction()
                .add(R.id.sendInvitationPanel, fragment)
                .commit()
        }
    }
}
package com.alejandrolopez.connecta4game

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alejandrolopez.connecta4game.MainActivity.Companion.clientName
import com.alejandrolopez.connecta4game.MainActivity.Companion.clients
import com.alejandrolopez.connecta4game.fragments.SendInvitationFragment

class OpponentSelectionActivity : AppCompatActivity() {

    private lateinit var panel : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opponent_selection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.opponent_selection)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        panel = this.findViewById<LinearLayout>(R.id.sendInvitationPanel)

        MainActivity.currentActivityRef = this
    }

    override fun onStart() {
        super.onStart()

        createSendList()
    }

    public fun createSendList() {

        if (panel.childCount > 0) {
            panel.removeAllViews()
        }

        for (client in clients) {

            if (client.name.equals(clientName)) continue

            val fragment = SendInvitationFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.sendInvitationPanel, fragment)
                .runOnCommit {
                    fragment.setName(client.name!!)
                }
                .commit()
        }
    }
}
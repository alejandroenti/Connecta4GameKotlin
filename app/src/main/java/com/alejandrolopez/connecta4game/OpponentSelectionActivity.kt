package com.alejandrolopez.connecta4game

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import com.alejandrolopez.connecta4game.MainActivity.Companion.clientName
import com.alejandrolopez.connecta4game.MainActivity.Companion.clients
import com.alejandrolopez.connecta4game.fragments.ReceiveInvitationFragment
import com.alejandrolopez.connecta4game.fragments.SendInvitationFragment

class OpponentSelectionActivity : AppCompatActivity() {

    private lateinit var panelSend : LinearLayout
    private lateinit var panelReceive : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opponent_selection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.opponent_selection)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        panelSend = this.findViewById<LinearLayout>(R.id.sendInvitationPanel)
        panelReceive = this.findViewById<LinearLayout>(R.id.receiveInvitationPanel)

        MainActivity.currentActivityRef = this
    }

    override fun onStart() {
        super.onStart()

        createSendList()
    }

    public fun createSendList() {

        if (panelSend.childCount > 0) {
            panelSend.removeAllViews()
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

    public fun addInvitation (name : String) {
        val fragment = ReceiveInvitationFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.receiveInvitationPanel, fragment)
            .runOnCommit {
                fragment.setName(name)
            }
            .commit()
    }

    public fun removeInvitations() {
        for (v in panelReceive.children) {
            (v as ReceiveInvitationFragment).declineInvitation()
        }

        panelReceive.removeAllViews()
    }

    public fun removeInvitation(view : View) {
        panelReceive.removeView(view)
    }

    public fun removeInvitation(name : String) {
        for (v in panelReceive.children) {
            if ((v as ReceiveInvitationFragment).getName().equals(name)) {
                panelReceive.removeView(v)
                break
            }
        }
    }

    public fun passToWait() {
        val intent = Intent(this, WaitActivity::class.java)
        startActivity(intent)
    }
}
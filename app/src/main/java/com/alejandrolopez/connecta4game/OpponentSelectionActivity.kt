package com.alejandrolopez.connecta4game

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.Person
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

    public fun showInvitationPopUp(name : String) {


    }

    fun showFloatingPopup() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // Inflamos el layout
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.fragment_receive_invitation, null)

        // Establecemos las propiedades de la ventana flotante
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.x = 100
        params.y = 100

        windowManager.addView(layout, params)

        layout.setOnClickListener {
            windowManager.removeView(layout)
        }
    }

    public fun passToWait() {
        val intent = Intent(this, WaitActivity::class.java)
        startActivity(intent)
    }
}
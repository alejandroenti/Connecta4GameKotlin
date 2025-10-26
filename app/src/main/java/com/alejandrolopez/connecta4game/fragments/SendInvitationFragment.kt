package com.alejandrolopez.connecta4game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alejandrolopez.connecta4game.MainActivity.Companion.clientName
import com.alejandrolopez.connecta4game.MainActivity.Companion.wsClient
import com.alejandrolopez.connecta4game.R
import org.json.JSONObject



class SendInvitationFragment : Fragment() {

    private lateinit var userName : TextView
    private lateinit var btn : ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflas el layout XML de este fragment
        return inflater.inflate(R.layout.fragment_send_invitation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = view.findViewById<TextView>(R.id.usernName)
        btn = view.findViewById<ImageButton>(R.id.sendInvitation)

        btn.setOnClickListener {
            val json = JSONObject()
            json.put("type", "clientSendInvitation")
            json.put("sendFrom", clientName)
            json.put("sendTo", userName.text)
            wsClient.send(json.toString())

            btn.isEnabled = false
            btn.setBackgroundColor(R.color.red)
        }
    }

    public fun setName(name : String) {
        userName.setText(name)
    }
}
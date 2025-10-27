package com.alejandrolopez.connecta4game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alejandrolopez.connecta4game.MainActivity
import com.alejandrolopez.connecta4game.MainActivity.Companion.clientName
import com.alejandrolopez.connecta4game.MainActivity.Companion.wsClient
import com.alejandrolopez.connecta4game.OpponentSelectionActivity
import com.alejandrolopez.connecta4game.R
import com.alejandrolopez.connecta4game.classes.KeyValues
import org.json.JSONObject

class ReceiveInvitationFragment : Fragment() {

    private lateinit var userName : TextView
    private lateinit var btnAccept : Button
    private lateinit var btnReject : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflas el layout XML de este fragment
        return inflater.inflate(R.layout.fragment_receive_invitation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = view.findViewById<TextView>(R.id.usernName)
        btnAccept = view.findViewById<Button>(R.id.btnAccept)
        btnReject = view.findViewById<Button>(R.id.btnDecline)

        btnAccept.setOnClickListener {
            var json : JSONObject = JSONObject()
            json.put(KeyValues.K_TYPE.value, KeyValues.K_CLIENT_ANSWER_INVITATION.value)
            json.put(KeyValues.K_SEND_FROM.value, userName.text)
            json.put(KeyValues.K_SEND_TO.value, clientName)
            json.put(KeyValues.K_VALUE.value, true)

            wsClient.send(json.toString())

            if (MainActivity.currentActivityRef is OpponentSelectionActivity) {
                (MainActivity.currentActivityRef as OpponentSelectionActivity).passToWait()
            }
        }

        btnReject.setOnClickListener {
            declineInvitation()
        }
    }

    public fun setName(name : String) {
        userName.setText(name)
    }

    public fun getName() : String {
        return userName.text.toString()
    }

    public fun declineInvitation() {
        var json : JSONObject = JSONObject()
        json.put(KeyValues.K_TYPE.value, KeyValues.K_CLIENT_ANSWER_INVITATION.value)
        json.put(KeyValues.K_SEND_FROM.value, userName.text)
        json.put(KeyValues.K_SEND_TO.value, clientName)
        json.put(KeyValues.K_VALUE.value, false)

        wsClient.send(json.toString())
    }
}
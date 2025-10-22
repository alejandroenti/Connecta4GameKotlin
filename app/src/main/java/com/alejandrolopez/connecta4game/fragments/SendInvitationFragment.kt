package com.alejandrolopez.connecta4game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alejandrolopez.connecta4game.R

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

        val textView = view.findViewById<TextView>(R.id.usernName)
        val imageButton = view.findViewById<ImageButton>(R.id.sendInvitation)

        imageButton.setOnClickListener {
            // Send Invitation
        }
    }

    public fun setName(name : String) {
        userName.setText(name)
    }
}
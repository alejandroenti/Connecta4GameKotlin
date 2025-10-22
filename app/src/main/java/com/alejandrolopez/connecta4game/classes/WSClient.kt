package com.alejandrolopez.connecta4game.classes

import android.util.Log
import com.alejandrolopez.connecta4game.MainActivity
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI

class WSClient(serverUri : URI) : WebSocketClient(serverUri) {
    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("WSConnection", "[*] Opened Connection!")
        var msgObject : JSONObject = JSONObject()
        msgObject.put(KeyValues.K_TYPE.value, KeyValues.K_SET_PLAYER_NAME.value)
        msgObject.put(KeyValues.K_NAME.value, MainActivity.clientName)
        MainActivity.wsClient.send(msgObject.toString())
        Log.d("WSConnection", "[*] Message to server: " + msgObject.toString())
    }

    override fun onMessage(message: String?) {
        Log.d("WSConnection", "[*] Message received!")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("WSConnection", "[*] Closed Connection!")
    }

    override fun onError(ex: Exception?) {
        Log.d("WSConnection", "[*] An error ocurred!" + ex.toString())
    }
}
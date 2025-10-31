package com.alejandrolopez.connecta4game.classes

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import com.alejandrolopez.connecta4game.ConnectionActivity
import com.alejandrolopez.connecta4game.MainActivity
import com.alejandrolopez.connecta4game.MainActivity.Companion.clientName
import com.alejandrolopez.connecta4game.MainActivity.Companion.clients
import com.alejandrolopez.connecta4game.MainActivity.Companion.myColor
import com.alejandrolopez.connecta4game.MainActivity.Companion.objects
import com.alejandrolopez.connecta4game.MainActivity.Companion.opponentName
import com.alejandrolopez.connecta4game.MainActivity.Companion.tracker
import com.alejandrolopez.connecta4game.MainActivity.Companion.wsClient
import com.alejandrolopez.connecta4game.OpponentSelectionActivity
import com.alejandrolopez.connecta4game.PlayActivity
import com.alejandrolopez.connecta4game.WaitActivity
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
        msgObject.put(KeyValues.K_NAME.value, clientName)
        MainActivity.wsClient.send(msgObject.toString())
        Log.d("WSConnection", "[*] Message to server: " + msgObject.toString())
    }

    override fun onMessage(message: String?) {
        wsMessage(message!!)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("WSConnection", "[*] Closed Connection!")
    }

    override fun onError(ex: Exception?) {
        Log.d("WSConnection", "[*] An error ocurred!" + ex.toString())
    }

    private fun wsMessage(response: String) {
        val msgObj = JSONObject(response)

        when (msgObj.getString(KeyValues.K_TYPE.value)) {
            KeyValues.K_CLIENT_NAME.value -> clientName = msgObj.getString(KeyValues.K_VALUE.value)
            KeyValues.K_SERVER_DATA.value -> {
                //clientName = msgObj.getString(KeyValues.K_VALUE.value)

                val arrClients = msgObj.getJSONArray(KeyValues.K_CLIENT_LIST.value)
                val newClients: MutableList<ClientData> = ArrayList<ClientData>()
                run {
                    var i = 0
                    while (i < arrClients.length()) {
                        val obj = arrClients.getJSONObject(i)
                        newClients.add(ClientData.fromJSON(obj))
                        i++
                    }
                }
                clients = newClients

                // Actualizar mi color basado en el cliente actual
                for (client in clients) {
                    if (client.name.equals(clientName)) {
                        myColor = client.color!!
                        break
                    }
                }

                val arrObjects = msgObj.getJSONArray(KeyValues.K_OBJECT_LIST.value)
                val newObjects: MutableList<GameObject> = ArrayList<GameObject>()
                var i = 0
                while (i < arrObjects.length()) {
                    val obj = arrObjects.getJSONObject(i)
                    newObjects.add(GameObject.fromJSON(obj))
                    i++
                }
                objects = newObjects
            }

            KeyValues.K_COUNTDOWN.value -> {
                val value = msgObj.getInt(KeyValues.K_VALUE.value)

                if (MainActivity.currentActivityRef is WaitActivity) {
                    if (value == 0) {
                        (MainActivity.currentActivityRef as WaitActivity).passToPlay()
                    }
                    (MainActivity.currentActivityRef as WaitActivity).setCounter(value)
                }
            }

            KeyValues.K_PLAY_ACCEPTED.value -> {
                val pieceId = msgObj.getString(KeyValues.K_PIECE_ID.value)
                val col = msgObj.getInt(KeyValues.K_COLUMN.value)
                val row = msgObj.getInt(KeyValues.K_ROW.value)
                val gameEnded = msgObj.getBoolean(KeyValues.K_GAME_ENDED.value)
                val winner = msgObj.optString(KeyValues.K_WINNER.value, "")

                // Procesar coordenadas de línea ganadora si existen
                var winningLineCoords = IntArray(0)
                if (msgObj.has(KeyValues.K_WINNING_LINE_COORDS.value) && !msgObj.isNull(KeyValues.K_WINNING_LINE_COORDS.value)) {
                    val coordsArray = msgObj.getJSONArray(KeyValues.K_WINNING_LINE_COORDS.value)
                    winningLineCoords = IntArray(coordsArray.length())
                    var i = 0
                    while (i < coordsArray.length()) {
                        winningLineCoords[i] = coordsArray.getInt(i)
                        i++
                    }
                }

                if (MainActivity.currentActivityRef is PlayActivity) {
                    val activity = MainActivity.currentActivityRef as PlayActivity
                    activity.runOnUiThread {
                        activity.handlePlayAccepted(pieceId, row, col, winner, winningLineCoords)
                    }
                }

                // Si el juego terminó
                if (gameEnded && winner != null) {
                    /*pauseDuring(1500, {
                        var result = ""
                        if (winner == "DRAW") {
                            result = "DRAW"
                        } else if (winner == myColor) {
                            result = "WIN"
                        } else {
                            result = "LOSE"
                        }

                        val ctrlResult: CtrlResult =
                            UtilsViews.getController("ViewResult") as CtrlResult
                        ctrlResult.setResultData(result, myColor, winner, ctrlPlay.boardState)
                        UtilsViews.setViewAnimating("ViewResult")
                    })*/
                }
            }

            KeyValues.K_PLAY_REJECTED.value -> {
                val reason = msgObj.optString(KeyValues.K_REASON.value, "Invalid move")

                if (MainActivity.currentActivityRef is PlayActivity) {
                    (MainActivity.currentActivityRef as PlayActivity).handlePlayRejected(reason)
                }
            }

            KeyValues.K_CLIENTS_LIST.value -> {
                val arr = msgObj.getJSONArray(KeyValues.K_CLIENT_LIST.value)
                clients.clear()

                var i = 0
                while (i < arr.length()) {
                    val obj = arr.getJSONObject(i)
                    println(obj)
                    val name = obj.getString(KeyValues.K_NAME.value)
                    val color = obj.getString(KeyValues.K_COLOR.value)
                    val isPlaying = obj.getBoolean(KeyValues.K_PLAY.value)

                    val cd: ClientData = ClientData(name, color)
                    cd.SetIsPlaying(isPlaying)

                    clients.add(cd)
                    i++
                }

                if (MainActivity.currentActivityRef is OpponentSelectionActivity) {
                    (MainActivity.currentActivityRef as OpponentSelectionActivity).createSendList()
                }
            }

            KeyValues.K_CLIENT_SEND_INVITATION.value -> {
                val username = msgObj.getString(KeyValues.K_SEND_FROM.value)

                if (MainActivity.currentActivityRef is OpponentSelectionActivity) {
                    (MainActivity.currentActivityRef as OpponentSelectionActivity).addInvitation(username)
                }
            }

            KeyValues.K_CLIENT_ANSWER_INVITATION.value -> {
                if (MainActivity.currentActivityRef is OpponentSelectionActivity) {

                    val user: String = msgObj.getString(KeyValues.K_SEND_FROM.value)
                    val value: Boolean = msgObj.getString(KeyValues.K_VALUE.value).toBoolean()

                    if (!value) {
                        if (MainActivity.currentActivityRef is OpponentSelectionActivity) {
                            (MainActivity.currentActivityRef as OpponentSelectionActivity).enbleSendInvitation(
                                user
                            )
                        }
                        return
                    }

                    opponentName = user

                    (MainActivity.currentActivityRef as OpponentSelectionActivity).removeInvitations()
                    (MainActivity.currentActivityRef as OpponentSelectionActivity).passToWait()
                }
            }
        }
    }
}
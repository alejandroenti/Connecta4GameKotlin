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

                /*
                if (ctrlPlay != null) {
                    ctrlPlay.updateGameState(msgObj)
                }
                 */

                /*if (clients.size === 1) {
                    ctrlWait.txtPlayer0.setText(clients.get(0).name)
                } else if (clients.size > 1) {
                    ctrlWait.txtPlayer0.setText(clients.get(0).name)
                    ctrlWait.txtPlayer1.setText(clients.get(1).name)
                    ctrlPlay.title.setText(clients.get(0).name + " vs " + clients.get(1).name)
                }*/

                /*if (UtilsViews.getActiveView().equals("ViewConfig")) {
                    UtilsViews.setViewAnimating("ViewOpponentSelection")
                }*/
            }

            KeyValues.K_COUNTDOWN.value -> {
                /*if (!UtilsViews.getActiveView().equals("ViewWait")) {
                    // Rebutgem la resta de peticions
                    (UtilsViews.getController("ViewOpponentSelection") as CtrlOpponentSelection).rejectAllPetions()
                    UtilsViews.setView("ViewWait")
                }*/
                val value = msgObj.getInt(KeyValues.K_VALUE.value)

                if (value == 0) {
                    //UtilsViews.setViewAnimating("ViewPlay")
                }
                (MainActivity.currentActivityRef as WaitActivity).setCounter(value)
            }

            KeyValues.K_PLAY_ACCEPTED.value -> {
                val pieceId = msgObj.getString(KeyValues.K_PIECE_ID.value)
                val col = msgObj.getInt(KeyValues.K_COLUMN.value)
                val row = msgObj.getInt(KeyValues.K_ROW.value)
                val gameEnded = msgObj.getBoolean(KeyValues.K_GAME_ENDED.value)
                val winner = msgObj.optString(KeyValues.K_WINNER.value, null)

                // Procesar coordenadas de línea ganadora si existen
                val winningLineCoords: IntArray? = null
                if (msgObj.has(KeyValues.K_WINNING_LINE_COORDS.value) && !msgObj.isNull(KeyValues.K_WINNING_LINE_COORDS.value)) {
                    val coordsArray = msgObj.getJSONArray(KeyValues.K_WINNING_LINE_COORDS.value)
                    //winningLineCoords = IntArray(coordsArray.length())
                    var i = 0
                    while (i < coordsArray.length()) {
                        //winningLineCoords[i] = coordsArray.getInt(i)
                        i++
                    }
                }

                /*if (ctrlPlay != null) {
                    ctrlPlay.handlePlayAccepted(pieceId, col, row, winner, winningLineCoords)
                }*/

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
                val rejectedPieceId = msgObj.getString(KeyValues.K_PIECE_ID.value)
                val reason = msgObj.optString(KeyValues.K_REASON.value, "Invalid move")
                println("Play rejected: " + reason)
                /*if (ctrlPlay != null) {
                    ctrlPlay.handlePlayRejected(rejectedPieceId)
                }*/

                /*val arr = msgObj.getJSONArray(KeyValues.K_CLIENT_LIST.value)
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
                }*/

                if (MainActivity.currentActivityRef is OpponentSelectionActivity) {
                    (MainActivity.currentActivityRef as OpponentSelectionActivity).createSendList()
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

            "clientAnswerInvitation" -> {
                val user : String = msgObj.getString(KeyValues.K_SEND_FROM.value)
                val value : Boolean = msgObj.getString(KeyValues.K_VALUE.value).toBoolean()

                if (!value) {
                    if (MainActivity.currentActivityRef is OpponentSelectionActivity) {
                        (MainActivity.currentActivityRef as OpponentSelectionActivity).enbleSendInvitation(user)
                    }
                    return
                }

                (MainActivity.currentActivityRef as OpponentSelectionActivity).removeInvitations()
                (MainActivity.currentActivityRef as OpponentSelectionActivity).passToWait()
            }
        }
    }
}
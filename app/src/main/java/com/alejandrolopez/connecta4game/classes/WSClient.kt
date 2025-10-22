package com.alejandrolopez.connecta4game.classes

import android.util.Log
import com.alejandrolopez.connecta4game.MainActivity
import com.alejandrolopez.connecta4game.MainActivity.Companion.clientName
import com.alejandrolopez.connecta4game.MainActivity.Companion.clients
import com.alejandrolopez.connecta4game.MainActivity.Companion.myColor
import com.alejandrolopez.connecta4game.MainActivity.Companion.objects
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
        Log.d("WSConnection", "[*] Message received!")
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
                clientName = msgObj.getString(KeyValues.K_VALUE.value)

                val arrClients = msgObj.getJSONArray("clientsList")
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

                val arrObjects = msgObj.getJSONArray("objectsList")
                val newObjects: MutableList<GameObject> = ArrayList<GameObject>()
                var i = 0
                while (i < arrObjects.length()) {
                    val obj = arrObjects.getJSONObject(i)
                    newObjects.add(GameObject.fromJSON(obj))
                    i++
                }
                objects = newObjects

                if (ctrlPlay != null) {
                    ctrlPlay.updateGameState(msgObj)
                }

                if (clients.size === 1) {
                    ctrlWait.txtPlayer0.setText(clients.get(0).name)
                } else if (clients.size > 1) {
                    ctrlWait.txtPlayer0.setText(clients.get(0).name)
                    ctrlWait.txtPlayer1.setText(clients.get(1).name)
                    ctrlPlay.title.setText(clients.get(0).name + " vs " + clients.get(1).name)
                }

                if (UtilsViews.getActiveView().equals("ViewConfig")) {
                    UtilsViews.setViewAnimating("ViewOpponentSelection")
                }
            }

            "countdown" -> {
                if (!UtilsViews.getActiveView().equals("ViewWait")) {
                    // Rebutgem la resta de peticions
                    (UtilsViews.getController("ViewOpponentSelection") as CtrlOpponentSelection).rejectAllPetions()
                    UtilsViews.setView("ViewWait")
                }

                val value = msgObj.getInt("value")
                val txt = value.toString()
                if (value == 0) {
                    UtilsViews.setViewAnimating("ViewPlay")
                    txt = "GO"
                }
                ctrlWait.txtTitle.setText(txt)
            }

            "playAccepted" -> {
                val pieceId = msgObj.getString("pieceId")
                val col = msgObj.getInt("column")
                val row = msgObj.getInt("row")
                val gameEnded = msgObj.getBoolean("gameEnded")
                val winner = msgObj.optString("winner", null)

                // Procesar coordenadas de línea ganadora si existen
                val winningLineCoords: IntArray? = null
                if (msgObj.has("winningLineCoords") && !msgObj.isNull("winningLineCoords")) {
                    val coordsArray = msgObj.getJSONArray("winningLineCoords")
                    winningLineCoords = IntArray(coordsArray.length())
                    var i = 0
                    while (i < coordsArray.length()) {
                        winningLineCoords[i] = coordsArray.getInt(i)
                        i++
                    }
                }

                if (ctrlPlay != null) {
                    ctrlPlay.handlePlayAccepted(pieceId, col, row, winner, winningLineCoords)
                }

                // Si el juego terminó
                if (gameEnded && winner != null) {
                    pauseDuring(1500, {
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
                    })
                }
            }

            "playRejected" -> {
                val rejectedPieceId = msgObj.getString("pieceId")
                val reason = msgObj.optString("reason", "Invalid move")
                println("Play rejected: " + reason)
                /*if (ctrlPlay != null) {
                    ctrlPlay.handlePlayRejected(rejectedPieceId)
                }*/

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

                //(UtilsViews.getController("ViewOpponentSelection") as CtrlOpponentSelection).loadSendList()
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

                //(UtilsViews.getController("ViewOpponentSelection") as CtrlOpponentSelection).loadSendList()
            }

            "clientSendInvitation" -> {
                val username = msgObj.getString("sendFrom")
                /*(UtilsViews.getController("ViewOpponentSelection") as CtrlOpponentSelection).addFromReceiveList(
                    username
                )
                (UtilsViews.getController("ViewOpponentSelection") as CtrlOpponentSelection).addToSendInvitation(
                    username
                )*/
            }

            "clientAnswerInvitation" -> {
                println(msgObj)
                val user = msgObj.getString("sendTo")
                /*(UtilsViews.getController("ViewOpponentSelection") as CtrlOpponentSelection).reactivateFromSendList(
                    user
                )
                (UtilsViews.getController("ViewOpponentSelection") as CtrlOpponentSelection).removeFromSendInvitation(
                    user
                )*/
            }
        }
    }
}
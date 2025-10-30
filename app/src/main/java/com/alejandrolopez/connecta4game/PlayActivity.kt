package com.alejandrolopez.connecta4game

import android.graphics.PorterDuff
import android.media.Image
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility
import com.alejandrolopez.connecta4game.MainActivity.Companion.clientName
import com.alejandrolopez.connecta4game.MainActivity.Companion.clients
import com.alejandrolopez.connecta4game.MainActivity.Companion.opponentName
import com.alejandrolopez.connecta4game.classes.ClientData

class PlayActivity : AppCompatActivity() {

    private val NUM_ROWS : Int = 6
    private val NUM_COLS : Int = 7

    private var tableRows : MutableList<TableRow> = mutableListOf<TableRow>()
    private var players : HashMap<String, ClientData> = HashMap<String, ClientData>()

    private lateinit var board : TableLayout
    private lateinit var title : TextView
    private lateinit var playerTurn : TextView
    private lateinit var colorTurn : ImageView
    private lateinit var turnPlay : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_play)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.play)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        board = findViewById<TableLayout>(R.id.playBoard)
        title = findViewById<TextView>(R.id.playTitle)
        playerTurn = findViewById<TextView>(R.id.playerTurn)
        colorTurn = findViewById<ImageView>(R.id.colorTurn)
        turnPlay = findViewById<ImageView>(R.id.playTurn)

        getPlayers()
        setTitle()
        turnPlay.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN)
        initializeBoard()
    }

    private fun initializeBoard() {
        var paramsColumn : TableRow.LayoutParams = TableRow.LayoutParams(0,
            TableRow.LayoutParams.MATCH_PARENT,1.0f)
        var paramsRow : TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.MATCH_PARENT)

        var firstRow : TableRow = TableRow(this)
        firstRow.layoutParams = paramsRow
        firstRow.gravity = Gravity.CENTER
        for (i in 0..NUM_COLS - 1) {
            val button : Button = Button(this)
            button.setOnClickListener {
                // Send message to server
            }
            button.text = (NUM_ROWS - 1).toString()
            button.gravity = Gravity.CENTER
            button.layoutParams = paramsColumn
            firstRow.addView(button)
        }
        tableRows.add(firstRow)
        board.addView(firstRow)

        for (i in 1..NUM_ROWS - 1) {
            var row : TableRow = TableRow(this)
            row.layoutParams = paramsRow
            row.gravity = Gravity.CENTER
            row.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_navy))
            for (j in 0..NUM_COLS - 1) {
                var chipImage = ImageView(this)
                chipImage.setImageResource(R.drawable.ic_chip)
                chipImage.layoutParams = paramsColumn
                chipImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                chipImage.setPadding(30, 30, 30, 30)
                row.addView(chipImage)
            }
            tableRows.add(row)
            board.addView(row)
        }
    }

    private fun setTitle() {
        title.text = clientName + " VS " + opponentName
    }

    private fun getPlayers() {
        for (client in clients) {
            if (client.name.equals(clientName) || client.name.equals(opponentName)) {
                players.put(client.name!!, client)
            }
        }
    }

    private fun setTurn(name : String, color : String) {
        playerTurn.text = name

        if (color.equals("RED")) {
            colorTurn.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN)
        }
        else {
            colorTurn.setColorFilter(ContextCompat.getColor(this, R.color.yellow), PorterDuff.Mode.SRC_IN)
        }

        if (name.equals(clientName)) {
            playerTurn.visibility = View.VISIBLE
        }
        else {
            playerTurn.visibility = View.INVISIBLE
        }
    }

    private fun fillChip(row : Int, column : Int, name : String) {
        var r = tableRows.get(row + 1)
        var chip = r.getChildAt(column) as ImageView

        if (players.getValue(name).color.equals("RED")) {
            chip.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN)
        }
        else {
            chip.setColorFilter(ContextCompat.getColor(this, R.color.yellow), PorterDuff.Mode.SRC_IN)
        }
    }

    public fun handlePlayAccepted(turn : String, pieceId : String, row : Int, column : Int, winner : String?, winningLineCoords : IntArray?) {
        if (winner.isNullOrEmpty()) {
            fillChip(row, column, turn)
        }
    }

    public fun handlePlayRejected(reason : String) {
        Toast.makeText(this, reason, Toast.LENGTH_LONG).show()
    }
}
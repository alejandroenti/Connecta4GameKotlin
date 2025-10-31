package com.alejandrolopez.connecta4game.Activities

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Looper
import android.os.Handler
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
import com.alejandrolopez.connecta4game.R
import com.alejandrolopez.connecta4game.classes.ClientData
import com.alejandrolopez.connecta4game.classes.KeyValues
import org.json.JSONObject


class PlayActivity : AppCompatActivity() {

    private val NUM_ROWS : Int = 6
    private val NUM_COLS : Int = 7

    private val CHIPS_NUM_ROWS = 3
    private val CHIPS_NUM_COL = 14


    private var boardValue : Array<Array<Char>> = Array(NUM_ROWS) { Array(NUM_COLS) { ' ' } }
    private var tableRows : MutableList<TableRow> = mutableListOf<TableRow>()
    private var players : HashMap<String, ClientData> = HashMap<String, ClientData>()
    private var chips : HashMap<String, ImageView> = HashMap<String, ImageView>()
    private lateinit var turns : Array<String>
    private val COLOR_TURNS : Array<Int> = arrayOf(R.color.red, R.color.yellow)

    private var turn : Int = 0
    private lateinit var pieceID : String
    private var pieceCount : Int = -1


    private lateinit var board : TableLayout
    private lateinit var chipBoard : TableLayout
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

        MainActivity.currentActivityRef = this

        board = findViewById<TableLayout>(R.id.playBoard)
        chipBoard = findViewById<TableLayout>(R.id.chipBoard)
        title = findViewById<TextView>(R.id.playTitle)
        playerTurn = findViewById<TextView>(R.id.playerTurn)
        colorTurn = findViewById<ImageView>(R.id.colorTurn)
        turnPlay = findViewById<ImageView>(R.id.playTurn)

        getPlayers()
        setTitle()
        turnPlay.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN)
        initializeBoard()
        initializeChips()
    }

    private fun initializeBoard() {
        var paramsColumn : TableRow.LayoutParams = TableRow.LayoutParams(0,
            TableRow.LayoutParams.MATCH_PARENT,1.0f)
        var paramsRow : TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT)

        var firstRow : TableRow = TableRow(this)
        firstRow.layoutParams = paramsRow
        firstRow.gravity = Gravity.CENTER
        for (i in 0..NUM_COLS - 1) {
            val button : Button = Button(this)
            button.setOnClickListener {
                val msg = JSONObject()
                msg.put(KeyValues.K_TYPE.value, KeyValues.K_CLIENT_REQUEST_PLAY.value)
                msg.put(KeyValues.K_PIECE_ID.value, pieceID + pieceCount.toString())
                msg.put(KeyValues.K_COLUMN.value, i)

                MainActivity.wsClient.send(msg.toString())
            }
            button.gravity = Gravity.CENTER
            button.layoutParams = paramsColumn
            firstRow.addView(button)
        }
        tableRows.add(firstRow)
        board.addView(firstRow)

        for (i in 0..NUM_ROWS - 1) {
            var row : TableRow = TableRow(this)
            row.layoutParams = paramsRow
            row.gravity = Gravity.CENTER
            row.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_navy))
            for (j in 0..NUM_COLS - 1) {
                var chipImage = ImageView(this)
                chipImage.setImageResource(R.drawable.ic_chip)
                chipImage.layoutParams = paramsColumn
                chipImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                chipImage.setPadding(10, 10, 10, 10)
                row.addView(chipImage)
            }
            tableRows.add(row)
            board.addView(row)
        }
    }

    private fun initializeChips() {
        var paramsColumn : TableRow.LayoutParams = TableRow.LayoutParams(0,
            TableRow.LayoutParams.MATCH_PARENT,1.0f)
        var paramsRow : TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT)

        for (i in 0..CHIPS_NUM_ROWS - 1) {
            var row : TableRow = TableRow(this)
            row.layoutParams = paramsRow
            row.gravity = Gravity.CENTER
            for (j in 0..CHIPS_NUM_COL - 1) {
                var chipImage = ImageView(this)
                chipImage.setImageResource(R.drawable.ic_chip)
                chipImage.layoutParams = paramsColumn
                chipImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                chipImage.setPadding(10, 10, 10, 10)

                if (j < 7) {
                    chipImage.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN)
                    chips.put("R_" + ((i * 7) + j).toString(), chipImage)
                }
                else {
                    chipImage.setColorFilter(ContextCompat.getColor(this, R.color.yellow), PorterDuff.Mode.SRC_IN)
                    chips.put("Y_" + ((i * 7) + (j - 7)).toString(), chipImage)
                }

                row.addView(chipImage)
            }
            tableRows.add(row)
            chipBoard.addView(row)
        }
    }

    private fun setTitle() {
        title.text = turns[0] + " VS " + turns[1]
    }

    private fun getPlayers() {
        for (client in MainActivity.Companion.clients) {
            if (client.name.equals(MainActivity.Companion.clientName)) {
                players.put(client.name!!, client)
                pieceID = client.color!!.get(0) + "_"
                if (client.color.equals("RED")) {
                    turns = arrayOf(
                        MainActivity.Companion.clientName,
                        MainActivity.Companion.opponentName
                    )
                }
            }

            if (client.name.equals(MainActivity.Companion.opponentName)) {
                players.put(client.name!!, client)
                if (client.color.equals("RED")) {

                    turns = arrayOf(
                        MainActivity.Companion.opponentName,
                        MainActivity.Companion.clientName
                    )
                }
            }
        }

        setTurn(COLOR_TURNS.get(turn))
    }

    private fun setTurn(color : Int) {
        playerTurn.text = turns.get(turn)
        colorTurn.setColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_IN)

        if (turns.get(turn).equals(MainActivity.Companion.clientName)) {
            turnPlay.visibility = View.VISIBLE
            pieceCount++
        }
        else {
            turnPlay.visibility = View.INVISIBLE
        }
    }

    private fun fillChip(row : Int, column : Int, color : Int) {
        var r = tableRows.get(row + 1)
        var chip = r.getChildAt(column) as ImageView
        chip.setColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_IN)

        passToBoard(row, column)
    }

    private fun markWinningChips(winningLineCoords : IntArray) {
        val startRow = winningLineCoords[0]
        val startCol = winningLineCoords[1]
        val endRow = winningLineCoords[2]
        val endCol = winningLineCoords[3]

        val rowStep = if (endRow > startRow) 1 else if (endRow < startRow) -1 else 0
        val colStep = if (endCol > startCol) 1 else if (endCol < startCol) -1 else 0

        var currentRow = startRow
        var currentCol = startCol

        for (i in 0..3) {
            fillChip(currentRow, currentCol, R.color.green)
            var r = boardValue.get(currentRow)
            r.set(currentCol, 'W')
            currentRow += rowStep
            currentCol += colStep
        }
    }

    private fun passToBoard(row : Int, col : Int) {
        var r = boardValue.get(row)
        var c =
        if (COLOR_TURNS[turn] == R.color.red) {
            r.set(col, 'X')
        }
        else {
            r.set(col, 'O')
        }
    }

    private fun passBoard() {
        val boardValueCopy = boardValue.map { it.copyOf() }.toTypedArray()
        MainActivity.board = boardValueCopy
    }

    public fun handlePlayAccepted(pieceId : String, row : Int, column : Int, winner : String, winningLineCoords : IntArray?) {
        if (winner.equals("null")) {
            chips.getValue(pieceId).visibility = View.INVISIBLE
            fillChip(row, column, COLOR_TURNS[turn])
            turn = (turn + 1) % 2
            setTurn(COLOR_TURNS.get(turn))
        }
        else {
            chips.getValue(pieceId).visibility = View.INVISIBLE
            fillChip(row, column, COLOR_TURNS[turn])
            markWinningChips(winningLineCoords!!)

            MainActivity.winner = winner
            passBoard()

            Handler(Looper.getMainLooper()).postDelayed({
                passToResults()
            }, 1500)
        }
    }

    public fun handlePlayRejected(reason : String) {
        Toast.makeText(this, reason, Toast.LENGTH_LONG).show()
    }

    public fun passToResults() {
        val intent = Intent(this, ResultsActivity::class.java)
        startActivity(intent)
        finish()
    }
}
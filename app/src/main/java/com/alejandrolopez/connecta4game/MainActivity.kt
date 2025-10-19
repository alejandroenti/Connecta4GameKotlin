package com.alejandrolopez.connecta4game

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Gravity
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
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private val NUM_ROWS : Int = 6
    private val NUM_COLS : Int = 7
    private val TURNS : Array<Char> = arrayOf('X', 'O')
    private val COLOR_tURNS : Array<Int> = arrayOf(R.color.red, R.color.yellow)

    private var turn : Int = 0
    private var turnChar : Char = TURNS.get(turn);
    private var valueBoard : Array<Array<Char>> = Array(NUM_ROWS - 1) { Array(NUM_COLS) { ' ' } }
    private var tableRows : MutableList<TableRow> = mutableListOf<TableRow>()

    private lateinit var board : TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        board = findViewById<TableLayout>(R.id.board)

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
                fillChip(i)
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

    private fun fillChip(column : Int) {
        for (i in NUM_ROWS - 2 downTo 0) {
            if (valueBoard.get(i).get(column) == ' ') {
                valueBoard.get(i).set(column, turnChar)
                var r = tableRows.get(i + 1)
                var chip = r.getChildAt(column) as ImageView
                chip.setColorFilter(ContextCompat.getColor(this, COLOR_tURNS.get(turn)), PorterDuff.Mode.SRC_IN)

                turn = (turn + 1) % 2
                turnChar = TURNS.get(turn)
                var firstRow = tableRows.get(0)
                var button = (firstRow as TableRow).getChildAt(column) as Button
                button.text = (button.text.toString().toInt() - 1).toString()

                return
            }
        }

        Toast.makeText(this, R.string.column_filled, Toast.LENGTH_SHORT).show()
    }
}
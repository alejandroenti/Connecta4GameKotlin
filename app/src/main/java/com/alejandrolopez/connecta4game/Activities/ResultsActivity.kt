package com.alejandrolopez.connecta4game.Activities

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alejandrolopez.connecta4game.R
import com.alejandrolopez.connecta4game.classes.KeyValues
import org.json.JSONObject

class ResultsActivity : AppCompatActivity() {

    private val NUM_ROWS : Int = 6
    private val NUM_COLS : Int = 7

    private lateinit var title : TextView
    private lateinit var status : TextView
    private lateinit var board : TableLayout
    private lateinit var btn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_results)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.results)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        title = findViewById<TextView>(R.id.resultsTitle)
        status = findViewById<TextView>(R.id.resultsStatus)
        board = findViewById<TableLayout>(R.id.resultsBoard)
        btn = findViewById<Button>(R.id.btnBackToOpponentSelection)

        btn.setOnClickListener {
            val intent = Intent(this, OpponentSelectionActivity::class.java)
            startActivity(intent)
            finish()
        }

        initializeBoard()

        if (MainActivity.winner.equals(MainActivity.clientName)) {
            title.text = "You Won!"
            title.setTextColor(ContextCompat.getColor(this, R.color.green))
        }
        else {
            title.text = "You Lose!"
            title.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        status.text = "Winner: " + MainActivity.winner
    }

    private fun initializeBoard() {
        var paramsColumn : TableRow.LayoutParams = TableRow.LayoutParams(0,
            TableRow.LayoutParams.MATCH_PARENT,1.0f)
        var paramsRow : TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT)

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
            board.addView(row)
        }
    }
}
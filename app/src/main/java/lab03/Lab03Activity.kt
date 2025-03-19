package lab03

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.gridlayout.widget.GridLayout
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab01.R.*

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoard: GridLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)


        mBoard = findViewById(R.id.main3)


        val rows = intent.getIntExtra("rows", 3)
        val columns = intent.getIntExtra("columns", 3)

        mBoard.rowCount = rows
        mBoard.columnCount = columns

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val btn = ImageButton(this).also {
                    it.tag = "${row}x${col}"


                    val layoutParams = GridLayout.LayoutParams()
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)

                    it.layoutParams = layoutParams
                    it.setImageResource(R.drawable.baseline_rocket_launch_24)


                    mBoard.addView(it)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main3)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets



        }
    }

}
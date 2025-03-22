package lab03

import MemoryBoardView
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.gridlayout.widget.GridLayout
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab01.R.*

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)

        mBoard = findViewById(R.id.main3)

        val rows = intent.getIntExtra("rows", 3)
        val columns = intent.getIntExtra("columns", 3)


        mBoardModel = MemoryBoardView(mBoard, columns, rows)

        mBoard.rowCount = rows
        mBoard.columnCount = columns



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main3)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

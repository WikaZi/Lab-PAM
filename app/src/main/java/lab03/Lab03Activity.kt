package lab03
import MemoryBoardView
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.gridlayout.widget.GridLayout
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab01.R.*
import java.util.Timer
import java.util.TimerTask

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView
    lateinit var completionPlayer: MediaPlayer
    lateinit var negativePlayer: MediaPlayer


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val gameState = mBoardModel.getState()
        outState.putIntArray("state", gameState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)

        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePlayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)

        mBoard = findViewById(R.id.main3)

        val rows = intent.getIntExtra("rows", 3)
        val columns = intent.getIntExtra("columns", 3)

        mBoardModel = MemoryBoardView(mBoard, columns, rows, negativePlayer, completionPlayer)
        mBoard.rowCount = rows
        mBoard.columnCount = columns

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main3)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState != null) {
            val savedState = savedInstanceState.getIntArray("state")
            savedState?.let {
                mBoardModel.setState(it)
            }
        } else {
            mBoardModel = MemoryBoardView(mBoard, columns, rows, negativePlayer, completionPlayer)
        }

        mBoardModel.setOnGameChangeListener { e ->
            run {
                when (e.state) {
                    GameStates.Matching -> {
                        e.tiles.forEach { tile ->
                            tile.revealed = true
                            tile.button.setImageResource(tile.tileResource)
                        }
                    }

                    GameStates.Match -> {
                        e.tiles.forEach { tile ->
                            tile.revealed = true
                            tile.button.setImageResource(tile.tileResource)
                        }
                    }

                    GameStates.NoMatch -> {
                        e.tiles.forEach { tile ->
                            tile.revealed = true
                            tile.button.setImageResource(tile.tileResource)
                        }

                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                runOnUiThread {
                                    e.tiles.forEach { tile ->
                                        tile.revealed = false
                                        tile.button.setImageResource(tile.deckResource)
                                    }
                                }
                            }
                        }, 2000)
                    }

                    GameStates.Finished -> {
                        runOnUiThread {
                            Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    override protected fun onResume() {
        super.onResume()
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePlayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)
    }


    override protected fun onPause() {
        super.onPause();
        completionPlayer.release()
        negativePlayer.release()
    }
}

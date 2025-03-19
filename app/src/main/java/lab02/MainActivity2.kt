package lab02

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import lab03.Lab03Activity
import pl.wsei.pam.lab01.R

class MainActivity2 : AppCompatActivity() {
    private var selectedRows: Int? = null
    private var selectedColumns: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun onBoardSizeSelected(v: View) {
        val tag: String? = v.tag as? String
        val tokens = tag?.split(" ")

        if (tokens != null && tokens.size == 2) {
            val rows = tokens[0].toIntOrNull()
            val columns = tokens[1].toIntOrNull()

            if (rows != null && columns != null) {
                Toast.makeText(this, "Wybrano planszę: ${rows}x${columns}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Lab03Activity::class.java)
                intent.putExtra("rows", rows)
                intent.putExtra("columns", columns)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Błąd odczytu rozmiaru planszy", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Nieprawidłowy format danych!", Toast.LENGTH_SHORT).show()
        }
    }

}
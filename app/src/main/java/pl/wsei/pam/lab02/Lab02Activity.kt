package pl.wsei.pam.lab02

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.wsei.pam.lab01.R

class Lab02Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab02)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun onBoardSizeSelected(v: View) {
        val tag: String? = v.tag as String?
        val tokens: List<String>? = tag?.split(" ")
        val rows = tokens?.get(0)?.toIntOrNull()
        val columns = tokens?.get(1)?.toIntOrNull()

        if (rows != null && columns != null) {
            Toast.makeText(this, "Wybrano planszę: ${rows}x${columns}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Błąd odczytu rozmiaru planszy", Toast.LENGTH_SHORT).show()
        }
    }
}
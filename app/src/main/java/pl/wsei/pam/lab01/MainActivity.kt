package pl.wsei.pam.lab01

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import lab02.MainActivity2
import lab06.MainActivity6


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun onClickMainBtnRunLab01(v: View){
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Lab01Activity::class.java)
        startActivity(intent)
    }
    fun onClickMainBtnRunLab02(v: View){
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
    }
    fun onClickMainBtnRunLab06(v: View) {
        Toast.makeText(this, "Przechodzę do Lab06", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity6::class.java)
        startActivity(intent)
    }
}
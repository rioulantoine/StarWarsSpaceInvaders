package com.example.starwars_spaceinvaders

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RulesActivity : AppCompatActivity() {
    private lateinit var backtoMain: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rules)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvRulesTitle)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //  ----------------- Bouton Retour -----------------
        backtoMain = findViewById(R.id.btnBack)
        backtoMain.setOnClickListener { retourAuMenu() }
    }

    fun retourAuMenu(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
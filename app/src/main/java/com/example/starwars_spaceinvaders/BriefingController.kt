package com.example.starwars_spaceinvaders

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BriefingController : AppCompatActivity() {
    //Bouton jouer
    private lateinit var playButton: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_infinite_briefing)

        playButton = findViewById(R.id.PlayButton)
        //Bouton lancement du jeu
        playButton.setOnClickListener { lancementJeu() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    /*
    Méthode du lancement du jeu
     */
    fun lancementJeu(){
        val intent = Intent(this, InfiniteGameActivity::class.java)
        startActivity(intent)
    }
}

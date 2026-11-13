package com.example.starwars_spaceinvaders

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    //Initialisation des différentes composante de la page d'acceuil
    private lateinit var PlayInfiniButton: Button
    private lateinit var PlayStoryButton: Button
    private lateinit var CreditButton: Button
    //Déclaration des différents managers
    lateinit var musicManager: MusicManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //==========================Managers==========================
        musicManager = MusicManager.getInstance(this)
        musicManager.start()
        //==========================Boutons==========================
        //  ----------------- Bouton Infini -----------------
        PlayInfiniButton = findViewById(R.id.playIniniButton)
        // Si le bouton est appuyé, change l'activity pour le mode de jeu
        PlayInfiniButton.setOnClickListener { ouvrirModeInfini() }
        //  ----------------- Bouton Histoire -----------------
        PlayStoryButton = findViewById(R.id.playStoryButton)
        PlayStoryButton.setOnClickListener { ouvrirStoryMode() }
        //  ----------------- Bouton Crédits -----------------
        CreditButton = findViewById(R.id.creditButton)
        CreditButton.setOnClickListener { ouvrirCredits() }
    }

    /*
    Méthode de changement pour choix des settings du mode infini
     */
    fun ouvrirModeInfini() {
        val intent = Intent(this, GameSettings::class.java)
        startActivity(intent)
    }
    /*
        Méthode de changement pour le mode story : différents levels avec des chapitres correspondant à chaque film
     */
    fun ouvrirStoryMode() {
        val intent = Intent(this, StoryMode::class.java)
        startActivity(intent)
    }
    /*
        Méthode de changement pour les crédits du jeu
     */
    fun ouvrirCredits(){
        val intent = Intent(this, CreditActivity::class.java)
        startActivity(intent)
    }

    /*

     */
    override fun onResume() {
        super.onResume()
        musicManager.start()
    }
    override fun onPause(){
        super.onPause()
        musicManager.stop()
    }
    override fun onDestroy() {
        super.onDestroy()
        musicManager.release()
    }
}
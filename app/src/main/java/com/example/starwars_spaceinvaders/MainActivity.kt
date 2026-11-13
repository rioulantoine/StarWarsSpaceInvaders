package com.example.starwars_spaceinvaders

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    //Initialisation des différentes composante de la page d'acceuil
    private lateinit var PlayInfiniButton: ImageView
    private lateinit var CreditButton: ImageView
    private lateinit var RulesButton: ImageView
    //Déclaration des différents managers
    lateinit var musicManager: MusicManager
    lateinit var tvTop1: TextView
    lateinit var tvTop2: TextView
    lateinit var tvTop3: TextView
    lateinit var scoreManager: ScoreManager

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
        //  ----------------- Bouton Crédits -----------------
        CreditButton = findViewById(R.id.creditButton)
        CreditButton.setOnClickListener { ouvrirCredits() }
        // ----------------- Bouton REGLES -----------------
        RulesButton = findViewById(R.id.rulesButton)
        RulesButton.setOnClickListener { ouvrirRegles() }
        //
        tvTop1 = findViewById<TextView>(R.id.tvTop1)
        tvTop2 = findViewById<TextView>(R.id.tvTop2)
        tvTop3 = findViewById<TextView>(R.id.tvTop3)

        scoreManager = ScoreManager(this)

        // On récupère les scores actuels enregistrés dans le téléphone
        val topScores = scoreManager.chargerScores()

        //Affichage des scores
        if (topScores.size > 0) tvTop1.text = "1. ${scoreManager.formaterTemps(topScores[0])}"
        if (topScores.size > 1) tvTop2.text = "2. ${scoreManager.formaterTemps(topScores[1])}"
        if (topScores.size > 2) tvTop3.text = "3. ${scoreManager.formaterTemps(topScores[2])}"
    }

    /*
    Méthode de changement pour choix des settings du mode infini
     */
    fun ouvrirModeInfini() {
        val intent = Intent(this, BriefingController::class.java)
        startActivity(intent)
    }
    /*
        Méthode de changement pour les crédits du jeu
     */
    fun ouvrirCredits(){
        val intent = Intent(this, CreditActivity::class.java)
        startActivity(intent)
    }

    /**
     * ouvrirRegles() -->  Méthode de changement pour les règles du jeu
     */
    fun ouvrirRegles(){
        val intent = Intent(this, RulesActivity::class.java)
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
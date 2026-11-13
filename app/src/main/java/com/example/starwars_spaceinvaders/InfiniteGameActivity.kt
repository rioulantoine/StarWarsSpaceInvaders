package com.example.starwars_spaceinvaders

import android.os.Bundle
import android.view.View
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InfiniteGameActivity : AppCompatActivity() {
    //Déclarations var
    lateinit var musicManager: MusicManager
    lateinit var capteurManager: CapteurManager
    lateinit var tvLumiere: TextView
    lateinit var ivVaisseau: ImageView
    lateinit var tvChrono: TextView
    lateinit var vObscurite: View
    lateinit var ivHalo: ImageView
    lateinit var bVie1: ImageView
    lateinit var bVie2: ImageView
    lateinit var bVie3: ImageView

    //Chronomètre
    val handler = android.os.Handler(android.os.Looper.getMainLooper())
    var startTime = 0L
    var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_infinite_game)
        //Déclaratios des vues
        tvLumiere = findViewById(R.id.tvLumiere)
        ivVaisseau = findViewById(R.id.ivVaisseau)
        ivVaisseau.setImageResource(R.drawable.xwing)
        tvChrono = findViewById(R.id.tvChrono)
        lancerChrono()
        vObscurite = findViewById(R.id.vObscurite)
        ivHalo = findViewById(R.id.ivHalo)
        //HealthBar
        bVie1 = findViewById(R.id.bVie1)
        bVie2 = findViewById(R.id.bVie2)
        bVie3 = findViewById(R.id.bVie3)
        bVie1.setImageResource(R.drawable.healthbar)
        bVie2.setImageResource(R.drawable.healthbar)
        bVie3.setImageResource(R.drawable.healthbar)

        //Capteurs
        musicManager = MusicManager.getInstance(this)
        musicManager.start()

        capteurManager = CapteurManager(this)
        capteurManager.afficheCapteur()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    /*

     */
    override fun onResume() {
        super.onResume()
        capteurManager.start()
        musicManager.start()
    }
    /*

     */
    override fun onPause(){
        super.onPause()
        capteurManager.stop()
        musicManager.stop()
        runnable?.let { handler.removeCallbacks(it) }
    }

    /**
     * Lancement du chronomètre
     */
    fun lancerChrono() {
        startTime = System.currentTimeMillis() // Heure actuelle

        runnable = object : Runnable {
            override fun run() {
                // Calcul du temps écoulé
                val millis = System.currentTimeMillis() - startTime
                val seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                val displaySeconds = seconds % 60

                // Formatage du texte (ex: 01:25)
                tvChrono.text = String.format("%02d:%02d", minutes, displaySeconds)
                handler.postDelayed(this, 1000)
            }
        }
        // Démarrage immédiat
        handler.post(runnable!!)
    }
}
package com.example.starwars_spaceinvaders

import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InfiniteGameActivity : AppCompatActivity() {

    // --- Vues ---
    lateinit var mainLayout: ConstraintLayout
    lateinit var musicManager: MusicManager
    lateinit var capteurManager: CapteurManager
    lateinit var scoreManager: ScoreManager
    lateinit var tvLumiere: TextView
    lateinit var ivVaisseau: ImageView
    lateinit var tvChrono: TextView
    lateinit var vObscurite: View
    lateinit var ivHalo: ImageView
    lateinit var ivPause: ImageView

    /**
     * Deux types de zones pour l'empire. Un cercle qui rebondit et un rectangle qui descends --> choisi au hasard entre les deux
     */
    lateinit var vZoneRougeRect: View
    lateinit var vZoneRougeCercle: View

    lateinit var bVie1: ImageView
    lateinit var bVie2: ImageView
    lateinit var bVie3: ImageView
    //Joueur
    var isGameOver = false
    //Menu pause
    var isPaused = false

    // --- Objets de jeu ---
    lateinit var joueur: Vaisseau
    val listeEnnemis = mutableListOf<Ennemi>()
    val listeOvnis = mutableListOf<Ovni>()

    // --- Temps et chronomètre ---
    val handler = android.os.Handler(android.os.Looper.getMainLooper())
    var startTime = 0L
    var runnable: Runnable? = null
    var chronoBase = 0L // Pour accélération
    //Zone rouge
    private var isEmpireActive = false
    private var typeEmpire = 0
    // Variables pour faire rebondir le cercle
    private var vitesseCercleX = 10f
    private var vitesseCercleY = 10f
    private var tempsDebutCercle = 0L
    //freq de spawn des obstacles
    var frequenceSpawnMultiplicateur = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_infinite_game)

        mainLayout = findViewById(R.id.main)

        // Récupération des vues
        tvLumiere = findViewById(R.id.tvLumiere)
        ivVaisseau = findViewById(R.id.ivVaisseau)
        ivVaisseau.setImageResource(R.drawable.xwing)
        tvChrono = findViewById(R.id.tvChrono)
        vObscurite = findViewById(R.id.vObscurite)
        ivHalo = findViewById(R.id.ivHalo)
        vZoneRougeRect = findViewById(R.id.vZoneRougeRect)
        vZoneRougeCercle = findViewById(R.id.vZoneRougeCercle)

        bVie1 = findViewById(R.id.bVie1)
        bVie2 = findViewById(R.id.bVie2)
        bVie3 = findViewById(R.id.bVie3)

        bVie1.setImageResource(R.drawable.healthbar)
        bVie2.setImageResource(R.drawable.healthbar)
        bVie3.setImageResource(R.drawable.healthbar)

        // Initialisation du Vaisseau
        joueur = Vaisseau(
            image = ivVaisseau,
            ecranLargeur = resources.displayMetrics.widthPixels,
            ecranHauteur = resources.displayMetrics.heightPixels
        )

        //Partie menu pause
        ivPause = findViewById(R.id.ivPause)
        ivPause.setOnClickListener { if (!isGameOver && ! isPaused){
                mettreEnPause()
            }
        }

        musicManager = MusicManager.getInstance(this)
        capteurManager = CapteurManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /**
         * LancerChrono() : Méthode initialisant le chronomètre tvChrono
         */
        lancerChrono()
        /**
         * LancerGameLoop() : Méthode reprennant le principe d'une game loop grâce à ce super site expliquant vraiment bien le principe selon moi
         * https://gameprogrammingpatterns.com/game-loop.html#:~:text=A%20game%20loop%20runs%20continuously,control%20the%20rate%20of%20gameplay.
         * Une phrase qui je trouve explique particulièrement bien le principe :
         * A game loop runs continuously during gameplay. Each turn of the loop, it processes user input without blocking, updates the game state, and renders the game.
         * It tracks the passage of time to control the rate of gameplay.
         */
        lancerGameLoop()
        /**
         *
         */
        lancerSpawnManager()
        /**
         *
         */
        lancerEmpireManager()
    }

    private fun lancerEmpireManager() {
        val empireRunnable = object : Runnable {
            override fun run() {
                if (!isGameOver) {
                    if(!isPaused){
                        isEmpireActive = true
                        // Choix au hasard entre 0 ou 1 --> 0 = Rectangle, 1 = Cercle
                        typeEmpire = (0..1).random()
                        if (typeEmpire == 0 ){
                            // ------ ZONE RECTANGLE ------
                            vZoneRougeRect.y = -vZoneRougeRect.height.toFloat()
                            vZoneRougeRect.visibility = View.VISIBLE
                            vZoneRougeCercle.visibility = View.INVISIBLE
                        }
                        else {
                            // ------ ZONE CERCLE ------
                            // Fait apparaitre le cercle aléatoirement en haut
                            vZoneRougeCercle.x = (0..(resources.displayMetrics.widthPixels - 200)).random().toFloat()
                            vZoneRougeCercle.y = -200f
                            vZoneRougeCercle.visibility = View.VISIBLE
                            vZoneRougeRect.visibility = View.INVISIBLE

                            //vitesse pour aller a gauche ou droite
                            vitesseCercleX = listOf(-15f, 15f).random()
                        }
                        handler.postDelayed(this, 15000)
                    }
                    else{
                        handler.postDelayed(this, 100)
                    }

                }
            }
        }
        handler.postDelayed(empireRunnable, 15000)
    }

    private fun lancerSpawnManager() {

        val spawnRunnable = object : Runnable {
            override fun run() {
                if (!isGameOver) {
                    if(!isPaused){
                        val largeurEcran = resources.displayMetrics.widthPixels

                        // Tirage aléatoire : 70% Ennemi, 30% Ovni
                        if ((0..10).random() > 3) {
                            listeEnnemis.add(Ennemi(this@InfiniteGameActivity, mainLayout, largeurEcran))
                        } else {
                            listeOvnis.add(Ovni(this@InfiniteGameActivity, mainLayout, largeurEcran))
                        }

                        // Accélération de l'apparition au fil du temps (le délai se réduit)
                        frequenceSpawnMultiplicateur += 0.2f
                        val delai = maxOf(500L, 1500L - (frequenceSpawnMultiplicateur * 20).toLong())

                        handler.postDelayed(this, delai)
                    }
                    else{
                        handler.postDelayed(this, 100)
                    }
                }
            }
        }
        handler.post(spawnRunnable)
    }

    /**
     *
     */
    private fun lancerGameLoop() {
        val gameLoopRunnable = object : Runnable {
            override fun run() {
                if (!isGameOver) {
                    //Vérifie que le jeu n'est pas en pause
                    if(!isPaused){
                        updateGame()
                    }
                    handler.postDelayed(this, 16L)
                }
            }


        }
        handler.post(gameLoopRunnable)
    }

    /**
     *
     */
    private fun updateGame() {
        // Déclarations de variables utile à l'update
        val screenHeight = resources.displayMetrics.heightPixels
        val joueurRect = joueur.getHitbox()
        val haloActif: Boolean

        // On vérifie si le halo de lumière est actuellement activé et on attribue vrai si c'est le cas
        if (ivHalo.alpha > 0.1f) {
            haloActif = true
        } else {
            haloActif = false
        }

        //----------------------------------------//
        //           Gestion des ennemis          //
        //----------------------------------------//
        val iteratorEnnemis = listeEnnemis.iterator()
        while (iteratorEnnemis.hasNext()) {
            val ennemi = iteratorEnnemis.next()

            // L'ennemi gère sa propre accélération
            ennemi.mettreAJourVitesse(chronoBase)
            ennemi.descendre()

            if (haloActif && ennemi.toucheHalo(ivHalo)) {
                ennemi.detruire()
                iteratorEnnemis.remove()
                continue
            }

            if (Rect.intersects(joueurRect, ennemi.getHitbox())) {
                prendreDegat()
                ennemi.detruire()
                iteratorEnnemis.remove()
                continue
            }

            if (ennemi.estSortiDeEcran(screenHeight)) {
                ennemi.detruire()
                iteratorEnnemis.remove()
            }
        }
        //----------------------------------------//
        //           Gestion des Ovnis            //
        //----------------------------------------//
        val iteratorOvnis = listeOvnis.iterator()
        while (iteratorOvnis.hasNext()) {
            val ovni = iteratorOvnis.next()

            // L'OVNI gère sa propre accélération
            ovni.mettreAJourVitesse(chronoBase)
            ovni.descendre()

            if (Rect.intersects(joueurRect, ovni.getHitbox())) {
                prendreDegat()
                ovni.supprimerVisuellement()
                iteratorOvnis.remove()
                continue
            }

            if (ovni.estSortiDeEcran(screenHeight)) {
                ovni.supprimerVisuellement()
                iteratorOvnis.remove()
            }
        }
        //----------------------------------------//
        //      Gestion des zones rouges          //
        //----------------------------------------//
        if (isEmpireActive) {
            val empireRect = Rect()

            if (typeEmpire == 0) {
                // --- LOGIQUE RECTANGLE---
                vZoneRougeRect.y += 10f
                vZoneRougeRect.getHitRect(empireRect)

                //Si joueur rentre en contact avec le rectangle
                if (Rect.intersects(joueurRect, empireRect) && haloActif) {
                    prendreDegat()
                }

                // Disparaît en bas de l'écran
                if (vZoneRougeRect.y > screenHeight) {
                    isEmpireActive = false
                    vZoneRougeRect.visibility = View.INVISIBLE
                }

            } else {
                // --- LOGIQUE CERCLE ---
                vZoneRougeCercle.x += vitesseCercleX // Bouge de gauche à droite
                vZoneRougeCercle.y += 10f // Tombe vers le bas

                // S'il touche un bord il repart dans l'autre sens
                if (vZoneRougeCercle.x <= 0 || vZoneRougeCercle.x >= (resources.displayMetrics.widthPixels - vZoneRougeCercle.width)) {
                    vitesseCercleX *= -1f
                }

                vZoneRougeCercle.getHitRect(empireRect)
                if (Rect.intersects(joueurRect, empireRect) && haloActif) {
                    prendreDegat()
                }

                // Disparaît en bas de l'écran
                if (vZoneRougeCercle.y > screenHeight) {
                    isEmpireActive = false
                    vZoneRougeCercle.visibility = View.INVISIBLE
                }
            }
        }


    }

    /**
     *
     */
    override fun onResume() {
        super.onResume()
        capteurManager.start()
        musicManager.start()
    }

    /**
     *
     */
    override fun onPause(){
        super.onPause()
        capteurManager.stop()
        musicManager.stop()
        runnable?.let { handler.removeCallbacks(it) }
    }

    /**
     * Gestion des dégàts
     */
    private fun prendreDegat() {
        val estMort = joueur.prendreDegats()
        //Animation de dégats
        val clignotement = android.view.animation.AlphaAnimation(1f, 0.2f)
        clignotement.duration = 150
        clignotement.repeatMode = android.view.animation.Animation.REVERSE
        clignotement.repeatCount = 6
        // On lance l'animation sur l'image du vaisseau
        ivVaisseau.startAnimation(clignotement)

        musicManager.jouerSonDegat()

        when (joueur.vies) {
            2 -> bVie3.visibility = View.INVISIBLE
            1 -> bVie2.visibility = View.INVISIBLE
            0 -> bVie1.visibility = View.INVISIBLE
        }

        if (estMort) {
            isGameOver = true

            // On stoppe les actions en cours
            runnable?.let { handler.removeCallbacks(it) }
            handler.removeCallbacksAndMessages(null)
            ivHalo.visibility = View.INVISIBLE
            afficherPopupGameOver()
        }
    }
    /**
     * Lancement du chronomètre
     */
    fun lancerChrono() {
        startTime = System.currentTimeMillis() // Heure actuelle
        chronoBase = SystemClock.elapsedRealtime()

        runnable = object : Runnable {
            override fun run() {
                if(!isGameOver){
                    if(!isPaused){
                        // Calcul du temps écoulé
                        val millis = System.currentTimeMillis() - startTime
                        val seconds = (millis / 1000).toInt()
                        val minutes = seconds / 60
                        val displaySeconds = seconds % 60

                        // Formatage du texte
                        tvChrono.text = String.format("%02d:%02d", minutes, displaySeconds)
                    }
                    handler.postDelayed(this, 1000)
                }
            }
        }
        // Démarrage immédiat
        handler.post(runnable!!)
    }

    /**
     * mettreEnPause() --> permet de mettre en pause le jeu en arrivant sur une popup du menu pause
     */
    private fun mettreEnPause(){
        isPaused = true

        // On sauvegarde le temps
        // On sauvegarde l'horloge du telephone (heureMiseEnPauseMillis) + chronomètre du processeur (heureMiseEnPauseRealtime) pour que quand le joueur reprends les ennemis n'auront pas eu l'acceleration même si le temps de du label est bien au bon moment
        //Si l'on ne fait pas ça, si le joueur mets pause 5 min et qu'il reprends, ils vont accélérer proportionnelement au temps de la pause
        val heureMiseEnPauseMillis = System.currentTimeMillis()
        val heureMiseEnPauseRealtime = SystemClock.elapsedRealtime()

        //Préparation de la vue
        val dialogView = layoutInflater.inflate(R.layout.pause_menu, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        // --- BOUTON REPRENDRE ---
        dialogView.findViewById<ImageView>(R.id.btnReprendre).setOnClickListener {
            dialog.dismiss()

            //Chrono : calcule combien de temps on est resté dans le menu pause
            val tempsPasseEnPauseMillis = System.currentTimeMillis() - heureMiseEnPauseMillis
            val tempsPasseEnPauseRealtime = SystemClock.elapsedRealtime() - heureMiseEnPauseRealtime

            //On mets a jour les valeurs avec le temps passé
            startTime += tempsPasseEnPauseMillis
            chronoBase += tempsPasseEnPauseRealtime

            isPaused = false
            musicManager.start()
        }

            // --- BOUTON QUITTER ---
            dialogView.findViewById<ImageView>(R.id.btnMenu).setOnClickListener {
                dialog.dismiss()
                val intent = android.content.Intent(this, MainActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }

            dialog.show()
    }
    /**
     * afficherPopupGameOver() --> affiche la popup lorsque le joueur perds et permet de gerer la sauvegarde en fin de partie
     */
    private fun afficherPopupGameOver() {
        //On calcule le temps final pour le tri de la sauvegarde
        val tempsFinalMillis = System.currentTimeMillis() - startTime
        //Temps final affiché lors de la mort
        val tempsAffichageFinal = tvChrono.text.toString()

        //On appel le scoreManager
        scoreManager = ScoreManager(this)
        val top3Scores = scoreManager.enregistrerScore(tempsFinalMillis)

        // ----- GESTION DE L'AFFICHAGE DE LA POPUP -----
        // Charge le design XML de la popup et le transforme en une Vue
        val dialogView = layoutInflater.inflate(R.layout.game_over, null)

        val tvScoreActuel = dialogView.findViewById<TextView>(R.id.tvScoreActuel)
        val tvTop1 = dialogView.findViewById<TextView>(R.id.tvTop1)
        val tvTop2 = dialogView.findViewById<TextView>(R.id.tvTop2)
        val tvTop3 = dialogView.findViewById<TextView>(R.id.tvTop3)
        val btnRejouer = dialogView.findViewById<ImageView>(R.id.btnRejouer)
        val btnMenu = dialogView.findViewById<ImageView>(R.id.btnMenu)

        tvScoreActuel.text = "Votre temps : $tempsAffichageFinal"

        // Pour le Top 3 on utilise la méthode formaterTemps du ScoreManager
        if (top3Scores.size > 0) tvTop1.text = "1. ${scoreManager.formaterTemps(top3Scores[0])}"
        if (top3Scores.size > 1) tvTop2.text = "2. ${scoreManager.formaterTemps(top3Scores[1])}"
        if (top3Scores.size > 2) tvTop3.text = "3. ${scoreManager.formaterTemps(top3Scores[2])}"

        // Affichage
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false) // Empêche de fermer en cliquant à côté

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnRejouer.setOnClickListener {
            dialog.dismiss()
            val intent = intent
            finish()
            startActivity(intent)
        }

        btnMenu.setOnClickListener {
            dialog.dismiss()
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
}
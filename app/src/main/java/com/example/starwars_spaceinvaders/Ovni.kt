package com.example.starwars_spaceinvaders

import android.graphics.Rect
import android.hardware.SensorEventListener
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

class Ovni {

    private val context: android.content.Context
    private val layout: ConstraintLayout
    private val screenWidth: Int

    val imageView: ImageView

    // Propriétés
    private var vitesseBase: Float = 12f
    private var vitesseMultiplicateur: Float = 1.0f

    // CONSTRUCTEUR
    constructor(
        context: android.content.Context,
        layout: ConstraintLayout,
        screenWidth: Int
    ) {
        this.context = context
        this.layout = layout
        this.screenWidth = screenWidth

        imageView = ImageView(context)

        imageView.setImageResource(choixTypeObjet())
        val dimension = (125..350).random()
        imageView.layoutParams = ConstraintLayout.LayoutParams(dimension, dimension)
        imageView.x = (0..(screenWidth - 120)).random().toFloat()
        imageView.y = -200f
        imageView.translationZ = 4f

        layout.addView(imageView)
    }

    // Fonction pour déterminer aléatoirement le vaisseau qui est créer
    private fun choixTypeObjet(): Int {
        //Liste des objets
        val objets = listOf(R.drawable.ovni1, R.drawable.ovni2, R.drawable.ovni3, R.drawable.ovni4, R.drawable.ovni5)
        return objets.random() // Choisi un objet parmi les 5 en mémoire
    }

    // --- GESTION DU TEMPS ET VITESSE ---
    fun mettreAJourVitesse(chronoBase: Long) {
        val secondesEcoulees = (SystemClock.elapsedRealtime() - chronoBase) / 1000
        vitesseMultiplicateur = 1.0f + (secondesEcoulees / 10) * 0.1f
    }

    fun descendre() {
        val vitesseActuelle = vitesseBase * vitesseMultiplicateur
        imageView.y += vitesseActuelle
        imageView.rotation += 2f
    }

    fun estSortiDeEcran(screenHeight: Int): Boolean {
        return imageView.y > screenHeight
    }

    fun getHitbox(): Rect {
        val rect = Rect()
        imageView.getHitRect(rect)
        return rect
    }

    fun supprimerVisuellement() {
        imageView.visibility = View.GONE
    }
}
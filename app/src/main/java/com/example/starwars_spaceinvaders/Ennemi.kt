package com.example.starwars_spaceinvaders

import android.graphics.Rect
import android.hardware.SensorEventListener
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout


class Ennemi  {
    private val context: android.content.Context
    private val screenWidth: Int
    private val layout: ConstraintLayout

    val imageView: ImageView

    // Vitesse de l'eznemi
    private var vitesseBase: Float = 10f
    private var vitesseMultiplicateur: Float = 1.0f

    // CONSTRUCTEUR
    constructor(context: android.content.Context, layout: ConstraintLayout, screenWidth: Int) {
        this.context = context
        this.layout = layout
        this.screenWidth = screenWidth
        imageView = ImageView(context)
        imageView.setImageResource(choixTypeEnnemi())
        val dimension = (125..400).random()
        imageView.layoutParams = ConstraintLayout.LayoutParams(dimension, dimension)
        imageView.x = (0..(screenWidth - 150)).random().toFloat()
        imageView.y = -200f
        imageView.translationZ = 3f
        layout.addView(imageView)
    }
    // Fonction pour déterminer aléatoirement le vaisseau qui est créer
    private fun choixTypeEnnemi(): Int {
        //Liste des vaisseau
        val ennemis = listOf(R.drawable.ennemi1, R.drawable.ennemi2, R.drawable.ennemi3, R.drawable.ennemi4, R.drawable.ennemi5)
        return ennemis.random() // Choisi un vaisseau parmi les 5 en mémoire
    }

    // --- GESTION DU TEMPS ET VITESSE ---
    fun mettreAJourVitesse(chronoBase: Long) {
        val secondesEcoulees = (SystemClock.elapsedRealtime() - chronoBase) / 1000
        vitesseMultiplicateur = 1.0f + (secondesEcoulees / 10) * 0.1f
    }

    fun descendre() {
        val vitesseActuelle = vitesseBase * vitesseMultiplicateur
        imageView.y += vitesseActuelle
    }

    fun estSortiDeEcran(screenHeight: Int): Boolean {
        return imageView.y > screenHeight
    }

    fun toucheHalo(halo: ImageView): Boolean {
        if (halo.alpha < 0.1f) return false

        val dx = (imageView.x + imageView.width / 2) - (halo.x + halo.width / 2)
        val dy = (imageView.y + imageView.height / 2) - (halo.y + halo.height / 2)
        val distance = kotlin.math.sqrt(dx * dx + dy * dy)

        return distance < (halo.width / 2)
    }

    fun detruire() {
        imageView.visibility = View.GONE
    }

    fun getHitbox(): Rect {
        val rect = Rect()
        imageView.getHitRect(rect)
        return rect
    }
}
package com.example.starwars_spaceinvaders

import android.graphics.Rect
import android.os.SystemClock
import android.view.View
import android.widget.ImageView

class Vaisseau {
    // PROPRIÉTÉS
    val image: ImageView
    private val ecranLargeur: Int
    private val ecranHauteur: Int
    var vies: Int = 3
    private var vitesseBase: Float = 3f

    // Pour éviter de perdre 3 vies en 1 seconde
    var estInvulnerable: Boolean = false


    // CONSTRUCTEUR DU VAISSEAU
    constructor(image: ImageView, ecranLargeur: Int, ecranHauteur: Int) {
        this.image = image
        this.ecranLargeur = ecranLargeur
        this.ecranHauteur = ecranHauteur
    }

    // --- MOUVEMENT DU VAISSEAU ---
    fun bouger(sensorX: Float, sensorY: Float) {
        val newX = image.x - (sensorX * vitesseBase)
        val newY = image.y + (sensorY * vitesseBase)

        // On empêche le vaisseau de sortir de l'écran (coerceIn)
        image.x = newX.coerceIn(0f, (ecranLargeur - image.width).toFloat())
        image.y = newY.coerceIn(0f, (ecranHauteur - image.height).toFloat())
    }

    // --- GESTION DES DÉGÂTS ---
    fun prendreDegats(): Boolean {
        // Si déjà touché récemment, on ignore
        if(estInvulnerable==false){
        vies--
        animToucher()

        if (vies <= 0) {
            image.visibility = View.INVISIBLE
            return true // Vrai pour dire que le joueur est mort
        }
        startInvulnerability()
        }
        return false // Le joueur est encore en vie

    }
    // RECUPRER LA HITBOX DU VAISSEAU
    fun getHitbox(): Rect {
        val rect = Rect()
        image.getHitRect(rect)
        return rect
    }

    // --- UTILITAIRES POUR LE HALO ---
    fun getCenterX(): Float = image.x + (image.width / 2)
    fun getCenterY(): Float = image.y + (image.height / 2)

    // --- ANIMATIONS QAUND DEGATS ---
    private fun animToucher() {
        // Clignotement rapide
        image.animate().alpha(0f).setDuration(100).withEndAction {
            image.animate().alpha(1f).duration = 100
        }
    }

    private fun startInvulnerability() {
        estInvulnerable = true
        image.alpha = 0.5f // Devient semi-transparent

        // Redevient vulnérable après 2 secondes
        image.postDelayed({
            estInvulnerable = false
            image.alpha = 1.0f
        }, 3000)
    }
}
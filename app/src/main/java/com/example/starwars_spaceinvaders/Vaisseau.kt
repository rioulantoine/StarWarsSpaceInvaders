package com.example.starwars_spaceinvaders

import android.os.SystemClock
import android.view.View
import android.widget.ImageView

class Vaisseau(
    val image: ImageView,
    private val ecranLargeur: Int,
    private val ecranHauteur: Int
) {
    // PROPRIÉTÉS
    var vies: Int = 3
        private set // On peut lire, mais modifiable uniquement via la méthode prendreDegats()

    private var vitesseBase: Float = 3f
    private var vitesseMultiplicateur: Float = 1.001f

    // Pour éviter de perdre 3 vies en 1 seconde
    var estInvulnerable: Boolean = false

    // --- MOUVEMENT ---
    fun bouger(sensorX: Float, sensorY: Float) {
        // La vitesse augmente avec le temps
        val vitesseActuelle = vitesseBase * vitesseMultiplicateur

        // Calcul de la nouvelle position
        // (On inverse X car l'accéléromètre est inversé par rapport à l'écran)
        val newX = image.x - (sensorX * vitesseActuelle)
        val newY = image.y + (sensorY * vitesseActuelle)

        // On empêche le vaisseau de sortir de l'écran (coerceIn)
        image.x = newX.coerceIn(0f, (ecranLargeur - image.width).toFloat())
        image.y = newY.coerceIn(0f, (ecranHauteur - image.height).toFloat())
    }

    // --- GESTION DU TEMPS ET VITESSE ---
    fun mettreAJourVitesse(chronoBase: Long) {
        // Calcule le temps écoulé en secondes depuis le lancement du chrono
        val secondesEcoulees = (SystemClock.elapsedRealtime() - chronoBase) / 1000

        // Formule : +10% de vitesse toutes les 15 secondes
        // Exemple : à 30 sec, vitesse x 1.2
        vitesseMultiplicateur = 1.0f + (secondesEcoulees / 15) * 0.1f
    }

    // --- GESTION DES DÉGÂTS ---
    fun prendreDegats(): Boolean {
        if (estInvulnerable) return false // Si déjà touché récemment, on ignore

        vies--
        animToucher() // Effet visuel

        if (vies <= 0) {
            image.visibility = View.INVISIBLE // Game Over visuel
            return true // Retourne "Vrai" pour dire que le joueur est mort
        }

        startInvulnerability()
        return false // Le joueur est encore en vie
    }

    // --- UTILITAIRES POUR LE HALO ---
    fun getCenterX(): Float = image.x + (image.width / 2)
    fun getCenterY(): Float = image.y + (image.height / 2)

    // --- ANIMATIONS INTERNES ---
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
        }, 2000)
    }
}
package com.example.starwars_spaceinvaders

import android.content.Context
import org.json.JSONArray
import java.io.File


class ScoreManager(pContext: Context) {
    private val fileName = "scores.json"
    private val context = pContext

    /**
     * enregistrerScore() --> Enregistre le score et renvoie le Top 3
     */
    fun enregistrerScore(nouvTemps : Long): List<Long> {
        val scores = chargerScores().toMutableList()
        scores.add(nouvTemps)

        // Trie du plus grand au plus petit temps de survie
        scores.sortDescending()

        // Garde uniquement les 3 meilleurs
        val top3 = scores.take(3)

        // Sauvegarde en JSON
        val jsonArray = JSONArray()
        top3.forEach { jsonArray.put(it) }
        val file = File(context.filesDir, fileName)
        file.writeText(jsonArray.toString())

        return top3
    }

    /**
     * chargerScores() --> Lit le fichier JSON et renvoie la liste des scores actuels
     */
    fun chargerScores(): List<Long> {
        val file = File(context.filesDir, fileName)

        // Si le joueur a jamais joué on renvoie une liste vide
        if (!file.exists()) {
            return emptyList()
        }

        // On lit le fichier
        val jsonString = file.readText()
        val jsonArray = JSONArray(jsonString)
        val scores = mutableListOf<Long>()

        // On parcourt le JSON pour avoir les scores
        for (i in 0 until jsonArray.length()) {
            scores.add(jsonArray.getLong(i))
        }

        return scores
    }

    /**
     * formaterTemps() --> Permet de convertir les millisecondes en texte
     */
    fun formaterTemps(millis: Long): String {
        val seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        val displaySeconds = seconds % 60
        return String.format("%02d:%02d", minutes, displaySeconds)
    }
}
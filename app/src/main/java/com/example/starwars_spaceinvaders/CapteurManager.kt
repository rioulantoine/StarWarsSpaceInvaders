package com.example.starwars_spaceinvaders



import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.appcompat.app.AlertDialog


class CapteurManager (pContext: InfiniteGameActivity) : SensorEventListener {

    //Variables
    private val context = pContext
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val accelerometre = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val largeurEcran: Int = context.resources.displayMetrics.widthPixels
    val hauteurEcran: Int = context.resources.displayMetrics.heightPixels

    init {
        start()
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) == null) {
            windowPasCapteurNecessaire()
        }
    }

    private fun windowPasCapteurNecessaire() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setTitle("Terminal incompatible")
            .setMessage("Besoin de capteur de lumière l'ekip")
            .setPositiveButton("OK") { dialog, which ->
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun afficheCapteur() {
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (s in deviceSensors) {
            Log.e("Antoine", s.stringType)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        // --- GESTION LUMIÈRE ---
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lux = event.values[0]
            context.tvLumiere.text = "Luminosité : $lux"

            if (lux > 130) {
                context.vObscurite.animate().alpha(0f).setDuration(500).start()
                context.ivHalo.animate().alpha(0.6f).setDuration(500).start() // Allume le halo
            } else {
                context.vObscurite.animate().alpha(1f).setDuration(500).start()
                context.ivHalo.animate().alpha(0f).setDuration(500).start()

            }
        }

        // --- GESTION MOUVEMENT ---
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sensorX = event.values[0]
            val sensorY = event.values[1]

            var newX = context.ivVaisseau.x - (sensorX * 3)
            var newY = context.ivVaisseau.y + (sensorY * 3)

            val maxX = largeurEcran - context.ivVaisseau.width.toFloat()
            val maxY = hauteurEcran - context.ivVaisseau.height.toFloat()

            // On déplace le vaisseau
            context.ivVaisseau.x = newX.coerceIn(0f, maxX)
            context.ivVaisseau.y = newY.coerceIn(0f, maxY)

            val centreVaisseauX = context.ivVaisseau.x + (context.ivVaisseau.width / 2)
            val centreVaisseauY = context.ivVaisseau.y + (context.ivVaisseau.height / 2)

            // Change ces valeurs (en pixels) pour décaler le halo
            val monDecalageX = -15
            val monDecalageY = -280 // |-| --> vers le haut | |+| --> vers le bas

            context.ivHalo.x = centreVaisseauX - (context.ivHalo.width / 2) + monDecalageX
            context.ivHalo.y = centreVaisseauY - (context.ivHalo.height / 2) + monDecalageY
        }
    }

    fun start() {
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerometre, SensorManager.SENSOR_DELAY_GAME)

    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

}
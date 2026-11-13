package com.example.starwars_spaceinvaders

import android.content.Context
import android.app.Activity
import android.media.MediaPlayer
import android.media.SoundPool

class MusicManager private constructor(context: Context) {

    private var mediaPlayer: MediaPlayer? = MediaPlayer.create(context.applicationContext, R.raw.maintheme)
    private var soundPool: SoundPool = SoundPool.Builder().setMaxStreams(5).build()
    private var mapDeSon: HashMap<String, Int> = HashMap()

    init {
        mediaPlayer?.apply {
            setVolume(0.3f, 0.3f)
            isLooping = true
        }
        // Chargement de tes sons
        mapDeSon["son1"] = soundPool.load(context, R.raw.maintheme, 1)
        mapDeSon["degat"] = soundPool.load(context, R.raw.sondegat, 1)
    }

    /*
    équivalent du static en Java, permet de garder une instance unique en mémoire
     */
    companion object {
        private var INSTANCE: MusicManager? = null
        fun getInstance(context: Context): MusicManager {
            return INSTANCE ?: synchronized(this) {
                val instance = MusicManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    fun start() {
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun stop() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }
    fun playSound(soundKey: String) {
        mapDeSon[soundKey]?.let { soundId ->
            soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1f)
        }
    }
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool.release()
        INSTANCE = null
    }
    fun jouerSonDegat() {
        mapDeSon["degat"]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
        }    }
}
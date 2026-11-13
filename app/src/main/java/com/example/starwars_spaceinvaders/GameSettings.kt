package com.example.starwars_spaceinvaders

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException

class GameSettings : AppCompatActivity() {

    private lateinit var loadButton: Button

    //Bouton jouer
    private lateinit var playButton: Button

    //Partie Bg
    private lateinit var ivBg: ImageView
    private lateinit var swBackground: Switch
    private lateinit var mainLayout: ConstraintLayout
    private var selectedImageUri: Uri? = null

    // Launcher pour sélectionner l'image depuis la galerie
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                displayImageInImageView(uri)
                Toast.makeText(this, "Image chargée dans l'aperçu !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_infinite_settings)

        // Initialisation des vues
        loadButton = findViewById(R.id.LoadButton)
        ivBg = findViewById(R.id.ivBg)
        swBackground = findViewById(R.id.swBackground)
        mainLayout = findViewById(R.id.main)

        playButton = findViewById(R.id.PlayButton)
        //Bouton lancement du jeu
        playButton.setOnClickListener { lancementJeu() }

        // Bouton Load - ouvre la galerie
        loadButton.setOnClickListener {
            openGallery()
        }

        // Switch - applique le background quand activé
        swBackground.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && selectedImageUri != null) {
                applyBackgroundImage(selectedImageUri!!)
                Toast.makeText(this, "Background appliqué !", Toast.LENGTH_SHORT).show()
            } else if (!isChecked) {
                mainLayout.setBackgroundColor(0) // Reset background
                Toast.makeText(this, "Background par défaut", Toast.LENGTH_SHORT).show()
            }
        }

        // Gestion des WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Ouvre la galerie
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    // Affiche l'image sélectionnée dans l'ImageView (aperçu)
    private fun displayImageInImageView(imageUri: Uri) {
        ivBg.setImageURI(imageUri)
        ivBg.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    // Applique l'image comme background du layout principal
    private fun applyBackgroundImage(imageUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            mainLayout.background = android.graphics.drawable.BitmapDrawable(resources, bitmap)

        } catch (e: IOException) {
            Toast.makeText(this, "Erreur chargement: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /*
    Méthode du lancement du jeu une fois les paramètres finis
     */
    fun lancementJeu(){
        val intent = Intent(this, InfiniteGameActivity::class.java)
        startActivity(intent)
    }
}

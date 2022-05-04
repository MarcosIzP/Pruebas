package com.tfg.aplicacio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Analiticas
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("mensaje", "Integracion de Firebase Analytics")
        analytics.logEvent("PantallaInicio", bundle)
    }
}
package com.tfg.aplicacio

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType {
//Tipo de autenticacion
//BASIC : autenticacion por email y contraseña
//GOOGLE : cuenta de google
    BASIC,
    GOOGLE

}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //creacion de un bundle que contiene el intent para poder recuperar los datos de email y proveedor
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        //llamada de la funcion "setup" que se va a encargar de la acción de registro,
        // pero esta vez le pasamos los parámetros obtenidos de la otra actividad
        setup(email ?: "", provider ?: "")

        //Mediante lo siguiente, conseguirmos que la cuenta del ususario que inicia sesión se quede guardada.
        //Creacion de una constante que contendrá un archivo de tipo clave valor
        val preferencias = getSharedPreferences(getString(R.string.fichero_preferencias), Context.MODE_PRIVATE).edit()
        //Guradado de preferencias (email y proveedor)
        preferencias.putString("email", email)
        preferencias.putString("proveedor", provider)
        preferencias.apply()
    }

    private fun setup(email: String, provider: String){

        //titulo que aparecerá arriba a la derecha
        title ="Inicio"
        emailtextView.text = email
        proveedortextview.text = provider

        logOutButton.setOnClickListener {

            //Una vez se pulse el boton de cerrar sesion se eliminaran las preferencias guardadas

            val preferencias = getSharedPreferences(getString(R.string.fichero_preferencias), Context.MODE_PRIVATE).edit()
            preferencias.clear()
            preferencias.apply()

            //Llamada a los sevicios de firebase
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
            //esta última línea sirve para una vez cerrada la sesión, volver a la pantalla de registro
        }
    }
}
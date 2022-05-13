package com.tfg.aplicacio

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    //constantes globales
    private val GOOGLE_SIGN_IN_CODE = 100

    //variables


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //Analiticas
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("mensaje", "Integracion de Firebase Analytics")
        analytics.logEvent("PantallaInicio", bundle)

        //llamada de la funcion "setup" que se va a encargar de la acción de registro
        setup()

        //Llamada de la función "sesion"
        sesion()

        //LLamada de la funcion que nos lleva a la pantalla de música
        music()
    }

    //a veces si la sesión queda iniciada y se cierra sesion, la pantalla de autenticacion permancece invisible
    //así que para asegurarse de que se muestre se vuelve a iniciar la vista aquí
    override fun onStart() {
        super.onStart()
        layout_auth.visibility = View.VISIBLE
    }

//Reproduccion

    private fun music() {



        song_screen.setOnClickListener {
            showmusic()
        }
    }


//Autenticacion

    //este siguiente bloque no funciona
    //Creacion de una funcion que se encargara de comprobar si existe una sesion activa (email, google, facebook)
    private fun sesion() {
        //Constante que guardará las preferencias pero esta vez sin el método "edit()", ya que no se van a editar datos, solo comprobar
        val preferencias = getSharedPreferences(getString(R.string.fichero_preferencias), Context.MODE_PRIVATE)
        //Constante que comprobará el email
        val email: String? = preferencias.getString("email", null)
        //Constante que comprobará el proveedor
        val provider: String? = preferencias.getString("provider", null)

        //Bucle que comprobará si existen los dos parámetros anteriores,
        // y si es así, nos enviará a la siguiente pantalla sin que tengamos que iniciar manualamente sesion
        if (email != null && provider != null) { //Mediante una operacion AND

            //Esta línea hará invisible la pantalla de autenticación si ya existe una sesion iniciada
            layout_auth.visibility = View.INVISIBLE

            //Nos enviará a la siguiente pantalla
            showHome(email, ProviderType.valueOf(provider))
        }

    }

    //creación de una funcion privada llamada setup
    private fun setup() {
        //titulo que aparecerá arriba a la derecha
        title = "Autenticacion"


        //Cuando se pulse el boton se ejecutara lo siguiente
        btn_registrar.setOnClickListener {
            //Se comprueba que los campos no estan vacíos (Se realiza una operacion AND
            if (intr_email.text.isNotEmpty() && intr_pw.text.isNotEmpty()) {
                //Uso de los servicios de autenticacion de Firebase
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    intr_email.text.toString(),
                    intr_pw.text.toString()
                ).addOnCompleteListener {
                    //Se comprobará si la operacion se ha realizado correctamente
                    if (it.isSuccessful) {
                        //Si los datos son correctos se llama a la funcion "showhome" que contiene en intent
                        //Se utiliza el enum creado en la actividad home
                        it.result?.user?.email?.let { it1 -> showHome(it1, ProviderType.BASIC) }
                    } else {
                        alerta()
                    }
                }
            }
        }

        btn_acceder.setOnClickListener {
            //Se comprueba que los campos no estan vacíos (Se realiza una operacion AND
            if (intr_email.text.isNotEmpty() && intr_pw.text.isNotEmpty()) {
                //Uso de los servicios de autenticacion de Firebase
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    intr_email.text.toString(),
                    intr_pw.text.toString()
                ).addOnCompleteListener {
                    //Se comprobará si la operacion se ha realizado correctamente
                    if (it.isSuccessful) {
                        //Si los datos son correctos se llama a la funcion "showhome" que contiene en intent
                        //Se utiliza el enum creado en la actividad home
                        it.result?.user?.email?.let { it1 -> showHome(it1, ProviderType.BASIC) }
                    } else {
                        alerta()
                    }
                }
            }
        }

        //Se ejecutará si se pulsa la opción de registrarse con google
        btn_google.setOnClickListener {

            //Se instancian los servicios de inicio de sesion de google
            //Para este paso se necesita haber generado el certificado de SHA-1 y haberlo añadido a el fichero strings. Se puede generar ejecutando el fichero signingreport desde gradle
            val initGoogle = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("36828474316-2tcie8802hdja88fnq2v418roh4gku51.apps.googleusercontent.com")
                .requestEmail().build()

            //Una vez configurada la auth con google, hay que configurar el cliente de google
            val googleClient = GoogleSignIn.getClient(this, initGoogle)

            //Se configura un cierre de sesión para que cada vez que se pulse el botón para iniciar sesion con google,
            // se cierre sesion de otra posible cuenta ya logeada que estuviese guardando el sistema
            googleClient.signOut()

            //Ahora se mostrará la pantalla de autenticacion de google,
            // y le pasaremos un identificador que funcionará como un código de petición para comprobar que los datos son correctos
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN_CODE)

        }

    }

    //Creación de una funcion que se utilizara cuando no se hayan introducido correctamente los datos de registro
    private fun alerta() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //Creacion de una funcion que nos llevará a la siguiente pantalla (HomeActivity)
    private fun showHome(email: String, provider: ProviderType) {
        //Creacion de un intent que nos redigirá a la pantalla home
        val homeIntent: Intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("proveedor", provider)
        }
        startActivity(homeIntent)
    }

    //Para que la actividad responda a ese intetno de autenticacion se realiza lo siguiente
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Se comprueba que el codigo de esa actividad es igual a la constante global creada, si es igual,
        // significará que se esta intentando autenticar con google
        if (requestCode == GOOGLE_SIGN_IN_CODE) {

            //Ahora se configuran los servicios que proporciona firebase para iniciar sesión con google
            //Primero se crea una constante que será guardad más adelante por otra. Se encarga de guardar los datos de inicio de sesion
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            //A veces, "ActivityonReuslt" puede llegar a dar error,
            // ya que si no existe la cuenta, la siguiente lñínea de código que se encarga de recuperar la cuenta, no la estaría encontrando.
            try {
                //recuperar la cuenta autenticada
                val account = task.getResult(ApiException::class.java)

                //se comprueba que la cuenta existe
                if (account != null) {

                    //Autenticacion en google
                    val credenciales = GoogleAuthProvider.getCredential(account.idToken, null)

                    //Autenticacion en firebase. Le pasasmos la constante creada, para que el ususario salga en la consola de Firebase
                    FirebaseAuth.getInstance().signInWithCredential(credenciales)
                        .addOnCompleteListener {
                            //Se ejecuta la funcion "Addoncompletelistener()" para saber cuando ha finalizado la utenticacion en firebase
                            if (it.isSuccessful) {

                                //Si los datos son correctos se llama a la funcion "showhome" que contiene en intent
                                //Se le indica la cuenta de email, la que hemos recuperado mediante la constante "Account",
                                // y el tipo proveedor que se ha indicado en la otra actividad (GOOGLE)
                                showHome(account.email ?: "", ProviderType.GOOGLE)

                            } else {
                                //Si ocurre algún error se ejecuta la funcion creada "alerta()"
                                alerta()

                            }
                        }
                }
            } catch (e: ApiException) {
                //Si ocurre algún error se ejecuta la funcion creada "alerta()"
                alerta()
            }

        }
    }


//Reproduccion de Audio

    private fun showmusic() {
        //Creacion de un intent que nos redigirá a la pantalla home
        val musicIntent:Intent = Intent (this, MusicActivity::class.java).apply {

        }
        startActivity(musicIntent)

    }
}








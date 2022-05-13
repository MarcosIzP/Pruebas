package com.tfg.aplicacio

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class MusicActivity : AppCompatActivity() {

//Variables

    //Variable que contendra el objeto de mediaplayer creada de manera "perezosa".
    // De esta manera la variable no tomará variable hasta que sea llamada por el "oncreate", cuando se inicia la actividad
    val repr1 by lazy {
        assets.openFd("lady_hearmetonight.mp3")
    }

    //Variable que contendra el objeto de mediaplayer creada de manera "perezosa".
    // De esta manera la variable no tomará variable hasta que sea llamada por el "oncreate", cuando se inicia la actividad
    val mp by lazy {
        val player = MediaPlayer()

        //Descriptor de fichero
        player.setDataSource(
            repr1.fileDescriptor,
            repr1.startOffset,
            repr1.length
        )
        repr1.close()

        //Es necesario preparar antes el objeto, ya que si no a la hora de utilizar el método start(), daría error
        player.prepare()

        //Se pone la variable creada con el descriptor de fichero para que se iguale a la primera creada, que es mp
        player

    }

    //Array de los que serán 4 objetos boton
    val controladores by lazy {

        listOf(R.id.btn_pre, R.id.btn_stop, R.id.btn_cont, R.id.btn_next).map {
            findViewById<MaterialButton>(it)
        }
    }

    //Objeto que me indicará el índice de cada boton, pudiendo editar su posicion tmabién
    object indice {
        val btn_pre = 0
        val btn_stop = 1
        val btn_cont = 2
        val btn_next = 3
    }

    val nombre_cancion by lazy {
        findViewById<TextView>(R.id.song_name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        //titulo arriba a la izquierda
        titulo()

        //Inicio del reproductor
        controladores[indice.btn_cont].setOnClickListener(this::playbutton)
        controladores[indice.btn_stop].setOnClickListener(this::stopbutton)

        //Nombre cancion
        nombre_cancion.text = "Lady - Hear Me Tonight"
    }

    private fun titulo() {
        title = "Escuchar Música"
    }

    //Las siguientes funciones controlan que pasa cuando se pulsan los botones. Como se trata de un elemento de la interfaz, hay que poner la vista de parámetro
    //boton de continuar
     fun playbutton(v: View) {
        //Si la funcion no se está reproduciendo cuando se pulsa el boton, se reproduce. Si se esta reproduciendo, la para
        if (!mp.isPlaying) {
            mp.start()
            //Cuando pulsemos esto permitirá que cambie le icono
            controladores[indice.btn_cont].setIconResource(R.drawable.ic_baseline_pause_48)
            //El nombre de la cancion aparecera
            nombre_cancion.visibility= View.VISIBLE
        } else {
            mp.pause()
            controladores[indice.btn_cont].setIconResource(R.drawable.ic_baseline_play_arrow_48)
        }
    }

    //boton parar
    fun stopbutton(v: View) {

        if (mp.isPlaying) {
            mp.pause()
            //Para que cuando pulsemos reiniciar,k no se quede en el mismo estado el boton de play
            controladores[indice.btn_cont].setIconResource(R.drawable.ic_baseline_play_arrow_48)

            nombre_cancion.visibility = View.INVISIBLE
        }
        mp.seekTo(0)
    }
}
package com.desailly.chat_sensuadesires.Perfil

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.desailly.chat_sensuadesires.R

private lateinit var ImagenPerfilActualizar : ImageView
private  lateinit var BtnElegirImagen : Button
private  lateinit var BtnActualizarImagen : Button
private var imageUri : Uri?=null

class EditarImagenPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_imagen_perfil)

        ImagenPerfilActualizar = findViewById(R.id.ImagenPerfilActualizar)
        BtnElegirImagen = findViewById(R.id.BtnElegirImagenDe)
        BtnActualizarImagen = findViewById(R.id.BtnActualizarImagen)

        BtnElegirImagen.setOnClickListener {
        MostrarDialog()
        // Toast.makeText(applicationContext, "Seleccionar Imagen", Toast.LENGTH_SHORT).show()
        }

        BtnActualizarImagen.setOnClickListener {
            Toast.makeText(applicationContext, "Actualizar Imagen", Toast.LENGTH_SHORT).show()
        }
    }
    private fun  AbrirGaleria(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        galeriaActivityResultLauncherActivity.launch(intent)
    }
    private  val galeriaActivityResultLauncherActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback <ActivityResult>{resultado->
            if(resultado.resultCode == RESULT_OK){
                val data = resultado.data
                imageUri = data!!.data
                ImagenPerfilActualizar.setImageURI(imageUri)
            }else{
                Toast.makeText(applicationContext, "Cancelado por el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun AbrirCamara(){
        val values=ContentValues()
            values.put(MediaStore.Images.Media.TITLE,"Titulo")
            values.put(MediaStore.Images.Media.DESCRIPTION,"Descripcion")
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            camaraActivityResultLauncher.launch(intent)

    }

    private val camaraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado_camara->
            if (resultado_camara.resultCode == RESULT_OK){
                ImagenPerfilActualizar.setImageURI(imageUri)
            }else{
                Toast.makeText(applicationContext, "Cancelado por el Usuario", Toast.LENGTH_SHORT).show()
            }
        }


    private fun MostrarDialog(){
        val Btn_abrir_galeria : Button
        val Btn_abrir_camara : Button

        val dialog = Dialog(this@EditarImagenPerfil)

        dialog.setContentView(R.layout.cuadro_d_seleccionar)

        Btn_abrir_galeria = dialog.findViewById(R.id.Btn_abrir_galeria)
        Btn_abrir_camara = dialog.findViewById(R.id.Btn_abrir_camara)

        Btn_abrir_galeria.setOnClickListener {
           // Toast.makeText(applicationContext, "Abrir galeria", Toast.LENGTH_SHORT).show()
             AbrirGaleria()
             dialog.dismiss()
        }
        Btn_abrir_camara.setOnClickListener {
           // Toast.makeText(applicationContext, "Abrir camara", Toast.LENGTH_SHORT).show()
            AbrirCamara()
            dialog.dismiss()
        }
        dialog.show()

    }
}
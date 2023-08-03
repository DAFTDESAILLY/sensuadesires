package com.desailly.chat_sensuadesires.Perfil

import android.app.Dialog
import android.app.ProgressDialog
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

private lateinit var ImagenPerfilActualizar : ImageView
private  lateinit var BtnElegirImagen : Button
private  lateinit var BtnActualizarImagen : Button
private var imageUri : Uri?=null

private lateinit var firebaseAuth: FirebaseAuth
private lateinit var progressDialog: ProgressDialog

class EditarImagenPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_imagen_perfil)

        ImagenPerfilActualizar = findViewById(R.id.ImagenPerfilActualizar)
        BtnElegirImagen = findViewById(R.id.BtnElegirImagenDe)
        BtnActualizarImagen = findViewById(R.id.BtnActualizarImagen)

        progressDialog = ProgressDialog(this@EditarImagenPerfil)
        progressDialog.setTitle("espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()


        BtnElegirImagen.setOnClickListener {
        MostrarDialog()
        // Toast.makeText(applicationContext, "Seleccionar Imagen", Toast.LENGTH_SHORT).show()

        }

        BtnActualizarImagen.setOnClickListener {
        ValidarImagen()
        // Toast.makeText(applicationContext, "Actualizar Imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ValidarImagen(){
        if(imageUri == null){
            Toast.makeText(applicationContext, "Es necesario una imagen", Toast.LENGTH_SHORT).show()
        }else{
            SubirImagen()
        }
    }

    private fun SubirImagen() {
        progressDialog.setMessage("Actualizando Imagen")
        progressDialog.show()
        val rutaImagen = "Perfil_usuario/"+ firebaseAuth.uid
        val referenceStore = FirebaseStorage.getInstance().getReference(rutaImagen)
        referenceStore.putFile(imageUri!!).addOnSuccessListener {tarea->
            val uriTarea : Task<Uri> = tarea.storage.downloadUrl
            while (!uriTarea.isSuccessful);
            val urlImagen = "${uriTarea.result}"
            ActualizarImagenBD(urlImagen)

        }.addOnFailureListener {e->
            Toast.makeText(applicationContext, "No se ha podido subir la imagen debido a:${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ActualizarImagenBD(urlImagen: String) {
        progressDialog.setMessage("Actualizar imagen de perfil")
        val hashMap : HashMap<String,Any> = HashMap()
        if (imageUri != null){
            hashMap["imagen"] = urlImagen
        }

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(firebaseAuth.uid!!).updateChildren(hashMap).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext, "Imagen Actualizada", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {e->
            Toast.makeText(applicationContext, "No se ha actualizado tu imagen debido a ${e.message}", Toast.LENGTH_SHORT).show()

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
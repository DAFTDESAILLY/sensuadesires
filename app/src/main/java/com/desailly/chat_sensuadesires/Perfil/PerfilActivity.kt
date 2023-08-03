package com.desailly.chat_sensuadesires.Perfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.desailly.chat_sensuadesires.Modelo.Usuario
import com.desailly.chat_sensuadesires.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PerfilActivity : AppCompatActivity() {

    private lateinit var P_imagen : ImageView
    private lateinit var P_n_usuario : TextView
    private lateinit var P_email : TextView
    private lateinit var P_nombre : TextView
    private lateinit var P_apellido : TextView
    private lateinit var P_profesion : TextView
    private lateinit var P_domicilio : TextView
    private lateinit var P_edad : TextView
    private lateinit var P_telefono : TextView
    private lateinit var Btn_guardar : Button
    private lateinit var Editar_imagen : ImageView

    var user :FirebaseUser? = null
    var reference : DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        InicializarVariables()
        ObtenerDatos()
        Btn_guardar.setOnClickListener {
            ActualizarInformacion()
        }
        Editar_imagen.setOnClickListener{
            val intent = Intent(applicationContext,EditarImagenPerfil::class.java)
            startActivity(intent)
        }
    }

    private fun InicializarVariables(){
        P_imagen = findViewById(R.id.P_imagen)
        P_n_usuario = findViewById(R.id.P_n_usuario)
        P_email = findViewById(R.id.P_email)
        P_nombre = findViewById(R.id.P_nombre)
        P_apellido = findViewById(R.id.P_apellido)
        P_profesion = findViewById(R.id.P_profesion)
        P_domicilio = findViewById(R.id.P_domicilio)
        P_edad = findViewById(R.id.P_edad)
        P_telefono = findViewById(R.id.P_telefono)
        Btn_guardar = findViewById(R.id.Btn_Guardar)
        Editar_imagen=findViewById(R.id.Editar_imagen)

        user = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(user!!.uid)
    }

    private fun ObtenerDatos(){
        reference!!.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists()){
                   //Obtener datos firebase
                   val usuario : Usuario?=snapshot.getValue(Usuario::class.java)
                   val str_n_usuario = usuario!!.getN_Usuario()
                   val str_email = usuario.getEmail()
                   val str_nombre = usuario.getNombres()
                   val str_apellidos = usuario.getApellidos()
                   val str_profesion = usuario.getProfesion()
                   val str_domicilio = usuario.getDomicilio()
                   val str_edad = usuario.getEdad()
                   val str_telefono = usuario.getTelefono()

                   //seteo la informacion

                   P_n_usuario.text = str_n_usuario
                   P_email.text = str_email
                   P_nombre.text = str_nombre
                   P_apellido.text = str_apellidos
                   P_profesion.text = str_profesion
                   P_domicilio.text = str_domicilio
                   P_edad.text = str_edad
                   P_telefono.text = str_telefono
                   Glide.with(applicationContext).load(usuario.getImagen()).placeholder(R.drawable.ic_item_usuario).into(P_imagen)

               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun ActualizarInformacion(){
        val str_nombre = P_nombre
        val str_apellidos = P_apellido
        val str_profesion = P_profesion
        val str_domicilio = P_domicilio
        val str_edad = P_edad
        val str_telefono = P_telefono

        val hashMap = HashMap<String,Any>()
        hashMap["nombres"] = str_nombre.text.toString()
        hashMap["apellidos"] = str_apellidos.text.toString()
        hashMap["profesion"] = str_profesion.text.toString()
        hashMap["domicilio"] = str_domicilio.text.toString()
        hashMap["edad"] = str_edad.text.toString()
        hashMap["telefono"] = str_telefono.text.toString()

        reference!!.updateChildren(hashMap).addOnCompleteListener {task->
            if (task.isSuccessful){
                Toast.makeText(applicationContext, "Se han actualizado los datos", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, "No se han actualizado los datos", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {e->
            Toast.makeText(applicationContext, "ha ocurrido un error ${e.message}", Toast.LENGTH_SHORT).show()

        }

    }
}
package com.desailly.chat_sensuadesires

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegistroActivity : AppCompatActivity() {

    private lateinit var R_Et_nombre_usuario : EditText
    private lateinit var R_Et_email : EditText
    private lateinit var R_Et_password : EditText
    private lateinit var R_Et_r_password : EditText
    private lateinit var Btn_registrar : Button

    private lateinit var auth: FirebaseAuth
    private lateinit var reference:DatabaseReference

    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        supportActionBar?.title="Registros"
        InicializarVariables()

        Btn_registrar.setOnClickListener {
            ValidarDatos()
        }

    }



    private fun InicializarVariables(){
        R_Et_nombre_usuario=findViewById(R.id.R_Et_nombre_usuario)
        R_Et_email=findViewById(R.id.R_Et_email)
        R_Et_password=findViewById(R.id.R_Et_password)
        R_Et_r_password=findViewById(R.id.R_Et_r_password)
        Btn_registrar = findViewById(R.id.Btn_registrar)
        auth = FirebaseAuth.getInstance()

        //configurar progress dialog

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Registrando informacion")
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun ValidarDatos() {
        val nombre_usuario : String =   R_Et_nombre_usuario.text.toString()
        val email : String = R_Et_email.text.toString()
        val password : String = R_Et_password.text.toString()
        val r_password : String = R_Et_r_password.text.toString()

        if (nombre_usuario.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese nombre de usuario", Toast.LENGTH_SHORT).show()
        }
        else if (email.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese Correo", Toast.LENGTH_SHORT).show()
        }
        else  if (password.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese password", Toast.LENGTH_SHORT).show()
        }
        else if (r_password.isEmpty()){
            Toast.makeText(applicationContext, "repetir password", Toast.LENGTH_SHORT).show()
        }
        else if(!password.equals(r_password)){
            Toast.makeText(applicationContext, "Contraseña no coinciden", Toast.LENGTH_SHORT).show()
        }
        else{
            RegistrarUsuario(email,password)
        }
    }

    private fun RegistrarUsuario(email: String, password: String) {
        progressDialog.setMessage("Espere por favor")
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    var uid : String=""
                    uid = auth.currentUser!!.uid
                    reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(uid)

                    val hashMap = HashMap<String,Any>()
                    val h_nombre_usuario : String = R_Et_nombre_usuario.text.toString()
                    val h_email : String = R_Et_email.text.toString()


                    hashMap["uid"] = uid
                    hashMap["n_usuario"] = h_nombre_usuario
                    hashMap["email"] = h_email
                    hashMap["imagen"] = ""
                    hashMap["buscar"] = h_nombre_usuario.lowercase()

                    /*Datos de usuario*/
                    hashMap["nombres"] =  ""
                    hashMap["apellidos"]= ""
                    hashMap["edad"] = ""
                    hashMap["profesion"] = ""
                    hashMap["domicilio"] = ""
                    hashMap["telefono"] = ""
                    hashMap["estado"] = "offline"
                    hashMap["proveedor"]="Email"


                    reference.updateChildren(hashMap).addOnCompleteListener{task2->
                        if (task2.isSuccessful){
                            val intent = Intent(this@RegistroActivity,MainActivity::class.java)
                            Toast.makeText(applicationContext, "Exito", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                        }

                    }.addOnFailureListener {e->
                        Toast.makeText(applicationContext, "{${e.message}}", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "ha ocurrido un error", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "{${e.message}}", Toast.LENGTH_SHORT).show()
            }
    }
}
package com.desailly.chat_sensuadesires

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Inicio : AppCompatActivity() {
    private  lateinit var Btn_ir_logeo:Button
    private  lateinit var Btn_ir_registro:Button

    var FirebaseUser : FirebaseUser?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)


        Btn_ir_registro = findViewById(R.id.Btn_ir_registro)
        Btn_ir_logeo = findViewById(R.id.Btn_ir_logeo)

        Btn_ir_registro.setOnClickListener {
            val intent = Intent(this@Inicio,RegistroActivity::class.java)
            Toast.makeText(applicationContext, "Registros", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        Btn_ir_logeo.setOnClickListener {
            val intent = Intent(this@Inicio,LoginActivity::class.java)
            Toast.makeText(applicationContext, "Login", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }
    private fun ComprobarSecion(){
        FirebaseUser = FirebaseAuth.getInstance().currentUser
        if(FirebaseUser!= null){
            val intent = Intent(this@Inicio,MainActivity::class.java)
            Toast.makeText(applicationContext, "Sesion activa", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        ComprobarSecion()
        super.onStart()
    }
}
package com.desailly.chat_sensuadesires.Perfil

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import com.bumptech.glide.Glide
import com.desailly.chat_sensuadesires.Modelo.Usuario
import com.desailly.chat_sensuadesires.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


class PerfilVisitado : AppCompatActivity() {

    private lateinit var PV_ImagenU : ImageView
    private lateinit var PV_NombreU : TextView
    private lateinit var PV_EmailU : TextView
    private lateinit var PV_Uid : TextView
    private lateinit var PV_nombres : TextView
    private lateinit var PV_apellidos : TextView
    private lateinit var PV_profesion : TextView
    private lateinit var PV_telefono : TextView
    private lateinit var PV_edad : TextView
    private lateinit var PV_domicilio : TextView
    private lateinit var PV_proveedor : TextView

    private lateinit var Btn_llamar : Button
    private lateinit var Btn_enviar_sms : Button

    var uid_usuario_visitado = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_visitado)
        InicializarVistas()
        ObtenerUid()
        LeerInformacionUsuario()

        Btn_llamar.setOnClickListener {

            if(ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED){
                RealizarLlamada()
            }else{
                requestCallPhonePermiso.launch(Manifest.permission.CALL_PHONE)
            }

        }

        Btn_enviar_sms.setOnClickListener {
            if (ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED){
                EnviarSms()
            }else{
                requestSendMessagePermiso.launch(Manifest.permission.SEND_SMS)
            }

        }

    }



    private fun InicializarVistas(){
        PV_ImagenU = findViewById(R.id.PV_ImagenU)
        PV_NombreU = findViewById(R.id.PV_NombreU)
        PV_EmailU = findViewById(R.id.PV_EmailU)
        PV_Uid = findViewById(R.id.PV_Uid)
        PV_nombres = findViewById(R.id.PV_nombres)
        PV_apellidos = findViewById(R.id.PV_apellidos)
        PV_profesion = findViewById(R.id.PV_profesion)
        PV_telefono = findViewById(R.id.PV_telefono)
        PV_edad = findViewById(R.id.PV_edad)
        PV_domicilio = findViewById(R.id.PV_domicilio)
        PV_proveedor = findViewById(R.id.PV_proveedor)

        Btn_llamar = findViewById(R.id.Btn_llamar)
        Btn_enviar_sms = findViewById(R.id.Btn_enviar_sms)

    }

    private fun ObtenerUid() {
        intent = intent
        uid_usuario_visitado = intent.getStringExtra("uid").toString()
    }

    private  fun LeerInformacionUsuario(){
        val reference = FirebaseDatabase.getInstance().reference
            .child("Usuarios")
            .child(uid_usuario_visitado)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               val usuario : Usuario? = snapshot.getValue(Usuario::class.java)
                //obtener la informacion
                PV_NombreU.text = usuario!!.getN_Usuario()
                PV_EmailU.text = usuario!!.getEmail()
                PV_Uid.text =  usuario!!.getUid()
                PV_nombres.text= usuario!!.getNombres()
                PV_apellidos.text = usuario!!.getApellidos()
                PV_profesion.text = usuario!!.getProfesion()
                PV_telefono.text = usuario!!.getTelefono()
                PV_edad.text = usuario!!.getEdad()
                PV_domicilio.text = usuario!!.getDomicilio()
                PV_proveedor.text = usuario!!.getProveedor()

                Glide.with(applicationContext).load(usuario.getImagen())
                    .placeholder(R.drawable.imagen_usuario_visitado)
                    .into(PV_ImagenU)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        }

    private fun RealizarLlamada() {
        val numeroUsuario = PV_telefono.text.toString()
        if(numeroUsuario.isEmpty()){
            Toast.makeText(applicationContext, "El usuario no cuenta con numero Telefonico", Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(Intent.ACTION_CALL)
            intent.setData(Uri.parse("tel:$numeroUsuario"))
            startActivity(intent)
        }
    }

    private fun EnviarSms() {
        val numeroUsuario = PV_telefono.text.toString()
        if (numeroUsuario.isEmpty()){
            Toast.makeText(applicationContext, "El usuario no cuenta con numero Telefonico", Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("smsto:${numeroUsuario}"))
            intent.putExtra("sms_body","")
            startActivity(intent)
        }
    }

    private val requestCallPhonePermiso = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permiso_concedido ->
        if (permiso_concedido) {
            RealizarLlamada()
        } else {
            Toast.makeText(applicationContext, "El permiso de llamadas telefónicas no ha sido concedido", Toast.LENGTH_SHORT).show()
        }
    }


    private val requestSendMessagePermiso = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permiso_concedido ->
        if (permiso_concedido) {
            EnviarSms()
        } else {
            Toast.makeText(applicationContext, "El permiso de Mensaje no ha sido concedido", Toast.LENGTH_SHORT).show()
        }
    }


}



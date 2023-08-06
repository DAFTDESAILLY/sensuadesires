package com.desailly.chat_sensuadesires

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class Inicio : AppCompatActivity() {
    private  lateinit var Btn_ir_logeo:MaterialButton
    private  lateinit var Btn_login_google:MaterialButton


    var FirebaseUser : FirebaseUser?=null
    private lateinit var auth :FirebaseAuth

    private lateinit var progressDialog : ProgressDialog
    private lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)


        Btn_ir_logeo = findViewById(R.id.Btn_ir_logeo)
        Btn_login_google = findViewById(R.id.Btn_login_google)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient =GoogleSignIn.getClient(this,gso)

        /*modificacion del error de google lateinit propery has not been initialized */
        auth = FirebaseAuth.getInstance()
        /*modificacion del error de google lateinit propery has not been initialized */

        Btn_ir_logeo.setOnClickListener {
            val intent = Intent(this@Inicio,LoginActivity::class.java)
            Toast.makeText(applicationContext, "Login", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        Btn_login_google.setOnClickListener{
           EmpezarinicioSesionGoogle()
        }
    }

    private fun EmpezarinicioSesionGoogle() {
       val googleSignIntent =  mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignIntent)
    }

    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){resultado->
        if(resultado.resultCode == RESULT_OK){
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                AutenticarGoogleFirebase(account.idToken)
            }catch (e:Exception){
                Toast.makeText(applicationContext, "Ha ocurrido un error debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun AutenticarGoogleFirebase(idToken: String?) {
       val credencial = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credencial)
            .addOnSuccessListener { authResult->
                /*Si el usuario es nuevo*/
                if(authResult.additionalUserInfo!!.isNewUser){
                    GuardarInfoBD()
                }
                /*si el usuario se registro */
                else{
                    startActivity(Intent(this,MainActivity::class.java))
                    finishAffinity()
                }
            }.addOnFailureListener {e->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun GuardarInfoBD() {
       progressDialog.setMessage("Se esta registrando su informacion")
        progressDialog.show()

        /*obtner info google*/
        val uidGoogle = auth.uid
        val correoGoogle = auth.currentUser?.email
        val n_Google = auth.currentUser?.displayName
        val nombre_usuario_G : String = n_Google.toString()

        val hashMap = HashMap<String,Any?>()
        hashMap["uid"] = uidGoogle
        hashMap["n_usuario"] = nombre_usuario_G
        hashMap["email"] = correoGoogle
        hashMap["imagen"] = ""
        hashMap["buscar"] = nombre_usuario_G.lowercase()

        /*Datos de usuario*/
        hashMap["nombres"] =  ""
        hashMap["apellidos"]= ""
        hashMap["edad"] = ""
        hashMap["profesion"] = ""
        hashMap["domicilio"] = ""
        hashMap["telefono"] = ""
        hashMap["estado"] = "offline"
        hashMap["proveedor"] = "Email"

        /*referencia a la base de datos*/
        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidGoogle!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(applicationContext,MainActivity::class.java))
                Toast.makeText(applicationContext, "Se ha registrado exitosamente", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
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
package com.desailly.chat_sensuadesires

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.desailly.chat_sensuadesires.Fragmentos.FragmentoChats
import com.desailly.chat_sensuadesires.Fragmentos.FragmentoUsuarios
import com.desailly.chat_sensuadesires.Modelo.Usuario
import com.desailly.chat_sensuadesires.Perfil.PerfilActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    var reference : DatabaseReference?=null
    var firebaseUser : FirebaseUser?=null
    private lateinit var nombre_usuario : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InicializarComponentes()
        ObtenerDato()

    }

    fun InicializarComponentes(){
        val toolbar : Toolbar = findViewById(R.id.toolbarMain)
        setSupportActionBar(toolbar)
        supportActionBar?.title=""

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(firebaseUser!!.uid)
        nombre_usuario = findViewById(R.id.Nombre_usuario)


        val tabLayout : TabLayout = findViewById(R.id.TabLayoutMain)
        val viewPager : ViewPager = findViewById(R.id.ViewPagerMain)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addItem(FragmentoUsuarios(),"Usuarios")
        viewPagerAdapter.addItem(FragmentoChats(),"Chats")

        viewPager.adapter= viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

    }

    fun ObtenerDato(){
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
              if(snapshot.exists()){
                  val usuario : Usuario? = snapshot.getValue(Usuario::class.java)
                  nombre_usuario.text = usuario!!.getN_Usuario()
              }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

        private val listaFragmentos : MutableList<Fragment> = ArrayList()
        private val listaTitulos : MutableList<String> = ArrayList()

        override fun getCount(): Int {
           return listaFragmentos.size
        }

        override fun getItem(position: Int): Fragment {
           return listaFragmentos[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return listaTitulos[position]
        }

        fun addItem(fragment: Fragment,titulo:String){
            listaFragmentos.add(fragment)
            listaTitulos.add(titulo)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_principal,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_perfil->{
                val intent = Intent(applicationContext, PerfilActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_acerca_de->{
                Toast.makeText(applicationContext, "Acerca de", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.menu_salir->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity,Inicio::class.java)
                Toast.makeText(applicationContext, "Has cerrado sesion", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                return true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }
}
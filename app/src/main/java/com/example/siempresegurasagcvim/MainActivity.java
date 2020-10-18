package com.example.siempresegurasagcvim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    static DocumentReference usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //Inicio
        db = FirebaseFirestore.getInstance();
        configurarEnlaces();
        configurarInicioSesion();
    }

    public void configurarEnlaces(){
        TextView link =findViewById(R.id.link_registro);
        link.setOnClickListener(irRegistro);
        link =findViewById(R.id.link_recuperarContraseña);
        link.setOnClickListener(irRecuperarContraseña);
    }
    View.OnClickListener irRegistro = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent mostrarRegistro = new Intent(MainActivity.this, NuevoRegistroActivity.class);
            mostrarRegistro.setFlags(mostrarRegistro.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            MainActivity.this.startActivity(mostrarRegistro);
        }
    };
    View.OnClickListener irRecuperarContraseña = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent mostrarRecuperar = new Intent(MainActivity.this, RecuperarContrasena.class);
            mostrarRecuperar.setFlags(mostrarRecuperar.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            MainActivity.this.startActivity(mostrarRecuperar);
        }
    };

    public void configurarInicioSesion(){
        Button entrar = findViewById(R.id.entrar);
        entrar.setOnClickListener(validarInicioSesion);
    }
    View.OnClickListener validarInicioSesion = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Obtener los datos
            EditText correo = findViewById(R.id.email);
            EditText contraseña = findViewById(R.id.contraseña);
            if(!correo.getText().toString().trim().equals("")) {
                if(!contraseña.getText().toString().trim().equals("")) {
                    db.collection("usuarios")
                            .whereEqualTo("correo", correo.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            Snackbar.make(view, "El email no está registrado, cree una cuenta primero", Snackbar.LENGTH_LONG)
                                                    .setAction("Alerta", null).show();
                                        } else {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.get("contraseña").equals(contraseña.getText().toString())) {
                                                    usuarioActual = document.getReference();
                                                    Snackbar.make(view, "Bienvenida " + document.get("nombre"), Snackbar.LENGTH_SHORT)
                                                            .setAction("Acceso correcto", null).show();
                                                } else {
                                                    Snackbar.make(view, "Contraseña incorrecta", Snackbar.LENGTH_LONG)
                                                            .setAction("Alerta", null).show();
                                                }
                                            }
                                        }
                                    } else {
                                        Snackbar.make(view, "Ocurrió un error al comprobar sus datos. Intente de nuevo", Snackbar.LENGTH_LONG)
                                                .setAction("Error", null).show();
                                    }
                                }
                            });
                }else{
                    Snackbar.make(view, "Ingresa tu contraseña también por favor", Snackbar.LENGTH_LONG)
                            .setAction("Error", null).show();
                }
            }else{
                Snackbar.make(view, "Ingresa tu correo primero", Snackbar.LENGTH_LONG)
                        .setAction("Error", null).show();
            }
        }
    };
}
package com.example.siempresegurasagcvim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //Inicio
        configurarEnlaceRegistro();
    }

    public void configurarEnlaceRegistro(){
        TextView link =findViewById(R.id.link_registro);
        link.setOnClickListener(irRegistro);
    }
    View.OnClickListener irRegistro = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent mostrarRegistro = new Intent(MainActivity.this, NuevoRegistroActivity.class);
            mostrarRegistro.setFlags(mostrarRegistro.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            MainActivity.this.startActivity(mostrarRegistro);
        }
    };

}
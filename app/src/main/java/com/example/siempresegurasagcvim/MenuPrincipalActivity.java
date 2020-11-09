package com.example.siempresegurasagcvim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuPrincipalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal_activity);

        colocarBarra();
        Button botonMisDatos = findViewById(R.id.button_misdatos);
        botonMisDatos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent nuevaIntent = new Intent(MenuPrincipalActivity.this, MisDatosVisualizarModificarActivity.class);
                MenuPrincipalActivity.this.startActivity(nuevaIntent);
            }
        });
        Button botonMisContactos = findViewById(R.id.button_miscontactos);
        botonMisContactos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent nuevaIntent = new Intent(MenuPrincipalActivity.this, MisContactosActivity.class);
                MenuPrincipalActivity.this.startActivity(nuevaIntent);
            }
        });
    }

    public void colocarBarra(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.barra_de_herramientas);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Menú Principal");
    }

}
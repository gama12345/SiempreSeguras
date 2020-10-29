package com.example.siempresegurasagcvim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class MenuPrincipalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal_activity);

        colocarBarra();
    }

    public void colocarBarra(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.barra_de_herramientas);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Menú Principal");
    }
}
package com.example.siempresegurasagcvim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class ConfiguracionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracion_activity);
        colocarBarra();
        MainActivity.usuarioActual.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Switch switchN = findViewById(R.id.switchNotificaciones);
                switchN.setChecked(Boolean.valueOf(task.getResult().get("ayudante").toString()));
                Switch switchA = findViewById(R.id.switchAlertas);
                switchA.setChecked(Boolean.valueOf(task.getResult().get("fotoAlertas").toString()));
            }
        });
        Button buttonGuardar = findViewById(R.id.buttonGuardar);
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch switchN = findViewById(R.id.switchNotificaciones);
                Switch switchA = findViewById(R.id.switchAlertas);
                MainActivity.usuarioActual.update("ayudante", switchN.isChecked(), "fotoAlertas", switchA.isChecked());
                Toast.makeText(view.getContext(), "Se han guardado tus cambios", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ConfiguracionActivity.this, MenuPrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                ConfiguracionActivity.this.startActivity(intent);
            }
        });
    }
    //Barra de herramientas
    public void colocarBarra(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.barra_de_herramientas);
        setSupportActionBar(myToolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Contactos de emergencia");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ConfiguracionActivity.this, MenuPrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
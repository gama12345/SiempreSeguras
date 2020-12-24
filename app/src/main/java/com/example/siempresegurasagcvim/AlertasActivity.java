package com.example.siempresegurasagcvim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;

public class AlertasActivity extends AppCompatActivity {
    static RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    static Activity miActivity;
    ArrayList<Alerta> alertas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertas_activity);

        miActivity = this;
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = findViewById(R.id.rvAlertas);
        recyclerView.setLayoutManager(lim);
        refreshLayout = findViewById(R.id.refreshAlertas);
        refreshLayout.setOnRefreshListener(onRefresh);

        colocarBarra();
        recuperarRegistros();
    }
    //Barra de herramientas
    public void colocarBarra(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.barra_de_herramientas);
        setSupportActionBar(myToolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Alertas recibidas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(AlertasActivity.this, MenuPrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void recuperarRegistros(){
        alertas = new ArrayList<>();
        MainActivity.usuarioActual.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String tel = task.getResult().get("telefono").toString();
                FirebaseFirestore.getInstance().collection("alertas").whereEqualTo("contacto", tel).orderBy("estado").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult()) {
                            alertas.add(new Alerta(document.get("contacto").toString(), document.get("imagen").toString(), document.get("mensaje").toString(), document.get("usuaria").toString(), document.get("estado").toString()));
                        }
                        AdaptadorAlertas adaptador = new AdaptadorAlertas(alertas, miActivity);
                        recyclerView.setAdapter(adaptador);
                    }
                });
            }
        });

    }
    SwipeRefreshLayout.OnRefreshListener onRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            recuperarRegistros();
            refreshLayout.setRefreshing(false);
        }
    };
}
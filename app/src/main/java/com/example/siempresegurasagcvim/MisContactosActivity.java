package com.example.siempresegurasagcvim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class MisContactosActivity extends AppCompatActivity {
    static RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    static Activity miActivity;
    ArrayList<Contacto> misContactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_contactos_activity);
        colocarBarra();
        Button botonTxtAgregarContactos = findViewById(R.id.botonTxtAgregarContactos);
        botonTxtAgregarContactos.setOnClickListener(mostrarContactos);
        ImageButton botonAgregarContactos = findViewById(R.id.botonAgregarContactos);
        botonAgregarContactos.setOnClickListener(mostrarContactos);

        miActivity = this;
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = findViewById(R.id.rvMisContactosEmergencias);
        recyclerView.setLayoutManager(lim);
        refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(onRefresh);

        recuperarContactos();
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
                Intent intent = new Intent(MisContactosActivity.this, MenuPrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    View.OnClickListener mostrarContactos = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MisContactosActivity.this, MisContactosTodosActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            MisContactosActivity.this.startActivity(intent);
        }
    };

    public void recuperarContactos(){
        HelperSQLite helper = new HelperSQLite(MisContactosActivity.this,"siempreseguras", null, 1);
        SQLiteDatabase bd = helper.getWritableDatabase();
        String[] datos = {"nombre", "telefono"};
        Cursor consulta = bd.query("miscontactos",datos,
                null, null,null,null,null);
        misContactos = new ArrayList<>();
        while (consulta.moveToNext()) {
            misContactos.add(new Contacto(consulta.getString(0), consulta.getString(1), true));
        }
        AdaptadorTodos adaptador = new AdaptadorTodos(misContactos, miActivity);
        recyclerView.setAdapter(adaptador);
    }

    SwipeRefreshLayout.OnRefreshListener onRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            recuperarContactos();
            refreshLayout.setRefreshing(false);
        }
    };
}
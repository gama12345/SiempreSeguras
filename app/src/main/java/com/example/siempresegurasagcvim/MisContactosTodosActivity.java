package com.example.siempresegurasagcvim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MisContactosTodosActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    static RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    static Activity miActivity;
    ArrayList<Contacto> allPhones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_contactos_todos_activity);
        colocarBarra();
        Button btnGuardarContactos = findViewById(R.id.guardar);
        btnGuardarContactos.setOnClickListener(guardarContactos);
        miActivity = this;
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = findViewById(R.id.rvMisContactos);
        recyclerView.setLayoutManager(lim);
        refreshLayout = findViewById(R.id.refreshTodos);
        refreshLayout.setOnRefreshListener(onRefresh);

        recuperarContactos();
    }
    //Barra de herramientas
    public void colocarBarra(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.contactos_barra_de_herramientas);
        setSupportActionBar(myToolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Mis contactos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MisContactosTodosActivity.this, MisContactosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void recuperarContactos(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            allPhones = new ArrayList<>();
            // The Phone class should be imported from CommonDataKinds.Phone
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER},  null,null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC");
            HelperSQLite helper = new HelperSQLite(MisContactosTodosActivity.this,"siempreseguras", null, 1);
            SQLiteDatabase bd = helper.getWritableDatabase();
            String[] datos = {"nombre", "telefono"};

            while (cursor != null && cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);

                Cursor consulta = bd.query("miscontactos",datos,
                        "nombre = ? and telefono = ?", new String[]{name, number},null,null,null);
                if(consulta.moveToFirst()) {
                    allPhones.add(new Contacto(name, number, true));
                }else{
                    allPhones.add(new Contacto(name, number, false));
                }
            }
            AdaptadorTodos adaptador = new AdaptadorTodos(allPhones, miActivity);
            recyclerView.setAdapter(adaptador);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                recuperarContactos();
            } else {
                Toast.makeText(this, "Debe otorgarse el permiso para poder mostrar los contactos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    SwipeRefreshLayout.OnRefreshListener onRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            recuperarContactos();
            refreshLayout.setRefreshing(false);
        }
    };

    View.OnClickListener guardarContactos = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            HelperSQLite helper = new HelperSQLite(MisContactosTodosActivity.this,"siempreseguras", null, 1);
            SQLiteDatabase bd = helper.getWritableDatabase();
            bd.execSQL("DELETE FROM miscontactos");

            for (int i=0; i<allPhones.size(); i++){
                if(allPhones.get(i).isSeleccionado()){
                    ContentValues user = new ContentValues();
                    user.put("nombre", allPhones.get(i).getNombre());
                    user.put("telefono", allPhones.get(i).getTelefono());
                    bd.insert("miscontactos", null, user);
                }
            }
            Toast.makeText(miActivity, "Contactos actualizados correctamente", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MisContactosTodosActivity.this, MisContactosActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            MisContactosTodosActivity.this.startActivity(intent);
        }
    };
}
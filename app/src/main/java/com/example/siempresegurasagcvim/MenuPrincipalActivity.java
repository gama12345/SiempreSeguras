package com.example.siempresegurasagcvim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MenuPrincipalActivity extends AppCompatActivity implements LocationListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 200;
    Activity miActivity;
    LocationListener escucha = this;
    ImageButton buttonPanico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal_activity);
        miActivity = this;
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
        buttonPanico = findViewById(R.id.imageButton_panico);
        buttonPanico.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                sendCurrentLocation();
                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }

        if(!isGPSEnabled()){
            Snackbar.make(buttonPanico, "Tu GPS esta desactivado, activalo para poder enviar alertas", Snackbar.LENGTH_LONG)
                    .setAction("Error", null).show();
        }
    }

    public void colocarBarra(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.barra_de_herramientas);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Menú Principal");
    }

    private void sendCurrentLocation(){
        if(isGPSEnabled()){
            Geocoder geo = new Geocoder(this, Locale.getDefault());
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, escucha);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, escucha);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            Location currentLocation = location;

            if (currentLocation != null) {
                List<Address> address = null;
                try {
                    address = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String direccion = address.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = address.get(0).getLocality();
                String state = address.get(0).getAdminArea();
                String country = address.get(0).getCountryName();
                String postalCode = address.get(0).getPostalCode();
                String knownName = address.get(0).getFeatureName();
                String latitud = address.get(0).getLatitude()+"";
                String longitud = address.get(0).getLongitude()+"";
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String hora = sdf.format(new Date());

                MainActivity.usuarioActual.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String smsMessage = task.getResult().get("nombre")+" "+task.getResult().get("primer_apellido")+" "+task.getResult().get("segundo_apellido")+" ha activado una alerta\n"+
                                "Dirección: "+direccion+"\nLatitud: "+latitud+"\nLongitud: "+longitud+"\nHora: "+hora+"\nVer en mapa: "+"https://www.google.com/maps/search/?api=1&query="+latitud+","+longitud;
                        String destinationAddress;
                        String scAddress = null;
                        PendingIntent sentIntent = null, deliveryIntent = null;
                        HelperSQLite helper = new HelperSQLite(MenuPrincipalActivity.this,"siempreseguras", null, 1);
                        SQLiteDatabase bd = helper.getWritableDatabase();
                        String[] datos = {"nombre", "telefono"};
                        Cursor consulta = bd.query("miscontactos",datos,
                                null, null,null,null,null);
                        while (consulta.moveToNext()) {
                            destinationAddress = consulta.getString(1);
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage
                                    (destinationAddress, scAddress, smsMessage,
                                            sentIntent, deliveryIntent);
                        }
                        Snackbar.make(buttonPanico, "Alerta emitida", Snackbar.LENGTH_LONG)
                                .setAction("Mensaje", null).show();
                    }
                });
            }
        }else{
            Snackbar.make(buttonPanico, "Tu GPS esta desactivado, activalo para poder enviar alertas", Snackbar.LENGTH_LONG)
                    .setAction("Error", null).show();
        }
    }

    private boolean isGPSEnabled() {
        try {
            int gpsSignal = Settings.Secure.getInt(miActivity.getContentResolver(), Settings.Secure.LOCATION_MODE);

            if (gpsSignal == 0) {
                return false;
            } else {
                return true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
            } else {
                Snackbar.make(buttonPanico, "Debe otorgarse el permiso de ubicacion para poder enviar las alertas", Snackbar.LENGTH_LONG)
                        .setAction("Error", null).show();
            }
        }else if (requestCode == PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
            } else {
                Snackbar.make(buttonPanico, "Debe otorgarse el permiso de envio SMS para poder enviar las alertas", Snackbar.LENGTH_LONG)
                        .setAction("Error", null).show();
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
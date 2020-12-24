package com.example.siempresegurasagcvim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.ImageCapture;
import androidx.core.content.FileProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraDevice;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MenuPrincipalActivity extends AppCompatActivity implements LocationListener{
    private int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 200;
    private static final int PERMISSIONS_REQUEST_CAMERA = 300;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 400;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static MenuPrincipalActivity instance;
    Activity miActivity;
    LocationListener escucha = this;
    ImageButton buttonPanico;
    Button botonMisContactos, botonAlertas, botonMisDatos;
    TextView textView6;
    String currentPhotoPath, smsMessage;
    boolean cancelado = false;
    int segundos = 10;


    /*private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private boolean msgE = false;

     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal_activity);
/*
        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

 */

        miActivity = this;
        instance = this;
        colocarBarra();
        textView6 = findViewById(R.id.textView6);
        botonMisDatos = findViewById(R.id.button_misdatos);
        botonMisDatos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent nuevaIntent = new Intent(MenuPrincipalActivity.this, MisDatosVisualizarModificarActivity.class);
                MenuPrincipalActivity.this.startActivity(nuevaIntent);
            }
        });
        botonAlertas = findViewById(R.id.button_misalertas);
        botonAlertas.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent nuevaIntent = new Intent(MenuPrincipalActivity.this, AlertasActivity.class);
                MenuPrincipalActivity.this.startActivity(nuevaIntent);
            }
        });
        botonMisContactos = findViewById(R.id.button_miscontactos);
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
                buttonPanico.setVisibility(View.GONE);
                botonAlertas.setVisibility(View.GONE);
                botonMisContactos.setVisibility(View.GONE);
                botonMisDatos.setVisibility(View.GONE);
                textView6.setText("La alerta se enviará en 10 segundos...");
                ejecutar();
                //sendCurrentLocation();
                return true;
            }
        });
        ImageButton buttonSettigs = findViewById(R.id.configuracion);
        buttonSettigs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nuevaIntent = new Intent(MenuPrincipalActivity.this, ConfiguracionActivity.class);
                MenuPrincipalActivity.this.startActivity(nuevaIntent);
            }
        });
        ImageButton buttonExit = findViewById(R.id.salir);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("¿Deseas cerrar sesión?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                BasedeDatosSQLite base = new BasedeDatosSQLite(MenuPrincipalActivity.this,"SQLite", null, 1);
                                SQLiteDatabase sqLite = base.getWritableDatabase();
                                String[] args = {MainActivity.usuarioActualEmail};
                                sqLite.delete("usuarias","correo = ?",args);
                                Intent intent = new Intent(MenuPrincipalActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                MenuPrincipalActivity.this.startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();
            }
        });

        if(!isGPSEnabled()){
            Snackbar.make(buttonPanico, "Tu GPS esta desactivado, activalo para poder enviar alertas", Snackbar.LENGTH_LONG)
                    .setAction("Error", null).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }

        Intent intent = new Intent(MenuPrincipalActivity.this, ShakeService.class);
        MenuPrincipalActivity.this.startService(intent);
    }

    public void colocarBarra(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.barra_de_herramientas);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Menú Principal");
    }

    public static MenuPrincipalActivity getInstance(){
        return instance;
    }
    /*
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 30 && !msgE) {
                sendCurrentLocation();
                msgE = true;
            }
            if (mAccel < 2 && msgE) {
                msgE = false;

            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

     */

    public void sendCurrentLocation(){
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
                        smsMessage = task.getResult().get("nombre") + " " + task.getResult().get("primer_apellido") + " activó una alerta\n" +
                                "Hora: "+ hora +"\nLat: " + latitud + "\nLong: " + longitud + "\nEn: "+ direccion;
                        if(smsMessage.length() > 160){
                            smsMessage = smsMessage.substring(0, 158);
                        }
                        if(task.getResult().get("fotoAlertas").toString().equals("true")){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                            }else{
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                                }else {

                                    HelperSQLite helper = new HelperSQLite(MenuPrincipalActivity.this, "siempreseguras", null, 1);
                                    SQLiteDatabase bd = helper.getWritableDatabase();
                                    String[] datos = {"nombre", "telefono"};
                                    Cursor consulta = bd.query("miscontactos", datos,
                                            null, null, null, null, null);

                                    if (consulta.getCount() > 0) {
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                            File photoFile = null;
                                            try {
                                                photoFile = createImageFile();
                                            } catch (IOException ex) {
                                                // Error occurred while creating the File
                                            }
                                            // Continue only if the File was successfully created
                                            if (photoFile != null) {
                                                Uri photoURI = FileProvider.getUriForFile(miActivity,
                                                        "com.example.siempresegurasagcvim.fileprovider",
                                                        photoFile);
                                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                            }
                                        }
                                    }else{
                                        Snackbar.make(buttonPanico, "No tienes contactos registrados", Snackbar.LENGTH_LONG)
                                                .setAction("Mensaje", null).show();
                                    }
                                }
                            }
                        }else {
                            String destinationAddress = "";
                            String scAddress = null;
                            PendingIntent sentIntent = null, deliveryIntent = null;
                            HelperSQLite helper = new HelperSQLite(MenuPrincipalActivity.this, "siempreseguras", null, 1);
                            SQLiteDatabase bd = helper.getWritableDatabase();
                            String[] datos = {"nombre", "telefono"};
                            Cursor consulta = bd.query("miscontactos", datos,
                                    null, null, null, null, null);
                            ArrayList<String> numeros = new ArrayList<>();
                            ArrayList<String> numeros2 = new ArrayList<>();
                            SmsManager smsManager = SmsManager.getDefault();
                            while (consulta.moveToNext()) {
                                numeros.add(consulta.getString(1));
                                numeros2.add(consulta.getString(1));
                                destinationAddress = consulta.getString(1);
                                sendSMS(destinationAddress, smsMessage);
                            }
                            //smsManager.sendTextMessage(destinationAddress, null, smsMessage,null, null);
                            bd.close();
                            if(numeros2.size() > 0) {
                                enviarNotificaciones(numeros2);
                                guardarAlertas(numeros, smsMessage, MainActivity.usuarioActualEmail, "");
                                Snackbar.make(buttonPanico, "Alerta emitida", Snackbar.LENGTH_LONG)
                                        .setAction("Mensaje", null).show();
                            }else{
                                Snackbar.make(buttonPanico, "No tienes contactos registrados", Snackbar.LENGTH_LONG)
                                        .setAction("Mensaje", null).show();
                            }
                        }

                    }
                });
            }
        }else{
            Snackbar.make(buttonPanico, "Tu GPS esta desactivado, activalo para poder enviar alertas", Snackbar.LENGTH_LONG)
                    .setAction("Error", null).show();
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        // ---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        ContentValues values = new ContentValues();
                        /*for (int i = 0; i < MobNumber.size() - 1; i++) {
                            values.put("address", MobNumber.get(i).toString());// txtPhoneNo.getText().toString());
                            values.put("body", MessageText.getText().toString());
                        }
                        getContentResolver().insert(
                                Uri.parse("content://sms/sent"), values);*/
                        //Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendMultipartTextMessage(phoneNumber, null, sms.divideMessage(message), null, null);
    }

    public void guardarAlertas(ArrayList<String> numContactos, String msg, String usuaria, String img){
        Map<String, Object> objAlerta = new HashMap<>();
        objAlerta.put("mensaje", msg);
        objAlerta.put("usuaria", usuaria);
        objAlerta.put("contacto", numContactos.get(0));
        objAlerta.put("imagen", img);
        numContactos.remove(0);
        FirebaseFirestore.getInstance().collection("alertas").add(objAlerta).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(numContactos.size() > 0) {
                    guardarAlertas(numContactos, msg, usuaria, img);
                }
            }
        });
    }
    public void enviarNotificaciones(ArrayList<String> numContactosT){
        if(numContactosT.size() > 0) {
            FirebaseFirestore.getInstance().collection("usuarios").whereEqualTo("telefono", numContactosT.get(0)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (!task.getResult().isEmpty()) {
                        if (task.getResult().getDocuments().get(0).get("ayudante").toString().equals("true")) {

                            AdapterNotifications adapterRestAPI = new AdapterNotifications();
                            PuntosConexion endpoints = adapterRestAPI.startAdapter();
                            Call<SolicitudNotificacion> usuarioResponseCall = endpoints.enviarNotificacion(task.getResult().getDocuments().get(0).get("token").toString());
                            usuarioResponseCall.enqueue(new Callback<SolicitudNotificacion>() {
                                @Override
                                public void onResponse(Call<SolicitudNotificacion> call, Response<SolicitudNotificacion> response) {
                                    SolicitudNotificacion respuesta = response.body();
                                    Log.d("TG: ", response.toString());
                                }

                                @Override
                                public void onFailure(Call<SolicitudNotificacion> call, Throwable t) {
                                    Log.d("T: ", t.toString());

                                }
                            });

                        }
                    }
                    numContactosT.remove(0);
                    if(numContactosT.size() > 0) {
                        enviarNotificaciones(numContactosT);
                    }
                }
            });
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
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
    public void onRequestPermissionsResult(int requestCodeP, String[] permissions, int[] grantResults) {
        Log.d("PERMISSIOM....", ""+requestCodeP);
            if (requestCodeP == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                } else {
                    Snackbar.make(buttonPanico, "Debe otorgarse el permiso de ubicacion para poder enviar las alertas", Snackbar.LENGTH_LONG)
                            .setAction("Error", null).show();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                }
            } else if (requestCodeP == PERMISSIONS_REQUEST_SEND_SMS) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                } else {
                    Snackbar.make(buttonPanico, "Debe otorgarse el permiso de envio SMS para poder enviar las alertas", Snackbar.LENGTH_LONG)
                            .setAction("Error", null).show();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                }
            } else if (requestCodeP == PERMISSIONS_REQUEST_CAMERA) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                } else {
                    Snackbar.make(buttonPanico, "Debe otorgarse el permiso de camara para poder enviar las alertas con una foto", Snackbar.LENGTH_LONG)
                            .setAction("Error", null).show();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                }
            } else if (requestCodeP == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                } else {
                    Snackbar.make(buttonPanico, "Debe otorgarse el permiso de storage (almacenamiento) para poder enviar las alertas con una foto", Snackbar.LENGTH_LONG)
                            .setAction("Error", null).show();
                }
            }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            File f = new File(currentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            try {
                MediaStore.Images.Media.insertImage(miActivity.getContentResolver(), f.getAbsolutePath(), f.getName(), null);
                miActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference mountainsRef = storageRef.child("imgSiempreSeguras/"+f.getName().toString());


            Bitmap imabit = (Bitmap) BitmapFactory.decodeFile(f.getPath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imabit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data2 = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data2);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(miActivity, "Error al guardar imagen", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String destinationAddress;
                    String scAddress = null;
                    PendingIntent sentIntent = null, deliveryIntent = null;
                    HelperSQLite helper = new HelperSQLite(MenuPrincipalActivity.this, "siempreseguras", null, 1);
                    SQLiteDatabase bd = helper.getWritableDatabase();
                    String[] datos = {"nombre", "telefono"};
                    Cursor consulta = bd.query("miscontactos", datos,
                            null, null, null, null, null);
                    ArrayList<String> numeros = new ArrayList<>();
                    ArrayList<String> numeros2 = new ArrayList<>();
                    while (consulta.moveToNext()) {
                        numeros.add(consulta.getString(1));
                        numeros2.add(consulta.getString(1));
                        destinationAddress = consulta.getString(1);
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage
                                (destinationAddress, scAddress, smsMessage,
                                        sentIntent, deliveryIntent);
                    }
                    enviarNotificaciones(numeros2);
                    guardarAlertas(numeros, smsMessage, MainActivity.usuarioActualEmail ,f.getName());
                    Snackbar.make(buttonPanico, "Alerta emitida", Snackbar.LENGTH_LONG)
                            .setAction("Mensaje", null).show();
                }
            });
        }
    }







    public void ejecutar(){
        Tiempo a = new Tiempo();
        a.execute();
    }
    public void hilo() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public class Tiempo extends AsyncTask<Void,Integer,Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            hilo();
            segundos--;
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //Aqui se actualiza la posición
            if(!cancelado && segundos > 0){
                textView6.setText("La alerta se enviará en "+segundos+" segundos...");
                ejecutar();
            }else{
                segundos = 10;
                buttonPanico.setVisibility(View.VISIBLE);
                botonAlertas.setVisibility(View.VISIBLE);
                botonMisContactos.setVisibility(View.VISIBLE);
                botonMisDatos.setVisibility(View.VISIBLE);
                textView6.setText("Si te encuentras en peligro manten presionado el icono de alerta");
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
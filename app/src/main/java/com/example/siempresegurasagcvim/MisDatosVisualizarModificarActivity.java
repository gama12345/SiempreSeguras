package com.example.siempresegurasagcvim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MisDatosVisualizarModificarActivity extends AppCompatActivity {
    static EditText fechaNac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_datos_visualizar_modificar_activity);
        colocarBarra();
        visualizarDatosUsuario();
        Button botonGuardarCambios = findViewById(R.id.button_modificar_datos);
        botonGuardarCambios.setOnClickListener(guardarCambios);
    }

    //Barra de herramientas
    public void colocarBarra(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.barra_de_herramientas);
        setSupportActionBar(myToolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Datos del usuario");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MisDatosVisualizarModificarActivity.this, MenuPrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void visualizarDatosUsuario(){
        MainActivity.usuarioActual.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                EditText nombreUsuario = findViewById(R.id.editText_nombre_usuario6);
                nombreUsuario.setText(task.getResult().get("nombre").toString());
                EditText primerApellidoUsuario = findViewById(R.id.editText_primer_apellido_usuario5);
                primerApellidoUsuario.setText(task.getResult().get("primer_apellido").toString());
                EditText segundoApellidoUsuario = findViewById(R.id.editText_segundo_apellido_usuario4);
                segundoApellidoUsuario.setText(task.getResult().get("segundo_apellido").toString());
                EditText telUsuario = findViewById(R.id.editText_celular_usuario);
                telUsuario.setText(task.getResult().get("telefono").toString());
                EditText correoUsuario = findViewById(R.id.editText_correo_usuario);
                correoUsuario.setText(task.getResult().get("correo").toString());
                EditText contraseñaUsuario = findViewById(R.id.editText_contraseña_usuario2);
                contraseñaUsuario.setText(task.getResult().get("contraseña").toString());
                Spinner listaMunicipios = findViewById(R.id.spinner_municipio);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                        R.array.lista_municipios, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listaMunicipios.setAdapter(adapter);
                listaMunicipios.setSelection(adapter.getPosition(task.getResult().get("municipio").toString()));
                fechaNac = findViewById(R.id.editText_fecha_nacimiento_usuario3);
                fechaNac.setText(task.getResult().get("fechaNac").toString());
                fechaNac.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogFragment newDate = new MisDatosVisualizarModificarActivity.DatePickerFragment();
                        newDate.show(getSupportFragmentManager(), "datePicker");
                    }
                });
            }
        });
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String formatoFecha = "%1$02d";
            fechaNac.setText(String.format(formatoFecha, day)+"-"+String.format(formatoFecha, month+1)+"-"+year);
        }
    }
    View.OnClickListener guardarCambios = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText nombreUsuario = findViewById(R.id.editText_nombre_usuario6);
            EditText primerApellidoUsuario = findViewById(R.id.editText_primer_apellido_usuario5);
            EditText segundoApellidoUsuario = findViewById(R.id.editText_segundo_apellido_usuario4);
            EditText telUsuario = findViewById(R.id.editText_celular_usuario);
            EditText correoUsuario = findViewById(R.id.editText_correo_usuario);
            EditText contraseñaUsuario = findViewById(R.id.editText_contraseña_usuario2);
            Spinner listaMunicipios = findViewById(R.id.spinner_municipio);
            fechaNac = findViewById(R.id.editText_fecha_nacimiento_usuario3);

            if(elementoNombreValido(nombreUsuario.getText().toString())){
                if(elementoNombreValido(primerApellidoUsuario.getText().toString())){
                    if(elementoNombreValido(segundoApellidoUsuario.getText().toString())){
                        if(!telUsuario.getText().toString().trim().equals("")) {
                            if (emailValido(correoUsuario.getText().toString())) {
                                if (contraseñaValida(contraseñaUsuario.getText().toString())) {
                                    //Creando el nuevo usuario
                                    Map<String, Object> usuario = new HashMap<>();
                                    usuario.put("nombre", nombreUsuario.getText().toString());
                                    usuario.put("primer_apellido", primerApellidoUsuario.getText().toString());
                                    usuario.put("segundo_apellido", segundoApellidoUsuario.getText().toString());
                                    usuario.put("fechaNac", fechaNac.getText().toString());
                                    usuario.put("municipio", listaMunicipios.getSelectedItem().toString());
                                    usuario.put("telefono", telUsuario.getText().toString());
                                    usuario.put("correo", correoUsuario.getText().toString());
                                    usuario.put("contraseña", contraseñaUsuario.getText().toString());
                                    //Comprobando email único
                                    FirebaseFirestore.getInstance().collection("usuarios").whereEqualTo("correo", correoUsuario.getText().toString())
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.getResult().isEmpty() || task.getResult().getDocuments().get(0).get("correo").equals(correoUsuario.getText().toString())) {
                                                //Actualizando en base de datos
                                                MainActivity.usuarioActual.update(usuario);
                                                visualizarDatosUsuario();
                                                Snackbar.make(view, "Datos actualizados", Snackbar.LENGTH_LONG)
                                                        .setAction("Mensaje", null).show();
                                            } else {
                                                Snackbar.make(view, "Email ya registrado, por favor ingrese uno diferente", Snackbar.LENGTH_LONG)
                                                        .setAction("Error", null).show();
                                            }
                                        }
                                    });
                                } else {
                                    Snackbar.make(view, "La contraseña debe contener al menos un caracter especial, número y mayúscula", Snackbar.LENGTH_LONG)
                                            .setAction("Error", null).show();
                                }
                            } else {
                                Snackbar.make(view, "Formato de correo electrónico incorrecto", Snackbar.LENGTH_LONG)
                                        .setAction("Error", null).show();
                            }
                        }else {
                            Snackbar.make(view, "Ingrese su número celular", Snackbar.LENGTH_LONG)
                                    .setAction("Error", null).show();
                        }
                    }else{
                        Snackbar.make(view, "Formato de apellido incorrecto - Segundo apellido", Snackbar.LENGTH_LONG)
                                .setAction("Error", null).show();
                    }
                }else{
                    Snackbar.make(view, "Formato de apellido incorrecto - Primer apellido", Snackbar.LENGTH_LONG)
                            .setAction("Error", null).show();
                }
            }else{
                Snackbar.make(view, "Formato de nombre incorrecto", Snackbar.LENGTH_LONG)
                        .setAction("Error", null).show();
            }
        }
    };
    //Funciones para validar datos
    public boolean elementoNombreValido(String cadena){
        return Pattern.matches("^([A-ZÁ-Úa-zá-ú]+\\s{0,1}[(A-ZÁ-Úa-zá-ú)]+)+$", cadena);
    }
    public boolean emailValido(String cadena){
        return Pattern.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@+[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})+$", cadena);
    }
    public boolean contraseñaValida(String cadena){
        return Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%^&\\*]).{6,}$", cadena);
    }
}
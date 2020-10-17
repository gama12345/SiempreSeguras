package com.example.siempresegurasagcvim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class NuevoRegistroActivity extends AppCompatActivity {
    static EditText fecha;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevoregistro_activity);
        configurarSpinner();
        configurarAccionesBotones();
        configurarSeleccionarFechaNac();
        db = FirebaseFirestore.getInstance();
    }

    private void configurarSpinner(){
        Spinner listaMunicipios = findViewById(R.id.municipio);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lista_municipios, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listaMunicipios.setAdapter(adapter);
    }

    private void configurarAccionesBotones(){
        Button btnRegistrar = findViewById(R.id.btn_registrar);
        btnRegistrar.setOnClickListener(guardarRegistro);
        Button btnCancelar = findViewById(R.id.btn_cancelar);
        btnCancelar.setOnClickListener(cancelarRegistro);
    }
    View.OnClickListener guardarRegistro = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText nombre = findViewById(R.id.nombre);
            EditText p_apellido = findViewById(R.id.p_apellido);
            EditText s_apellido = findViewById(R.id.s_apellido);
            EditText fechaNac = findViewById(R.id.fecha_nac);
            Spinner municipio = findViewById(R.id.municipio);
            EditText correo = findViewById(R.id.correo_registro);
            EditText contraseña = findViewById(R.id.contraseña_registro);

            if(elementoNombreValido(nombre.getText().toString())){
                if(elementoNombreValido(p_apellido.getText().toString())){
                    if(elementoNombreValido(s_apellido.getText().toString())){
                        if(emailValido(correo.getText().toString())){
                            if(contraseñaValida(contraseña.getText().toString())){
                                //Creando el nuevo usuario
                                Map<String, Object> usuario = new HashMap<>();
                                usuario.put("nombre",nombre.getText().toString());
                                usuario.put("primer_apellido", p_apellido.getText().toString());
                                usuario.put("segundo_apellido", s_apellido.getText().toString());
                                usuario.put("fechaNac", fechaNac.getText().toString());
                                usuario.put("municipio", municipio.getSelectedItem().toString());
                                usuario.put("correo", correo.getText().toString());
                                usuario.put("contraseña", contraseña.getText().toString());
                                //Guardando en base de datos
                                db.collection("usuarios").add(usuario)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(view.getContext(), "Registro éxitoso", Toast.LENGTH_LONG).show();
                                                Intent cancelarRegistro = new Intent(NuevoRegistroActivity.this, MainActivity.class);
                                                NuevoRegistroActivity.this.startActivity(cancelarRegistro);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(view.getContext(), "Ha ocurrido un error al guardar registro - Base de datos", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }else{
                                Snackbar.make(view, "La contraseña debe contener al menos un caracter especial, número y mayúscula", Snackbar.LENGTH_LONG)
                                        .setAction("Error", null).show();
                            }
                        }else{
                            Snackbar.make(view, "Formato de correo electrónico incorrecto", Snackbar.LENGTH_LONG)
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
    View.OnClickListener cancelarRegistro = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent cancelarRegistro = new Intent(NuevoRegistroActivity.this, MainActivity.class);
            NuevoRegistroActivity.this.startActivity(cancelarRegistro);
        }
    };


    private void configurarSeleccionarFechaNac(){
        fecha = findViewById(R.id.fecha_nac);
        fecha.setOnClickListener(mostrarFecha);
    }
    View.OnClickListener mostrarFecha = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DialogFragment newDate = new DatePickerFragment();
            newDate.show(getSupportFragmentManager(), "datePicker");
        }
    };
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
            fecha.setText(String.format(formatoFecha, day)+"-"+String.format(formatoFecha, month+1)+"-"+year);
        }
    }


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
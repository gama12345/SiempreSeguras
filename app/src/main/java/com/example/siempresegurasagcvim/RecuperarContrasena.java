package com.example.siempresegurasagcvim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RecuperarContrasena extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.recuperarcontrasena_activity);
        EditText editText = findViewById(R.id.correo_recuperarContraseña);
        configurarBotones();
    }

    private void configurarBotones(){
        Button enviar = findViewById(R.id.btn_enviarEmail);
        enviar.setOnClickListener(enviarEmail);
        Button cancelar = findViewById(R.id.btn_cancelarRecuperarContraseña);
        cancelar.setOnClickListener(cancelarRecuperarContraseña);
    }

    View.OnClickListener enviarEmail = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View internalView = view;
            EditText correo = findViewById(R.id.correo_recuperarContraseña);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios")
                    .whereEqualTo("correo", correo.getText().toString())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           if (task.isSuccessful()) {
                               if (!task.getResult().isEmpty()) {
                                   // Propiedades
                                   Properties properties = System.getProperties();
                                   properties.put("mail.smtp.host", "smtp.gmail.com");
                                   properties.put("mail.smtp.socketFactory.port", "465");
                                   properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                                   properties.put("mail.smtp.auth", "true");
                                   properties.put("mail.smtp.port", "587");

                                   //Configuramos la sesión
                                   Session session = Session.getDefaultInstance(properties, null);

                                   // Configuramos los valores de nuestro mensaje
                                   MimeMessage mensaje = new MimeMessage(session);
                                   try {
                                       mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(correo.getText().toString()));
                                       mensaje.setSubject("Recuperación de contraseña - Siempre seguras AGCVIM");
                                       String password = "";
                                       for (QueryDocumentSnapshot document : task.getResult()) {
                                           password = document.get("contraseña").toString();
                                       }
                                       mensaje.setContent("<h1 style='color:#3498db;'>Restablecer tu contraseña</h1>\n" +
                                               "    <p>Se ha solicitado el restablecimiento de contraseña para esta cuenta en Siempre seguras AGCVIM.\n" +
                                               "         <br>Tu contraseña es:\n" +
                                               "         <br><b>"+password+"</b>\n" +
                                               "    </p>", "text/html");
                                       // Configuramos como sera el envio del correo
                                       Transport transport = session.getTransport("smtp");
                                       transport.connect("smtp.gmail.com", "siempre.seguras.AGCVIM@gmail.com", "siempreSegurasApp");
                                       transport.sendMessage(mensaje, mensaje.getAllRecipients());
                                       transport.close();
                                       Toast.makeText(view.getContext(), "Se ha enviado un mensaje a tu correo electrónico", Toast.LENGTH_LONG).show();
                                       Intent volverMain = new Intent(RecuperarContrasena.this, MainActivity.class);
                                       volverMain.setFlags(volverMain.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                       RecuperarContrasena.this.startActivity(volverMain);
                                   } catch (MessagingException e) {
                                       Snackbar.make(internalView, e.getMessage().toString(), Snackbar.LENGTH_LONG)
                                               .setAction("Error", null).show();
                                   }
                               } else {
                                   Snackbar.make(view, "Correo no registrado, intenta de nuevo", Snackbar.LENGTH_LONG)
                                           .setAction("Error", null).show();
                               }
                           }
                       }
                   });

        }
    };
    View.OnClickListener cancelarRecuperarContraseña = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent volverMain = new Intent(RecuperarContrasena.this, MainActivity.class);
            volverMain.setFlags(volverMain.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            RecuperarContrasena.this.startActivity(volverMain);
        }
    };
}
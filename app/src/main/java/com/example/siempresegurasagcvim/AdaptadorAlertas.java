package com.example.siempresegurasagcvim;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdaptadorAlertas extends RecyclerView.Adapter<AdaptadorAlertas.AdaptadorViewHolder> {
    ArrayList<Alerta> alertas;
    static Activity actividad;
    static Context contexto;

    public AdaptadorAlertas(ArrayList<Alerta> alertas, Activity activity) {
        this.alertas = alertas;
        actividad = activity;
        contexto = activity;
    }

    @NonNull
    @Override
    public AdaptadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alerta_cardview, parent, false);
        return new AdaptadorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final AdaptadorViewHolder holder, int position) {
        String[] seg3 = alertas.get(position).getMensaje().split(" activó una alerta");
        holder.nombreUsuaria.setText("Emitida por: "+seg3[0]);
        String[] seg = alertas.get(position).getMensaje().split("En: ");
        holder.direccionUsuaria.setText("Última dirección: "+seg[1]);
        String[] seg2 = alertas.get(position).getMensaje().split("Lat: ");
        String[] hora = seg2[0].split("Hora: ");
        holder.horaUsuaria.setText("Hora: "+hora[1]);
        holder.edo.setText("Estado: "+alertas.get(position).getEstado());
        if (!alertas.get(position).getImagen().equals("")){

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://siempre-seguras-agcvim.appspot.com//imgSiempreSeguras//"+alertas.get(position).getImagen());

            try {
            final File localFile;
                localFile = File.createTempFile("images", "");

                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        if(taskSnapshot.getStorage().getName().equals(alertas.get(position).getImagen())) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            holder.imgUsuaria.setImageBitmap(bitmap);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return alertas.size();
    }

    public class AdaptadorViewHolder extends RecyclerView.ViewHolder{
        TextView nombreUsuaria, direccionUsuaria, horaUsuaria, edo;
        ImageView imgUsuaria;

        public AdaptadorViewHolder(@NonNull View itemView) {
            super(itemView);
            edo = itemView.findViewById(R.id.estadoAlerta);
            nombreUsuaria = itemView.findViewById(R.id.nombreUsuaria);
            direccionUsuaria = itemView.findViewById(R.id.direccionUsuaria);
            horaUsuaria = itemView.findViewById(R.id.horaUsuaria);
            imgUsuaria = itemView.findViewById(R.id.imageViewU);
        }
    }
}

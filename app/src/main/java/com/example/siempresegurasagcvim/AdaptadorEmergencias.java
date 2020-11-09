package com.example.siempresegurasagcvim;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdaptadorEmergencias extends RecyclerView.Adapter<AdaptadorEmergencias.AdaptadorViewHolder> {
    ArrayList<Contacto> contactos;
    static Activity actividad;
    static Context contexto;

    public AdaptadorEmergencias(ArrayList<Contacto> contactos, Activity activity) {
        this.contactos = contactos;
        actividad = activity;
        contexto = activity;
    }

    @NonNull
    @Override
    public AdaptadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacto_emergencia_cardview, parent, false);
        return new AdaptadorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final AdaptadorViewHolder holder, int position) {
        holder.nombre.setText(contactos.get(position).getNombre());
        holder.tel.setText(contactos.get(position).getTelefono());
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    public class AdaptadorViewHolder extends RecyclerView.ViewHolder{
        TextView nombre, tel;

        public AdaptadorViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreContacto);
            tel = itemView.findViewById(R.id.telefonoContacto);
        }
    }
}

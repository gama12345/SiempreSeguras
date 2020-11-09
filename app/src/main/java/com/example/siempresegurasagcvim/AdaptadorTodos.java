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

public class AdaptadorTodos extends RecyclerView.Adapter<AdaptadorTodos.AdaptadorViewHolder> {
    ArrayList<Contacto> contactos;
    static Activity actividad;
    static Context contexto;

    public AdaptadorTodos(ArrayList<Contacto> contactos, Activity activity) {
        this.contactos = contactos;
        actividad = activity;
        contexto = activity;
    }

    @NonNull
    @Override
    public AdaptadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacto_cardview, parent, false);
        return new AdaptadorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final AdaptadorViewHolder holder, int position) {
        holder.nombre.setText(contactos.get(position).getNombre());
        holder.tel.setText(contactos.get(position).getTelefono());
        if(contactos.get(position).isSeleccionado()){
            holder.esContactoEmergencia.setChecked(true);
        }else{
            holder.esContactoEmergencia.setChecked(false);
        }
        holder.esContactoEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.esContactoEmergencia.isChecked()){
                    contactos.get(position).setSeleccionado(true);
                }else {
                    contactos.get(position).setSeleccionado(false);
                }
                Log.d(contactos.get(position).nombre, String.valueOf(holder.esContactoEmergencia.isChecked()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    public class AdaptadorViewHolder extends RecyclerView.ViewHolder{
        TextView nombre, tel;
        ToggleButton esContactoEmergencia;

        public AdaptadorViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreContacto);
            tel = itemView.findViewById(R.id.telefonoContacto);
            esContactoEmergencia = itemView.findViewById(R.id.toggleButton);
        }
    }
}

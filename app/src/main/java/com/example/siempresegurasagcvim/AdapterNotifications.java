package com.example.siempresegurasagcvim;

import com.example.siempresegurasagcvim.PuntosConexion;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterNotifications {


    public PuntosConexion startAdapter(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://siempresegurasapp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(PuntosConexion.class);
    }
}

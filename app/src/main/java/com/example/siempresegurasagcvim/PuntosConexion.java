package com.example.siempresegurasagcvim;

import com.example.siempresegurasagcvim.SolicitudNotificacion;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PuntosConexion {
    String ENVIARNOTIFICACION = "enviaralerta/{contacto}";
    @GET(ENVIARNOTIFICACION)
    Call<SolicitudNotificacion> enviarNotificacion(@Path("contacto") String contacto);
}

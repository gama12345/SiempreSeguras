package com.example.siempresegurasagcvim;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class HelperSQLite extends SQLiteOpenHelper {


    public HelperSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table miscontactos(nombre text, telefono text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists miscontactos");
        db.execSQL("create table miscontactos(nombre text, telefono text)");
    }

}
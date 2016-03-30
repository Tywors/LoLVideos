package com.tywors.lolvideos.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lenovo on 15/11/2015.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static int version = 1;
    private static String name = "LolVideosBD" ;
    private static SQLiteDatabase.CursorFactory factory = null;
    private SQLiteDatabase db2;

    public DbHelper(Context context)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.i(this.getClass().toString(), "Creando base de datos");
        db2 = db;
        db.execSQL( "CREATE TABLE video_votado(" +
                "id_video TEXT)" );


        Log.i(this.getClass().toString(), "Tabla video_votado creada");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public void DeleteBd(){
        db2.execSQL("DELETE FROM "+name);
    }
}
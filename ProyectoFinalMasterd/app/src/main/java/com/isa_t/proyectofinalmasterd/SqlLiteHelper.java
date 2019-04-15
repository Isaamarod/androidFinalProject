package com.isa_t.proyectofinalmasterd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by isa_t on 01/08/2017.
 */

public class SqlLiteHelper extends SQLiteOpenHelper{
    /**
     * NO SE DEBEN METER LAS IMAGENES ENTONCES EN UN CAMPO METEMOS EL PATH DE LA IMAGEN
     *
     */
    public static final int CURRENT_VERSION = 1;
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE NOTAS (id INTEGER PRIMARY KEY, lat REAL, long REAL, fecha TEXT , descripcion TEXT , titulo TEXT , foto BLOB)";

    private static final String DROP_TABLE_SQL =
            "DROP TABLE IF EXISTS NOTAS";


    public SqlLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    } //crear tabla nueva

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //para las actualizaciones
        db.execSQL(DROP_TABLE_SQL);
        this.onCreate(db);
    }
}





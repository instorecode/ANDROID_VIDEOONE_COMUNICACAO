package com.banco;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.br.instore.utils.Banco;
import com.utils.RegistrarLog;

public class BancoDAO {

    private Banco banco = new Banco();
    private Cursor cursor;
    private RegistrarLog registrarLog;
    private DatabaseHelper helper;
    private SQLiteDatabase db;


    public BancoDAO(Context context) {
        this.helper = new DatabaseHelper(context);
        this.registrarLog = new RegistrarLog(context);
    }

    public SQLiteDatabase getDb() {
        if (null == db) {
            db = helper.getWritableDatabase();
        }
        return db;
    }

    public void close() {
        helper.close();
    }

    public String quantidadeComerciaisNoBanco() {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT Arquivo FROM Comercial", new String[]{});
        String comerciaisNoBanco = "";
        comerciaisNoBanco = String.valueOf(cursor.getCount());
        cursor.close();
        return comerciaisNoBanco;
    }

    public String quantidadeVideoNoBanco() {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT Arquivo FROM Video", new String[]{});
        String videoNoBanco = "";
        videoNoBanco = String.valueOf(cursor.getCount());
        cursor.close();
        return videoNoBanco;
    }

}

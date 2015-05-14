package com.banco;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.br.instore.utils.Banco;
import com.utils.RegistrarLog;

import java.io.File;

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
        File banco = new File ( Environment.getExternalStorageDirectory().getAbsolutePath().concat("/videoOne/").concat("videoOneDs.db-journal"));
        if(banco.exists()){
            banco.delete();
            RegistrarLog.imprimirMsg("Log","BANCO EXCLUIDO");
        }
        if (null == db) {
            db = helper.getReadableDatabase();
        }
        return db;
    }

    public void close() {
        helper.close();
    }

    public String quantidadeComerciaisNoBanco() {
        SQLiteDatabase db = helper.getWritableDatabase();
        cursor = db.rawQuery("SELECT Arquivo FROM Comercial", new String[]{});
        String comerciaisNoBanco = "";
        comerciaisNoBanco = String.valueOf(cursor.getCount());
        return comerciaisNoBanco;
    }

    public String quantidadeVideoNoBanco() {
        SQLiteDatabase db = getDb();
        cursor = db.rawQuery("SELECT Arquivo FROM Video", new String[]{});
        String videoNoBanco = "";
        videoNoBanco = String.valueOf(cursor.getCount());
        return videoNoBanco;
    }
}

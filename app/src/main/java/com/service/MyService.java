package com.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import com.comunicacao.Comunicacao;
import com.comunicacao.MainActivity;
public class MyService extends Service {

    private Comunicacao comunicacao;
    private SQLiteDatabase db;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Context context = getApplicationContext();
        Intent intentMain = new Intent(context, MainActivity.class);
        intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentMain);
        return START_NOT_STICKY;
    }
}

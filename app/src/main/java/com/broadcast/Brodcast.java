package com.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.service.MyService;


public class Brodcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Brodcast OK", Toast.LENGTH_LONG).show();
        Intent startServiceIntent = new Intent(context, MyService.class);
        context.startService(startServiceIntent);
    }
}

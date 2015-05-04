package com.comunicacao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.broadcast.Brodcast;
import com.service.MyService;
import com.tarefa.TarefaComunicao;
import com.tarefa.TaskDiretorios;
import com.tarefa.TaskLerProperties;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    static Context context;
    static PopularBanco popularBanco;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        context = getApplicationContext();
        Toast.makeText(context, "MainActivity OK", Toast.LENGTH_LONG).show();

        ScheduledExecutorService thread1 = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService thread2 = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService thread3 = Executors.newScheduledThreadPool(1);
        thread1.scheduleAtFixedRate(new TaskLerProperties(context), 0, 10, TimeUnit.SECONDS);
        thread2.scheduleAtFixedRate(new TaskDiretorios(context), 500, 10000, TimeUnit.MILLISECONDS);
        thread3.scheduleAtFixedRate(new TarefaComunicao(context), 2000, 600000, TimeUnit.MILLISECONDS);
	}

}

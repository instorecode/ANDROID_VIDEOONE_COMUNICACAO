package com.comunicacao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.tarefa.TarefaComunicao;
import com.tarefa.TaskLerProperties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        context = getApplicationContext();

        ScheduledExecutorService threadLerProperties = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService threadComunicacaoNormal = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService threadComunicacaoEmergencia = Executors.newScheduledThreadPool(1);
        threadLerProperties.scheduleAtFixedRate(new TaskLerProperties(context), 0, 10, TimeUnit.SECONDS);
        threadComunicacaoNormal.scheduleAtFixedRate(new TarefaComunicao(context), 2, 30, TimeUnit.SECONDS);
        //threadComunicacaoEmergencia.scheduleAtFixedRate(new TarefaComunicao(context), 5, 1800, TimeUnit.SECONDS);
	}

}

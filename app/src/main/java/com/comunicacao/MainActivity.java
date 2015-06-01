package com.comunicacao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.tarefas.TaskBackup;
import com.tarefas.TaskComerciaisDeterminados;
import com.tarefas.TaskCriarViewExcluirInvalidos;
import com.tarefas.TaskLerProperties;
import com.tarefas.TarefaComunicao;
import com.tarefas.TaskVideoAndComerciais;

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

        ScheduledExecutorService lerProperties = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService criarViewExcluirVencidos = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService threadComunicacaoNormal = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService threadComunicacaoEmergencia = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService criarPlayListDeterminados = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService criarPlayListNormal = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService backup = Executors.newScheduledThreadPool(1);


        lerProperties.scheduleAtFixedRate(new TaskLerProperties(context), 0, 10, TimeUnit.SECONDS);
        backup.scheduleAtFixedRate(new TaskBackup(), 2, 864000,TimeUnit.SECONDS);
        criarViewExcluirVencidos.scheduleAtFixedRate(new TaskCriarViewExcluirInvalidos(context), 0, 8, TimeUnit.HOURS);
        threadComunicacaoNormal.scheduleAtFixedRate(new TarefaComunicao(context,false), 2, 120, TimeUnit.SECONDS);
        threadComunicacaoEmergencia.scheduleAtFixedRate(new TarefaComunicao(context,true), 10, 1800, TimeUnit.SECONDS);
        criarPlayListDeterminados.scheduleAtFixedRate(new TaskComerciaisDeterminados(context), 4, 120, TimeUnit.SECONDS);
        criarPlayListNormal.scheduleAtFixedRate(new TaskVideoAndComerciais(context), 4, 120, TimeUnit.SECONDS);
	}

}

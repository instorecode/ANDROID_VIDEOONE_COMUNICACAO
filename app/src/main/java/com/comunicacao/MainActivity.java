package com.comunicacao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.banco.BancoDAO;
import com.tarefas.TaskBackup;
import com.tarefas.TaskBanco;
import com.tarefas.TaskLerProperties;
import com.tarefas.TarefaComunicao;
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
        BancoDAO.getInstance(context);

        ScheduledExecutorService lerProperties = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService threadComunicacaoNormal = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService threadComunicacaoEmergencia = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService criarPlayList = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService backup = Executors.newScheduledThreadPool(1);


        lerProperties.scheduleAtFixedRate(new TaskLerProperties(context), 0, 10, TimeUnit.SECONDS);
        backup.scheduleAtFixedRate(new TaskBackup(), 2, 864000,TimeUnit.SECONDS);
        threadComunicacaoNormal.scheduleAtFixedRate(new TarefaComunicao(context,false,this), 2, 60, TimeUnit.SECONDS);
        threadComunicacaoEmergencia.scheduleAtFixedRate(new TarefaComunicao(context,true, this), 10, 1800, TimeUnit.SECONDS);
        criarPlayList.scheduleAtFixedRate(new TaskBanco(), 3, 120, TimeUnit.SECONDS);
	}

}

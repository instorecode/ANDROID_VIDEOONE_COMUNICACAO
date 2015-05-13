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
        Toast.makeText(context, "MainActivity OK", Toast.LENGTH_LONG).show();

        ScheduledExecutorService thread1 = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService thread2 = Executors.newScheduledThreadPool(1);
        thread1.scheduleAtFixedRate(new TaskLerProperties(), 0, 10, TimeUnit.SECONDS);
        thread2.schedule(new TarefaComunicao(context), 2, TimeUnit.SECONDS);




	}

}

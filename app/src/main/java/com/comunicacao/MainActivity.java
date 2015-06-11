package com.comunicacao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.banco.BancoDAO;
import com.br.instore.bean.Ftp;
import com.br.instore.utils.LogUtils;
import com.tarefas.TaskBackup;
import com.tarefas.TaskBanco;
import com.tarefas.TaskLerProperties;
import com.tarefas.TarefaComunicao;
import com.timers.SimpleTimer;

import java.util.Timer;
import java.util.TimerTask;

import it.sauronsoftware.ftp4j.FTPClient;

public class MainActivity extends Activity {
    static Context context;

    FTPClient ftp = new FTPClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        context = getApplicationContext();
        BancoDAO.getInstance(context);
        SimpleTimer timer = new SimpleTimer();

        final TaskLerProperties taskLerProperties = new TaskLerProperties(context);
        timer.sched(new TimerTask() {
            @Override
            public void run() {
                taskLerProperties.run();
            }
        }, 0, 10000l);

        final TaskBackup taskBackup = new TaskBackup();
        timer.sched(new TimerTask() {
            @Override
            public void run() {
                taskBackup.run();
            }
        }, 3000l, (8 * (60 * (60 * (1 * 1000)))));

        final TarefaComunicao tarefaComunicao = new TarefaComunicao();
        timer.sched(new TimerTask() {
            @Override
            public void run() {
                if(!ftp.isConnected()){
                    tarefaComunicao.run(false, ftp);
                }
            }
        }, 1000l, 180000l );

        timer.sched(new TimerTask() {
            @Override
            public void run() {
                if (!ftp.isConnected()) {
                    tarefaComunicao.run(true, ftp);
                }
            }
        }, 10000l, (30 * (60 * (1 * 1000))));

        final TaskBanco taskBanco = new TaskBanco();
        timer.sched(new TimerTask() {
            @Override
            public void run() {
                taskBanco.run();
            }
        }, 3000l, 120000);
	}

}

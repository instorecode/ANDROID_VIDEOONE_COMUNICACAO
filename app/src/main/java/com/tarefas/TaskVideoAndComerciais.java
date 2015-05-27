package com.tarefas;

import android.content.Context;

import com.banco.BancoDAO;
import com.utils.RegistrarLog;

public class TaskVideoAndComerciais implements Runnable {
    private BancoDAO bancoDAO;
    private Context context;

    public TaskVideoAndComerciais(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        bancoDAO = new BancoDAO(context);
        bancoDAO.programacoes();
        bancoDAO.criarArquivoPlaylist();
        bancoDAO.close();
        RegistrarLog.imprimirMsg("Log", "TaskVideoAndComerciais");
    }
}

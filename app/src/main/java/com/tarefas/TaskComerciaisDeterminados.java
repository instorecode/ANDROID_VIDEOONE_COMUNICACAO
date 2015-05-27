package com.tarefas;

import android.content.Context;

import com.banco.BancoDAO;
import com.utils.RegistrarLog;

public class TaskComerciaisDeterminados implements Runnable {

    private BancoDAO bancoDAO;
    private Context context;

    public TaskComerciaisDeterminados(Context context) {
        this.bancoDAO = new BancoDAO(context);
        this.context = context;
    }


    @Override
    public void run() {
        bancoDAO.comerciaisDeterminados();
        bancoDAO.controladorComercialDependencia();
        bancoDAO.criarPlaylistDeterminados();
        bancoDAO.close();
        RegistrarLog.imprimirMsg("Log", "TaskComerciaisDeterminados");
    }
}

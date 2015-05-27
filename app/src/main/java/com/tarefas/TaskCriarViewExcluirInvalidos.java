package com.tarefas;

import android.content.Context;

import com.banco.BancoDAO;
import com.utils.RegistrarLog;

public class TaskCriarViewExcluirInvalidos implements Runnable {

    private Context context;
    private BancoDAO bancoDAO;

    public TaskCriarViewExcluirInvalidos(Context context){
        this.context = context;
    }

    @Override
    public void run() {
        bancoDAO = new BancoDAO(context);
        bancoDAO.criarViewComercial();
        bancoDAO.criarViewComercialDetermidos();
        bancoDAO.criarViewProgramacao();
        bancoDAO.criarViewVideo();
        bancoDAO.excluirComercialDoBanco();
        bancoDAO.excluirVideosDoBanco();
        bancoDAO.close();
        RegistrarLog.imprimirMsg("Log", "TaskCriarViewExcluirInvalidos");
    }
}

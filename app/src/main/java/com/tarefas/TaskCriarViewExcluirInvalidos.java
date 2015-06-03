package com.tarefas;

import android.content.Context;
import android.os.Environment;

import com.banco.BancoDAO;
import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.LogUtils;
import com.utils.RegistrarLog;

import java.io.File;

public class TaskCriarViewExcluirInvalidos implements Runnable {

    private final File arquivoBanco = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/videoOne/").concat("videoOneDs.db"));
    private Context context;
    private BancoDAO bancoDAO;

    public TaskCriarViewExcluirInvalidos(Context context){
        this.context = context;
    }

    @Override
    public void run() {
        if(arquivoBanco.exists()) {
            /*bancoDAO = new BancoDAO(context);
            bancoDAO.criarViewComercial();
            bancoDAO.criarViewComercialDetermidos();
            bancoDAO.criarViewProgramacao();
            bancoDAO.criarViewVideo();
            bancoDAO.excluirComercialDoBanco();
            bancoDAO.excluirVideosDoBanco();
            bancoDAO.close();*/
        } else {
            RegistrarLog.imprimirMsg("Log", "Banco não foi encontrado : TaskCriarViewExcluirInvalidos");
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : TaskCriarViewExcluirInvalidos");
        }
        RegistrarLog.imprimirMsg("Log", "TaskCriarViewExcluirInvalidos");
    }
}

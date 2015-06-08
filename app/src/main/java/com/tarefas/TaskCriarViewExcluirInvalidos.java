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


    @Override
    public void run() {
        if(arquivoBanco.exists()) {
            BancoDAO.criarViewComercial();
            BancoDAO.criarViewComercialDetermidos();
            BancoDAO.criarViewProgramacao();
            BancoDAO.criarViewVideo();
            BancoDAO.excluirComercialDoBanco();
            BancoDAO.excluirVideosDoBanco();
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : TaskCriarViewExcluirInvalidos");
        }
        RegistrarLog.imprimirMsg("Log", "TaskCriarViewExcluirInvalidos");
    }
}

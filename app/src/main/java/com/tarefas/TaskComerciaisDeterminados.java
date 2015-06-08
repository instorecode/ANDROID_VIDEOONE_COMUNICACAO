package com.tarefas;

import android.os.Environment;

import com.banco.BancoDAO;
import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.LogUtils;
import com.utils.RegistrarLog;

import java.io.File;

public class TaskComerciaisDeterminados implements Runnable {

    private final File arquivoBanco = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/videoOne/").concat("videoOneDs.db"));

    @Override
    public void run() {
        if(arquivoBanco.exists()){
            BancoDAO.comerciaisDeterminados();
            BancoDAO.controladorComercialDependencia();
            BancoDAO.criarPlaylistDeterminados();
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : TaskComerciaisDeterminados");
        }
        RegistrarLog.imprimirMsg("Log", "TaskComerciaisDeterminados");
    }
}

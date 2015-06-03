package com.tarefas;

import android.content.Context;
import android.os.Environment;

import com.banco.BancoDAO;
import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.LogUtils;
import com.utils.RegistrarLog;

import java.io.File;

public class TaskComerciaisDeterminados implements Runnable {

    private BancoDAO bancoDAO;
    private Context context;
    private final File arquivoBanco = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/videoOne/").concat("videoOneDs.db"));

    public TaskComerciaisDeterminados(Context context) {
        this.context = context;
    }


    @Override
    public void run() {
        if(arquivoBanco.exists()){
            /*
            bancoDAO = new BancoDAO(context);
            bancoDAO.comerciaisDeterminados();
            bancoDAO.controladorComercialDependencia();
            bancoDAO.criarPlaylistDeterminados();
            bancoDAO.close();*/
        } else {
            RegistrarLog.imprimirMsg("Log", "Banco não foi encontrado : TaskComerciaisDeterminados");
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : TaskComerciaisDeterminados");
        }
        RegistrarLog.imprimirMsg("Log", "TaskComerciaisDeterminados");
    }
}

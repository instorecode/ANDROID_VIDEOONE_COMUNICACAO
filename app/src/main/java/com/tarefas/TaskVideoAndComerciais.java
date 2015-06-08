package com.tarefas;

import android.content.Context;
import android.os.Environment;

import com.banco.BancoDAO;
import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.LogUtils;
import com.utils.RegistrarLog;

import java.io.File;

public class TaskVideoAndComerciais implements Runnable {

    private final File arquivoBanco = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/videoOne/").concat("videoOneDs.db"));
    private BancoDAO bancoDAO;
    private Context context;

    public TaskVideoAndComerciais(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        RegistrarLog.imprimirMsg("Log", "INICIO TaskVideoAndComerciais");
        if(arquivoBanco.exists()) {
            BancoDAO.programacoes();
            BancoDAO.criarArquivoPlaylist();

        } else {
            RegistrarLog.imprimirMsg("Log", "Banco não foi encontrado : TaskVideoAndComerciais");
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : TaskVideoAndComerciais");
        }
        RegistrarLog.imprimirMsg("Log", "FIM TaskVideoAndComerciais");
    }
}

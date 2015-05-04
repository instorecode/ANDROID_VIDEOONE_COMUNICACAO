package com.tarefa;

import android.content.Context;
import android.os.Environment;

import com.br.instore.utils.ConfiguaracaoUtils;
import com.utils.RegistrarLog;

import java.io.File;

public class TaskLerProperties implements Runnable {

    private final String barraDoSistema = System.getProperty("file.separator");
    private final String caminho = Environment.getExternalStorageDirectory().toString();
    private RegistrarLog registrarLog;
    private Context context;

    public TaskLerProperties(Context context) {
        this.context = context;
        registrarLog = new RegistrarLog(context);
    }

    @Override
    public void run() {
        String caminhoProperties = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config").concat(barraDoSistema).concat("configuracoes.properties");
        File properties = new File(caminhoProperties);
        if (properties.exists()) {
            ConfiguaracaoUtils.lerProperties(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config").concat(barraDoSistema).concat("configuracoes.properties"));
        } else {
            registrarLog.escrever(" Properties não existe TaskLerProperties");
        }
    }
}

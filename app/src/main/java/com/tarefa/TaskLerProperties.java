package com.tarefa;

import android.os.Environment;

import com.br.instore.utils.ConfiguaracaoUtils;
import com.utils.RegistrarLog;

public class TaskLerProperties implements Runnable {
    private final String barraDoSistema = System.getProperty("file.separator");
    private final String caminho = Environment.getExternalStorageDirectory().toString();

    @Override
    public void run() {
        ConfiguaracaoUtils.lerProperties(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config").concat(barraDoSistema).concat("configuracoes.properties"));
        RegistrarLog.imprimirMsg("Log", "TaskLerProperties");
    }
}

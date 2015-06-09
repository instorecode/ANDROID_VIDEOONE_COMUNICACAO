package com.tarefas;

import android.content.Context;
import android.os.Environment;

import com.br.instore.utils.ConfiguaracaoUtils;
import com.utils.RegistrarLog;

public class TaskLerProperties  {
    private final String barraDoSistema = System.getProperty("file.separator");
    private final String caminho = Environment.getExternalStorageDirectory().toString();
    private Context context;

    public TaskLerProperties(Context context){
        this.context = context;
    }

    public void run() {
        ConfiguaracaoUtils.lerProperties(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config").concat(barraDoSistema).concat("configuracoes.properties"));
        RegistrarLog.getInstance(context);
        //RegistrarLog.imprimirMsg("Log", "Fim TaskLerProperties");
    }
}

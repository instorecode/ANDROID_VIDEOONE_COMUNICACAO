package com.tarefa;

import android.content.Context;
import android.os.Environment;

import com.br.instore.utils.Arquivo;
import com.br.instore.utils.ConfiguaracaoUtils;

import java.io.File;

/**
 * Created by usuario on 28/04/2015.
 */
public class TaskDiretorios implements Runnable {

    private final String barraDoSistema = System.getProperty("file.separator");
    private final String caminho = Environment.getExternalStorageDirectory().toString();
    public static String diretorioVideoPrimario = "";
    public static String diretorioVideoSecundario = "";
    public static String diretorioVideoTerciario = "";
    public static String diretorioPlaylist = "";
    private Context context;


    public TaskDiretorios (Context context){
        this.context = context;
    }

    @Override
    public void run() {
        String caminhoProperties = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config").concat(barraDoSistema).concat("configuracoes.properties");
        File properties = new File(caminhoProperties);

        if (!properties.exists()) {
            this.diretorioVideoPrimario = caminho.concat(barraDoSistema).concat("videos").concat(barraDoSistema);
            this.diretorioVideoSecundario = caminho.concat(barraDoSistema).concat("videos").concat(barraDoSistema);
            this.diretorioVideoTerciario = caminho.concat(barraDoSistema).concat("videos").concat(barraDoSistema);
            this.diretorioPlaylist = caminho.concat(barraDoSistema).concat("videoOne/playlist").concat(barraDoSistema);

            Arquivo.criarDiretorio(new File(this.diretorioVideoPrimario));
            Arquivo.criarDiretorio(new File(this.diretorioPlaylist));
            return;
        }

        this.diretorioVideoPrimario = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo()).concat(barraDoSistema);
        this.diretorioVideoSecundario = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioSecundarioVideo()).concat(barraDoSistema);
        this.diretorioVideoTerciario = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioTerciario()).concat(barraDoSistema);
        this.diretorioPlaylist = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioPlaylist()).concat(barraDoSistema);
    }

}

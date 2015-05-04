package com.comunicacao;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private final String barraDoSistema = System.getProperty("file.separator");
    private final String caminho = Environment.getExternalStorageDirectory().toString();
    private Informacoes informacoes;
    public Context context;


    public Utils(Context contextParametro){
        this.context = contextParametro;
    }

    public void criarLog(LogUtils logUtils) {
        ConfiguaracaoUtils.lerProperties(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config").concat(barraDoSistema).concat("configuracoes.properties"));
        informacoes = new Informacoes(context);
        String nomeVersaoOs = informacoes.versaoAndroid(context);
        String versaoApp = informacoes.versaoAndroid(context);
        String ip = informacoes.ip();
        String dia = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String nomeDispositivo = informacoes.nomeDispositivo();
        String espacoTotal = informacoes.espacoTotal();
        String espacoDisponivel = informacoes.espacoDisponivel();
        String arquivosDiretorio = informacoes.arquivosDiretorio();
        String videosNoBanco = informacoes.videosNoBanco();
        String comerciaisNoBanco = informacoes.comerciaisNoBanco();
        String diretorioLogs = Environment.getExternalStorageDirectory() + "/videoOne/log";
        logUtils.parametros(nomeVersaoOs, versaoApp, ip, dia, nomeDispositivo, espacoTotal, espacoDisponivel, videosNoBanco, comerciaisNoBanco, arquivosDiretorio, diretorioLogs);
    }
}

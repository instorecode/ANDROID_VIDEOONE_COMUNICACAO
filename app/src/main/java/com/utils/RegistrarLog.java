package com.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.banco.BancoDAO;
import com.br.instore.utils.Arquivo;
import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.LogUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class RegistrarLog {

    private LogUtils logUtils;
    private Context context;
    private String barraDoSistema = System.getProperty("file.separator");
    private String caminho = Environment.getExternalStorageDirectory().toString();
    private String nomeVersaoOs = "";
    private String versaoApp = "";
    private String ip = "";
    private String dia = "";
    private String nomeDispositivo = "";
    private String espacoTotal = "";
    private String espacoDisponivel = "";
    private String arquivosDiretorio = "";
    private String videosNoBanco = "";
    private String comerciaisNoBanco = "";
    private String diretorioLogs;
    private BancoDAO bancoDAO;

    public RegistrarLog(Context context) {
        this.context = context;
        this.logUtils = new LogUtils();
        this.bancoDAO = new BancoDAO(context);
    }

    public static void imprimirMsg(String tag, String texto){
        Log.e(tag, texto);
    }

    private void informacaoes() {
        videosNoBanco = bancoDAO.quantidadeVideoNoBanco();
        comerciaisNoBanco = bancoDAO.quantidadeComerciaisNoBanco();
        nomeVersaoOs = versaoAndroid(context);
        versaoApp = versaoAndroid(context);
        ip = ip();
        dia = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        nomeDispositivo = nomeDispositivo();
        espacoTotal = espacoTotal();
        espacoDisponivel = espacoDisponivel();
        arquivosDiretorio = arquivosDiretorio();
        diretorioLogs = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("log");
        logUtils.parametros(nomeVersaoOs, versaoApp, ip, dia, nomeDispositivo, espacoTotal, espacoDisponivel, videosNoBanco, comerciaisNoBanco, arquivosDiretorio, diretorioLogs);
    }

    public void escrever(String texto) {
        String caminhoProperties = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config").concat(barraDoSistema).concat("configuracoes.properties");
        File properties = new File(caminhoProperties);

        if(properties.exists()){
            informacaoes();
        }
        Log.e("Log",texto);
        logUtils.registrar(texto);
    }

    private String versaoAndroid(Context context) {
        String versaoApp = "";
        try {
            versaoApp = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            versaoApp = versaoApp + "." + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return versaoApp;
        } catch (NullPointerException e){
            return versaoApp;
        } catch (Exception e){
            return versaoApp;
        }
        return versaoApp;
    }

    private String nomeDispositivo() {
        String nomeDispositivo = "";
        nomeDispositivo = android.os.Build.MODEL;
        return nomeDispositivo;
    }

    private String ip() {
        String ipDispositivo = "";
        try {
            Enumeration<NetworkInterface> enumerationNetworkInterface = NetworkInterface.getNetworkInterfaces();
            while (enumerationNetworkInterface.hasMoreElements()) {
                NetworkInterface networkInterface = enumerationNetworkInterface.nextElement();
                Enumeration<InetAddress> enumerationInetAddress = networkInterface.getInetAddresses();
                while (enumerationInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumerationInetAddress.nextElement();
                    ipDispositivo = inetAddress.getHostAddress().toString();
                }
            }
        } catch (SocketException e) {
            return ipDispositivo;
        } catch (NullPointerException e){
            return ipDispositivo;
        } catch (Exception e){
            return ipDispositivo;
        }
        return ipDispositivo;
    }

    private String espacoTotal() {
        String espacoTotal = "";
        long espaco = new File(Environment.getExternalStorageDirectory().getAbsolutePath()).getTotalSpace();
        double espacoDouble = espaco / (1024 * 1024);

        try{
            espacoTotal = String.valueOf(espacoDouble);
        } catch (NullPointerException e){
            return espacoTotal;
        } catch (InvalidParameterException e){
            return espacoTotal;
        } catch (Exception e){
            return espacoTotal;
        }
        return espacoTotal;
    }

    private String espacoDisponivel() {
        String espacoDisponivel = "";
        long espaco = new File(Environment.getExternalStorageDirectory().getAbsolutePath()).getFreeSpace();
        double espacoDouble = espaco / (1024 * 1024);
        try{
            espacoDisponivel = String.valueOf(espacoDouble);
        } catch (NullPointerException e) {
            return espacoDisponivel;
        } catch (InvalidParameterException e){
            return espacoDisponivel;
        } catch (Exception e){
            return espacoDisponivel;
        }
        return espacoDisponivel;
    }

    private String arquivosDiretorio() {
        String arquivosNoDiretorio = "";
        try {
            File file = new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo()));
            Arquivo.criarDiretorio(file);
            arquivosNoDiretorio = String.valueOf(file.listFiles().length);
        } catch (NullPointerException e) {
            return arquivosNoDiretorio;
        } catch (InvalidParameterException e){
            return arquivosNoDiretorio;
        } catch (Exception e) {
            return arquivosNoDiretorio;
        }
        return arquivosNoDiretorio;
    }

}

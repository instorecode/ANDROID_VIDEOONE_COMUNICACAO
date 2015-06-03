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

    private String barraDoSistema = System.getProperty("file.separator");
    private String caminho = Environment.getExternalStorageDirectory().toString();
    private String dia = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    private String diretorioLogs = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("log");
    private String caminhoArquivoDiasLogCompleto = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config").concat(barraDoSistema);

    private Context context;
    private static RegistrarLog registrarLog;

    private RegistrarLog(Context context){
        this.context = context;
    }

    public static RegistrarLog getInstance() throws Exception{
        if(null == registrarLog){
            try {
               throw new Exception("Informe o parametro");
            } catch (Exception e){
               AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            }
        }

        try {
            LogUtils.getInstance();
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
        }
        return registrarLog;
    }

    public static RegistrarLog getInstance(Context context){
        if(null == registrarLog){
            registrarLog = new RegistrarLog(context);
        }
        LogUtils.getInstance(registrarLog.caminhoArquivoDiasLogCompleto, "dias.exp", registrarLog.nomeVersaoOs(registrarLog.context), registrarLog.nomeDispositivo(), registrarLog.ip(), registrarLog.dia, registrarLog.nomeDispositivo(), registrarLog.espacoTotal(), registrarLog.espacoDisponivel(), BancoDAO.quantidadeVideoNoBanco(), BancoDAO.quantidadeComerciaisNoBanco(), registrarLog.arquivosDiretorio(), registrarLog.diretorioLogs);
        return registrarLog;
    }

    public static void imprimirMsg(String tag, String texto){
        Log.e(tag, texto);
    }

    private String nomeVersaoOs(Context context) {
        String versaoApp = "";
        try {
            versaoApp = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            versaoApp = versaoApp + "." + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return versaoApp;
        } catch (NullPointerException e){
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return versaoApp;
        } catch (Exception e){
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
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
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return ipDispositivo;
        } catch (NullPointerException e){
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return ipDispositivo;
        } catch (Exception e){
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
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
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return espacoTotal;
        } catch (InvalidParameterException e){
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return espacoTotal;
        } catch (Exception e){
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
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
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return espacoDisponivel;
        } catch (InvalidParameterException e){
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return espacoDisponivel;
        } catch (Exception e){
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return espacoDisponivel;
        }
        return espacoDisponivel;
    }

    private String arquivosDiretorio() {
        String arquivosNoDiretorio = "";
        try {
            File file = new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo()));
            arquivosNoDiretorio = String.valueOf(file.listFiles().length);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return arquivosNoDiretorio;
        } catch (InvalidParameterException e){
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return arquivosNoDiretorio;
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(RegistrarLog.class, e);
            return arquivosNoDiretorio;
        }
        return arquivosNoDiretorio;
    }

}

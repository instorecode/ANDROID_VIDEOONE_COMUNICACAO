package com.comunicacao;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import com.br.instore.utils.Arquivo;
import com.br.instore.utils.ConfiguaracaoUtils;



public class Informacoes {


    private static SQLiteDatabase db;
    private Context context;
    private final String caminho = Environment.getExternalStorageDirectory().toString();
    private String barraDoSistema = System.getProperty("file.separator");

    public Informacoes(Context contextParametro) {
        this.context = contextParametro;
    }

    public String versaoAndroid(Context context) {
        String versaoApp = null;
        try {
            versaoApp = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            versaoApp = versaoApp + "." + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versaoApp;
    }

    public String nomeDispositivo() {
        String nomeDispositivo = null;
        nomeDispositivo = android.os.Build.MODEL;
        return nomeDispositivo;
    }

    public String ip() {
        String ipDispositivo = null;
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
            e.printStackTrace();
        }
        return ipDispositivo;
    }

    public String espacoTotal() {
        String espacoTotal = null;
        long espaco = new File(Environment.getExternalStorageDirectory().getAbsolutePath()).getTotalSpace();
        double espacoDouble = espaco / (1024 * 1024);
        espacoTotal = String.valueOf(espacoDouble);
        return espacoTotal;
    }

    public String espacoDisponivel() {
        String espacoDisponivel = null;
        long espaco = new File(Environment.getExternalStorageDirectory().getAbsolutePath()).getFreeSpace();
        double espacoDouble = espaco / (1024 * 1024);
        espacoDisponivel = String.valueOf(espacoDouble);
        return espacoDisponivel;
    }

    public String arquivosDiretorio() {
        String arquivosNoDiretorio = null;
        String caminho = Environment.getExternalStorageDirectory() + "/" + ConfiguaracaoUtils.diretorio.getDiretorioVideo();
        File file = new File(caminho);
        Arquivo.criarDiretorio(file);
        arquivosNoDiretorio = String.valueOf(file.listFiles().length);
        return arquivosNoDiretorio;
    }

    public String comerciaisNoBanco() {
        db = context.openOrCreateDatabase(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("videoOneDs.db"), Context.MODE_PRIVATE, null);
        String comerciaisNoBanco = null;
        Cursor cursor = db.rawQuery("SELECT Arquivo FROM Comercial", new String[]{});
        comerciaisNoBanco = String.valueOf(cursor.getCount());
        cursor.close();
        while(db.isOpen()){
            db.close();
        }
        return comerciaisNoBanco;
    }

    public String videosNoBanco() {
        db = context.openOrCreateDatabase(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("videoOneDs.db"), Context.MODE_PRIVATE, null);
        String videosNoBanco = null;
        Cursor cursor = db.rawQuery("SELECT Arquivo FROM Video", new String[]{});
        videosNoBanco = String.valueOf(cursor.getCount());
        cursor.close();
        while(db.isOpen()){
            db.close();
        }
        return videosNoBanco;
    }
}

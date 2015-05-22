package com.tarefa;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.banco.BancoDAO;
import com.br.instore.utils.Arquivo;
import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.LogUtils;
import com.br.instore.utils.Md5Utils;
import com.comunicacao.TransferCustom;
import com.comunicacao.ValidarDiaAndHora;
import com.utils.AndroidImprimirUtils;
import com.utils.RegistrarLog;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

public class TarefaComunicao implements Runnable {

    private final Context context;
    private FTPClient ftp = new FTPClient();
    private Md5Utils md5Utils = new Md5Utils();
    private BancoDAO bancoDAO;

    private final String barraDoSistema = System.getProperty("file.separator");
    private final String caminho = Environment.getExternalStorageDirectory().toString();
    private String salvar_importes = "";
    private String salvar_export = "";
    private String caminhoArquivoZip = "";
    private String diretorioConfig = "";
    private String diretorioDeVideos = "";
    private String diretorioLog = "";
    private String diretorioVideoOne = "";
    private int maximoTentativas = 10;
    private int tentativasRealizadas = 0;
    private String hashId = "";
    private ValidarDiaAndHora validarHoraAndDia;


    public TarefaComunicao(Context context) {
        Toast.makeText(context, "CONSTRUTOR", Toast.LENGTH_LONG).show();
        this.context = context;
        bancoDAO = new BancoDAO(context);
    }

    @Override
    public void run(){
        try {
            String a = null;
            a.concat(null);
        } catch (NullPointerException e){
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
        } catch (Exception e){
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
        }

        Log.e("Log", "Ok");
    }



    public void runa() {
        File properties = new File(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config"));

        try {
            validarHoraAndDia = new ValidarDiaAndHora(caminho.concat("/videoOne/config/configuracoes.properties"));

            if(properties.exists()){
                informacoesConexao();
                validarHoraAndDia.procurarHorarioValido();

                if (validarHoraAndDia.isValid()) {
                    if (!hashId.equals(validarHoraAndDia.hashId())) {
                        //ZERA AS TENTATIVAS DEVIDO OS HORARIOS SEREM DIFERENTES
                        RegistrarLog.imprimirMsg("Log","É UM NOVO HORARIO VALIDO RODANDO");
                        tentativasRealizadas = 0;
                    }

                    if (tentativasRealizadas < maximoTentativas) {
                        RegistrarLog.imprimirMsg("Log","É UM HORARIO VALIDO RODANDO TENTATIVAS TAMBEM VALIDAS");
                        conectarEnderecoFtp(false);
                        popularBanco();
                        hashId = validarHoraAndDia.hashId();
                    }

                } else {
                    // executar o emergencia
                    RegistrarLog.imprimirMsg("Log","NÂO É UM HORARIO VALIDO RODANDO EMERGENCIA");
                    conectarEnderecoFtp(true);
                    popularBanco();
                    tentativasRealizadas = 0;
                }

            } else {
                LogUtils.registrar(01, true, " Arquivo de configurações não existe, não é possível se comunicar");
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                } catch (NullPointerException e){
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                } catch (InstantiationError e){
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                } catch (InvalidParameterException e){
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                }
                run();
            }


        } catch (NullPointerException e){
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
        } catch (InstantiationError e){
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
        } catch (InvalidParameterException e){
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
        }
    }

    private void informacoesConexao() {
        try {
            salvar_importes = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioImportacao());
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
        } catch (NullPointerException e) {
            salvar_importes = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("import");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
        } catch (InvalidParameterException e) {
            salvar_importes = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("import");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
        } catch (Exception e) {
            salvar_importes = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("import");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
        }

        try {
            salvar_export = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioExportacao());
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_export);
        } catch (NullPointerException e) {
            salvar_export = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("export");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_export);
        } catch (InvalidParameterException e) {
            salvar_export = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("export");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_export);
        } catch (Exception e) {
            salvar_export = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("export");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_export);
        }

        try {
            caminhoArquivoZip = salvar_export.concat(barraDoSistema).concat("videoOne.zip");
        } catch (NullPointerException e) {
            caminhoArquivoZip = "/mnt/sdcard/videoOne/export/videoOne.zip";
        } catch (InvalidParameterException e) {
            caminhoArquivoZip = "/mnt/sdcard/videoOne/export/videoOne.zip";
        } catch (Exception e) {
            caminhoArquivoZip = "/mnt/sdcard/videoOne/export/videoOne.zip";
        }

        try {
            diretorioConfig = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config");
        } catch (NullPointerException e) {
            diretorioConfig = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioConfig);
        } catch (InvalidParameterException e) {
            diretorioConfig = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioConfig);
        } catch (Exception e) {
            diretorioConfig = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioConfig);
        }

        try {
            diretorioLog = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioLog());
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioLog);
        } catch (NullPointerException e) {
            diretorioLog = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("log");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioLog);
        } catch (InvalidParameterException e) {
            diretorioLog = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("log");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioLog);
        } catch (Exception e) {
            diretorioLog = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("log");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioLog);
        }

        try {
            diretorioDeVideos = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo());
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioDeVideos);
        } catch (NullPointerException e) {
            diretorioDeVideos = caminho.concat(barraDoSistema).concat("videos");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioDeVideos);
        } catch (InvalidParameterException e) {
            diretorioDeVideos = caminho.concat(barraDoSistema).concat("videos");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioDeVideos);
        } catch (Exception e) {
            diretorioDeVideos = caminho.concat(barraDoSistema).concat("videos");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioDeVideos);
        }

        try {
            diretorioVideoOne = caminho.concat(barraDoSistema).concat("videoOne");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioVideoOne);
        } catch (NullPointerException e) {
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioVideoOne);
        } catch (InvalidParameterException e) {
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
        } catch (Exception e) {
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
        }
    }

    private void conectarEnderecoFtp(boolean emergencia) {
        String ip = ConfiguaracaoUtils.ftp.getEnderecoFtp();
        RegistrarLog.imprimirMsg("Log", "conectarEnderecoFtp");
        try {
            RegistrarLog.imprimirMsg("Log", ip + " aqui");
            ftp.connect(ip, 21);
            RegistrarLog.imprimirMsg("Log", " IP " + ip + " está correto");
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP " + ip + " está correto");
            conectarLoginFtp(emergencia);
        } catch (IllegalStateException e) {
            RegistrarLog.imprimirMsg("Log", "1 IP ou endereço esta errado " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (IOException e) {
            RegistrarLog.imprimirMsg("Log", "2 IP ou endereço esta errado " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            RegistrarLog.imprimirMsg("Log", "3 IP ou endereço esta errado " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPException e) {
            RegistrarLog.imprimirMsg("Log", "4 IP ou endereço esta errado " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", "5 IP ou endereço esta errado " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", "6 IP ou endereço esta errado " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", "7 IP ou endereço esta errado " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        }
    }

    private void conectarLoginFtp(boolean emergencia) {
        RegistrarLog.imprimirMsg("Log", "Conectar Login Ftp");
        try {
            ftp.login(ConfiguaracaoUtils.ftp.getUsuario(), ConfiguaracaoUtils.ftp.getSenha());
            RegistrarLog.imprimirMsg("Log", " Usuario: " + ConfiguaracaoUtils.ftp.getUsuario() + " e senha: " + ConfiguaracaoUtils.ftp.getSenha() + " de FTP estão corretos ");
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario: " + ConfiguaracaoUtils.ftp.getUsuario() + " e senha: " + ConfiguaracaoUtils.ftp.getSenha() + " de FTP estão corretos ");
            conectarDiretorioFtp(emergencia);
        } catch (IllegalStateException e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (IOException e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPException e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        }
    }

    private void conectarDiretorioFtp(boolean emergencia) {
        try {
            if(emergencia){
                ftp.changeDirectory(ConfiguaracaoUtils.ftp.getDiretorioRemoto().concat(barraDoSistema).concat("emergencia"));
                download();
            } else {
                ftp.changeDirectory(ConfiguaracaoUtils.ftp.getDiretorioRemoto());
                RegistrarLog.imprimirMsg("Log", " Diretório ftp de download " + ConfiguaracaoUtils.ftp.getDiretorioRemoto());
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Entrou no diretório " + ConfiguaracaoUtils.ftp.getDiretorioRemoto() + " com sucesso");
                zipArquivos();
            }
        } catch (IllegalStateException e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado" + " 1" + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (IOException e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado" + " 2" + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado" + " 3" + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPException e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado" + " 4" + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado " + " 5" + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Diretório informado esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado " + " 6" + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Diretório informado esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado " + " 7" + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Diretório informado esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        }
    }

    public void zipArquivos() {
        RegistrarLog.imprimirMsg("Log", "Zip Arquivos");
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        File arquivoZip = new File(caminhoArquivoZip);
        if (arquivoZip.exists()) {
            arquivoZip.delete();
        }
        ZipFile zip = null;

        try {
            zip = new ZipFile(arquivoZip);
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 1");
            download();
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 2");
            download();
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 3");
            download();
        }

        List<File> arquivos = new ArrayList<File>();

        try {
            File arquivoDeBanco = new File(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("videoOneDs.db"));
            if (arquivoDeBanco.exists()) {
                arquivos.add(arquivoDeBanco);
            } else {
                RegistrarLog.imprimirMsg("Log", " Arquivo do banco não foi encontrado");
            }
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 4");
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 5");
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 6");
        }

        try {
            File arquivoDeConfig = new File(diretorioConfig.concat(barraDoSistema).concat("configuracoes.properties"));
            if (arquivoDeConfig.exists()) {
                arquivos.add(arquivoDeConfig);
            } else {
                RegistrarLog.imprimirMsg("Log", " Arquivo de config não foi encontrado");
            }
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 7");
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 8");
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 9");
        }

        try {
            File arquivosLog = new File(diretorioLog);
            for (File arquivoLog : arquivosLog.listFiles()) {
                if (arquivoLog.exists()) {
                    arquivos.add(arquivoLog);
                }
            }
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 10");
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 11");
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", e.getMessage() + " 12");
        }


        for (File arquivosHaCompactar : arquivos) {
            if (arquivosHaCompactar.exists()) {
                try {
                    zip.addFile(arquivosHaCompactar, parameters);
                } catch (ZipException e) {
                    e.printStackTrace();
                    RegistrarLog.imprimirMsg("Log", "1 Arquivo que não foi compactado " + arquivosHaCompactar.getAbsolutePath() + " " + e.getMessage());
                    continue;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    RegistrarLog.imprimirMsg("Log", "2 Arquivo que não foi compactado " + arquivosHaCompactar.getAbsolutePath() + " " + e.getMessage());
                    continue;
                } catch (InvalidParameterException e) {
                    e.printStackTrace();
                    RegistrarLog.imprimirMsg("Log", "3 Arquivo que não foi compactado " + arquivosHaCompactar.getAbsolutePath() + " " + e.getMessage());
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    RegistrarLog.imprimirMsg("Log", "4 Arquivo que não foi compactado " + arquivosHaCompactar.getAbsolutePath() + " " + e.getMessage());
                    continue;
                }
            }
        }
        RegistrarLog.imprimirMsg("Log", "Compactar");
        uploadZip();
    }

    private void uploadZip() {
        RegistrarLog.imprimirMsg("Log", "Entrou no metodo de upload de zip");
        try {
            ftp.upload(new File(caminhoArquivoZip));
            RegistrarLog.imprimirMsg("Log", " Arquivo zip " + new File(caminhoArquivoZip).getName() + " foi enviado com sucesso");
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Arquivo zip " + new File(caminhoArquivoZip).getName() + " foi enviado com sucesso");
            download();
        } catch (IllegalStateException e) {
            RegistrarLog.imprimirMsg("Log", " O servidor ftp não esta conectado. Não foi possível enviar o zip" + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " O servidor ftp não esta conectado. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (FileNotFoundException e) {
            RegistrarLog.imprimirMsg("Log", " O arquivo zip" + new File(caminhoArquivoZip).getName() + " não foi encontrado " + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " O arquivo zip" + new File(caminhoArquivoZip).getName() + " não foi encontrado " + " " + e.getMessage());
            download();
        } catch (IOException e) {
            RegistrarLog.imprimirMsg("Log", " Erro na entrada ou saida de dados. Não foi possível enviar o zip" + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Erro na entrada ou saida de dados. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (FTPIllegalReplyException e) {
            RegistrarLog.imprimirMsg("Log", " Servidor se comportou de forma inesperada. Não foi possível enviar o zip" + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Servidor se comportou de forma inesperada. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (FTPException e) {
            RegistrarLog.imprimirMsg("Log", " Operação falhou. Não foi possível enviar o zip" + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Operação falhou. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (FTPDataTransferException e) {
            RegistrarLog.imprimirMsg("Log", " Erro na entrada ou saida de dados mas possivelmente a conexão ainda esta ativa. Não foi possível enviar o zip" + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Erro na entrada ou saida de dados mas possivelmente a conexão ainda esta ativa. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (FTPAbortedException e) {
            RegistrarLog.imprimirMsg("Log", " O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", " O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", " O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", " O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        }
    }

    private void download() {
        RegistrarLog.imprimirMsg("Log", "download");
        FTPFile[] files = null;
        try {
            files = ftp.list();
        } catch (IOException e) {
            RegistrarLog.imprimirMsg("Log", "Erro ao pegar os arquivos do ftp 1");
            desconectarFtp();
            return;
        } catch (FTPIllegalReplyException e) {
            RegistrarLog.imprimirMsg("Log", "Erro ao pegar os arquivos do ftp 2");
            desconectarFtp();
            return;
        } catch (FTPException e) {
            RegistrarLog.imprimirMsg("Log", "Erro ao pegar os arquivos do ftp 3");
            desconectarFtp();
            return;
        } catch (FTPDataTransferException e) {
            RegistrarLog.imprimirMsg("Log", "Erro ao pegar os arquivos do ftp 4");
            desconectarFtp();
            return;
        } catch (FTPAbortedException e) {
            RegistrarLog.imprimirMsg("Log", "Erro ao pegar os arquivos do ftp 5");
            desconectarFtp();
            return;
        } catch (FTPListParseException e) {
            RegistrarLog.imprimirMsg("Log", "Erro ao pegar os arquivos do ftp 6");
            desconectarFtp();
            return;
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", "Erro ao pegar os arquivos do ftp 7");
            desconectarFtp();
            return;
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", "Erro ao pegar os arquivos do ftp 8");
            desconectarFtp();
            return;
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", "Erro ao pegar os arquivos do ftp 9");
            desconectarFtp();
            return;
        }

        if (null != files && files.length > 0) {
            for (FTPFile file : files) {
                if (!file.getName().endsWith(".@@@")) {
                    if (file.getName().contains(".mov") || file.getName().contains(".md5") || file.getName().contains(".db") || file.getName().contains(".exp")) {
                        try {
                            ftp.download(file.getName(), new FileOutputStream(new File(salvar_importes.concat(barraDoSistema).concat(file.getName()))), 0, new TransferCustom());
                            RegistrarLog.imprimirMsg("Log", " Arquivos no servidor FTP " + file.getName() + " baixado com sucesso");
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Arquivos no servidor FTP " + file.getName() + " baixado com sucesso");
                        } catch (IllegalStateException e) {
                            RegistrarLog.imprimirMsg("Log", " Não foi possível baixar o arquivo " + file.getName() + ". . O cliente não está conectado ou não autenticado." + " " + e.getMessage());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + ". . O cliente não está conectado ou não autenticado." + " " + e.getMessage());
                            continue;
                        } catch (FileNotFoundException e) {
                            RegistrarLog.imprimirMsg("Log", " Não foi possível baixar o arquivo " + file.getName() + ". O arquivo em questão não pode ser encontrado." + " " + e.getMessage());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + ". O arquivo em questão não pode ser encontrado." + " " + e.getMessage());
                            continue;
                        } catch (IOException e) {
                            RegistrarLog.imprimirMsg("Log", " Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados" + " " + e.getMessage());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados" + " " + e.getMessage());
                            continue;
                        } catch (FTPIllegalReplyException e) {
                            RegistrarLog.imprimirMsg("Log", " Não foi possível baixar o arquivo " + file.getName() + ". O servidor respondeu de forma ilegal" + " " + e.getMessage());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + ". O servidor respondeu de forma ilegal" + " " + e.getMessage());
                            continue;
                        } catch (FTPException e) {
                            RegistrarLog.imprimirMsg("Log", " Não foi possível baixar o arquivo " + file.getName() + ". A operação de download falhou" + " " + e.getMessage());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + ". A operação de download falhou" + " " + e.getMessage());
                            continue;
                        } catch (FTPDataTransferException e) {
                            RegistrarLog.imprimirMsg("Log", " Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados e falhou na tranferencia" + " " + e.getMessage());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados e falhou na tranferencia" + " " + e.getMessage());
                            continue;
                        } catch (FTPAbortedException e) {
                            RegistrarLog.imprimirMsg("Log", " Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        } catch (NullPointerException e) {
                            RegistrarLog.imprimirMsg("Log", " Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        } catch (InvalidParameterException e) {
                            RegistrarLog.imprimirMsg("Log", " Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        } catch (Exception e) {
                            RegistrarLog.imprimirMsg("Log", " Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        }

                        if (TransferCustom.transferido) {
                            try {
                                ftp.rename(file.getName(), file.getName().concat(".@@@"));
                                RegistrarLog.imprimirMsg("Log", file.getName() + " Renomear");
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Arquivo " + file.getName() + " foi renomeado no servidor com sucesso");
                            } catch (IllegalStateException e) {
                                RegistrarLog.imprimirMsg("Log", e.getMessage());
                                e.printStackTrace();
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (IOException e) {
                                RegistrarLog.imprimirMsg("Log", e.getMessage());
                                e.printStackTrace();
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (FTPIllegalReplyException e) {
                                RegistrarLog.imprimirMsg("Log", e.getMessage());
                                e.printStackTrace();
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (FTPException e) {
                                RegistrarLog.imprimirMsg("Log", e.getMessage());
                                e.printStackTrace();
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (NullPointerException e) {
                                RegistrarLog.imprimirMsg("Log", e.getMessage());
                                e.printStackTrace();
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (InvalidParameterException e) {
                                RegistrarLog.imprimirMsg("Log", e.getMessage());
                                e.printStackTrace();
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (Exception e) {
                                RegistrarLog.imprimirMsg("Log", e.getMessage());
                                e.printStackTrace();
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            }
                        }
                    }
                }
            }
        }
        files = null;
        validarMd5();
    }

    private void validarMd5() {
        RegistrarLog.imprimirMsg("Log", "validarMD5");
        md5Utils.controladorArquivosFragmentados(salvar_importes);
        List<File> arquivosValidos = md5Utils.getArquivosValidos();

        if (null != arquivosValidos && !arquivosValidos.isEmpty()) {
            RegistrarLog.imprimirMsg("Log", "Tem arquivos com md5 validos");
            for (File file : arquivosValidos) {
                RegistrarLog.imprimirMsg("Log", "Arquivo md5 valido " + file.getAbsolutePath());
                boolean moveuArquivo = Arquivo.moverArquivo(file, new File(diretorioDeVideos.concat(barraDoSistema).concat(file.getName())));

                if (moveuArquivo) {
                    RegistrarLog.imprimirMsg("Log", "Arquivo movido " + file.getAbsolutePath());

                    File arquivosNoDiretorio = new File(salvar_importes);
                    for (File todosArquivos : arquivosNoDiretorio.listFiles()) {
                        String nomeArquivoMovido = file.getName().substring(0, (file.getName().length() - 4));
                        String nomeArquivosCorrente = todosArquivos.getName().substring(0, (todosArquivos.getName().length() - 4));
                        if (nomeArquivosCorrente.contains(nomeArquivoMovido)) {
                            RegistrarLog.imprimirMsg("Log", "Arquivo deletado " + todosArquivos.getAbsolutePath());
                            todosArquivos.delete();
                        }
                    }
                } else {
                    RegistrarLog.imprimirMsg("Log", "Arquivo não movido " + file.getAbsolutePath());
                }
            }
        }
        md5Utils.getArquivosValidos().clear();
        arquivosValidos.clear();
        validarExp();
    }

    public void validarExp() {
        RegistrarLog.imprimirMsg("Log", "Validar Exp");
        File videoOneExp = null;
        try {
            videoOneExp = new File(salvar_importes.concat(barraDoSistema).concat("videoOne.exp"));
        } catch (NullPointerException e) {
            desconectarFtp();
        } catch (InvalidParameterException e) {
            desconectarFtp();
        } catch (Exception e) {
            desconectarFtp();
        }

        long tamanho = 0;
        try {
            tamanho = videoOneExp.length();
        } catch (NullPointerException e) {
            desconectarFtp();
        } catch (Exception e) {
            desconectarFtp();
        }


        if (videoOneExp.exists() && tamanho != 0) {
            ZipFile zip = null;
            try {
                zip = new ZipFile(videoOneExp);
            } catch (ZipException e) {
                RegistrarLog.imprimirMsg("Log", "1 " + e.getMessage());
                desconectarFtp();
            } catch (NullPointerException e) {
                RegistrarLog.imprimirMsg("Log", "2 " + e.getMessage());
                desconectarFtp();
            } catch (InvalidParameterException e) {
                RegistrarLog.imprimirMsg("Log", "3 " + e.getMessage());
                desconectarFtp();
            } catch (Exception e) {
                RegistrarLog.imprimirMsg("Log", "4 " + e.getMessage());
                desconectarFtp();
            }

            if (null != zip) {
                try {
                    zip.extractAll(salvar_importes);
                } catch (ZipException e) {
                    RegistrarLog.imprimirMsg("Log", "5 " + e.getMessage());
                    desconectarFtp();
                } catch (NullPointerException e) {
                    RegistrarLog.imprimirMsg("Log", "6 " + e.getMessage());
                    desconectarFtp();
                } catch (InvalidParameterException e) {
                    RegistrarLog.imprimirMsg("Log", "7 " + e.getMessage());
                    desconectarFtp();
                } catch (Exception e) {
                    RegistrarLog.imprimirMsg("Log", "8 " + e.getMessage());
                    desconectarFtp();
                }
            } else {
                RegistrarLog.imprimirMsg("Log", "Arquivo de zip está luno");
                desconectarFtp();
            }


            File arquivosNoDiretorioImport = new File(salvar_importes);
            if (null != arquivosNoDiretorioImport) {
                for (File arquivo : arquivosNoDiretorioImport.listFiles()) {

                    if (arquivo.getName().endsWith(".apk")) {
                        boolean moveuArquivo = Arquivo.moverArquivo(arquivo, new File(diretorioVideoOne.concat(barraDoSistema).concat(arquivo.getName())));
                        if (moveuArquivo) {
                            RegistrarLog.imprimirMsg("Log", "Moveu o arquivo " + arquivo.getAbsolutePath() + " com sucesso");
                            arquivo.delete();
                        }
                    }

                    if (arquivo.getName().endsWith(".db")) {
                        boolean moveuArquivo = Arquivo.moverArquivo(arquivo, new File(diretorioVideoOne.concat(barraDoSistema).concat(arquivo.getName())));
                        if (moveuArquivo) {
                            RegistrarLog.imprimirMsg("Log", "Moveu o arquivo " + arquivo.getAbsolutePath() + " com sucesso");
                            arquivo.delete();
                        }
                    }

                    if (arquivo.getName().endsWith(".properties")) {
                        boolean moveuArquivo = Arquivo.moverArquivo(arquivo, new File(diretorioVideoOne.concat(barraDoSistema).concat("config").concat(barraDoSistema).concat(arquivo.getName())));
                        if (moveuArquivo) {
                            RegistrarLog.imprimirMsg("Log", "Moveu o arquivo " + arquivo.getAbsolutePath() + " com sucesso");
                            arquivo.delete();
                        }
                    }
                }
            } else {
                RegistrarLog.imprimirMsg("Log", "Nenhum arquivo foi encontrado no diretório import");
                desconectarFtp();
            }

            videoOneExp.delete();
            desconectarFtp();

        } else {
            RegistrarLog.imprimirMsg("Log", "Nenhum arquivo videoOne.exp foi encontrado");
            desconectarFtp();
        }
    }

    private void desconectarFtp() {
        RegistrarLog.imprimirMsg("Log", "Metodo Desconectar ftp");
        if (ftp.isConnected()) {
            RegistrarLog.imprimirMsg("Log", "Conectado ao servidor");
            try {
                ftp.disconnect(true);
            } catch (IOException e) {
                RegistrarLog.imprimirMsg("Log", " Ftp não esta conectado, não é possível desconectar-se " + e.getMessage());
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Ftp não esta conectado, não é possível desconectar-se " + e.getMessage());
                ftp = new FTPClient();
            } catch (FTPIllegalReplyException e) {
                RegistrarLog.imprimirMsg("Log", " O servidor ftp respondeu de uma forma inesperada " + e.getMessage());
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " O servidor ftp respondeu de uma forma inesperada " + e.getMessage());
                ftp = new FTPClient();
            } catch (FTPException e) {
                RegistrarLog.imprimirMsg("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (NullPointerException e) {
                RegistrarLog.imprimirMsg("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (InvalidParameterException e) {
                RegistrarLog.imprimirMsg("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (Exception e) {
                RegistrarLog.imprimirMsg("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            }
        }

        if (ftp.isConnected()) {
            RegistrarLog.imprimirMsg("Log", "ESTA CONECTADO AINDA");
        } else {
            RegistrarLog.imprimirMsg("Log", "NÃO ESTA CONECTADO AINDA");
        }

        RegistrarLog.imprimirMsg("Log", "Desconectou-se do ftp");
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Desconectou-se do ftp");
    }

    private void popularBanco(){
        String categoria = salvar_importes.concat(barraDoSistema).concat("Categoria.exp");
        String comercial = salvar_importes.concat(barraDoSistema).concat("Comercial.exp");
        String programacao = salvar_importes.concat(barraDoSistema).concat("Programacao.exp");
        String video = salvar_importes.concat(barraDoSistema).concat("Video.exp");

        File arquivoCategoria = new File(categoria);
        File arquivoComercial = new File(comercial);
        File arquivoProgramacao = new File(programacao);
        File arquivoVideo = new File(video);

        if(arquivoCategoria.exists()){
            try {
                bancoDAO.insertCategoria(arquivoCategoria.getAbsolutePath());
                File renomearExpCategoria = new File(salvar_importes.concat(barraDoSistema).concat("Categoria.old"));
                arquivoCategoria.renameTo(renomearExpCategoria);
            } catch (NullPointerException e){
                RegistrarLog.imprimirMsg("Log","1 " + e.getMessage());
            } catch (InvalidParameterException e) {
                RegistrarLog.imprimirMsg("Log","2 " + e.getMessage());
            } catch (Exception e){
                RegistrarLog.imprimirMsg("Log","3 " + e.getMessage());
            }
        }

        if(arquivoComercial.exists()){
            try{
                bancoDAO.insertComercial(arquivoComercial.getAbsolutePath());
                File renomearExpComercial = new File(salvar_importes.concat(barraDoSistema).concat("Comercial.old"));
                arquivoComercial.renameTo(renomearExpComercial);
            } catch (NullPointerException e){
                RegistrarLog.imprimirMsg("Log","1 " + e.getMessage());
            } catch (InvalidParameterException e) {
                RegistrarLog.imprimirMsg("Log","2 " + e.getMessage());
            } catch (Exception e){
                RegistrarLog.imprimirMsg("Log","3 " + e.getMessage());
            }
        }

        if(arquivoProgramacao.exists()){
            try {
                bancoDAO.insertProgramacao(arquivoProgramacao.getAbsolutePath());
                File renomearExpProgramacao = new File(salvar_importes.concat(barraDoSistema).concat("Programacao.old"));
                arquivoProgramacao.renameTo(renomearExpProgramacao);
            } catch (NullPointerException e){
                RegistrarLog.imprimirMsg("Log","1 " + e.getMessage());
            } catch (InvalidParameterException e) {
                RegistrarLog.imprimirMsg("Log","2 " + e.getMessage());
            } catch (Exception e){
                RegistrarLog.imprimirMsg("Log","3 " + e.getMessage());
            }
        }

        if(arquivoVideo.exists()){
            try {
                bancoDAO.insertVideo(arquivoVideo.getAbsolutePath());
                File renomearExpVideo = new File(salvar_importes.concat(barraDoSistema).concat("Video.old"));
                arquivoVideo.renameTo(renomearExpVideo);
            } catch (NullPointerException e){
                RegistrarLog.imprimirMsg("Log","1 " + e.getMessage());
            } catch (InvalidParameterException e) {
                RegistrarLog.imprimirMsg("Log","2 " + e.getMessage());
            } catch (Exception e){
                RegistrarLog.imprimirMsg("Log","3 " + e.getMessage());
            }
        }

        RegistrarLog.imprimirMsg("Log","Banco Populado ");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RegistrarLog.imprimirMsg("Log","Rodar novamente");
        run();
    }
}

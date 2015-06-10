package com.tarefas;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
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

public class TarefaComunicao {

    private final Context context;

    private FTPClient ftp = new FTPClient();

    private Md5Utils md5Utils = new Md5Utils();
    private final String barraDoSistema = System.getProperty("file.separator");
    private final String caminho = Environment.getExternalStorageDirectory().toString();
    private File properties = new File(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config").concat(barraDoSistema).concat("configuracoes.properties"));
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
    private boolean isEmergencia;
    private Activity activity;

    private boolean erro = false;
    private boolean conecta = true;
    public TarefaComunicao(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    //@Override
    public void run(boolean emergencia) {
        isEmergencia = emergencia;
        RegistrarLog.imprimirMsg("Log", "INICIO TaskComunicacao " + isEmergencia + " É EMERGENCIA");
        controlador();
        RegistrarLog.imprimirMsg("Log", "FIM TaskComunicacao " + isEmergencia + " É EMERGENCIA");
    }

    private void controlador() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "controlador()", Toast.LENGTH_SHORT).show();
            }
        });
        if (properties.exists()) {
            informacoesConexao();
            try {
                validarHoraAndDia = new ValidarDiaAndHora(caminho.concat("/videoOne/config/configuracoes.properties"));
                validarHoraAndDia.procurarHorarioValido();
                RegistrarLog.imprimirMsg("Log", ftp.isConnected() + " ESTA CONECTADO");
                RegistrarLog.imprimirMsg("Log", isEmergencia + " É EMERGENCIA");
                if (isEmergencia && !ftp.isConnected() ){
                    LogUtils.registrar(20, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 20 Começou emergencia");
                    RegistrarLog.imprimirMsg("Log","RODANDO EMERGENCIA");
                    conecta = true;
                    conectar();
                } else {
                    if (validarHoraAndDia.isValid()) {
                        if (!hashId.equals(validarHoraAndDia.hashId())) {
                            conecta = true;
                            erro = false;
                            tentativasRealizadas = 0;
                        }

                        RegistrarLog.imprimirMsg("Log", tentativasRealizadas + " Tentativas realizadas");

                        if (tentativasRealizadas <= maximoTentativas) {
                            LogUtils.registrar(20, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 20 Começou comunicação");
                            if(!ftp.isConnected()) {
                                erro = false;
                                conectar();
                                hashId = validarHoraAndDia.hashId();
                            }
                        }
                    }
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (IllegalAccessException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (InstantiationError e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            }
        } else {
            LogUtils.registrar(01, true, " 01 Arquivo de configurações não existe, não é possível se comunicar");
        }
    }

    private void conectar () {
        if(conecta) {
            RegistrarLog.imprimirMsg("Log","Pode se conectar");
            zipArquivos();
            conectarEnderecoFtp();
            if (!erro) {
                conectarLoginFtp();
            }

            if (!erro) {
                conectarLoginFtp();
            }

            if (!erro) {
                conectarDiretorioFtp(isEmergencia);
            }

            if (!erro) {
                if (!isEmergencia) {
                    uploadZip();
                }
            }

            if (!erro) {
                download();
            }

            desconectarFtp();
            validarMd5();
            validarDescompactarVideoOneExp();
            popularBanco();
        } else {
            RegistrarLog.imprimirMsg("Log","Nao pode se conectar");
        }
    }

    public void zipArquivos() {
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo zipArquivos");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo zipArquivos", Toast.LENGTH_SHORT).show();
            }
        });

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
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
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
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        }

        try {
            File arquivoDeConfig = new File(diretorioConfig.concat(barraDoSistema).concat("configuracoes.properties"));
            if (arquivoDeConfig.exists()) {
                arquivos.add(arquivoDeConfig);
            } else {
                RegistrarLog.imprimirMsg("Log", " Arquivo de config não foi encontrado");
            }
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        }

        try {
            File arquivosLog = new File(diretorioLog);
            for (File arquivoLog : arquivosLog.listFiles()) {
                if (arquivoLog.exists()) {
                    arquivos.add(arquivoLog);
                }
            }
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        }


        for (File arquivosHaCompactar : arquivos) {
            if (arquivosHaCompactar.exists()) {
                try {
                    zip.addFile(arquivosHaCompactar, parameters);
                } catch (ZipException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                    continue;
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                    continue;
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                    continue;
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                    continue;
                }
            }
        }
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo zipArquivos");
    }

    private void informacoesConexao() {
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo informacoesConexao");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo informacoesConexao", Toast.LENGTH_SHORT).show();
            }
        });

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
            maximoTentativas = Integer.parseInt(ConfiguaracaoUtils.ftp.getTentativasConexao());
        } catch (NullPointerException e) {
            maximoTentativas = 10;
        } catch (InvalidParameterException e) {
            maximoTentativas = 10;
        } catch (Exception e) {
            maximoTentativas = 10;
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
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo informacoesConexao");
    }

    //------- Conectar --------//
    private void conectarEnderecoFtp() {
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo conectarEnderecoFtp");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo conectarEnderecoFtp", Toast.LENGTH_SHORT).show();
            }
        });

        String ip = ConfiguaracaoUtils.ftp.getEnderecoFtp();
        try {
            ftp.connect(ip, 21);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP " + ip + " está correto");
        } catch (IllegalStateException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            erro = true;
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            erro = true;
        } catch (FTPIllegalReplyException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            erro = true;
        } catch (FTPException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            erro = true;
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            erro = true;
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            erro = true;
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            erro = true;
        }
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo conectarLoginFtp");
    }

    private void conectarLoginFtp() {
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo conectarLoginFtp");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo conectarLoginFtp", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            ftp.login(ConfiguaracaoUtils.ftp.getUsuario(), ConfiguaracaoUtils.ftp.getSenha());
            //ftp.login("a", ConfiguaracaoUtils.ftp.getSenha());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Usuario: " + ConfiguaracaoUtils.ftp.getUsuario() + " e senha: " + ConfiguaracaoUtils.ftp.getSenha() + " de FTP estão corretos ");
        } catch (IllegalStateException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            erro = true;
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            erro = true;
        } catch (FTPIllegalReplyException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            erro = true;
        } catch (FTPException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            erro = true;
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            erro = true;
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            erro = true;
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            erro = true;
        }
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo conectarLoginFtp");
    }

    private void conectarDiretorioFtp(boolean emergencia) {
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo conectarDiretorioFtp");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo conectarDiretorioFtp", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            if (emergencia) {
                ftp.changeDirectory(ConfiguaracaoUtils.ftp.getDiretorioRemoto().concat(barraDoSistema).concat("emergencia"));
                //ftp.changeDirectory(ConfiguaracaoUtils.ftp.getDiretorioRemoto().concat(barraDoSistema).concat("emeragencia"));
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Entrou no diretório " + ConfiguaracaoUtils.ftp.getDiretorioRemoto() + " com sucesso");
            } else {
                ftp.changeDirectory(ConfiguaracaoUtils.ftp.getDiretorioRemoto());
                //ftp.changeDirectory("a");
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Entrou no diretório " + ConfiguaracaoUtils.ftp.getDiretorioRemoto() + " com sucesso");
            }
        } catch (IllegalStateException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado" + " " + e.getMessage());
            erro = true;
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado" + " " + e.getMessage());
            erro = true;
        } catch (FTPIllegalReplyException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado" + " " + e.getMessage());
            erro = true;
        } catch (FTPException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado" + " " + e.getMessage());
            erro = true;
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado " + " " + e.getMessage());
            erro = true;
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado " + " " + e.getMessage());
            erro = true;
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado " + " " + e.getMessage());
            erro = true;
        }
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo conectarDiretorioFtp");
    }

    private void uploadZip() {
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo uploadZip");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo uploadZip", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            ftp.upload(new File(caminhoArquivoZip));
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Arquivo zip " + new File(caminhoArquivoZip).getName() + " foi enviado com sucesso");
        } catch (IllegalStateException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp não esta conectado. Não foi possível enviar o zip" + " " + e.getMessage());
        } catch (FileNotFoundException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O arquivo zip" + new File(caminhoArquivoZip).getName() + " não foi encontrado " + " " + e.getMessage());
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Erro na entrada ou saida de dados. Não foi possível enviar o zip" + " " + e.getMessage());
        } catch (FTPIllegalReplyException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Servidor se comportou de forma inesperada. Não foi possível enviar o zip" + " " + e.getMessage());
            erro = true;
        } catch (FTPException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Operação falhou. Não foi possível enviar o zip" + " " + e.getMessage());
            erro = true;
        } catch (FTPDataTransferException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Erro na entrada ou saida de dados mas possivelmente a conexão ainda esta ativa. Não foi possível enviar o zip" + " " + e.getMessage());
            erro = true;
        } catch (FTPAbortedException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            erro = true;
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
        }
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo uploadZip");
    }

    private void download() {
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo download");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo download", Toast.LENGTH_SHORT).show();
            }
        });

        FTPFile[] files = new FTPFile[0];
        try {
            files = ftp.list();
        } catch (IOException e) {
            RegistrarLog.imprimirMsg("Log","1");
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            erro = true;
        } catch (FTPIllegalReplyException e) {
            RegistrarLog.imprimirMsg("Log","2");
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            erro = true;
        } catch (FTPException e) {
            RegistrarLog.imprimirMsg("Log","3");
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            erro = true;
        } catch (FTPDataTransferException e) {
            RegistrarLog.imprimirMsg("Log","4");
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            erro = true;
        } catch (FTPAbortedException e) {
            RegistrarLog.imprimirMsg("Log","5");
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            erro = true;
        } catch (FTPListParseException e) {
            RegistrarLog.imprimirMsg("Log","6");
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            erro = true;
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log","7");
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            erro = true;
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log","8");
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            erro = true;
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log","9");
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            erro = true;
        }

        if (files.length > 0) {
            for (final FTPFile file : files) {
                if (!file.getName().endsWith(".@@@")) {
                    if (file.getName().contains(".mov") || file.getName().contains(".md5") || file.getName().contains(".db") || file.getName().contains(".exp")) {
                        try {
                            RegistrarLog.imprimirMsg("Log", salvar_importes.concat(barraDoSistema).concat(file.getName()));
                            File download = new File(salvar_importes.concat(barraDoSistema).concat(file.getName()));
                            ftp.download(file.getName(), download, new TransferCustom());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Arquivos no servidor FTP " + file.getName() + " baixado com sucesso");
                        } catch (IllegalStateException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". . O cliente não está conectado ou não autenticado." + " " + e.getMessage());
                            erro = true;
                            continue;
                        } catch (FileNotFoundException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". O arquivo em questão não pode ser encontrado." + " " + e.getMessage());
                            erro = true;
                            continue;
                        } catch (IOException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados" + " " + e.getMessage());
                            erro = true;
                            continue;
                        } catch (FTPIllegalReplyException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". O servidor respondeu de forma ilegal" + " " + e.getMessage());
                            erro = true;
                            continue;
                        } catch (FTPException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". A operação de download falhou" + " " + e.getMessage());
                            erro = true;
                            continue;
                        } catch (FTPDataTransferException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados e falhou na tranferencia" + " " + e.getMessage());
                            erro = true;
                            continue;
                        } catch (FTPAbortedException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            erro = true;
                            continue;
                        } catch (NullPointerException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            erro = true;
                            continue;
                        } catch (InvalidParameterException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            erro = true;
                            continue;
                        } catch (Exception e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            erro = true;
                            continue;
                        }

                        if (TransferCustom.transferido) {
                            try {
                                ftp.rename(file.getName(), file.getName().concat(".@@@"));
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Arquivo " + file.getName() + " foi renomeado no servidor com sucesso");
                            } catch (IllegalStateException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível renomear o arquivo " + file.getName() + " " + e.getMessage());
                                erro = true;
                                continue;
                            } catch (IOException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível renomear o arquivo " + file.getName() + " " + e.getMessage());
                                erro = true;
                                continue;
                            } catch (FTPIllegalReplyException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível renomear o arquivo " + file.getName() + " " + e.getMessage());
                                erro = true;
                                continue;
                            } catch (FTPException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível renomear o arquivo " + file.getName() + " " + e.getMessage());
                                erro = true;
                                continue;
                            } catch (NullPointerException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível renomear o arquivo " + file.getName() + " " + e.getMessage());
                                erro = true;
                                continue;
                            } catch (InvalidParameterException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível renomear o arquivo " + file.getName() + " " + e.getMessage());
                                erro = true;
                                continue;
                            } catch (Exception e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível renomear o arquivo " + file.getName() + " " + e.getMessage());
                                erro = true;
                                continue;
                            }
                        }
                    }
                }
            }
            files = null;
        }
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo download");
    }

    private void desconectarFtp() {
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo desconectarFpt");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo desconectarFtp", Toast.LENGTH_SHORT).show();
            }
        });

        if (ftp.isConnected()) {
            try {
                ftp.disconnect(true);
            } catch (IOException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Ftp não esta conectado, não é possível desconectar-se " + e.getMessage());
            } catch (FTPIllegalReplyException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp respondeu de uma forma inesperada " + e.getMessage());
            } catch (FTPException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp não finalizou a conexão " + e.getMessage());
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp não finalizou a conexão " + e.getMessage());
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp não finalizou a conexão " + e.getMessage());
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp não finalizou a conexão " + e.getMessage());
            } finally {
                RegistrarLog.imprimirMsg("Log", "é emergencia " + isEmergencia);
                RegistrarLog.imprimirMsg("Log", "teve erro " + erro);
                if(erro){
                    if(!isEmergencia) {
                        tentativasRealizadas++;
                    }
                    //conecta = true;
                } else {
                    conecta = false;
                }
                ftp = null;
                ftp = new FTPClient();
            }
        }

        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo desconectarFtp");
        LogUtils.registrar(20, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 20 Fim da comunicação");
        RegistrarLog.imprimirMsg("Log", "Acabou a comunicação");
    }

    //-------- MD5 --------//
    private void validarMd5() {
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo validarMd5");
        //RegistrarLog.imprimirMsg("Log", "Inicio validarM5");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo validarMd5", Toast.LENGTH_SHORT).show();
            }
        });

        md5Utils.controladorArquivosFragmentados(salvar_importes);
        List<File> arquivosValidos = md5Utils.getArquivosValidos();
        if (null != arquivosValidos && !arquivosValidos.isEmpty()) {
            for (File file : arquivosValidos) {
                RegistrarLog.imprimirMsg("Log", file.getAbsolutePath());
                boolean moveuArquivo = Arquivo.moverArquivo(file, new File(diretorioDeVideos.concat(barraDoSistema).concat(file.getName())));
                if (moveuArquivo) {
                    deletarArquivosValidosMovidos(file.getName());
                } else {
                    RegistrarLog.imprimirMsg("Log", "Arquivo não movido " + file.getAbsolutePath());
                    LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Arquivo não movido " + file.getAbsolutePath());
                }
            }
        }
        md5Utils.getArquivosValidos().clear();
        arquivosValidos.clear();
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo validarMd5");
        //RegistrarLog.imprimirMsg("Log", "Fim validarM5");
    }

    private void deletarArquivosValidosMovidos(String nomeArquivo){
        String nomeDoArquivoSemExtencao = nomeArquivo.substring(0,(nomeArquivo.length() - 4));
        String diretorioImport = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioImportacao());
        File arquivos = new File(diretorioImport);

        for(File arquivo : arquivos.listFiles()){
            if(arquivo.getName().startsWith(nomeDoArquivoSemExtencao)){
                RegistrarLog.imprimirMsg("Log", arquivo.getAbsolutePath());
                arquivo.delete();
            }
        }
    }

    //--------- VideoOneExp -----------------//
    public void validarDescompactarVideoOneExp() {
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo validarDescompactarVideoOneExp");
        //RegistrarLog.imprimirMsg("Log", "Inicio validarDescompactarVideoOneExp");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo validarDescompactarVideoOneExp", Toast.LENGTH_SHORT).show();
            }
        });

        File videoOneExp = null;
        try {
            videoOneExp = new File(salvar_importes.concat(barraDoSistema).concat("videoOne.exp"));
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        }

        long tamanho = 0;
        try {
            tamanho = videoOneExp.length();
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        }


        if (videoOneExp.exists() && tamanho != 0) {
            ZipFile zip = null;
            try {
                zip = new ZipFile(videoOneExp);
            } catch (ZipException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            }

            if (null != zip) {
                try {
                    zip.extractAll(salvar_importes);
                    moverOsArquivosDOImport();
                    deletarArquivoVideoOneExp(videoOneExp);
                } catch (ZipException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                }
            } else {
                RegistrarLog.imprimirMsg("Log", "Arquivo de zip está luno");
            }
        } else {
            moverOsArquivosDOImport();
            //RegistrarLog.imprimirMsg("Log", "Nenhum arquivo videoOne.exp foi encontrado");
        }
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo validarDescompactarVideoOneExp");
        //RegistrarLog.imprimirMsg("Log", "Fim validarDescompactarVideoOneExp");
    }

    private void deletarArquivoVideoOneExp(File arquivo){
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo deletarArquivoVideoOneExp");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo deletarArquivoVideoOneExp", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            File apk = new File(diretorioVideoOne.concat(barraDoSistema).concat("videoOne.apk"));
            File db = new File(diretorioVideoOne.concat(barraDoSistema).concat("videoOneDs.db"));
            File properties = new File(diretorioVideoOne.concat(barraDoSistema).concat("configuracoes.properties"));
            if (apk.exists() && db.exists() && properties.exists()) {
                arquivo.delete();
            }
        } catch (NullPointerException e){
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (InvalidParameterException e){
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (Exception e){
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        }
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo deletarArquivoVideoOneExp");
    }

    private void moverOsArquivosDOImport(){
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo moverOsArquivosDOImport");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo moverOsArquivosDOImport", Toast.LENGTH_SHORT).show();
            }
        });

        File arquivosNoDiretorioImport = new File(salvar_importes);
        if (null != arquivosNoDiretorioImport && arquivosNoDiretorioImport.exists()) {
            for (File arquivo : arquivosNoDiretorioImport.listFiles()) {
                try {
                    if (arquivo.getName().endsWith(".apk")) {
                        boolean moveuArquivo = Arquivo.moverArquivo(arquivo, new File(diretorioVideoOne.concat(barraDoSistema).concat(arquivo.getName())));
                        if (moveuArquivo) {
                            arquivo.delete();
                        }
                    }

                    if (arquivo.getName().endsWith(".db")) {
                        boolean moveuArquivo = Arquivo.moverArquivo(arquivo, new File(diretorioVideoOne.concat(barraDoSistema).concat(arquivo.getName())));
                        if (moveuArquivo) {
                            arquivo.delete();
                        }
                    }

                    if (arquivo.getName().endsWith(".properties")) {
                        boolean moveuArquivo = Arquivo.moverArquivo(arquivo, new File(diretorioVideoOne.concat(barraDoSistema).concat("config").concat(barraDoSistema).concat(arquivo.getName())));
                        if (moveuArquivo) {
                            arquivo.delete();
                        }
                    }
                } catch (NullPointerException e){
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                    continue;
                } catch (InvalidParameterException e){
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                    continue;
                } catch (Exception e){
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                    continue;
                }

            }
        } else {
            RegistrarLog.imprimirMsg("Log", "Nenhum arquivo foi encontrado no diretório import");
        }
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo moverOsArquivosDOImport");
    }

    //------- Popular Banco -----------//
    private void popularBanco() {
        LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Inicio do metodo validarDescompactarVideoOneExp");
       // RegistrarLog.imprimirMsg("Log","popularOBanco");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Metodo popularBanco", Toast.LENGTH_SHORT ).show();
            }
        });

        String categoria = salvar_importes.concat(barraDoSistema).concat("Categoria.exp");
        String comercial = salvar_importes.concat(barraDoSistema).concat("Comercial.exp");
        String programacao = salvar_importes.concat(barraDoSistema).concat("Programacao.exp");
        String video = salvar_importes.concat(barraDoSistema).concat("Video.exp");

        File arquivoCategoria = new File(categoria);
        File arquivoComercial = new File(comercial);
        File arquivoProgramacao = new File(programacao);
        File arquivoVideo = new File(video);


        if (arquivoCategoria.exists()) {
            try {
                boolean atualizou = BancoDAO.insertCategoria(arquivoCategoria.getAbsolutePath());
                if(atualizou) {
                    File renomearExpCategoria = new File(salvar_importes.concat(barraDoSistema).concat("Categoria.old"));
                    arquivoCategoria.renameTo(renomearExpCategoria);
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            }
        }

        if (arquivoComercial.exists()) {
            try {
                boolean atualizou = BancoDAO.insertComercial(arquivoComercial.getAbsolutePath());
                if(atualizou) {
                    File renomearExpComercial = new File(salvar_importes.concat(barraDoSistema).concat("Comercial.old"));
                    arquivoComercial.renameTo(renomearExpComercial);
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            }
        }

        if (arquivoProgramacao.exists()) {
            try {
                boolean atualizou = BancoDAO.insertProgramacao(arquivoProgramacao.getAbsolutePath());
                if(atualizou) {
                    File renomearExpProgramacao = new File(salvar_importes.concat(barraDoSistema).concat("Programacao.old"));
                    arquivoProgramacao.renameTo(renomearExpProgramacao);
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            }
        }

        if (arquivoVideo.exists()) {
            try {
                boolean atualizou = BancoDAO.insertVideo(arquivoVideo.getAbsolutePath());
                if(atualizou) {
                    File renomearExpVideo = new File(salvar_importes.concat(barraDoSistema).concat("Video.old"));
                    arquivoVideo.renameTo(renomearExpVideo);
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            }
        }
        //LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Fim do metodo popularBanco");
    }
}

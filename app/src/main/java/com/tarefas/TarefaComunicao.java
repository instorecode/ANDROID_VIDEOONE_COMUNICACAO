package com.tarefas;

import android.content.Context;
import android.os.Environment;
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
    private boolean isEmergencia;

    public TarefaComunicao(Context context, boolean isEmergencia) {
        this.context = context;
        this.isEmergencia = isEmergencia;
        bancoDAO = new BancoDAO(context);
    }

    @Override
    public void run() {
        RegistrarLog.imprimirMsg("Log", "------------------------------------------------------------------------------------------------------------------------------------------------");
        File properties = new File(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config"));
        RegistrarLog.imprimirMsg("Log", "INICIOU A COMUNICAÇÃO ");

        try {
            validarHoraAndDia = new ValidarDiaAndHora(caminho.concat("/videoOne/config/configuracoes.properties"));

            if (properties.exists()) {
                informacoesConexao();
                validarHoraAndDia.procurarHorarioValido();

                RegistrarLog.imprimirMsg("Log", "TENTATIVAS REALIZADAS " + tentativasRealizadas);
                LogUtils.registrar(10,ConfiguaracaoUtils.diretorio.isLogCompleto(), " 10 " + tentativasRealizadas);
                RegistrarLog.imprimirMsg("Log", "MAXIMAS DE TENTATIVAS " + maximoTentativas);

                if (!ftp.isConnected()) {
                    if (validarHoraAndDia.isValid()) {
                        if (!hashId.equals(validarHoraAndDia.hashId())) {
                            //ZERA AS TENTATIVAS DEVIDO OS HORARIOS SEREM
                            tentativasRealizadas = 0;
                        }

                        if (tentativasRealizadas <= maximoTentativas) {
                            RegistrarLog.imprimirMsg("Log", "COMUNICACAO");
                            conectarEnderecoFtp(false);
                            popularBanco();
                            bancoDAO.close();
                            hashId = validarHoraAndDia.hashId();
                        }
                    }//8
                }

                if(isEmergencia && null != ftp && !ftp.isConnected()) {
                    // executar o emergencia
                    RegistrarLog.imprimirMsg("Log", "RODANDO EMERGENCIA");
                    conectarEnderecoFtp(true);
                    popularBanco();
                    bancoDAO.close();
                }

            } else {
                LogUtils.registrar(01, true, " Arquivo de configurações não existe, não é possível se comunicar");
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                } catch (InstantiationError e) {
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

        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
        } catch (InstantiationError e) {
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
        try {
            ftp.connect(ip, 21);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP " + ip + " está correto");
            conectarLoginFtp(emergencia);
        } catch (IllegalStateException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (FTPIllegalReplyException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (FTPException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 IP ou endereço esta errado " + " " + e.getMessage());
            desconectarFtp(true);
        }
    }

    private void conectarLoginFtp(boolean emergencia) {
        try {
            ftp.login(ConfiguaracaoUtils.ftp.getUsuario(), ConfiguaracaoUtils.ftp.getSenha());
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Usuario: " + ConfiguaracaoUtils.ftp.getUsuario() + " e senha: " + ConfiguaracaoUtils.ftp.getSenha() + " de FTP estão corretos ");
            conectarDiretorioFtp(emergencia);
        } catch (IllegalStateException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (FTPIllegalReplyException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (FTPException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            desconectarFtp(true);
        }
    }

    private void conectarDiretorioFtp(boolean emergencia) {
        try {
            if (emergencia) {
                ftp.changeDirectory(ConfiguaracaoUtils.ftp.getDiretorioRemoto().concat(barraDoSistema).concat("emergencia"));
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Entrou no diretório " + ConfiguaracaoUtils.ftp.getDiretorioRemoto() + " com sucesso");
                download();
            } else {
                //ftp.changeDirectory(null);
                ftp.changeDirectory(ConfiguaracaoUtils.ftp.getDiretorioRemoto());
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Entrou no diretório " + ConfiguaracaoUtils.ftp.getDiretorioRemoto() + " com sucesso");
                zipArquivos();
            }
        } catch (IllegalStateException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado" + " " + e.getMessage());
            desconectarFtp(true);
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado" + " " + e.getMessage());
            desconectarFtp(true);
        } catch (FTPIllegalReplyException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado" + " " + e.getMessage());
            desconectarFtp(true);
        } catch (FTPException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado" + " " + e.getMessage());
            desconectarFtp(true);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado " + " " + e.getMessage());
            desconectarFtp(true);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Diretório informado esta errado " + " " + e.getMessage());
            desconectarFtp(true);
        }
    }

    public void zipArquivos() {
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
            download();
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            download();
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
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
        uploadZip();
    }

    private void uploadZip() {
        try {
            ftp.upload(new File(caminhoArquivoZip));
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Arquivo zip " + new File(caminhoArquivoZip).getName() + " foi enviado com sucesso");
            download();
        } catch (IllegalStateException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp não esta conectado. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (FileNotFoundException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O arquivo zip" + new File(caminhoArquivoZip).getName() + " não foi encontrado " + " " + e.getMessage());
            download();
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Erro na entrada ou saida de dados. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (FTPIllegalReplyException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Servidor se comportou de forma inesperada. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (FTPException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Operação falhou. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (FTPDataTransferException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Erro na entrada ou saida de dados mas possivelmente a conexão ainda esta ativa. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (FTPAbortedException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            download();
        }
    }

    private void download() {
        FTPFile[] files = null;
        try {
            files = ftp.list();
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
            return;
        } catch (FTPIllegalReplyException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
            return;
        } catch (FTPException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
            return;
        } catch (FTPDataTransferException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
            return;
        } catch (FTPAbortedException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
            return;
        } catch (FTPListParseException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
            return;
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
            return;
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
            return;
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
            return;
        }

        if (null != files && files.length > 0) {
            for (FTPFile file : files) {
                if (!file.getName().endsWith(".@@@")) {
                    if (file.getName().contains(".mov") || file.getName().contains(".md5") || file.getName().contains(".db") || file.getName().contains(".exp")) {
                        try {
                            RegistrarLog.imprimirMsg("Log", " Arquivos no servidor FTP " + file.getName());
                            RegistrarLog.imprimirMsg("Log", new File(salvar_importes.concat(barraDoSistema).concat(file.getName())).getAbsolutePath());
                            ftp.download(file.getName(), new FileOutputStream(new File(salvar_importes.concat(barraDoSistema).concat(file.getName()))), 0, new TransferCustom());
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Arquivos no servidor FTP " + file.getName() + " baixado com sucesso");
                        } catch (IllegalStateException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". . O cliente não está conectado ou não autenticado." + " " + e.getMessage());
                            continue;
                        } catch (FileNotFoundException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". O arquivo em questão não pode ser encontrado." + " " + e.getMessage());
                            continue;
                        } catch (IOException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados" + " " + e.getMessage());
                            continue;
                        } catch (FTPIllegalReplyException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". O servidor respondeu de forma ilegal" + " " + e.getMessage());
                            continue;
                        } catch (FTPException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". A operação de download falhou" + " " + e.getMessage());
                            continue;
                        } catch (FTPDataTransferException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados e falhou na tranferencia" + " " + e.getMessage());
                            continue;
                        } catch (FTPAbortedException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        } catch (NullPointerException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        } catch (InvalidParameterException e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        } catch (Exception e) {
                            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        }

                        if (TransferCustom.transferido) {
                            try {
                                ftp.rename(file.getName(), file.getName().concat(".@@@"));
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Arquivo " + file.getName() + " foi renomeado no servidor com sucesso");
                            } catch (IllegalStateException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (IOException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (FTPIllegalReplyException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (FTPException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (NullPointerException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (InvalidParameterException e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                                continue;
                            } catch (Exception e) {
                                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
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
        md5Utils.controladorArquivosFragmentados(salvar_importes);
        List<File> arquivosValidos = md5Utils.getArquivosValidos();
        if (null != arquivosValidos && !arquivosValidos.isEmpty()) {
            for (File file : arquivosValidos) {
                RegistrarLog.imprimirMsg("Log", file.getAbsolutePath());
                boolean moveuArquivo = Arquivo.moverArquivo(file, new File(diretorioDeVideos.concat(barraDoSistema).concat(file.getName())));

                if (moveuArquivo) {
                    File arquivosNoDiretorio = new File(salvar_importes);
                    for (File todosArquivos : arquivosNoDiretorio.listFiles()) {
                        String nomeArquivoMovido = file.getName().substring(0, (file.getName().length() - 4));
                        String nomeArquivosCorrente = todosArquivos.getName().substring(0, (todosArquivos.getName().length() - 4));
                        if (nomeArquivosCorrente.contains(nomeArquivoMovido)) {
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
        validarDescompactarVideoOneExp();
    }

    public void validarDescompactarVideoOneExp() {
        File videoOneExp = null;
        try {
            videoOneExp = new File(salvar_importes.concat(barraDoSistema).concat("videoOne.exp"));
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
        }

        long tamanho = 0;
        try {
            tamanho = videoOneExp.length();
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
            AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
            desconectarFtp(false);
        }


        if (videoOneExp.exists() && tamanho != 0) {
            ZipFile zip = null;
            try {
                zip = new ZipFile(videoOneExp);
            } catch (ZipException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                desconectarFtp(false);
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                desconectarFtp(false);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                desconectarFtp(false);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                desconectarFtp(false);
            }

            if (null != zip) {
                try {
                    zip.extractAll(salvar_importes);
                } catch (ZipException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                    desconectarFtp(false);
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                    desconectarFtp(false);
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                    desconectarFtp(false);
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                    AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e, 90);
                    desconectarFtp(false);
                }
                moverOsArquivosDOImport();
                deletarArquivoVideoOneExp(videoOneExp);
                desconectarFtp(true);
            } else {
                RegistrarLog.imprimirMsg("Log", "Arquivo de zip está luno");
                desconectarFtp(false);
            }
        } else {
            moverOsArquivosDOImport();
            RegistrarLog.imprimirMsg("Log", "Nenhum arquivo videoOne.exp foi encontrado");
            desconectarFtp(true);
        }
    }

    private void deletarArquivoVideoOneExp(File arquivo){
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
    }

    private void moverOsArquivosDOImport(){
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
    }

    private void desconectarFtp(boolean incrementarTentativasRealizadas) {
        RegistrarLog.imprimirMsg("Log", "Metodo Desconectar ftp");
        if (ftp.isConnected()) {
            try {
                ftp.disconnect(true);
            } catch (IOException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Ftp não esta conectado, não é possível desconectar-se " + e.getMessage());
                ftp = new FTPClient();
            } catch (FTPIllegalReplyException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp respondeu de uma forma inesperada " + e.getMessage());
                ftp = new FTPClient();
            } catch (FTPException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(TarefaComunicao.class, e);
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } finally {
                if(incrementarTentativasRealizadas && !isEmergencia){
                    tentativasRealizadas++;
                }
            }
        }

        RegistrarLog.imprimirMsg("Log", "Desconectou-se do ftp");
        LogUtils.registrar(20, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 20 Desconectou-se do ftp");
    }

    private void popularBanco() {
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
                boolean atualizou = bancoDAO.insertCategoria(arquivoCategoria.getAbsolutePath());
                if(atualizou) {
                    File renomearExpCategoria = new File(salvar_importes.concat(barraDoSistema).concat("Categoria.old"));
                    //arquivoCategoria.renameTo(renomearExpCategoria);
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
                boolean atualizou = bancoDAO.insertComercial(arquivoComercial.getAbsolutePath());
                if(atualizou) {
                    File renomearExpComercial = new File(salvar_importes.concat(barraDoSistema).concat("Comercial.old"));
                    //arquivoComercial.renameTo(renomearExpComercial);
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
                boolean atualizou = bancoDAO.insertProgramacao(arquivoProgramacao.getAbsolutePath());
                if(atualizou) {
                    File renomearExpProgramacao = new File(salvar_importes.concat(barraDoSistema).concat("Programacao.old"));
                    //arquivoProgramacao.renameTo(renomearExpProgramacao);
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
                boolean atualizou = bancoDAO.insertVideo(arquivoVideo.getAbsolutePath());
                if(atualizou) {
                    File renomearExpVideo = new File(salvar_importes.concat(barraDoSistema).concat("Video.old"));
                    //arquivoVideo.renameTo(renomearExpVideo);
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
        RegistrarLog.imprimirMsg("Log", "Banco Populado ");
    }
}

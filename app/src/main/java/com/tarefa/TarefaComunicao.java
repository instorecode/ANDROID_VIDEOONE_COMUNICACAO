package com.tarefa;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.br.instore.utils.Arquivo;
import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.ImprimirUtils;
import com.br.instore.utils.Md5Utils;
import com.comunicacao.TransferCustom;
import com.syso.Imprimir;
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

    private Context context;
    private FTPClient ftp = new FTPClient();
    private RegistrarLog registrarLog;
    private final String barraDoSistema = System.getProperty("file.separator");
    private final String caminho = Environment.getExternalStorageDirectory().toString();
    private String salvar_importes = "";
    private String salvar_export = "";
    private String arquivoZip = "";
    private String diretorioConfig = "";
    private String diretorioDeVideos = "";
    private String diretorioLog = "";
    private String diretorioVideoOne = "";
    private int maximoTentativas = 10;
    private int tentativasRealizadas = 0;
    private List<File> listaArquivosFtp = new ArrayList<File>();
    private Md5Utils md5Utils = new Md5Utils();
    private Arquivo arquivoUtils = new Arquivo();
    public Handler mHandler;
    public TarefaComunicao(Context context) {
        Toast.makeText(context, "CONSTRUTOR", Toast.LENGTH_LONG).show();
        this.context = context;
        registrarLog = new RegistrarLog(context);
}

    @Override
    public void run() {
        informacoesConexao();
        conectarEnderecoFtp();
    }

    private void informacoesConexao() {
        RegistrarLog.imprimirMsg("Log", "INICIO");
        try {
            salvar_importes = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioImportacao());
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_importes + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_importes + " não foi criado");
            }
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", "1 Erro ao pegar as informações de configurações");
            salvar_importes = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("import");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_importes + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_importes + " não foi criado");
            }
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", "2 Erro ao pegar as informações de configurações");
            salvar_importes = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("import");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_importes + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_importes + " não foi criado");
            }
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", "3 Erro ao pegar as informações de configurações");
            salvar_importes = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("import");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_importes + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_importes + " não foi criado");
            }
        }

        try {
            salvar_export = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioExportacao());
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_export);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_export + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_export + " não foi criado");
            }
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", "4 Erro ao pegar as informações de configurações");
            salvar_export = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("export");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_export);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_export + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_export + " não foi criado");
            }
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", "5 Erro ao pegar as informações de configurações");
            salvar_export = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("export");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_export);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_export + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_export + " não foi criado");
            }
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", "6 Erro ao pegar as informações de configurações");
            salvar_export = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("export");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_export);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_export + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + salvar_export + " não foi criado");
            }
        }

        try {
            arquivoZip = salvar_export.concat(barraDoSistema).concat("videoOne.zip");
         } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", "7 Erro ao pegar as informações de configurações");
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", "8 Erro ao pegar as informações de configurações");
                   } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", "9 Erro ao pegar as informações de configurações");

        }

        try {
            diretorioConfig = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config");
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", "10 Erro ao pegar as informações de configurações");
            diretorioConfig = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioConfig);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioConfig + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioConfig + " não foi criado");
            }
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", "11 Erro ao pegar as informações de configurações");
            diretorioConfig = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioConfig);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioConfig + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioConfig + " não foi criado");
            }
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", "12 Erro ao pegar as informações de configurações");
            diretorioConfig = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioConfig);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioConfig + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioConfig + " não foi criado");
            }
        }

        try {
            diretorioLog = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioLog());
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioLog);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioLog + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioLog + " não foi criado");
            }
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", "13 Erro ao pegar as informações de configurações");
            diretorioLog = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("log");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioLog);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioLog + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioLog + " não foi criado");
            }
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", "14 Erro ao pegar as informações de configurações");
            diretorioLog = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("log");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioLog);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioLog + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioLog + " não foi criado");
            }
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", "15 Erro ao pegar as informações de configurações");
            diretorioLog = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("log");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioLog);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioLog + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioLog + " não foi criado");
            }
        }

        try {
            diretorioDeVideos = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo());
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioDeVideos);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioDeVideos + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioDeVideos + " não foi criado");
            }
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", "16 Erro ao pegar as informações de configurações");
            diretorioDeVideos = caminho.concat(barraDoSistema).concat("videos");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioDeVideos);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioDeVideos + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioDeVideos + " não foi criado");
            }
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", "17 Erro ao pegar as informações de configurações");
            diretorioDeVideos = caminho.concat(barraDoSistema).concat("videos");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioDeVideos);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioDeVideos + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioDeVideos + " não foi criado");
            }
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", "18 Erro ao pegar as informações de configurações");
            diretorioDeVideos = caminho.concat(barraDoSistema).concat("videos");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioDeVideos);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioDeVideos + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioDeVideos + " não foi criado");
            }
        }

        try {
            diretorioVideoOne = caminho.concat(barraDoSistema).concat("videoOne");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioVideoOne);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioVideoOne + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioVideoOne + " não foi criado");
            }
        } catch (NullPointerException e) {
            RegistrarLog.imprimirMsg("Log", "19 Erro ao pegar as informações de configurações");
            boolean diretorioCriado = Arquivo.criarDiretorio(diretorioVideoOne);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioVideoOne + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioVideoOne + " não foi criado");
            }
        } catch (InvalidParameterException e) {
            RegistrarLog.imprimirMsg("Log", "20 Erro ao pegar as informações de configurações");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioVideoOne + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioVideoOne + " não foi criado");
            }
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", "21 Erro ao pegar as informações de configurações");
            boolean diretorioCriado = Arquivo.criarDiretorio(salvar_importes);
            if (diretorioCriado) {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioVideoOne + " criado");
            } else {
                RegistrarLog.imprimirMsg("Log", "Diretório " + diretorioVideoOne + " não foi criado");
            }
        }
        RegistrarLog.imprimirMsg("Log", "FIM");
    }

    private void conectarEnderecoFtp() {
        String ip = ConfiguaracaoUtils.ftp.getEnderecoFtp();
        RegistrarLog.imprimirMsg("Log", "conectarEnderecoFtp");
        try {
            RegistrarLog.imprimirMsg("Log", ip + " aqui");
            ftp.connect(ip);
            RegistrarLog.imprimirMsg("Log", " IP " + ip + " está correto");
            registrarLog.escrever(" IP " + ip + " está correto");
            conectarLoginFtp();
        } catch (IllegalStateException e) {
            RegistrarLog.imprimirMsg("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (IOException e) {
            RegistrarLog.imprimirMsg("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            RegistrarLog.imprimirMsg("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPException e) {
            RegistrarLog.imprimirMsg("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (NullPointerException e){
            RegistrarLog.imprimirMsg("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (InvalidParameterException e){
            RegistrarLog.imprimirMsg("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        }
    }

    private void conectarLoginFtp() {
        try {
            ftp.login(ConfiguaracaoUtils.ftp.getUsuario(), ConfiguaracaoUtils.ftp.getSenha());
            RegistrarLog.imprimirMsg("Log", " Usuario: " + ConfiguaracaoUtils.ftp.getUsuario() + " e senha: " + ConfiguaracaoUtils.ftp.getSenha() + " de FTP estão corretos ");
            registrarLog.escrever(" Usuario: " + ConfiguaracaoUtils.ftp.getUsuario() + " e senha: " + ConfiguaracaoUtils.ftp.getSenha() + " de FTP estão corretos ");
            conectarDiretorioFtp();
        } catch (IllegalStateException e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (IOException e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPException e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (NullPointerException e){
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (InvalidParameterException e){
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        }
    }

    private void conectarDiretorioFtp() {
        try {
            ftp.changeDirectory(ConfiguaracaoUtils.ftp.getDiretorioRemoto());
            RegistrarLog.imprimirMsg("Log", " Diretório ftp de download " + ConfiguaracaoUtils.ftp.getDiretorioRemoto());
            registrarLog.escrever(" Entrou no diretório " + ConfiguaracaoUtils.ftp.getDiretorioRemoto() + " com sucesso");
        } catch (IllegalStateException e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado" + " 1" + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (IOException e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado" + " 2" + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado" + " 3" + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPException e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado" + " 4" + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (NullPointerException e){
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado " + " 5" + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (InvalidParameterException e){
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado " + " 6" + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (Exception e) {
            RegistrarLog.imprimirMsg("Log", " Diretório informado esta errado " + " 7" + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        }
        download();
    }

    private void download() {
        RegistrarLog.imprimirMsg("Log", "download");
        FTPFile[] files = null;
        try {
            files = ftp.list();
        } catch (IOException e) {
            RegistrarLog.imprimirMsg("Log","Erro ao pegar os arquivos do ftp 1");
            desconectarFtp();
            return;
        } catch (FTPIllegalReplyException e) {
            RegistrarLog.imprimirMsg("Log","Erro ao pegar os arquivos do ftp 2");
            desconectarFtp();
            return;
        } catch (FTPException e) {
            RegistrarLog.imprimirMsg("Log","Erro ao pegar os arquivos do ftp 3");
            desconectarFtp();
            return;
        } catch (FTPDataTransferException e) {
            RegistrarLog.imprimirMsg("Log","Erro ao pegar os arquivos do ftp 4");
            desconectarFtp();
            return;
        } catch (FTPAbortedException e) {
            RegistrarLog.imprimirMsg("Log","Erro ao pegar os arquivos do ftp 5");
            desconectarFtp();
            return;
        } catch (FTPListParseException e) {
            RegistrarLog.imprimirMsg("Log","Erro ao pegar os arquivos do ftp 6");
            desconectarFtp();
            return;
        } catch (NullPointerException e){
            RegistrarLog.imprimirMsg("Log","Erro ao pegar os arquivos do ftp 7");
            desconectarFtp();
            return;
        } catch (InvalidParameterException e){
            RegistrarLog.imprimirMsg("Log","Erro ao pegar os arquivos do ftp 8");
            desconectarFtp();
            return;
        } catch (Exception e ){
            RegistrarLog.imprimirMsg("Log","Erro ao pegar os arquivos do ftp 9");
            desconectarFtp();
            return;
        }

        if(null != files && files.length > 0) {
            for (FTPFile file : files) {
                if (!file.getName().endsWith(".@@@")) {
                    if (file.getName().contains(".mov") || file.getName().contains(".md5") || file.getName().contains(".db") || file.getName().contains(".exp")) {
                        try {
                            RegistrarLog.imprimirMsg("Log", file.getName());
                            ftp.download(file.getName(), new FileOutputStream(new File(salvar_importes.concat(barraDoSistema).concat(file.getName()))), 0, new TransferCustom());
                            registrarLog.escrever(" Arquivos no servidor FTP " + file.getName() + " baixado com sucesso");
                        } catch (IllegalStateException e) {
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + ". . O cliente não está conectado ou não autenticado." + " " + e.getMessage());
                            continue;
                        } catch (FileNotFoundException e) {
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + ". O arquivo em questão não pode ser encontrado." + " " + e.getMessage());
                            continue;
                        } catch (IOException e) {
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados" + " " + e.getMessage());
                            continue;
                        } catch (FTPIllegalReplyException e) {
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + ". O servidor respondeu de forma ilegal" + " " + e.getMessage());
                            continue;
                        } catch (FTPException e) {
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + ". A operação de download falhou" + " " + e.getMessage());
                            continue;
                        } catch (FTPDataTransferException e) {
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados e falhou na tranferencia" + " " + e.getMessage());
                            continue;
                        } catch (FTPAbortedException e) {
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        } catch (NullPointerException e) {
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        } catch (InvalidParameterException e) {
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        } catch (Exception e) {
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                            continue;
                        }

                        try {
                            ftp.rename(file.getName(), file.getName().concat(".@@@"));
                            RegistrarLog.imprimirMsg("Log", file.getName() + " Renomear");
                            registrarLog.escrever(" Arquivo " + file.getName() + " foi renomeado no servidor com sucesso");
                        } catch (IllegalStateException e) {
                            RegistrarLog.imprimirMsg("Log", e.getMessage());
                            e.printStackTrace();
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                            continue;
                        } catch (IOException e) {
                            RegistrarLog.imprimirMsg("Log", e.getMessage());
                            e.printStackTrace();
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                            continue;
                        } catch (FTPIllegalReplyException e) {
                            RegistrarLog.imprimirMsg("Log", e.getMessage());
                            e.printStackTrace();
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                            continue;
                        } catch (FTPException e) {
                            RegistrarLog.imprimirMsg("Log", e.getMessage());
                            e.printStackTrace();
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                            continue;
                        } catch (NullPointerException e) {
                            RegistrarLog.imprimirMsg("Log", e.getMessage());
                            e.printStackTrace();
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                            continue;
                        } catch (InvalidParameterException e) {
                            RegistrarLog.imprimirMsg("Log", e.getMessage());
                            e.printStackTrace();
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                            continue;
                        } catch (Exception e) {
                            RegistrarLog.imprimirMsg("Log", e.getMessage());
                            e.printStackTrace();
                            registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                            continue;
                        }
                    }
                }
            }
        }
        files = null;
        validarMd5();
    }

    private void validarMd5(){
        RegistrarLog.imprimirMsg("Log","validarMD5");
        md5Utils.controladorArquivosFragmentados(salvar_importes);
        List<File> arquivosValidos = md5Utils.getArquivosValidos();

        if(null != arquivosValidos && !arquivosValidos.isEmpty()){
            for(File file : arquivosValidos) {
                RegistrarLog.imprimirMsg("Log", "Arquivo md5 valido " + file.getAbsolutePath());
                boolean moveuArquivo = Arquivo.moverArquivo(file, new File(diretorioDeVideos.concat(barraDoSistema).concat(file.getName())));

                if(moveuArquivo){
                    RegistrarLog.imprimirMsg("Log","Arquivo movido " + file.getAbsolutePath());

                    File arquivosNoDiretorio = new File(salvar_importes);
                    for(File todosArquivos : arquivosNoDiretorio.listFiles()){
                        String nomeArquivoMovido = file.getName().substring(0, (file.getName().length() - 4));
                        String nomeArquivosCorrente = todosArquivos.getName().substring(0, (todosArquivos.getName().length() - 4));
                        if(nomeArquivosCorrente.contains(nomeArquivoMovido)){
                            RegistrarLog.imprimirMsg("Log" , "Arquivo deletado " + todosArquivos.getAbsolutePath());
                            todosArquivos.delete();
                        }
                    }
                } else {
                    RegistrarLog.imprimirMsg("Log","Arquivo não movido " + file.getAbsolutePath());
                }
            }
        }

       validarExp();
    }

    private void validarExp() {
        RegistrarLog.imprimirMsg("Log","Metodo validar  exp");
        File fileExp = new File(salvar_importes + "/videoOne.exp");
        long tamanho = fileExp.length();

        //--- Verifica se existe o arquivo videoOne.exp
        if (fileExp.exists() && tamanho != 0) {
            ZipFile zip = null;
            try {
                zip = new ZipFile(fileExp);
            } catch (ZipException e) {
                registrarLog.escrever(" Erro ao pegar o arquivo videoOne.exp " + e.getMessage());
                desconectarFtp();
            } catch (NullPointerException e){
                registrarLog.escrever(" Erro ao pegar o arquivo videoOne.exp " + e.getMessage());
                desconectarFtp();
            } catch (Exception e){
                registrarLog.escrever(" Erro ao pegar o arquivo videoOne.exp " + e.getMessage());
                desconectarFtp();
            }

            if (null != zip) {
                try {
                    zip.extractAll(salvar_importes);
                } catch (ZipException e) {
                    registrarLog.escrever(" Não foi possível descompactar o arquivo " + zip.getFile().getName());
                    desconectarFtp();
                } catch (NullPointerException e){
                    registrarLog.escrever(" Não foi possível descompactar o arquivo " + zip.getFile().getName());
                    desconectarFtp();
                } catch (Exception e){
                    registrarLog.escrever(" Não foi possível descompactar o arquivo " + zip.getFile().getName());
                    desconectarFtp();
                }
            } else {
                registrarLog.escrever(" O arquivo zip " + zip.getFile().getName() + " está nulo");
                desconectarFtp();
            }

            //--- Percore o diretório de import e registra os arquivos que na estiverem ---//
            File[] arquivosNoDiretorioImport = new File(salvar_importes).listFiles();
            if (null != arquivosNoDiretorioImport || arquivosNoDiretorioImport.length != 0) {
                for (File arquivos : arquivosNoDiretorioImport) {

                    if (arquivos.getName().endsWith(".db")) {
                        boolean moverArquivo = Arquivo.moverArquivo(arquivos, new File(diretorioVideoOne.concat(barraDoSistema).concat(arquivos.getName())));
                        if(moverArquivo){
                            arquivos.delete();
                        }
                    }

                    if (arquivos.getName().endsWith(".apk")) {
                        boolean moverArquivo = Arquivo.moverArquivo(arquivos, new File(diretorioVideoOne.concat(barraDoSistema).concat(arquivos.getName())));
                        if(moverArquivo){
                            arquivos.delete();
                        }
                    }

                    if (arquivos.getName().endsWith(".properties")) {

                        boolean moverArquivo = Arquivo.moverArquivo(arquivos, new File(diretorioConfig.concat(barraDoSistema).concat(arquivos.getName())));
                        if(moverArquivo){
                            arquivos.delete();
                        }
                    }
                }
            } else {
                desconectarFtp();
                return;
            }
            fileExp.delete();
            desconectarFtp();


        } else {
            registrarLog.escrever(" Arquivos videoOne.exp não foi encontrado ");
            RegistrarLog.imprimirMsg("Log","Arquivos videoOne.exp não foi encontrado");
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
                registrarLog.escrever(" Ftp não esta conectado, não é possível desconectar-se " + e.getMessage());
                ftp = new FTPClient();
            } catch (FTPIllegalReplyException e) {
                RegistrarLog.imprimirMsg("Log", " O servidor ftp respondeu de uma forma inesperada " + e.getMessage());
                registrarLog.escrever(" O servidor ftp respondeu de uma forma inesperada " + e.getMessage());
                ftp = new FTPClient();
            } catch (FTPException e) {
                RegistrarLog.imprimirMsg("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                registrarLog.escrever(" O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (NullPointerException e) {
                RegistrarLog.imprimirMsg("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                registrarLog.escrever(" O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (InvalidParameterException e) {
                RegistrarLog.imprimirMsg("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                registrarLog.escrever(" O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (Exception e) {
                RegistrarLog.imprimirMsg("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                registrarLog.escrever(" O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            }
        }
        RegistrarLog.imprimirMsg("Log", "Desconectou-se do ftp");
        registrarLog.escrever(" Desconectou-se do ftp");
    }

    private void zipArquivos() {
        RegistrarLog.imprimirMsg("Log", "Entrou no metodo zip");
        registrarLog.escrever(" Criando zip");
        File fileZip = new File(arquivoZip);

        ZipFile zip = null;
        try {
            zip = new ZipFile(fileZip);
        } catch (ZipException e) {
            ImprimirUtils.imprimirErro(TarefaComunicao.this, e);
        }

        List<File> arquivos = new ArrayList<File>();

        File banco = new File(diretorioVideoOne.concat(barraDoSistema).concat("videoOneDs.db"));
        if (banco.exists()) {
            //arquivos.add(banco);
        }

        File properties = new File(diretorioConfig.concat(barraDoSistema).concat("configuracoes.properties"));
        if (properties.exists()) {
            //arquivos.add(properties);
        }

        File file = new File(diretorioLog);
        for (File arquivoLog : file.listFiles()) {
            arquivos.add(arquivoLog);
        }

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        for (File arquivosACompactar : arquivos) {
            if (arquivosACompactar.exists()) {
                try {
                    RegistrarLog.imprimirMsg("Log", arquivosACompactar.getAbsolutePath());
                    zip.addFile(arquivosACompactar, parameters);
                } catch (ZipException e) {
                    registrarLog.escrever(" " + e.getMessage() + " Não pode compactar o arquivo " + arquivosACompactar.getName() + " " + e.getMessage());
                    continue;
                }
            }
        }
        RegistrarLog.imprimirMsg("Log", zip.getFile().getAbsolutePath());
        desconectarFtp();
        //uploadZip();
    }

    private void uploadZip() {
        RegistrarLog.imprimirMsg("Log", "Entrou no metodo de upload de zip");
        try {
            ftp.upload(new File(arquivoZip));
            RegistrarLog.imprimirMsg("Log", " Arquivo zip " + new File(arquivoZip).getName() + " foi enviado com sucesso");
            registrarLog.escrever(" Arquivo zip " + new File(arquivoZip).getName() + " foi enviado com sucesso");
            //arquivosNoFtp();
            desconectarFtp();
        } catch (IllegalStateException e) {
            RegistrarLog.imprimirMsg("Log", " O servidor ftp não esta conectado. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" O servidor ftp não esta conectado. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        } catch (FileNotFoundException e) {
            RegistrarLog.imprimirMsg("Log", " O arquivo zip" + new File(arquivoZip).getName() + " não foi encontrado " + " " + e.getMessage());
            registrarLog.escrever(" O arquivo zip" + new File(arquivoZip).getName() + " não foi encontrado " + " " + e.getMessage());
            desconectarFtp();
        } catch (IOException e) {
            RegistrarLog.imprimirMsg("Log", " Erro na entrada ou saida de dados. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" Erro na entrada ou saida de dados. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            RegistrarLog.imprimirMsg("Log", " Servidor se comportou de forma inesperada. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" Servidor se comportou de forma inesperada. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPException e) {
            RegistrarLog.imprimirMsg("Log", " Operação falhou. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" Operação falhou. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPDataTransferException e) {
            RegistrarLog.imprimirMsg("Log", " Erro na entrada ou saida de dados mas possivelmente a conexão ainda esta ativa. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" Erro na entrada ou saida de dados mas possivelmente a conexão ainda esta ativa. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPAbortedException e) {
            RegistrarLog.imprimirMsg("Log", " O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        }
    }
}

package com.tarefa;

import android.content.Context;
import android.os.Environment;
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
    private FTPFile[] listaArquivosFtp;
    private Md5Utils md5Utils = new Md5Utils();
    private Arquivo arquivoUtils = new Arquivo();

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
        salvar_importes = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioImportacao());
        salvar_export = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioExportacao());
        arquivoZip = salvar_export.concat(barraDoSistema).concat("videoOne.zip");
        diretorioConfig = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config");
        diretorioLog = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioLog());
        diretorioDeVideos = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo());
        diretorioVideoOne = caminho.concat(barraDoSistema).concat("videoOne");
        Log.e("Log", "informacoesConexao");
    }

    private void conectarEnderecoFtp() {
        String ip = ConfiguaracaoUtils.ftp.getEnderecoFtp();
        Log.e("Log", "conectarEnderecoFtp");
        try {
            Log.e("Log", ip + " aqui");
            ftp.connect(ip);
            Log.e("Log", " IP " + ip + " está correto");
            registrarLog.escrever(" IP " + ip + " está correto");
            conectarLoginFtp();
        } catch (IllegalStateException e) {
            Log.e("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (IOException e) {
            Log.e("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            Log.e("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPException e) {
            Log.e("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (NullPointerException e){
            Log.e("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (InvalidParameterException e){
            Log.e("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (Exception e) {
            Log.e("Log", " IP ou endereço esta errado " + " " + e.getMessage());
            registrarLog.escrever(" IP ou endereço esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        }
    }

    private void conectarLoginFtp() {
        try {
            ftp.login(ConfiguaracaoUtils.ftp.getUsuario(), ConfiguaracaoUtils.ftp.getSenha());
            Log.e("Log", " Usuario: " + ConfiguaracaoUtils.ftp.getUsuario() + " e senha: " + ConfiguaracaoUtils.ftp.getSenha() + " de FTP estão corretos ");
            registrarLog.escrever(" Usuario: " + ConfiguaracaoUtils.ftp.getUsuario() + " e senha: " + ConfiguaracaoUtils.ftp.getSenha() + " de FTP estão corretos ");
            conectarDiretorioFtp();
        } catch (IllegalStateException e) {
            Log.e("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (IOException e) {
            Log.e("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            Log.e("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPException e) {
            Log.e("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (NullPointerException e){
            Log.e("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (InvalidParameterException e){
            Log.e("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (Exception e) {
            Log.e("Log", " Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            registrarLog.escrever(" Usuario ou senha de FTP estão incorretos " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        }
    }

    private void conectarDiretorioFtp() {
        try {
            Log.e("Log", " Diretório ftp de download " + ConfiguaracaoUtils.ftp.getDiretorioRemoto());
            ftp.changeDirectory(ConfiguaracaoUtils.ftp.getDiretorioRemoto());
            registrarLog.escrever(" Entrou no diretório " + ConfiguaracaoUtils.ftp.getDiretorioRemoto() + " com sucesso");
            arquivosNoFtp();
        } catch (IllegalStateException e) {
            Log.e("Log", " Diretório informado esta errado" + " " + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (IOException e) {
            Log.e("Log", " Diretório informado esta errado" + " " + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            Log.e("Log", " Diretório informado esta errado" + " " + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (FTPException e) {
            Log.e("Log", " Diretório informado esta errado" + " " + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado" + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (NullPointerException e){
            Log.e("Log", " Diretório informado esta errado " + " " + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (InvalidParameterException e){
            Log.e("Log", " Diretório informado esta errado " + " " + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        } catch (Exception e) {
            Log.e("Log", " Diretório informado esta errado " + " " + e.getMessage());
            registrarLog.escrever(" Diretório informado esta errado " + " " + e.getMessage());
            tentativasRealizadas++;
            desconectarFtp();
        }
    }

    private void zipArquivos() {
        Log.e("Log", "Entrou no metodo zip");
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
                    Log.e("Log", arquivosACompactar.getAbsolutePath());
                    zip.addFile(arquivosACompactar, parameters);
                } catch (ZipException e) {
                    registrarLog.escrever(" " + e.getMessage() + " Não pode compactar o arquivo " + arquivosACompactar.getName() + " " + e.getMessage());
                    continue;
                }
            }
        }
        Log.e("Log", zip.getFile().getAbsolutePath());
        desconectarFtp();
        //uploadZip();
    }

    private void uploadZip() {
        Log.e("Log", "Entrou no metodo de upload de zip");
        try {
            ftp.upload(new File(arquivoZip));
            Log.e("Log", " Arquivo zip " + new File(arquivoZip).getName() + " foi enviado com sucesso");
            registrarLog.escrever(" Arquivo zip " + new File(arquivoZip).getName() + " foi enviado com sucesso");
            //arquivosNoFtp();
            desconectarFtp();
        } catch (IllegalStateException e) {
            Log.e("Log", " O servidor ftp não esta conectado. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" O servidor ftp não esta conectado. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        } catch (FileNotFoundException e) {
            Log.e("Log", " O arquivo zip" + new File(arquivoZip).getName() + " não foi encontrado " + " " + e.getMessage());
            registrarLog.escrever(" O arquivo zip" + new File(arquivoZip).getName() + " não foi encontrado " + " " + e.getMessage());
            desconectarFtp();
        } catch (IOException e) {
            Log.e("Log", " Erro na entrada ou saida de dados. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" Erro na entrada ou saida de dados. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            Log.e("Log", " Servidor se comportou de forma inesperada. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" Servidor se comportou de forma inesperada. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPException e) {
            Log.e("Log", " Operação falhou. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" Operação falhou. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPDataTransferException e) {
            Log.e("Log", " Erro na entrada ou saida de dados mas possivelmente a conexão ainda esta ativa. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" Erro na entrada ou saida de dados mas possivelmente a conexão ainda esta ativa. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPAbortedException e) {
            Log.e("Log", " O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            registrarLog.escrever(" O upload foi abortado por outra thread. Não foi possível enviar o zip" + " " + e.getMessage());
            desconectarFtp();
        }
    }

    private void arquivosNoFtp() {
        Log.e("Log", "Metodo arquivos no ftp");
        try {
            listaArquivosFtp = ftp.list();
        } catch (IOException e) {
            Log.e("Log", e.getMessage());
            registrarLog.escrever(" Nenhum arquivo foi encontrado mov" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPIllegalReplyException e) {
            Log.e("Log", e.getMessage());
            registrarLog.escrever(" Nenhum arquivo foi encontrado mov" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPException e) {
            Log.e("Log", e.getMessage());
            registrarLog.escrever(" Nenhum arquivo foi encontrado mov" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPDataTransferException e) {
            Log.e("Log", e.getMessage());
            registrarLog.escrever(" Nenhum arquivo foi encontrado mov" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPAbortedException e) {
            Log.e("Log", e.getMessage());
            registrarLog.escrever(" Nenhum arquivo foi encontrado mov" + " " + e.getMessage());
            desconectarFtp();
        } catch (FTPListParseException e) {
            Log.e("Log", e.getMessage());
            registrarLog.escrever(" Nenhum arquivo foi encontrado mov" + " " + e.getMessage());
            desconectarFtp();
        } catch (NullPointerException e){
            Log.e("Log", e.getMessage());
            registrarLog.escrever(" Nenhum arquivo foi encontrado mov" + " " + e.getMessage());
            desconectarFtp();
        } catch (InvalidParameterException e){
            Log.e("Log", e.getMessage());
            registrarLog.escrever(" Nenhum arquivo foi encontrado mov" + " " + e.getMessage());
            desconectarFtp();
        } catch (Exception e) {
            Log.e("Log", e.getMessage());
            registrarLog.escrever(" Nenhum arquivo foi encontrado mov" + " " + e.getMessage());
            desconectarFtp();
        }

        Log.e("Log", "Final arquivosNoFtp");
        if (null != listaArquivosFtp && listaArquivosFtp.length > 0) {
            download(listaArquivosFtp);
        } else {
            desconectarFtp();
        }
    }

    private void download(FTPFile[] arquivos) {
        Log.e("Log", "download");
        for (FTPFile file : arquivos) {
            if (file.getName().contains(".mov") || file.getName().contains(".md5") || file.getName().contains(".db") || file.getName().contains(".exp")) {
                try {
                    Log.e("Log", file.getName());
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
                    Log.e("Log", file.getName() + " Renomear");
                    ftp.rename(file.getName(), file.getName());
                    registrarLog.escrever(" Arquivo " + file.getName() + " foi renomeado no servidor com sucesso");
                } catch (IllegalStateException e) {
                    Log.e("Log", e.getMessage());
                    e.printStackTrace();
                    registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                    continue;
                } catch (IOException e) {
                    Log.e("Log", e.getMessage());
                    e.printStackTrace();
                    registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                    continue;
                } catch (FTPIllegalReplyException e) {
                    Log.e("Log", e.getMessage());
                    e.printStackTrace();
                    registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                    continue;
                } catch (FTPException e) {
                    Log.e("Log", e.getMessage());
                    e.printStackTrace();
                    registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                    continue;
                } catch (NullPointerException e) {
                    Log.e("Log", e.getMessage());
                    e.printStackTrace();
                    registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                    continue;
                } catch (InvalidParameterException e) {
                    Log.e("Log", e.getMessage());
                    e.printStackTrace();
                    registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                    continue;
                } catch (Exception e) {
                    Log.e("Log", e.getMessage());
                    e.printStackTrace();
                    registrarLog.escrever(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                    continue;
                }
            }
        }
        listaArquivosFtp = null;
        validarMd5();
    }

    private void validarMd5(){
        Log.e("Log","validarMD5");
        md5Utils.controladorArquivosFragmentados(salvar_importes);
        List<File> arquivosValidos = md5Utils.getArquivosValidos();

        if(null != arquivosValidos && !arquivosValidos.isEmpty()){
            for(File file : arquivosValidos) {
                String resultado = Arquivo.moverArquivo(file, new File(diretorioDeVideos.concat(barraDoSistema).concat(file.getName())));
            }
        }

       validarExp();
    }

    private void validarExp() {
        Log.e("Log","Metodo validar  exp");
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
                        registrarLog.escrever(" Encontrou o arquivo de banco " + arquivos.getName());
                        String resultado = Arquivo.moverArquivo(arquivos, new File(diretorioVideoOne.concat(barraDoSistema).concat(arquivos.getName())));
                        registrarLog.escrever(" " + resultado);
                        arquivos.delete();
                    }

                    if (arquivos.getName().endsWith(".apk")) {
                        registrarLog.escrever(" Encontrou o arquivo " + arquivos.getName());
                        String resultado = Arquivo.moverArquivo(arquivos, new File(diretorioVideoOne.concat(barraDoSistema).concat(arquivos.getName())));
                        registrarLog.escrever(" " + resultado);
                        arquivos.delete();
                    }

                    if (arquivos.getName().endsWith(".properties")) {
                        registrarLog.escrever(" Encontrou o arquivo " + arquivos.getName());
                        String resultado = Arquivo.moverArquivo(arquivos, new File(diretorioConfig.concat(barraDoSistema).concat(arquivos.getName())));
                        registrarLog.escrever(" " + resultado);
                        arquivos.delete();
                    }
                }
            } else {
                registrarLog.escrever(" Não foram encontrados arquivos no diretório " + salvar_importes);
                desconectarFtp();
                return;
            }
            fileExp.delete();
            desconectarFtp();


        } else {
            registrarLog.escrever(" Arquivos videoOne.exp não foi encontrado ");
            Log.e("Log","Arquivos videoOne.exp não foi encontrado");
            desconectarFtp();
        }
    }

    private void desconectarFtp() {
        System.out.println("Metodo Desconectar ftp");
        if (ftp.isConnected()) {
            Log.e("Log", "Conectado ao servidor");
            try {
                ftp.disconnect(true);
            } catch (IOException e) {
                Log.e("Log", " Ftp não esta conectado, não é possível desconectar-se " + e.getMessage());
                registrarLog.escrever(" Ftp não esta conectado, não é possível desconectar-se " + e.getMessage());
                ftp = new FTPClient();
            } catch (FTPIllegalReplyException e) {
                Log.e("Log", " O servidor ftp respondeu de uma forma inesperada " + e.getMessage());
                registrarLog.escrever(" O servidor ftp respondeu de uma forma inesperada " + e.getMessage());
                ftp = new FTPClient();
            } catch (FTPException e) {
                Log.e("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                registrarLog.escrever(" O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (NullPointerException e) {
                Log.e("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                registrarLog.escrever(" O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (InvalidParameterException e) {
                Log.e("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                registrarLog.escrever(" O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            } catch (Exception e) {
                Log.e("Log", " O servidor ftp não finalizou a conexão " + e.getMessage());
                registrarLog.escrever(" O servidor ftp não finalizou a conexão " + e.getMessage());
                ftp = new FTPClient();
            }
        }
        Log.e("Log", "Desconectou-se do ftp");
        registrarLog.escrever(" Desconectou-se do ftp");
    }
}

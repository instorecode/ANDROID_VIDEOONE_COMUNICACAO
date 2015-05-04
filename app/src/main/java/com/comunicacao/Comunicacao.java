package com.comunicacao;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.br.instore.utils.Arquivo;
import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.ImprimirUtils;
import com.br.instore.utils.LogUtils;
import com.syso.Imprimir;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

public class Comunicacao  {
/*
    private Utils utils;
    private Context context;
    private FTPClient ftp = new FTPClient();
    private FTPFile[] listaArquivosFtp;

    private PopularBanco popularBanco;
    private CriarPlaylist criarPlaylist;
    private LogUtils logUtils = new LogUtils();



    private int tentativasRealizadas = 0;


    private final byte[] buffer = new byte[1024];

    private Date ultimaComunicacao;
    private boolean podeExecutar = true;


    private ValidarDiaAndHora validarHoraAndDia = new ValidarDiaAndHora(Environment.getExternalStorageDirectory() + "/videoOne/config/configuracoes.properties");
    private String hashId = "";

    public Comunicacao(Context contextParametro) {
        this.context = contextParametro;
    }

    //---Thread---//
    @Override
    protected Void doInBackground(Integer... params) {
        while (true) {
            boolean existeProperties = ConfiguaracaoUtils.lerProperties(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config").concat(barraDoSistema).concat("configuracoes.properties"));

            if (existeProperties) {
                utils = new Utils(context);
                utils.criarLog(logUtils);
                informacoesConexao();

                validarHoraAndDia.procurarHorarioValido();

                if (validarHoraAndDia.isValid()) {
                    Log.e("O horario é valido");
                    if (!hashId.equals(validarHoraAndDia.hashId())) {
                        Log.e("hash é diferente do hash anterior");
                        tentativasRealizadas = 0;
                    }

                    System.out.println("é um dia valido, Maximo tentativas = " + maximoTentativas);
                    System.out.println("é um dia valido, Tentativas realizadas " + tentativasRealizadas);

                    if (tentativasRealizadas < maximoTentativas) {
                        Log.e(" Rodando Thread ");
                        logUtils.registrar(" Rodando Thread");
                        conectarEnderecoFtp();
                        moverVideos();
                        hashId = validarHoraAndDia.hashId();
                    }

                } else {
                    // executar o emergencia
                    System.out.println("Não é um dia valido, Maximo tentativas = " + maximoTentativas);
                    System.out.println("Não é um dia valido, Tentativas realizadas " + tentativasRealizadas);
                    Log.e(" Não é válido o dia");
                    tentativasRealizadas = 0;
                }

            } else {
                logUtils.registrar(" Arquivo de configurações foi excluido");
                Log.e("ERRO::", "Arquivo de configurações foi excluido");
            }
        }
    }


    private void downloadDeVideos() {
        System.out.println("Metodo download de videos");
        for (FTPFile file : listaArquivosFtp) {
            if (file.getName().endsWith(".mov")) {
                try {
                    Log.e(" Onde vai salvar o download = " + salvar_importes.concat(barraDoSistema).concat(file.getName()));
                    ftp.download(file.getName(), new FileOutputStream(new File(salvar_importes.concat(barraDoSistema).concat(file.getName()))), 0, new TransferCustom());
                    Log.e(" Arquivos no servidor FTP " + file.getName() + " baixado com sucesso");

                    logUtils.registrar(" Arquivos no servidor FTP " + file.getName() + " baixado com sucesso");
                } catch (IllegalStateException e) {
                    Log.e(" Não foi possível baixar o arquivo " + file.getName() + ". O cliente não está conectado ou não autenticado." + " " + e.getMessage());
                    logUtils.registrar(" Não foi possível baixar o arquivo " + file.getName() + ". . O cliente não está conectado ou não autenticado." + " " + e.getMessage());
                    continue;
                } catch (FileNotFoundException e) {
                    Log.e(" Não foi possível baixar o arquivo " + file.getName() + ". O arquivo em questão não pode ser encontrado." + " " + e.getMessage());
                    logUtils.registrar(" Não foi possível baixar o arquivo " + file.getName() + ". O arquivo em questão não pode ser encontrado." + " " + e.getMessage());
                    continue;
                } catch (IOException e) {
                    Log.e(" Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados" + " " + e.getMessage());
                    logUtils.registrar(" Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados" + " " + e.getMessage());
                    continue;
                } catch (FTPIllegalReplyException e) {
                    Log.e(" Não foi possível baixar o arquivo " + file.getName() + ". O servidor respondeu de forma ilegal" + " " + e.getMessage());
                    logUtils.registrar(" Não foi possível baixar o arquivo " + file.getName() + ". O servidor respondeu de forma ilegal" + " " + e.getMessage());
                    continue;
                } catch (FTPException e) {
                    Log.e(" Não foi possível baixar o arquivo " + file.getName() + ". A operação de download falhou" + " " + e.getMessage());
                    logUtils.registrar(" Não foi possível baixar o arquivo " + file.getName() + ". A operação de download falhou" + " " + e.getMessage());
                    continue;
                } catch (FTPDataTransferException e) {
                    Log.e(" Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados e falhou na tranferencia" + " " + e.getMessage());
                    logUtils.registrar(" Não foi possível baixar o arquivo " + file.getName() + ". Erro na entrada ou saida de dados e falhou na tranferencia" + " " + e.getMessage());
                    continue;
                } catch (FTPAbortedException e) {
                    Log.e(" Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                    logUtils.registrar(" Não foi possível baixar o arquivo " + file.getName() + ". A operação foi abortada por outro fator" + " " + e.getMessage());
                    continue;
                }

                try {
                    ftp.rename(file.getName(), file.getName().concat(".@@@"));
                    Log.e(" Arquivo renomeado no servidor " + file.getName());
                    logUtils.registrar(" Arquivo " + file.getName() + " foi renomeado no servidor com sucesso");
                } catch (IllegalStateException e) {
                    Log.e(" Não foi possível renomear o arquivo " + file.getName() + " " + e.getMessage());
                    logUtils.registrar(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                    continue;
                } catch (IOException e) {
                    Log.e(" Não foi possível renomear o arquivo " + file.getName() + " " + e.getMessage());
                    logUtils.registrar(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                    continue;
                } catch (FTPIllegalReplyException e) {
                    Log.e(" Não foi possível renomear o arquivo " + file.getName() + " " + e.getMessage());
                    logUtils.registrar(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                    continue;
                } catch (FTPException e) {
                    Log.e(" Não foi possível renomear o arquivo " + file.getName() + " " + e.getMessage());
                    logUtils.registrar(" Não foi possível baixar o arquivo " + file.getName() + " " + e.getMessage());
                    continue;
                }
            }
        }
        downloadExp();
    }

    private void validarExp() {
        Log.e("Metodo validar  exp");
        File fileExp = new File(salvar_importes + "/videoOne.exp");
        long tamanho = fileExp.length();

        //--- Verifica se existe o arquivo videoOne.exp
        if (fileExp.exists() && tamanho != 0) {
            ZipFile zip = null;
            try {
                zip = new ZipFile(fileExp);
            } catch (ZipException e) {
                Log.e("Erro ao pegar o arquivo videoOne.exp " + e.getMessage());
                logUtils.registrar(" Erro ao pegar o arquivo videoOne.exp " + e.getMessage());
                desconectarFtp();
            }

            if (null != zip) {
                try {
                    zip.extractAll(salvar_importes);
                } catch (ZipException e) {
                    Log.e("Não foi possível descompactar o arquivo zip" + zip.getFile().getName());
                    logUtils.registrar(" Não foi possível descompactar o arquivo " + zip.getFile().getName());
                    desconectarFtp();
                }
            } else {
                Log.e("O arquivo zip " + zip.getFile().getName() + " está nulo");
                logUtils.registrar(" O arquivo zip " + zip.getFile().getName() + " está nulo");
                desconectarFtp();
            }

            //--- Percore o diretório de import e registra os arquivos que na estiverem ---//
            File[] arquivosNoDiretorioImport = new File(salvar_importes).listFiles();
            if (null != arquivosNoDiretorioImport || arquivosNoDiretorioImport.length != 0) {
                for (File arquivos : arquivosNoDiretorioImport) {

                    if (arquivos.getName().endsWith(".db")) {
                        logUtils.registrar(" Encontrou o arquivo de banco " + arquivos.getName());
                        String resultado = Arquivo.moverArquivo(arquivos, new File(diretorioVideoOne + "/" + arquivos.getName()));
                        logUtils.registrar(" " + resultado);
                        arquivos.delete();
                    }

                    if (arquivos.getName().endsWith(".apk")) {
                        logUtils.registrar(" Encontrou o arquivo " + arquivos.getName());
                        String resultado = Arquivo.moverArquivo(arquivos, new File(diretorioVideoOne + "/" + arquivos.getName()));
                        logUtils.registrar(" " + resultado);
                        arquivos.delete();
                    }

                    if (arquivos.getName().endsWith(".properties")) {
                        logUtils.registrar(" Encontrou o arquivo " + arquivos.getName());
                        String resultado = Arquivo.moverArquivo(arquivos, new File(diretorioConfig + "/" + arquivos.getName()));
                        logUtils.registrar(" " + resultado);
                        arquivos.delete();
                    }
                }
            } else {
                Log.e("Não foram encontrados arquivos no diretório " + salvar_importes);
                logUtils.registrar(" Não foram encontrados arquivos no diretório " + salvar_importes);
                desconectarFtp();
                return;
            }
            fileExp.delete();
            desconectarFtp();


        } else {
            Log.e("Arquivos videoOne.exp não foi encontrado ");
            logUtils.registrar(" Arquivos videoOne.exp não foi encontrado ");
            desconectarFtp();
        }
    }



    private void moverVideos() {
        System.out.println("Metodo Mover Videos");
        /*String diretorioVideo = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo());
        File diretorio = new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioImportacao()));
        File[] arquivos = diretorio.listFiles();
        for (File file : arquivos) {
            if(file.getName().endsWith(".mov")) {
                File destino = new File(diretorioVideo.concat(barraDoSistema).concat(file.getName()));
                String resultado = Arquivo.moverArquivo(file, destino);
                logUtils.registrar(" ".concat(resultado));
                file.delete();
            }
        }
        finalizar();
    }

    private void finalizar() {
        popularBanco = new PopularBanco(context);
        List<String> erroCategoria = PopularBanco.errosCategoria;
        List<String> erroComercial = PopularBanco.errosComercial;
        List<String> erroProgramacao = PopularBanco.errosProgramacao;
        List<String> erroVideo = PopularBanco.errosVideo;

        if (null != erroCategoria && erroCategoria.size() > 0) {
            for (String erro : erroCategoria) {
                logUtils.registrar(" Categoria: ERRO - " + erro);
            }
        }

        if (null != erroComercial && erroComercial.size() > 0) {
            for (String erro : erroComercial) {
                logUtils.registrar(" Comercial: ERRO - " + erro);
            }
        }

        if (null != erroProgramacao && erroProgramacao.size() > 0) {
            for (String erro : erroProgramacao) {
                logUtils.registrar(" Programação: ERRO - " + erro);
            }
        }

        if (null != erroVideo && erroVideo.size() > 0) {
            for (String erro : erroVideo) {
                logUtils.registrar(" Video:ERRO - " + erro);
            }
        }

        criarPlaylist = new CriarPlaylist(context, logUtils);
        criarPlaylist.comerciaisDeterminados();
        criarPlaylist.horariosComercialDeterminado();
        criarPlaylist.criarArquivoComercialDet(caminho.concat("/videoOne/playlist") ,"PlaylistDet.exp");
        criarPlaylist.programacao();
        criarPlaylist.categoria();
        criarPlaylist.criarArquivoPlaylist(caminho.concat("/videoOne/playlist"), "Playlist.exp");

        tentativasRealizadas++;
        logUtils.registrar(" Final Tentativas realizadas " + tentativasRealizadas);
    }
*/
}

package com.tarefas;

import android.os.Environment;

import com.br.instore.utils.ConfiguaracaoUtils;
import com.utils.AndroidImprimirUtils;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class TaskBackup implements Runnable {

    String caminho = Environment.getExternalStorageDirectory().toString();
    String barraDoSistema = System.getProperty("file.separator");

    @Override
    public void run() {
        List<File> listaDeArquivos = new ArrayList<File>();
        String caminhoBackup = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("backup");
        String caminhoBanco = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("videoOneDs.db");
        String caminhoProperties = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("config").concat(barraDoSistema).concat("configuracoes.properties");
        String caminhoImport = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioImportacao());
        String caminhoLog = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioLog());
        String caminhoPlaylist = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioPlaylist());
        String caminhoZip = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("backup").concat(barraDoSistema).concat("backup1.zip");
        String caminhoZip2 = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("backup").concat(barraDoSistema).concat("backup2.zip");
        String caminhoZip3 = caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("backup").concat(barraDoSistema).concat("backup3.zip");

        try {
            File fileBackup = new File(caminhoBackup);
            if (!fileBackup.exists()) {
                fileBackup.mkdir();
            }
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
            return;
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
            return;
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
            return;
        }


        File arquivoProperties = null;
        try {
            arquivoProperties = new File(caminhoProperties);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }

        File arquivoBanco = null;
        try {
            arquivoBanco = new File(caminhoBanco);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }

        File diretorioImport = null;
        try {
            diretorioImport = new File(caminhoImport);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }

        File diretorioLog = null;
        try {
            diretorioLog = new File(caminhoLog);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }

        File diretorioPlaylist = null;
        try {
            diretorioPlaylist = new File(caminhoPlaylist);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }

        try {
            if (null != diretorioImport) {
                for (File file : diretorioImport.listFiles()) {
                    try {
                        if (file.exists()) {
                            listaDeArquivos.add(file);
                        }
                    } catch (NullPointerException e) {
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    } catch (InvalidParameterException e) {
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    } catch (Exception e) {
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    }
                }
            }
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }

        try {
            if (null != diretorioLog) {
                for (File file : diretorioLog.listFiles()) {
                    try {
                        if (file.exists()) {
                            listaDeArquivos.add(file);
                        }
                    } catch (NullPointerException e) {
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    } catch (InvalidParameterException e) {
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    } catch (Exception e) {
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    }
                }
            }
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }

        try {
            if (null != diretorioPlaylist) {
                for (File file : diretorioPlaylist.listFiles()) {
                    try {
                        if (file.exists()) {
                            listaDeArquivos.add(file);
                        }
                    } catch (NullPointerException e) {
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    } catch (InvalidParameterException e) {
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    } catch (Exception e) {
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                        AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    }
                }
            }
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }

        try {
            listaDeArquivos.add(arquivoProperties);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }


        try {
            listaDeArquivos.add(arquivoBanco);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }


        try {
            if (null != listaDeArquivos && !listaDeArquivos.isEmpty()) {
                File arquivoZip = new File(caminhoZip);

                if (arquivoZip.exists()) {
                    File fileZip3 = new File(caminhoZip3);
                    if (fileZip3.exists()) {
                        fileZip3.delete();
                    }


                    File fileZip2 = new File(caminhoZip2);
                    if (fileZip2.exists()) {
                        fileZip2.renameTo(fileZip3);
                    }

                    arquivoZip.renameTo(fileZip2);
                    compactar(listaDeArquivos, arquivoZip);

                } else {
                    compactar(listaDeArquivos, arquivoZip);
                }
            }
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }
    }

    private void compactar(List<File> arquivos, File file) {
        ZipFile zip = null;
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        try {
            zip = new ZipFile(file);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
            AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
        }

        for (File files : arquivos) {
            if (files.exists()) {

                try {
                   zip.addFile(files, parameters);
                } catch (ZipException e) {
                    AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                    AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    continue;
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                    AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    continue;
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                    AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    continue;
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(TaskBackup.class, e);
                    AndroidImprimirUtils.imprimirErro(TaskBackup.class, e, 90);
                    continue;
                }
            }
        }
    }
}

package com.banco;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.ExpUtils;
import com.utils.RegistrarLog;

import java.io.File;

public class PopularBanco {

    private BancoDAO bancoDAO;
    private final String caminho = Environment.getExternalStorageDirectory().toString();
    private String barraDoSistema = System.getProperty("file.separator");
    private Context context;



    public PopularBanco(Context context) {
        this.context = context;
        this.bancoDAO = new BancoDAO(context);

        controlador();
        criarViewComercialDet();
        criarViewProgramacao();
        criarViewComercialRandom();
    }

    private void controlador() {
        String categoria = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioImportacao().concat(barraDoSistema).concat("Categoria.exp"));
        String comercial = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioImportacao().concat(barraDoSistema).concat("Comercial.exp"));
        String programacao = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioImportacao().concat(barraDoSistema).concat("Programacao.exp"));
        String video = caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioImportacao().concat(barraDoSistema).concat("Video.exp"));

        File arquivoCategoria = new File(categoria);
        File arquivoComercial = new File(comercial);
        File arquivoProgramacao = new File(programacao);
        File arquivoVideo = new File(video);

        if (!arquivoCategoria.exists()) {
            RegistrarLog.imprimirMsg("Log"," O arquivo Categoria.exp não foi encontrado");
        } else {
            String resultado = insertCategoria(categoria);
            RegistrarLog.imprimirMsg(resultado);

            if (null != ExpUtils.errosLerCategoria && ExpUtils.errosLerCategoria.size() > 0) {
                errosCategoria = ExpUtils.errosLerCategoria;
            }

            File renomearCategoria = new File(caminho + "/" + ConfiguaracaoUtils.diretorio.getDiretorioImportacao() + "/Categoria.old");
            arquivoCategoria.renameTo(renomearCategoria);

            if (renomearCategoria.exists()) {
                RegistrarLog.imprimirMsg("Renomeu com sucesso Categoria.exp");
            } else {
                RegistrarLog.imprimirMsg("Não renomeu com sucesso Categoria.exp");
            }
        }

        if (!arquivoComercial.exists()) {
            System.out.println("O arquivo Comercial.exp não foi encontrado");
        } else {
            String resultado = insertComercial(comercial);
            RegistrarLog.imprimirMsg(resultado);

            if (null != ExpUtils.errosLerComercial && ExpUtils.errosLerComercial.size() > 0) {
                errosComercial = ExpUtils.errosLerComercial;
            }

            File renomearComercial = new File(caminho + "/" + ConfiguaracaoUtils.diretorio.getDiretorioImportacao() + "/Comercial.old");
            arquivoComercial.renameTo(renomearComercial);

            if (renomearComercial.exists()) {
                RegistrarLog.imprimirMsg("Renomeu com sucesso Comercial.exp");
            } else {
                RegistrarLog.imprimirMsg("Não renomeu com sucesso Comercial.exp");
            }
        }

        if (!arquivoProgramacao.exists()) {
            System.out.println(" O arquivo Programacao.exp não foi encontrado");
        } else {
            String resultado = insertProgramacao(programacao);
            RegistrarLog.imprimirMsg(resultado);

            if (null != ExpUtils.errosLerProgramacao && ExpUtils.errosLerProgramacao.size() > 0) {
                errosProgramacao = ExpUtils.errosLerProgramacao;
            }

            File renomearProgramacao = new File(caminho + "/" + ConfiguaracaoUtils.diretorio.getDiretorioImportacao() + "/Programacao.old");
            arquivoProgramacao.renameTo(renomearProgramacao);

            if (renomearProgramacao.exists()) {
                RegistrarLog.imprimirMsg("Renomeu com sucesso Programcao.exp ");
            } else {
                RegistrarLog.imprimirMsg("Não renomeu com sucesso Programcao.exp ");
            }
        }


        if (!arquivoVideo.exists()) {
            System.out.println(" O arquivo Video.exp não foi encontrado");
        } else {
            String resultado = insertVideo(video);
            RegistrarLog.imprimirMsg(resultado);

            if (null != ExpUtils.errosLerVideo && ExpUtils.errosLerVideo.size() > 0) {
                errosVideo = ExpUtils.errosLerVideo;
            }

            File renomearVideo = new File(caminho + "/" + ConfiguaracaoUtils.diretorio.getDiretorioImportacao() + "/Video.old");
            arquivoVideo.renameTo(renomearVideo);

            if (renomearVideo.exists()) {
                RegistrarLog.imprimirMsg("Renomeu com sucesso");
            } else {
                RegistrarLog.imprimirMsg("Não renomeu com sucesso");
            }
        }
    }

    private String insertProgramacao(String caminho) {
        db = context.openOrCreateDatabase(Environment.getExternalStorageDirectory() + "/videoOne/videoOneDs.db", Context.MODE_PRIVATE, null);
        db.beginTransaction();
        try {
            List<ProgramacaoExp> listaProgramacoes = ExpUtils.lerProgramacao(caminho);

            for (ProgramacaoExp p : listaProgramacoes) {
                ContentValues values = new ContentValues();
                values.put("Descricao", p.descricao.trim());
                values.put("Dia", new SimpleDateFormat("dd").format(p.dataInicial));
                values.put("Mes", new SimpleDateFormat("MM").format(p.dataInicial));
                values.put("Ano", new SimpleDateFormat("yyyy").format(p.dataInicial));
                values.put("Diaf", new SimpleDateFormat("dd").format(p.dataFinal));
                values.put("Mesf", new SimpleDateFormat("MM").format(p.dataFinal));
                values.put("Anof", new SimpleDateFormat("yyyy").format(p.dataFinal));
                values.put("DiaSemana", p.diaDaSemana);
                values.put("HoraInicio", new SimpleDateFormat("HH:mm:ss").format(p.horarioInicio));
                values.put("HoraFinal", new SimpleDateFormat("HH:mm:ss").format(p.horarioFinal));
                values.put("Categoria1", p.categoria1);
                values.put("Categoria2", p.categoria2);
                values.put("Categoria3", p.categoria3);
                values.put("Categoria4", p.categoria4);
                values.put("Categoria5", p.categoria5);
                values.put("Categoria6", p.categoria6);
                values.put("Categoria7", p.categoria7);
                values.put("Categoria8", p.categoria8);
                values.put("Categoria9", p.categoria9);
                values.put("Categoria10", p.categoria10);
                values.put("Categoria11", p.categoria11);
                values.put("Categoria12", p.categoria12);
                values.put("Categoria13", p.categoria13);
                values.put("Categoria14", p.categoria14);
                values.put("Categoria15", p.categoria15);
                values.put("Categoria16", p.categoria16);
                values.put("Categoria17", p.categoria17);
                values.put("Categoria18", p.categoria18);
                values.put("Categoria19", p.categoria19);
                values.put("Categoria20", p.categoria20);
                values.put("Categoria21", p.categoria21);
                values.put("Categoria22", p.categoria22);
                values.put("Categoria23", p.categoria23);
                values.put("Categoria24", p.categoria24);
                values.put("Conteudo", p.conteudo);
                db.replace("Programacao", null, values);
            }
            db.setTransactionSuccessful();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            String resultado;
            resultado = " Erro ao popular o banco com as informações do exp de programação " + e.getMessage();
            return resultado;

        } catch (IOException e) {
            e.printStackTrace();
            String resultado;
            resultado = " Erro ao popular o banco com as informações do exp de programação " + e.getMessage();
            return resultado;

        } catch (ParseException e) {
            e.printStackTrace();
            String resultado;
            resultado = " Erro ao popular o banco com as informações do exp de programação " + e.getMessage();
            return resultado;

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            String resultado;
            resultado = " Erro ao popular o banco com as informações do exp de programação " + e.getMessage();
            return resultado;

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            String resultado;
            resultado = " Erro ao popular o banco com as informações do exp de programação " + e.getMessage();
            return resultado;
        } finally {
            db.endTransaction();
            while(db.isOpen()){
                db.close();
            }
        }
        return " Banco foi populado com as informações do exp de programação com sucesso";
    }

    private String insertVideo(String caminho) {
        db = context.openOrCreateDatabase(Environment.getExternalStorageDirectory() + "/videoOne/videoOneDs.db", Context.MODE_PRIVATE, null);
        db.beginTransaction();
        try {
            List<VideoExp> listaVideo = ExpUtils.lerVideo(caminho);

            for (VideoExp v : listaVideo) {
                ContentValues values = new ContentValues();
                values.put("Arquivo", v.arquivo.trim());
                values.put("Interprete", v.interprete.trim());
                values.put("TipoInterprete", v.tipoInterprete);
                values.put("Titulo", v.titulo.trim());
                values.put("Cut", (v.cut == true) ? 1 : 0);
                values.put("Categoria1", v.categoria1);
                values.put("Categoria2", v.categoria2);
                values.put("Categoria3", v.categoria3);
                values.put("Crossover", (v.crossover == true) ? 1 : 0);
                values.put("DataVenctoCrossOver", new SimpleDateFormat("yyyy-MM-dd").format(v.dataVencimentoCrossover));
                values.put("DiasExecucao", v.diasExecucao1);
                values.put("DiasExecucao2", v.diasExecucao2);
                values.put("Afinidade1", v.afinadade1.trim());
                values.put("Afinidade2", v.afinadade2.trim());
                values.put("Afinidade3", v.afinadade3.trim());
                values.put("Afinidade4", v.afinadade4.trim());
                values.put("Gravadora", v.gravadora);
                values.put("AnoGravacao", v.anoGravacao);
                values.put("Velocidade", v.velocidade);
                values.put("Data", new SimpleDateFormat("yyyy-MM-dd").format(v.data));
                values.put("UltimaExecucaoData", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(v.ultimaExecucaoData));
                values.put("TempoTotal", new SimpleDateFormat("HH:mm:ss").format(v.tempoTotal));
                values.put("QtdePlayer", v.quantidadePlayerTotal);
                values.put("DataVencto", new SimpleDateFormat("yyyy-MM-dd").format(v.dataVencimento));
                values.put("FrameInicio", v.frameInicio);
                values.put("FrameFinal", v.frameFinal);
                values.put("Msg", v.mensagem);

                db.replace("Video", null, values);
            }
            db.setTransactionSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            String resultado;
            resultado = " Erro ao popular o banco com as informações do exp de videos " + e.getMessage();
            return resultado;

        } catch (ParseException e) {
            e.printStackTrace();
            e.printStackTrace();
            String resultado;
            resultado = " Erro ao popular o banco com as informações do exp de videos " + e.getMessage();
            return resultado;
        } finally {
            db.endTransaction();
            while(db.isOpen()){
                db.close();
            }
        }
        return " Banco foi populado com as informações do exp de videos com sucesso";
    }


    private void criarViewComercialDet() {
        System.out.println("Metodo criar View Comercial det");
        db = context.openOrCreateDatabase(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("videoOneDs.db"), Context.MODE_PRIVATE, null);
        String viewComercialDet = banco.scriptComercialDetView;
        db.execSQL(viewComercialDet);
        while(db.isOpen()){
            db.close();
        }
    }

    private void criarViewProgramacao() {
        System.out.println("Metodo criar View Programação");
        db = context.openOrCreateDatabase(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("videoOneDs.db"), Context.MODE_PRIVATE, null);
        db.execSQL(banco.scriptProgramacaoView);
        while(db.isOpen()){
            db.close();
        }
    }

    public void criarViewComercialRandom() {
        System.out.println("Metodo criar View Comercial Random");
        db = context.openOrCreateDatabase(caminho.concat(barraDoSistema).concat("videoOne").concat(barraDoSistema).concat("videoOneDs.db"), Context.MODE_PRIVATE, null);
        String viewComercialRandom = banco.scriptComercialRandomView;
        db.execSQL(viewComercialRandom);
        while(db.isOpen()){
            db.close();
        }
    }
}
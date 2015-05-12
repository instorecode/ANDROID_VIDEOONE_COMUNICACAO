package com.comunicacao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.br.instore.exp.bean.ComercialExp;
import com.br.instore.exp.bean.ProgramacaoExp;
import com.br.instore.utils.Arquivo;
import com.br.instore.utils.ImprimirUtils;
import com.br.instore.utils.LogUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CriarPlaylist {

    /*
    private SQLiteDatabase db;
    private Context context;
    private String barraDoSistema = System.getProperty("file.separator");
    private LogUtils logUtils;

    private final String caminho = android.os.Environment.getExternalStorageDirectory().toString();
    private List<ComercialExp> listaComercialDeterminados = new ArrayList<ComercialExp>();
    private List<String> listaComercialDeterminadosArquivo = new ArrayList<String>();


    public List<ProgramacaoExp> listaProgramacao = new ArrayList<ProgramacaoExp>();
    public List<String> listaDeArquivos = new ArrayList<String>();

    public CriarPlaylist(Context contextParamentro, LogUtils logUtilsParametro) {
        this.context = contextParamentro;
        this.logUtils = logUtilsParametro;
    }

    public void comerciaisDeterminados() {
        db = context.openOrCreateDatabase(SdCard.caminhoBanco(Environment.getExternalStorageDirectory().getAbsolutePath()).concat("/videoOneDs.db"), Context.MODE_PRIVATE, null);
        String scriptComercial = "SELECT * FROM VIEW_CARREGAR_COMERCIAL_DET";
        Cursor cursor = db.rawQuery(scriptComercial, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                ComercialExp comercialExp = new ComercialExp();
                comercialExp.arquivo = cursor.getString(cursor.getColumnIndex("Arquivo"));
                comercialExp.cliente = cursor.getString(cursor.getColumnIndex("Cliente"));
                comercialExp.titulo = cursor.getString(cursor.getColumnIndex("Titulo"));
                comercialExp.categoria = cursor.getInt(cursor.getColumnIndex("Categoria"));

                try {
                    comercialExp.periodoInicial = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(cursor.getColumnIndex("PeriodoInicial")));
                } catch (ParseException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }

                try {
                    comercialExp.periodoFinal = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(cursor.getColumnIndex("PeriodoFinal")));
                } catch (ParseException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }


                for (int i = 1; i <= 24; i++) {
                    Field fieldSemana = null;
                    try {
                        fieldSemana = comercialExp.getClass().getDeclaredField("semana" + i);
                    } catch (NoSuchFieldException e) {
                        ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                    } catch (SecurityException e) {
                        ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                    }

                    fieldSemana.setAccessible(true);

                    try {
                        fieldSemana.set(comercialExp, cursor.getString(cursor.getColumnIndex("Semana" + i)));
                    } catch (IllegalArgumentException e) {
                        ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                    } catch (IllegalAccessException e) {
                        ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                    }

                    Field fieldHorario = null;
                    try {
                        fieldHorario = comercialExp.getClass().getDeclaredField("horario" + i);
                    } catch (NoSuchFieldException e) {
                        ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                    } catch (SecurityException e) {
                        ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                    }

                    fieldHorario.setAccessible(true);

                    Date horario = null;
                    try {
                        horario = new SimpleDateFormat("HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex("Horario" + i)));
                    } catch (ParseException e) {
                        ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                    }

                    try {
                        fieldHorario.set(comercialExp, horario);
                    } catch (IllegalArgumentException e) {
                        ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                    } catch (IllegalAccessException e) {
                        ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                    }
                }

                comercialExp.diaSemana = cursor.getString(cursor.getColumnIndex("DiaSemana"));
                comercialExp.diasAlternados = (cursor.getString(cursor.getColumnIndex("DiasAlternados")) == "1") ? true : false;
                comercialExp.tipoHorario = cursor.getInt(cursor.getColumnIndex("TipoHorario"));

                Date data = null;
                try {
                    data = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(cursor.getColumnIndex("Data")));
                } catch (ParseException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }
                comercialExp.data = data;

                Date ultimaExecucao = null;
                try {
                    ultimaExecucao = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex("UltimaExecucao")));
                } catch (ParseException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }
                comercialExp.ultimaExecucao = ultimaExecucao;

                Date tempoTotal = null;
                try {
                    tempoTotal = new SimpleDateFormat("HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex("TempoTotal")));
                } catch (ParseException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }

                comercialExp.tempoTotal = tempoTotal;
                comercialExp.random = cursor.getInt(cursor.getColumnIndex("Random"));
                comercialExp.qtdePlayer = cursor.getInt(cursor.getColumnIndex("QtdePlayer"));
                comercialExp.quantidade = cursor.getInt(cursor.getColumnIndex("Qtde"));

                Date dataVencimento = null;
                try {
                    dataVencimento = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(cursor.getColumnIndex("DataVencto")));
                } catch (ParseException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }

                comercialExp.dataVencimento = dataVencimento;
                comercialExp.dependencia1 = cursor.getString(cursor.getColumnIndex("Dependencia1"));
                comercialExp.dependencia2 = cursor.getString(cursor.getColumnIndex("Dependencia2"));
                comercialExp.dependencia3 = cursor.getString(cursor.getColumnIndex("Dependencia3"));
                listaComercialDeterminados.add(comercialExp);
            } while (cursor.moveToNext());
        }

        cursor.close();
        while(db.isOpen()){
            db.close();
        }
    }

    public void horariosComercialDeterminado() {
        for (ComercialExp comercialExp : listaComercialDeterminados) {
            for (int i = 1; i <= 24; i++) {
                Field fieldSemana = null;
                Field fieldHorario = null;
                try {
                    fieldSemana = comercialExp.getClass().getDeclaredField("semana" + i);
                    fieldHorario = comercialExp.getClass().getDeclaredField("horario" + i);
                } catch (NoSuchFieldException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                } catch (SecurityException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }

                fieldSemana.setAccessible(true);
                fieldHorario.setAccessible(true);

                try {
                    if (null != fieldSemana.get(comercialExp)) {
                        listaComercialDeterminadosArquivo.add(fieldSemana.get(comercialExp).toString() + "|" + new SimpleDateFormat("HH:mm").format(fieldHorario.get(comercialExp)) + "|" + comercialExp.arquivo.trim() + "|" +  comercialExp.titulo.trim() +"|"+ comercialExp.categoria +"|"+ new SimpleDateFormat("HH:mm:ss").format(comercialExp.tempoTotal));
                    }
                } catch (IllegalArgumentException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                } catch (IllegalAccessException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }

            }
        }

    }

    public void criarArquivoComercialDet(String caminhoDoArquivoDetComercial, String nomeDoArquivo) {
        File diretorio = new File(caminhoDoArquivoDetComercial);
        Arquivo.criarDiretorio(diretorio);

        File arquivo = new File(diretorio.getAbsolutePath().concat(barraDoSistema).concat(nomeDoArquivo));
        if (arquivo.exists()) {
            arquivo.delete();
        }

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(arquivo, true);
        } catch (IOException e) {
            ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
        }

        for (String str : listaComercialDeterminadosArquivo) {
            try {
                fileWriter.write(str.concat("\n"));
            } catch (IOException e) {
                ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
            }
        }
        listaComercialDeterminados.clear();
        listaComercialDeterminadosArquivo.clear();

        try {
            fileWriter.close();
        } catch (IOException e) {
            ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
        }
    }

    ////------------------------------------------------------------------////
    public void programacao() {
        System.out.println("Metodo programacao");
        db = context.openOrCreateDatabase(SdCard.caminhoBanco(Environment.getExternalStorageDirectory().getAbsolutePath()).concat("/videoOneDs.db"), Context.MODE_PRIVATE, null);
        String scriptProgramacao = "SELECT * FROM VIEW_CARREGAR_PROGRAMACAO";
        Cursor cursor = db.rawQuery(scriptProgramacao, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                ProgramacaoExp programacao = new ProgramacaoExp();
                programacao.descricao = cursor.getString(cursor.getColumnIndex("Descricao"));

                try {
                    String ano = cursor.getString(cursor.getColumnIndex("Ano")).trim();
                    String mes = cursor.getString(cursor.getColumnIndex("mesInicialFormatado")).trim();
                    String dia = cursor.getString(cursor.getColumnIndex("diaInicialFormatado")).trim();
                    programacao.dataInicial = new SimpleDateFormat("yyyy-MM-dd").parse(ano + "-" + mes + "-" + dia);
                } catch (ParseException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }

                try {
                    String anoF = cursor.getString(cursor.getColumnIndex("Anof")).trim();
                    String mesF = cursor.getString(cursor.getColumnIndex("mesfInicialFormatado")).trim();
                    String diaF = cursor.getString(cursor.getColumnIndex("diafInicialFormatado")).trim();
                    programacao.dataFinal = new SimpleDateFormat("yyyy-MM-dd").parse(anoF + "-" + mesF + "-" + diaF);
                } catch (ParseException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }

                try {
                    programacao.horarioInicio = new SimpleDateFormat("HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex("HoraInicio")));
                } catch (ParseException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }

                try {
                    programacao.horarioFinal = new SimpleDateFormat("HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex("HoraFinal")));
                } catch (ParseException e) {
                    ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
                }

                programacao.diaDaSemana = cursor.getString(cursor.getColumnIndex("DiaSemana"));
                programacao.categoria1 = cursor.getShort(cursor.getColumnIndex("Categoria1"));
                programacao.categoria2 = cursor.getShort(cursor.getColumnIndex("Categoria2"));
                programacao.categoria3 = cursor.getShort(cursor.getColumnIndex("Categoria3"));
                programacao.categoria4 = cursor.getShort(cursor.getColumnIndex("Categoria4"));
                programacao.categoria5 = cursor.getShort(cursor.getColumnIndex("Categoria5"));
                programacao.categoria6 = cursor.getShort(cursor.getColumnIndex("Categoria6"));
                programacao.categoria7 = cursor.getShort(cursor.getColumnIndex("Categoria7"));
                programacao.categoria8 = cursor.getShort(cursor.getColumnIndex("Categoria8"));
                programacao.categoria9 = cursor.getShort(cursor.getColumnIndex("Categoria9"));
                programacao.categoria10 = cursor.getShort(cursor.getColumnIndex("Categoria10"));
                programacao.categoria11 = cursor.getShort(cursor.getColumnIndex("Categoria11"));
                programacao.categoria12 = cursor.getShort(cursor.getColumnIndex("Categoria12"));
                programacao.categoria13 = cursor.getShort(cursor.getColumnIndex("Categoria13"));
                programacao.categoria14 = cursor.getShort(cursor.getColumnIndex("Categoria14"));
                programacao.categoria15 = cursor.getShort(cursor.getColumnIndex("Categoria15"));
                programacao.categoria16 = cursor.getShort(cursor.getColumnIndex("Categoria16"));
                programacao.categoria17 = cursor.getShort(cursor.getColumnIndex("Categoria17"));
                programacao.categoria18 = cursor.getShort(cursor.getColumnIndex("Categoria18"));
                programacao.categoria19 = cursor.getShort(cursor.getColumnIndex("Categoria19"));
                programacao.categoria20 = cursor.getShort(cursor.getColumnIndex("Categoria20"));
                programacao.categoria21 = cursor.getShort(cursor.getColumnIndex("Categoria21"));
                programacao.categoria22 = cursor.getShort(cursor.getColumnIndex("Categoria22"));
                programacao.categoria23 = cursor.getShort(cursor.getColumnIndex("Categoria23"));
                programacao.categoria24 = cursor.getShort(cursor.getColumnIndex("Categoria24"));
                programacao.conteudo = cursor.getString(cursor.getColumnIndex("Conteudo"));

                listaProgramacao.add(programacao);

            } while (cursor.moveToNext());
        }

        cursor.close();
        while(db.isOpen()){
            db.close();
        }
    }

    public void categoria() {
        for (ProgramacaoExp p : listaProgramacao) {
            RegistrarLog.imprimirMsg("PROGRAMACAO", p.descricao);
            codigoCategoria(p.categoria1, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria2, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria3, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria4, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria5, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria6, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria7, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria8, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria9, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria10, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria11, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria12, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria13, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria14, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria15, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria16, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria17, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria18, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria19, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria20, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria21, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria22, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria23, p.horarioInicio, p.horarioFinal);
            codigoCategoria(p.categoria24, p.horarioInicio, p.horarioFinal);
        }
    }

    public void codigoCategoria(Short codigo, Date horaInicialProgramacao, Date horaFinalProgramacao) {
        if (codigo != 0) {
            db = context.openOrCreateDatabase(SdCard.caminhoBanco(Environment.getExternalStorageDirectory().getAbsolutePath()).concat("/videoOneDs.db"), Context.MODE_PRIVATE, null);
            String scriptCategoria = "Select * from Categoria where Codigo = '" + codigo + "' and date('now') >= dataInicio and date('now') < dataFinal";
            Cursor cursor = db.rawQuery(scriptCategoria, new String[]{});

            if (cursor.moveToFirst()) {
                do {
                    short codigoCategoria = cursor.getShort(cursor.getColumnIndex("Codigo"));
                    short tipoCategoria = cursor.getShort(cursor.getColumnIndex("Tipo"));

                    if (tipoCategoria == 3) {

                    } else if (tipoCategoria == 2) {
                        comercial(codigoCategoria, horaInicialProgramacao, horaFinalProgramacao, tipoCategoria);
                    }
                } while (cursor.moveToNext());
            }

            cursor.isClosed();
            while(db.isOpen()){
                db.close();
            }
        }
    }


    public void comercial(Short codigoCategoria, Date horaInicialProgramacao, Date horaFinalProgramacao, short tipoCategoria) {
        //db = context.openOrCreateDatabase(SdCard.caminhoBanco(Environment.getExternalStorageDirectory().getAbsolutePath()).concat("/videoOneDs.db"), Context.MODE_PRIVATE, null);

        String horaInicial = new SimpleDateFormat("HH:mm:ss").format(horaInicialProgramacao);
        String horaFinal = new SimpleDateFormat("HH:mm:ss").format(horaFinalProgramacao);
        String scriptCategoria = "Select * from VIEW_CARREGAR_COMERCIAIS_RANDOM where Categoria = " + codigoCategoria;
        Cursor cursor = db.rawQuery(scriptCategoria, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                String arquivo = cursor.getString(cursor.getColumnIndex("Arquivo"));
                String titulo = cursor.getString(cursor.getColumnIndex("Titulo"));
                String tempoTotal = cursor.getString(cursor.getColumnIndex("TempoTotal"));
                String dependencia1 = cursor.getString(cursor.getColumnIndex("Dependencia1"));
                String dependencia2 = cursor.getString(cursor.getColumnIndex("Dependencia2"));
                String dependencia3 = cursor.getString(cursor.getColumnIndex("Dependencia3"));

                if (!dependencia1.trim().toLowerCase().equals("nenhuma")) {
                    String resultado = validarArquivo(dependencia1, true);
                    if (!resultado.equals("")) {
                        listaDeArquivos.add(horaInicial + "|" + horaFinal + "|" + resultado + "|" + titulo.trim() + "|" + codigoCategoria + "|" + tempoTotal + "|" + tipoCategoria);
                    }
                }

                if (!dependencia2.trim().toLowerCase().equals("nenhuma")) {
                    String resultado = validarArquivo(dependencia2, true);
                    if (!resultado.equals("")) {
                        listaDeArquivos.add(horaInicial + "|" + horaFinal + "|" + resultado + "|" + titulo.trim() + "|" + codigoCategoria + "|" + tempoTotal + "|" + tipoCategoria);
                    }
                }

                if (!dependencia3.trim().toLowerCase().equals("nenhuma")) {
                    String resultado = validarArquivo(dependencia3, true);
                    if (!resultado.equals("")) {
                        listaDeArquivos.add(horaInicial + "|" + horaFinal + "|" + resultado + "|" + titulo.trim() + "|" + codigoCategoria + "|" + tempoTotal + "|" + tipoCategoria);
                    }
                }

                String resultado = validarArquivo(arquivo, false);
                if (resultado.equals("")) {
                    continue;
                } else {
                    listaDeArquivos.add(horaInicial + "|" + horaFinal + "|" + resultado + "|" + titulo.trim() + "|" + codigoCategoria + "|" + tempoTotal + "|" + tipoCategoria);
                    continue;
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private String validarArquivo(String nome,  boolean dependencia) {
        //db = context.openOrCreateDatabase(SdCard.caminhoBanco(Environment.getExternalStorageDirectory().getAbsolutePath()).concat("/videoOneDs.db"), Context.MODE_PRIVATE, null);
        String nomeDoVideo = "";
        File diretorio = new File(caminho.concat("/videos"));
        Arquivo.criarDiretorio(diretorio);

        if (dependencia) {
            String script = "SELECT * FROM VIEW_CARREGAR_COMERCIAIS_RANDOM WHERE Arquivo = '" + nome + "'";
            Cursor cursor = db.rawQuery(script, new String[]{});
            if (cursor.moveToFirst()) {
                do {
                    nomeDoVideo = cursor.getString(cursor.getColumnIndex("Arquivo"));
                } while (cursor.moveToNext());
            } else {
                logUtils.registrar(" Desprezada com tipo 99. O arquivo " + nome.trim() + " não foi encontrado no banco");
                cursor.close();
                return "";
            }

            File arquivoDependencia = new File(diretorio.getAbsolutePath().concat(barraDoSistema).concat(nome.trim()));
            if (!arquivoDependencia.exists()) {
                logUtils.registrar(" Desprezada com tipo 99. O arquivo " + nome.trim() + " não foi encontrado");
                cursor.close();
                return "";
            }

            cursor.close();
            return nomeDoVideo;
        } else {
            File arquivo = new File(diretorio.getAbsolutePath().concat(barraDoSistema).concat(nome.trim()));
            if (!arquivo.exists()) {
                logUtils.registrar(" Desprezada com tipo 99. O arquivo " + nome.trim() + " não foi encontrado");
                return "";
            }
        }
        return nome;
    }

    public void criarArquivoPlaylist(String diretorioArquivoPlaylist, String nomeArquivo) {
        File diretorio = new File(diretorioArquivoPlaylist);
        Arquivo.criarDiretorio(diretorio);

        File arquivo = new File(diretorio.getAbsolutePath().concat(barraDoSistema).concat(nomeArquivo));
        if (arquivo.exists()) {
            arquivo.delete();
        }

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(arquivo, true);
        } catch (IOException e) {
            ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
        }

        for (String str : listaDeArquivos) {
            try {
                fileWriter.write(str.concat("\n"));
            } catch (IOException e) {
                ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
            }
        }

        listaDeArquivos.clear();
        listaProgramacao.clear();

        try {
            fileWriter.close();
        } catch (IOException e) {
            ImprimirUtils.imprimirErro(CriarPlaylist.this, e);
        }
    }*/
}

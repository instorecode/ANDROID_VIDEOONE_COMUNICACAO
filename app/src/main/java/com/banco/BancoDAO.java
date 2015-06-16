package com.banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.os.Environment;
import com.bean.ComercialDependencia;
import com.bean.ComercialDet;
import com.br.instore.exp.bean.CategoriaExp;
import com.br.instore.exp.bean.ComercialExp;
import com.br.instore.exp.bean.ProgramacaoExp;
import com.br.instore.exp.bean.VideoExp;
import com.br.instore.utils.Banco;
import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.DataUtils;
import com.br.instore.utils.ExpUtils;
import com.br.instore.utils.LogUtils;
import com.br.instore.utils.StringUtils;
import com.utils.AndroidImprimirUtils;
import com.utils.RegistrarLog;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class BancoDAO {
    private static final File arquivoBanco = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/videoOne/").concat("videoOneDs.db"));
    private static List<ProgramacaoExp> listaProgramacao = new ArrayList<ProgramacaoExp>();
    private static List<String> listaDeArquivos = new ArrayList<String>();
    private static List<ComercialDet> listaComercialDeterminados = new ArrayList<ComercialDet>();
    private static List<String> linhasPlaylistDet = new ArrayList<String>();
    private static ExpUtils expUtils = new ExpUtils();
    private static final String VIEW_PROGRAMACAO = "SELECT * FROM VIEW_CARREGAR_PROGRAMACAO";
    private static final String barraDoSistema = System.getProperty("file.separator");
    private static String caminho = Environment.getExternalStorageDirectory().toString();
    private int valorRandom = 0;
    private static Banco banco = new Banco();
    private static DatabaseHelper helper;
    private static SQLiteDatabase db;
    private static Cursor cursor;
    private static BancoDAO bancoDAO;

    private BancoDAO(Context context){
        this.helper = new DatabaseHelper(context);
    }

    public static void getInstance(Context context){
        if(null == bancoDAO){
            bancoDAO = new BancoDAO(context);
        }
    }

    public static SQLiteDatabase getDb() {
        if (null == db) {
            db = helper.getWritableDatabase();
        }
        return db;
    }

    public static void close() {
        db = null;
        helper.close();
    }

    ///------------------ VIDEOS COMERCIAIS -------------------///
    public static void programacoes() {
        if (arquivoBanco.exists()) {
            SQLiteDatabase db = getDb();
            cursor = db.rawQuery(VIEW_PROGRAMACAO, new String[]{});
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    try {
                        ProgramacaoExp programacaoExp = new ProgramacaoExp();
                        programacaoExp.descricao = cursor.getString(cursor.getColumnIndex("Descricao"));
                        programacaoExp.anoInicial = cursor.getString(cursor.getColumnIndex("Ano")).trim();
                        programacaoExp.mesInicial = cursor.getString(cursor.getColumnIndex("mesInicialFormatado")).trim();
                        programacaoExp.diaInicial = cursor.getString(cursor.getColumnIndex("diaInicialFormatado")).trim();
                        programacaoExp.anoFinal = cursor.getString(cursor.getColumnIndex("Anof")).trim();
                        programacaoExp.mesFinal = cursor.getString(cursor.getColumnIndex("mesfInicialFormatado")).trim();
                        programacaoExp.diaInicial = cursor.getString(cursor.getColumnIndex("diafInicialFormatado")).trim();
                        programacaoExp.horarioInicio = cursor.getString(cursor.getColumnIndex("HoraInicio"));
                        programacaoExp.horarioFinal = cursor.getString(cursor.getColumnIndex("HoraFinal"));
                        programacaoExp.diaDaSemana = cursor.getString(cursor.getColumnIndex("DiaSemana"));
                        programacaoExp.categoria1 = cursor.getString(cursor.getColumnIndex("Categoria1"));
                        programacaoExp.categoria2 = cursor.getString(cursor.getColumnIndex("Categoria2"));
                        programacaoExp.categoria3 = cursor.getString(cursor.getColumnIndex("Categoria3"));
                        programacaoExp.categoria4 = cursor.getString(cursor.getColumnIndex("Categoria4"));
                        programacaoExp.categoria5 = cursor.getString(cursor.getColumnIndex("Categoria5"));
                        programacaoExp.categoria6 = cursor.getString(cursor.getColumnIndex("Categoria6"));
                        programacaoExp.categoria7 = cursor.getString(cursor.getColumnIndex("Categoria7"));
                        programacaoExp.categoria8 = cursor.getString(cursor.getColumnIndex("Categoria8"));
                        programacaoExp.categoria9 = cursor.getString(cursor.getColumnIndex("Categoria9"));
                        programacaoExp.categoria10 = cursor.getString(cursor.getColumnIndex("Categoria10"));
                        programacaoExp.categoria11 = cursor.getString(cursor.getColumnIndex("Categoria11"));
                        programacaoExp.categoria12 = cursor.getString(cursor.getColumnIndex("Categoria12"));
                        programacaoExp.categoria13 = cursor.getString(cursor.getColumnIndex("Categoria13"));
                        programacaoExp.categoria14 = cursor.getString(cursor.getColumnIndex("Categoria14"));
                        programacaoExp.categoria15 = cursor.getString(cursor.getColumnIndex("Categoria15"));
                        programacaoExp.categoria16 = cursor.getString(cursor.getColumnIndex("Categoria16"));
                        programacaoExp.categoria17 = cursor.getString(cursor.getColumnIndex("Categoria17"));
                        programacaoExp.categoria18 = cursor.getString(cursor.getColumnIndex("Categoria18"));
                        programacaoExp.categoria19 = cursor.getString(cursor.getColumnIndex("Categoria19"));
                        programacaoExp.categoria20 = cursor.getString(cursor.getColumnIndex("Categoria20"));
                        programacaoExp.categoria21 = cursor.getString(cursor.getColumnIndex("Categoria21"));
                        programacaoExp.categoria22 = cursor.getString(cursor.getColumnIndex("Categoria22"));
                        programacaoExp.categoria23 = cursor.getString(cursor.getColumnIndex("Categoria23"));
                        programacaoExp.categoria24 = cursor.getString(cursor.getColumnIndex("Categoria24"));
                        programacaoExp.conteudo = cursor.getString(cursor.getColumnIndex("Conteudo"));
                        listaProgramacao.add(programacaoExp);
                    } catch (SQLiteCantOpenDatabaseException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                        continue;
                    } catch (SQLiteReadOnlyDatabaseException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                        continue;
                    } catch (SQLiteDatabaseCorruptException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                        continue;
                    } catch (SQLiteDatabaseLockedException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                        continue;
                    } catch (NullPointerException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                        continue;
                    } catch (InvalidParameterException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                        continue;
                    } catch (Exception e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                        continue;
                    }

                }
                categorias();
            } else {
                LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não a programação válida para o horario");
                RegistrarLog.imprimirMsg("Log", " 90 Não a programação válida para o horario");
                listaProgramacao.clear();
                listaDeArquivos.add("semVideo");
                return;
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : programacoes()");
        }
    }

    private static void categorias() {
        if (null != listaProgramacao && !listaProgramacao.isEmpty()) {
            for (ProgramacaoExp p : listaProgramacao) {
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
        } else {
            return;
        }

        if (null == listaDeArquivos || listaDeArquivos.isEmpty()) {
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Não a videos nem comerciais válidos");
            RegistrarLog.imprimirMsg("Log", " 90 Não a videos nem comerciais válidos");
            listaDeArquivos.add("semVideo");
            listaProgramacao.clear();
            return;
        }

    }

    private static void codigoCategoria(String codigo, String horaInicialProgramacao, String horaFinalProgramacao) {
        if (arquivoBanco.exists()) {
            try {
                if (null != codigo && null != horaFinalProgramacao && null != horaInicialProgramacao && !codigo.equals("0")) {
                    SQLiteDatabase db = getDb();
                    String scriptCategoria = "SELECT * FROM Categoria WHERE Codigo = '" + codigo + "' AND date('now') >= dataInicio AND date('now') < dataFinal";
                    Cursor cursorCodigosCategoria = db.rawQuery(scriptCategoria, new String[]{});
                    if (cursorCodigosCategoria.getCount() > 0) {
                        while (cursorCodigosCategoria.moveToNext()) {
                            try {
                                String codigoCategoria = cursorCodigosCategoria.getString(cursorCodigosCategoria.getColumnIndex("Codigo"));
                                String tipoCategoria = cursorCodigosCategoria.getString(cursorCodigosCategoria.getColumnIndex("Tipo"));
                                if (tipoCategoria.equals("1")) {
                                    video(codigoCategoria, horaInicialProgramacao, horaFinalProgramacao);
                                } else if (tipoCategoria.equals("2")) {
                                    comercial(codigoCategoria, horaInicialProgramacao, horaFinalProgramacao);
                                }
                            } catch (SQLiteCantOpenDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteReadOnlyDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseCorruptException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseLockedException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (NullPointerException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (InvalidParameterException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (Exception e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            }
                        }
                        cursorCodigosCategoria.close();
                    }

                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : codigoCategoria()");
        }
    }

    private static void video(String codigoCategoria, String horaInicial, String horaFinal) {
        if (arquivoBanco.exists()) {
            try {
                String scriptVideo = "SELECT * FROM VIEW_CARREGAR_VIDEOS WHERE (Categoria1 = " + codigoCategoria + " OR Categoria2 = " + codigoCategoria + " OR Categoria3 = " + codigoCategoria + " )";
                SQLiteDatabase db = getDb();
                cursor = db.rawQuery(scriptVideo, new String[]{});
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        try {
                            String arquivo = StringUtils.nuloParaVazio(cursor.getString(cursor.getColumnIndex("Arquivo")));
                            String titulo = StringUtils.nuloParaVazio(cursor.getString(cursor.getColumnIndex("Titulo")));
                            String velocidade = StringUtils.nuloParaVazio(cursor.getString(cursor.getColumnIndex("Velocidade")));
                            String caminhoDoArquivoDeVideo = validarExistenciaDoVideo(arquivo);
                            if (null != caminhoDoArquivoDeVideo) {
                                listaDeArquivos.add("normal|" + horaInicial + "|" + horaFinal + "|" + caminhoDoArquivoDeVideo + "|0|0|" + caminhoDoArquivoDeVideo + "|" + titulo + "|" + codigoCategoria + "|" + velocidade + "|1");
                                return;
                            } else {
                                LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. O video " + arquivo + " não foi encontrado em nenhum diretório");
                            }

                        } catch (SQLiteCantOpenDatabaseException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteReadOnlyDatabaseException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteDatabaseCorruptException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteDatabaseLockedException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (NullPointerException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (InvalidParameterException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (Exception e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        }
                    }
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : video()");
        }
    }

    private static void comercial(String codigoCategoria, String horaInicial, String horaFinal) {
        if (arquivoBanco.exists()) {
            try {
                if (null != codigoCategoria && null != horaInicial && null != horaFinal) {
                    SQLiteDatabase db = getDb();
                    String scriptCategoria = "SELECT * FROM VIEW_CARREGAR_COMERCIAL WHERE (QtdePlayer is null OR QtdePlayer > Qtde) AND  Categoria = " + codigoCategoria;
                    Cursor cursorComercial = db.rawQuery(scriptCategoria, new String[]{});
                    if (cursorComercial.getCount() > 0) {
                        while (cursorComercial.moveToNext()) {
                            try {
                                String arquivo = cursorComercial.getString(cursorComercial.getColumnIndex("Arquivo"));
                                String diasAlternados = cursorComercial.getString(cursorComercial.getColumnIndex("DiasAlternados"));
                                String dataStr = cursorComercial.getString(cursorComercial.getColumnIndex("Data"));
                                String titulo = StringUtils.nuloParaVazio(cursorComercial.getString(cursorComercial.getColumnIndex("Titulo")));
                                String dependencia1 = StringUtils.nuloParaVazio(cursorComercial.getString(cursorComercial.getColumnIndex("Dependencia1")));
                                String dependencia2 = StringUtils.nuloParaVazio(cursorComercial.getString(cursorComercial.getColumnIndex("Dependencia2")));
                                String dependencia3 = StringUtils.nuloParaVazio(cursorComercial.getString(cursorComercial.getColumnIndex("Dependencia3")));
                                boolean comercialEvalido = validarDiasComercial(arquivo, titulo, dependencia1, dependencia2, dependencia3, horaInicial, horaFinal, codigoCategoria, dataStr, diasAlternados);
                                if (comercialEvalido) {
                                    break;
                                }

                            } catch (SQLiteCantOpenDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteReadOnlyDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseCorruptException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseLockedException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (NullPointerException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (InvalidParameterException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (Exception e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            }
                        }
                    }
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : comercial()");
        }
    }

    private static boolean validarDiasComercial(String arquivo, String titulo, String dependencia1, String dependencia2, String dependencia3, String horaInicial, String horaFinal, String codigoCategoria, String dataStr, String diasAlternados) {
        Date data = null;
        if (null != dataStr) {
            try {
                data = new SimpleDateFormat("yyyy-MM-dd").parse(dataStr);
            } catch (ParseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return false;
            }

            Calendar diaQueTocou = Calendar.getInstance();
            diaQueTocou.setTime(data);
            diaQueTocou.add(Calendar.DAY_OF_MONTH, 1);

            Calendar diaAtual = Calendar.getInstance();
            diaAtual.setTime(new Date());

            if (diasAlternados.equals("0")) {
                boolean comercialEDependenciasExistemNosDiretoriosEBanco = validarDependenciasComercial(arquivo, titulo, dependencia1, dependencia2, dependencia3, horaInicial, horaFinal, codigoCategoria);
                if (comercialEDependenciasExistemNosDiretoriosEBanco) {
                    return true;
                }
            } else {
                if (diaQueTocou.get(Calendar.DAY_OF_MONTH) == diaAtual.get(Calendar.DAY_OF_MONTH)) {
                    LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. O comercial " + arquivo + " não pode ser tocado devido ter sido cadastrado como dias alternados");
                    return false;
                } else {
                    boolean comercialEDependenciasExistemNosDiretoriosEBanco = validarDependenciasComercial(arquivo, titulo, dependencia1, dependencia2, dependencia3, horaInicial, horaFinal, codigoCategoria);
                    if (comercialEDependenciasExistemNosDiretoriosEBanco) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean validarDependenciasComercial(String arquivo, String titulo, String dependencia1, String dependencia2, String dependencia3, String horaInicial, String horaFinal, String codigoCategoria) {
        if (!dependencia1.trim().toLowerCase().contains("nenhuma")) {
            String caminhoDoArquivo = validarExistenciaDoVideo(dependencia1);
            if (caminhoDoArquivo == null) {
                LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A Dependência " + dependencia1 + " do arquivo " + arquivo + " não foi encontrado em nenhum diretório");
            } else {
                ComercialDependencia cd = validarDependenciaNoBanco(dependencia1);
                if (cd == null) {
                    LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A dependência " + dependencia1 + " do arquivo " + arquivo + " não foi encontrada no banco");
                } else {
                    listaDeArquivos.add("normal|" + horaInicial + "|" + horaFinal + "|" + caminhoDoArquivo + "|1|0|" + arquivo + "|" + cd.titulo + "|" + cd.categoria + "|0|2");
                }
            }
        }

        if (!dependencia2.trim().toLowerCase().contains("nenhuma")) {
            String caminhoDoArquivo = validarExistenciaDoVideo(dependencia2);
            if (caminhoDoArquivo == null) {
                LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A Dependência " + dependencia2 + " do arquivo " + arquivo + " não foi encontrado em nenhum diretório");
            } else {
                ComercialDependencia cd = validarDependenciaNoBanco(dependencia2);
                if (cd == null) {
                    LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A dependência " + dependencia2 + " do arquivo " + arquivo + " não foi encontrada no banco");
                } else {
                    listaDeArquivos.add("normal|" + horaInicial + "|" + horaFinal + "|" + caminhoDoArquivo + "|1|0|" + arquivo + "|" + cd.titulo + "|" + cd.categoria + "|0|2");
                }
            }
        }

        if (!dependencia3.trim().toLowerCase().contains("nenhuma")) {
            String caminhoDoArquivo = validarExistenciaDoVideo(dependencia2);
            if (caminhoDoArquivo == null) {
                LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A Dependência " + dependencia3 + " do arquivo " + arquivo + " não foi encontrado em nenhum diretório");
            } else {
                ComercialDependencia cd = validarDependenciaNoBanco(dependencia3);
                if (cd == null) {
                    LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A dependência " + dependencia3 + " do arquivo " + arquivo + " não foi encontrada no banco");
                } else {
                    listaDeArquivos.add("normal|" + horaInicial + "|" + horaFinal + "|" + caminhoDoArquivo + "|1|0|" + arquivo + "|" + cd.titulo + "|" + cd.categoria + "|0|2");
                }
            }
        }

        String caminhoDoArquivo = validarExistenciaDoVideo(arquivo);
        if (caminhoDoArquivo == null) {
            LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. O arquivo " + arquivo + " não foi encontrado em nenhum diretório");
        } else {
            listaDeArquivos.add("normal|" + horaInicial + "|" + horaFinal + "|" + caminhoDoArquivo + "|0|0|" + arquivo + "|" + titulo + "|" + codigoCategoria + "|0|2");
            return true;
        }

        return false;
    }

    private static ComercialDependencia validarDependenciaNoBanco(String nome) {
        if (arquivoBanco.exists()) {
            try {
                if (null != nome && !nome.replaceAll("\\s", "").trim().isEmpty()) {
                    try {
                        SQLiteDatabase db = getDb();
                        String script = "SELECT * FROM VIEW_CARREGAR_COMERCIAL WHERE Arquivo = '" + nome + "'";
                        Cursor cursorComercialDependencia = db.rawQuery(script, new String[]{});
                        if (cursorComercialDependencia.moveToFirst()) {
                            String arquivo = StringUtils.nuloParaVazio(cursorComercialDependencia.getString(cursorComercialDependencia.getColumnIndex("Arquivo")));
                            String titulo = StringUtils.nuloParaVazio(cursorComercialDependencia.getString(cursorComercialDependencia.getColumnIndex("Titulo")));
                            String categoria = StringUtils.nuloParaVazio(cursorComercialDependencia.getString(cursorComercialDependencia.getColumnIndex("Categoria")));

                            ComercialDependencia cd = new ComercialDependencia();
                            cd.arquivo = arquivo;
                            cd.titulo = titulo;
                            cd.categoria = categoria;
                            return cd;
                        }
                    } catch (SQLiteCantOpenDatabaseException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    } catch (SQLiteReadOnlyDatabaseException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    } catch (SQLiteDatabaseCorruptException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    } catch (SQLiteDatabaseLockedException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    } catch (NullPointerException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    } catch (InvalidParameterException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    } catch (Exception e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    }
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : validarDependenciaNoBanco()");
        }
        return null;
    }

    public static void criarArquivoPlaylist() {
        File playlistAntiga = new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioPlaylist()).concat(barraDoSistema).concat("playlist.exp"));
        if (playlistAntiga.exists()) {
            playlistAntiga.delete();
        }

        File playlistNova = new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioPlaylist()).concat(barraDoSistema).concat("playlist.exp"));
        try {
            playlistNova.createNewFile();
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            listaDeArquivos.clear();
            listaProgramacao.clear();
            return;
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            listaDeArquivos.clear();
            listaProgramacao.clear();
            return;
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            listaDeArquivos.clear();
            listaProgramacao.clear();
            return;
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            listaDeArquivos.clear();
            listaProgramacao.clear();
            return;
        }


        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(playlistNova, true);
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            listaDeArquivos.clear();
            listaProgramacao.clear();
            return;
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            listaDeArquivos.clear();
            listaProgramacao.clear();
            return;
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            listaDeArquivos.clear();
            listaProgramacao.clear();
            return;
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            listaDeArquivos.clear();
            listaProgramacao.clear();
            return;
        }

        if (null != listaDeArquivos && !listaDeArquivos.isEmpty()) {
            for (String linha : listaDeArquivos) {
                try {
                    fileWriter.write(linha.concat("\n"));
                } catch (IOException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    continue;
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    continue;
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    continue;
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    continue;
                }
            }

        }

        listaDeArquivos.clear();
        listaProgramacao.clear();

        if (null != fileWriter) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return;
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return;
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return;
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return;
            }
        }
    }

    ///----------------- COMERCIAL DETERMINADO ----------///
    public static void comerciaisDeterminados() {
        if (arquivoBanco.exists()) {
            try {
                SQLiteDatabase db = getDb();
                String script = "SELECT * FROM VIEW_CARREGAR_COMERCIAL_DET WHERE (QtdePlayer is null OR QtdePlayer > Qtde)";
                Cursor cursorDet = db.rawQuery(script, new String[]{});
                cursorDet.moveToFirst();
                if (cursorDet.getCount() > 0) {
                    while (cursorDet.moveToNext()) {
                        try {
                            ComercialDet comercialDet = new ComercialDet();
                            comercialDet.arquivo = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Arquivo")));
                            comercialDet.cliente = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Cliente")));
                            comercialDet.titulo = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Titulo")));
                            comercialDet.categoria = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Categoria")));
                            comercialDet.dataInicial = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("PeriodoInicial")));
                            comercialDet.dataFinal = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("PeriodoFinal")));
                            comercialDet.horario1 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario1")));
                            comercialDet.horario2 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario2")));
                            comercialDet.horario3 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario3")));
                            comercialDet.horario4 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario4")));
                            comercialDet.horario5 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario5")));
                            comercialDet.horario6 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario6")));
                            comercialDet.horario7 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario7")));
                            comercialDet.horario8 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario8")));
                            comercialDet.horario9 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario9")));
                            comercialDet.horario10 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario10")));
                            comercialDet.horario11 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario11")));
                            comercialDet.horario12 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario12")));
                            comercialDet.horario13 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario13")));
                            comercialDet.horario14 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario14")));
                            comercialDet.horario15 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario15")));
                            comercialDet.horario16 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario16")));
                            comercialDet.horario17 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario17")));
                            comercialDet.horario18 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario18")));
                            comercialDet.horario19 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario19")));
                            comercialDet.horario20 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario20")));
                            comercialDet.horario21 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario21")));
                            comercialDet.horario22 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario22")));
                            comercialDet.horario23 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario23")));
                            comercialDet.horario24 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Horario24")));
                            comercialDet.semana1 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana1")));
                            comercialDet.semana2 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana2")));
                            comercialDet.semana3 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana2")));
                            comercialDet.semana4 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana4")));
                            comercialDet.semana5 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana5")));
                            comercialDet.semana6 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana6")));
                            comercialDet.semana7 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana7")));
                            comercialDet.semana8 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana8")));
                            comercialDet.semana9 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana9")));
                            comercialDet.semana10 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana10")));
                            comercialDet.semana11 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana11")));
                            comercialDet.semana12 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana12")));
                            comercialDet.semana13 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana13")));
                            comercialDet.semana14 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana14")));
                            comercialDet.semana15 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana15")));
                            comercialDet.semana16 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana16")));
                            comercialDet.semana17 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana17")));
                            comercialDet.semana18 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana18")));
                            comercialDet.semana19 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana19")));
                            comercialDet.semana20 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana20")));
                            comercialDet.semana21 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana21")));
                            comercialDet.semana22 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana22")));
                            comercialDet.semana23 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana23")));
                            comercialDet.semana24 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Semana24")));
                            comercialDet.diaSemana = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("DiaSemana")));
                            comercialDet.diasAlternados = (cursorDet.getString(cursorDet.getColumnIndex("DiasAlternados")) == "1") ? true : false;
                            comercialDet.data = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Data")));
                            comercialDet.ultimaExecucao = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("UltimaExecucao")));
                            comercialDet.tempoTotal = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("TempoTotal")));
                            comercialDet.random = cursorDet.getInt(cursorDet.getColumnIndex("Random"));
                            comercialDet.qtdePlayer = cursorDet.getInt(cursorDet.getColumnIndex("QtdePlayer"));
                            comercialDet.qtdePlayer = cursorDet.getInt(cursorDet.getColumnIndex("Qtde"));
                            comercialDet.dataVencimento = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("DataVencto")));
                            comercialDet.dependencia1 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Dependencia1")));
                            comercialDet.dependencia2 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Dependencia2")));
                            comercialDet.dependencia3 = StringUtils.nuloParaVazio(cursorDet.getString(cursorDet.getColumnIndex("Dependencia3")));
                            listaComercialDeterminados.add(comercialDet);
                        } catch (SQLiteCantOpenDatabaseException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteReadOnlyDatabaseException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteDatabaseCorruptException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteDatabaseLockedException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (NullPointerException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (InvalidParameterException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (Exception e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        }
                    }
                }
                cursorDet.close();
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : comerciaisDeterminados()");
        }
    }

    public static void controladorComercialDependencia() {
        if (null != listaComercialDeterminados && listaComercialDeterminados.size() > 0 && !listaComercialDeterminados.isEmpty()) {
            for (ComercialDet comercialDet : listaComercialDeterminados) {
                ComercialDet comercialDetComDepencias = dependenciaDeterminados(comercialDet);
                if (null != comercialDetComDepencias) {
                    semanaAndHorario(comercialDet.semana1, comercialDet.horario1, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana2, comercialDet.horario2, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana3, comercialDet.horario3, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana4, comercialDet.horario4, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana5, comercialDet.horario5, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana6, comercialDet.horario6, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana7, comercialDet.horario7, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana8, comercialDet.horario8, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana9, comercialDet.horario9, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana10, comercialDet.horario10, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana11, comercialDet.horario11, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana12, comercialDet.horario12, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana13, comercialDet.horario13, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana14, comercialDet.horario14, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana15, comercialDet.horario15, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana16, comercialDet.horario16, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana17, comercialDet.horario17, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana18, comercialDet.horario18, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana19, comercialDet.horario19, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana20, comercialDet.horario20, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana21, comercialDet.horario21, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana22, comercialDet.horario22, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana23, comercialDet.horario23, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                    semanaAndHorario(comercialDet.semana24, comercialDet.horario24, comercialDet.arquivo, comercialDet.titulo, comercialDet.categoria, comercialDet.listaDependencias, comercialDet.data);
                }
            }
        } else {
            linhasPlaylistDet.add("semVideo");
        }

    }

    private static ComercialDet dependenciaDeterminados(ComercialDet comercialDet) {
        String nomeComercial = comercialDet.arquivo;
        String dependencia1 = comercialDet.dependencia1;
        String dependencia2 = comercialDet.dependencia2;
        String dependencia3 = comercialDet.dependencia3;

        String arquivoComercialDetermidado = validarExistenciaDoVideo(nomeComercial);
        if (null != arquivoComercialDetermidado) {
            comercialDet.arquivo = arquivoComercialDetermidado;
            ///-----------------------DEPENDENCOA 1 ---------------------------------------------------------------///
            try {
                if (!dependencia1.toLowerCase().contains("nenhuma")) {
                    String resultadoDependencia1 = validarExistenciaDoVideo(dependencia1);
                    if (null != resultadoDependencia1) {
                        ComercialDependencia resultadoExisteNoBanco = validarDependenciaDeUmDeterminadoNoBanco(dependencia1);
                        if (null != resultadoExisteNoBanco) {
                            resultadoExisteNoBanco.arquivo = resultadoDependencia1;
                            comercialDet.listaDependencias.add(resultadoExisteNoBanco);
                        } else {
                            LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A Dependência " + dependencia1 + " do arquivo " + nomeComercial + " não foi encontrada no banco");
                        }
                    } else {
                        LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A Dependência " + dependencia1 + " do arquivo " + nomeComercial + " não foi encontrada em nenhum diretório");
                    }
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
            ///-----------------------DEPENDENCOA 2 ---------------------------------------------------------------///
            try {
                if (!dependencia2.toLowerCase().equals("nenhuma") && !dependencia2.toLowerCase().contains("nenhuma")) {
                    String resultadoDependencia2 = validarExistenciaDoVideo(dependencia2);
                    if (null != resultadoDependencia2) {
                        ComercialDependencia resultadoExisteNoBanco = validarDependenciaDeUmDeterminadoNoBanco(dependencia2);
                        if (null != resultadoExisteNoBanco) {
                            resultadoExisteNoBanco.arquivo = resultadoDependencia2;
                            comercialDet.listaDependencias.add(resultadoExisteNoBanco);
                        } else {
                            LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A Dependência " + dependencia2 + " do arquivo " + nomeComercial + " não foi encontrada no banco");
                        }
                    } else {
                        LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A Dependência " + dependencia2 + " do arquivo " + nomeComercial + " não foi encontrada em nenhum diretório");
                    }
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
            ///-----------------------DEPENDENCOA 3 ---------------------------------------------------------------///
            try {
                if (!dependencia3.toLowerCase().equals("nenhuma") && !dependencia3.toLowerCase().contains("nenhuma")) {
                    String resultadoDependencia3 = validarExistenciaDoVideo(dependencia3);
                    if (null != resultadoDependencia3) {
                        ComercialDependencia resultadoExisteNoBanco = validarDependenciaDeUmDeterminadoNoBanco(dependencia3);
                        if (null != resultadoExisteNoBanco) {
                            resultadoExisteNoBanco.arquivo = resultadoDependencia3;
                            comercialDet.listaDependencias.add(resultadoExisteNoBanco);
                        } else {
                            LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A Dependência " + dependencia3 + " do arquivo " + nomeComercial + " não foi encontrada no banco");
                        }
                    } else {
                        LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. A Dependência " + dependencia3 + " do arquivo " + nomeComercial + " não foi encontrada em nenhum diretório");
                    }
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(99, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 99 Desprezada com tipo 99. O comercial " + nomeComercial + " não foi encontrado em nenhum diretório");
            return null;
        }
        return comercialDet;
    }

    private static ComercialDependencia validarDependenciaDeUmDeterminadoNoBanco(String nome) {
        if (arquivoBanco.exists()) {
            try {
                if (null != nome && !nome.replaceAll("\\s", "").trim().isEmpty()) {
                    SQLiteDatabase db = helper.getWritableDatabase();
                    String sql = "SELECT * FROM VIEW_CARREGAR_COMERCIAL WHERE (QtdePlayer is null OR QtdePlayer > Qtde) AND Arquivo = '" + nome + "';";
                    Cursor cursorDependencia = db.rawQuery(sql, new String[]{});
                    if (cursorDependencia.getCount() > 0) {
                        ComercialDependencia comercialDependencia = new ComercialDependencia();
                        while (cursorDependencia.moveToNext()) {
                            try {
                                comercialDependencia.arquivo = StringUtils.nuloParaVazio(cursorDependencia.getString(cursorDependencia.getColumnIndex("Arquivo")));
                                comercialDependencia.cliente = StringUtils.nuloParaVazio(cursorDependencia.getString(cursorDependencia.getColumnIndex("Cliente")));
                                comercialDependencia.categoria = StringUtils.nuloParaVazio(cursorDependencia.getString(cursorDependencia.getColumnIndex("Categoria")));
                                comercialDependencia.data = StringUtils.nuloParaVazio(cursorDependencia.getString(cursorDependencia.getColumnIndex("Data")));
                                comercialDependencia.dataInicial = StringUtils.nuloParaVazio(cursorDependencia.getString(cursorDependencia.getColumnIndex("PeriodoInicial")));
                                comercialDependencia.dataFinal = StringUtils.nuloParaVazio(cursorDependencia.getString(cursorDependencia.getColumnIndex("PeriodoFinal")));
                                comercialDependencia.dataVencimento = StringUtils.nuloParaVazio(cursorDependencia.getString(cursorDependencia.getColumnIndex("DataVencto")));
                                comercialDependencia.titulo = StringUtils.nuloParaVazio(cursorDependencia.getString(cursorDependencia.getColumnIndex("Titulo")));
                            } catch (SQLiteCantOpenDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteReadOnlyDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseCorruptException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseLockedException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (NullPointerException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (InvalidParameterException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (Exception e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            }
                        }
                        cursorDependencia.close();
                        return comercialDependencia;
                    }
                    cursorDependencia.close();
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return null;
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return null;
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return null;
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : validarDependenciaDeUmDeterminadoNoBanco()");
        }
        return null;
    }

    private static void semanaAndHorario(String semana, String horario, String arquivo, String titulo, String categoria, List<ComercialDependencia> listaDependenciasComercialDeterminado, String dataUltimaExecucao) {
        if (!semana.equals("") && !horario.equals("")) {
            if (dataUltimaExecucao.isEmpty() || dataUltimaExecucao.replaceAll("\\s", "").trim().equals("")) {
                popularListaDeLinhasPlaylistDet(listaDependenciasComercialDeterminado, horario, semana, arquivo, titulo, categoria);
            } else {
                int diaDaUltimaExecucaoEmNumero = DataUtils.dataDaUltimaExecucaoParaNumero(dataUltimaExecucao);
                int diaAtualEmNumero = DataUtils.diaDaSemana();
                List<String> lista = new ArrayList<String>();

                //LINHA SEMANA DO EXP COMEÇA DE SEGUNDA

                try {
                    for (int i = 0; i <= (semana.length() - 1); i++) {
                        if (!StringUtils.verificarSeVazio("" + semana.charAt(i))) {
                            lista.add("" + (i + 1));
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return;
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return;
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return;
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return;
                }


                int posicaoNaListaDoDiaQueTocou = 8;
                int posicaoNaListaDoDiaAtual = 8;

                try {

                    for (String item : lista) {
                        if (Integer.parseInt(item) == diaDaUltimaExecucaoEmNumero) {
                            posicaoNaListaDoDiaQueTocou = lista.indexOf(item);
                        }

                        if (Integer.parseInt(item) == diaAtualEmNumero) {
                            posicaoNaListaDoDiaAtual = lista.indexOf(item);
                        }
                    }

                    if (posicaoNaListaDoDiaAtual != 8 && posicaoNaListaDoDiaQueTocou != 8) {
                        if (posicaoNaListaDoDiaQueTocou != (posicaoNaListaDoDiaAtual - 1)) {
                            popularListaDeLinhasPlaylistDet(listaDependenciasComercialDeterminado, horario, semana, arquivo, titulo, categoria);
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return;
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return;
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return;
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return;
                }
            }
        }
    }

    private static void popularListaDeLinhasPlaylistDet(List<ComercialDependencia> listaDependenciasComercialDeterminado, String horario, String semana, String arquivo, String titulo, String categoria) {
        try {
            if (("" + semana.charAt(DataUtils.diaDaSemana())).trim().toLowerCase().equals("n") || ("" + semana.charAt(DataUtils.diaDaSemana())).trim().toLowerCase().contains("n")) {
                semana = "0";
            } else {
                semana = "1";
            }
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
        }


        try {
            if (null != listaDependenciasComercialDeterminado && listaDependenciasComercialDeterminado.size() > 0 && !listaDependenciasComercialDeterminado.isEmpty()) {
                for (ComercialDependencia comercialDependencia : listaDependenciasComercialDeterminado) {
                    try {
                        String linha = "det|" + horario + "|" + semana + "|" + comercialDependencia.arquivo + "|1|0|" + arquivo + "|" + comercialDependencia.titulo + "|" + comercialDependencia.categoria + "|1|2";
                        linhasPlaylistDet.add(linha);
                    } catch (NullPointerException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                        continue;
                    } catch (InvalidParameterException e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                        continue;
                    } catch (Exception e) {
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                        AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                        continue;
                    }
                }
            }
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
        }

        try {
            String linha = "det|" + horario + "|" + semana + "|" + arquivo + "|0|1|" + arquivo + "|" + titulo + "|" + categoria + "|1|2";
            linhasPlaylistDet.add(linha);
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
        }
    }

    public static void criarPlaylistDeterminados() {
        File playlistAntiga = new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioPlaylist()).concat(barraDoSistema).concat("playlistDet.exp"));
        if (playlistAntiga.exists()) {
            playlistAntiga.delete();
        }

        File playlistDeterminados = new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioPlaylist()).concat(barraDoSistema).concat("playlistDet.exp"));

        try {
            playlistDeterminados.createNewFile();
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            linhasPlaylistDet.clear();
            listaComercialDeterminados.clear();
            return;
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            linhasPlaylistDet.clear();
            listaComercialDeterminados.clear();
            return;
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            linhasPlaylistDet.clear();
            listaComercialDeterminados.clear();
            return;
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            linhasPlaylistDet.clear();
            listaComercialDeterminados.clear();
            return;
        }


        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(playlistAntiga, true);
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            linhasPlaylistDet.clear();
            listaComercialDeterminados.clear();
            return;
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            linhasPlaylistDet.clear();
            listaComercialDeterminados.clear();
            return;
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            linhasPlaylistDet.clear();
            listaComercialDeterminados.clear();
            return;
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            linhasPlaylistDet.clear();
            listaComercialDeterminados.clear();
            return;
        }
        if (null != linhasPlaylistDet && !linhasPlaylistDet.isEmpty()) {
            for (String linha : linhasPlaylistDet) {
                try {
                    fileWriter.write(linha.concat("\n"));
                } catch (IOException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    continue;
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    continue;
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    continue;
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    continue;
                }
            }
        }

        linhasPlaylistDet.clear();
        listaComercialDeterminados.clear();

        if (null != fileWriter) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return;
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return;
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return;
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return;
            }
        }
    }

    //---------------------METODOS GENERICOS---------------------------//
    private static String validarExistenciaDoVideo(String nomeDoArquivo) {
        try {
            File video = null;
            if (new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo()).concat(barraDoSistema).concat(nomeDoArquivo)).exists()) {
                video = new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo()).concat(barraDoSistema).concat(nomeDoArquivo));
                return video.getAbsolutePath();
            } else if (new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo()).concat(barraDoSistema).concat(nomeDoArquivo)).exists()) {
                video = new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo()).concat(barraDoSistema).concat(nomeDoArquivo));
                return video.getAbsolutePath();
            } else if (new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo()).concat(barraDoSistema).concat(nomeDoArquivo)).exists()) {
                video = new File(caminho.concat(barraDoSistema).concat(ConfiguaracaoUtils.diretorio.getDiretorioVideo()).concat(barraDoSistema).concat(nomeDoArquivo));
                return video.getAbsolutePath();
            } else {
                return null;
            }
        } catch (InvalidParameterException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            return null;
        } catch (NullPointerException e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            return null;
        } catch (Exception e) {
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            return null;
        }
    }

    public static void excluirComercialDoBanco() {
        if (arquivoBanco.exists()) {
            try {
                SQLiteDatabase db = getDb();
                String script = "SELECT * FROM Comercial WHERE DataVencto <= date('now')";
                Cursor cursorExcluirComercialDoBanco = db.rawQuery(script, new String[]{});

                if (cursorExcluirComercialDoBanco.getCount() > 0) {
                    while (cursorExcluirComercialDoBanco.moveToNext()) {
                        try {
                            String arquivo = cursorExcluirComercialDoBanco.getString(cursorExcluirComercialDoBanco.getColumnIndex("Arquivo"));
                            String titulo = StringUtils.nuloParaVazio(cursorExcluirComercialDoBanco.getString(cursorExcluirComercialDoBanco.getColumnIndex("Titulo")));
                            String dataVencto = StringUtils.nuloParaVazio(cursorExcluirComercialDoBanco.getString(cursorExcluirComercialDoBanco.getColumnIndex("DataVencto")));
                            String resuldado = validarExistenciaDoVideo(arquivo);
                            if (resuldado != null) {
                                File file = new File(resuldado);
                                file.delete();
                                db.delete("Comercial", "Arquivo='" + arquivo + "'", null);
                                LogUtils.registrar(10, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 10 Excluindo comercial " + titulo + "-" + arquivo + "-" + dataVencto + "==" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));
                            }
                        } catch (SQLiteCantOpenDatabaseException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteReadOnlyDatabaseException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteDatabaseCorruptException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteDatabaseLockedException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (NullPointerException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (InvalidParameterException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (Exception e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        }
                    }
                }

            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : excluirComercialDoBanco()");
        }
    }

    public static void excluirVideosDoBanco() {
        if (arquivoBanco.exists()) {
            try {
                SQLiteDatabase db = getDb();
                String script = "SELECT * FROM Video WHERE DataVencto <= date('now')";
                Cursor cursorExcluirVideosDoBanco = db.rawQuery(script, new String[]{});
                if (cursorExcluirVideosDoBanco.getCount() > 0) {
                    while (cursorExcluirVideosDoBanco.moveToNext()) {
                        try {
                            String arquivo = cursorExcluirVideosDoBanco.getString(cursorExcluirVideosDoBanco.getColumnIndex("Arquivo"));
                            String titulo = StringUtils.nuloParaVazio(cursorExcluirVideosDoBanco.getString(cursorExcluirVideosDoBanco.getColumnIndex("Titulo")));
                            String dataVencto = StringUtils.nuloParaVazio(cursorExcluirVideosDoBanco.getString(cursorExcluirVideosDoBanco.getColumnIndex("DataVencto")));
                            String resuldado = validarExistenciaDoVideo(arquivo);

                            if (resuldado != null) {
                                File file = new File(resuldado);
                                file.delete();
                                db.delete("Video", "Arquivo='" + arquivo + "'", null);
                                LogUtils.registrar(10, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 10 Excluindo video " + titulo + "-" + arquivo + "-" + dataVencto + "==" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));
                            }
                        } catch (SQLiteCantOpenDatabaseException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteReadOnlyDatabaseException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteDatabaseCorruptException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (SQLiteDatabaseLockedException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (NullPointerException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (InvalidParameterException e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        } catch (Exception e) {
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                            AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                            continue;
                        }
                    }
                }
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : excluirVideosDoBanco()");
        }
    }

    //------- CRIAR AS VIEW ---////
    public static void criarViewProgramacao() {
        if (arquivoBanco.exists()) {
            try {
                SQLiteDatabase db = getDb();
                banco.criarViewProgramacao();
                db.execSQL(banco.scriptProgramacaoView);
            } catch (SQLiteCantOpenDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteReadOnlyDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteDatabaseCorruptException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteDatabaseLockedException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : criarViewProgramacao()");
        }
    }

    public static void criarViewComercial() {
        if (arquivoBanco.exists()) {
            try {
                SQLiteDatabase db = getDb();
                banco.criarViewComercial();
                db.execSQL(banco.scriptComercialView);
            } catch (SQLiteCantOpenDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteReadOnlyDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteDatabaseCorruptException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteDatabaseLockedException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : criarViewComercial()");
        }
    }

    public static void criarViewComercialDetermidos() {
        if (arquivoBanco.exists()) {
            try {
                SQLiteDatabase db = getDb();
                banco.criarViewComercialDet();
                db.execSQL(banco.scriptComercialDetView);
            } catch (SQLiteCantOpenDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteReadOnlyDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteDatabaseCorruptException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteDatabaseLockedException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : criarViewComercialDeterminado()");
        }
    }

    public static void criarViewVideo() {
        if (arquivoBanco.exists()) {
            try {
                SQLiteDatabase db = getDb();
                banco.criarViewVideo();
                db.execSQL(banco.scriptVideoView);
            } catch (SQLiteCantOpenDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteReadOnlyDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteDatabaseCorruptException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteDatabaseLockedException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : criarViewVideo()");
        }
    }

    //---- QUANTIDADE DE COMERCIAIS E VIDEOS NO BANCO ----------------------------------------------------------------------------------///
    public static String quantidadeComerciaisNoBanco() {
        String comerciaisNoBanco = "";
        if (arquivoBanco.exists()) {
            try {
                SQLiteDatabase db = getDb();
                Cursor cursorQuantidadeComerciaisNoBanco = db.rawQuery("SELECT Arquivo FROM Comercial", new String[]{});
                comerciaisNoBanco = String.valueOf(cursorQuantidadeComerciaisNoBanco.getCount());
            } catch (SQLiteCantOpenDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return comerciaisNoBanco;
            } catch (SQLiteReadOnlyDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return comerciaisNoBanco;
            } catch (SQLiteDatabaseCorruptException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return comerciaisNoBanco;
            } catch (SQLiteDatabaseLockedException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return comerciaisNoBanco;
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return comerciaisNoBanco;
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return comerciaisNoBanco;
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return comerciaisNoBanco;
            }
            return comerciaisNoBanco;
        } else {
            return comerciaisNoBanco;
        }
    }

    public static String quantidadeVideoNoBanco() {
        String videoNoBanco = "";
        if (arquivoBanco.exists()) {
            try {
                SQLiteDatabase db = getDb();
                Cursor cursorQuantidadeComerciaisNoBanco = db.rawQuery("SELECT Arquivo FROM Video", new String[]{});
                videoNoBanco = String.valueOf(cursorQuantidadeComerciaisNoBanco.getCount());
            } catch (SQLiteCantOpenDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return videoNoBanco;
            } catch (SQLiteReadOnlyDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return videoNoBanco;
            } catch (SQLiteDatabaseCorruptException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return videoNoBanco;
            } catch (SQLiteDatabaseLockedException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return videoNoBanco;
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return videoNoBanco;
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return videoNoBanco;
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                return videoNoBanco;
            }
            return videoNoBanco;
        } else {
            return videoNoBanco;
        }
    }

    ///---------------------------ATUALIZAR O BANCO DURANTE A REPRODUÇÃO ---------------------------------------------------------------///
    public void atualizarBanco(File video, int tempoDoVideo, String tipoCategoria) {
        if (arquivoBanco.exists()) {
            try {
                int quantidadePlayer = pegarQtdePlayer(video.getName().trim(), tipoCategoria);
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                String duracaoDoVideo = df.format(new Date(tempoDoVideo));
                String data = data();
                String ultimaExecucao = ultimaExecucao();
                validarRandom(tipoCategoria);

                SQLiteDatabase db = helper.getWritableDatabase();

                String sql = "";
                if (tipoCategoria.equals("1")) {
                    sql = "UPDATE Video SET Data ='" + data + "', UltimaExecucao = '" + ultimaExecucao + "', TempoTotal = '" + duracaoDoVideo + "', Random = " + valorRandom + ", QtdePlayer = " + quantidadePlayer + " WHERE Arquivo = '" + video.getName().trim() + "'";
                } else {
                    sql = "UPDATE Comercial SET Data ='" + data + "', UltimaExecucao = '" + ultimaExecucao + "', TempoTotal = '" + duracaoDoVideo + "', Random = " + valorRandom + ", QtdePlayer = " + quantidadePlayer + " WHERE Arquivo = '" + video.getName().trim() + "'";
                }
                db.execSQL(sql);
            } catch (SQLiteCantOpenDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteReadOnlyDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteDatabaseCorruptException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (SQLiteDatabaseLockedException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
            }
        } else {
            RegistrarLog.imprimirMsg("Log", "Banco não foi encontrado : atualizarBanco()");
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Banco não foi encontrado : atualizarBanco()");
        }
    }

    private int pegarQtdePlayer(String arquivo, String tipoVideo) {
        int quantidade = 1;
        if (arquivoBanco.exists()) {
            try {
                SQLiteDatabase db = helper.getWritableDatabase();
                String sql = "";

                if (tipoVideo.equals("1")) {
                    sql = "Select * from Video where Arquivo = '" + arquivo + "' LIMIT 1;";
                } else {
                    sql = "Select * from Comercial where Arquivo = '" + arquivo + "' LIMIT 1;";
                }

                cursor = db.rawQuery(sql, new String[]{});
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if (null == cursor.getString(cursor.getColumnIndex("QtdePlayer"))) {
                        quantidade = 1;
                    } else {
                        quantidade = cursor.getInt(cursor.getColumnIndex("QtdePlayer")) + 1;
                    }
                }
            } catch (SQLiteCantOpenDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return quantidade;
            } catch (SQLiteReadOnlyDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return quantidade;
            } catch (SQLiteDatabaseCorruptException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return quantidade;
            } catch (SQLiteDatabaseLockedException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return quantidade;
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return quantidade;
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return quantidade;
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                return quantidade;
            }
        } else {
            RegistrarLog.imprimirMsg("Log", "Banco não foi encontrado : pegarQtdePlayer()");
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Banco não foi encontrado : pegarQtdePlayer()");
        }
        return quantidade;
    }

    private String ultimaExecucao() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private String data() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private void validarRandom(String tipoCategoria) {
        Random random = new Random();
        int valor = random.nextInt(9999);

        if (arquivoBanco.exists()) {
            try {
                SQLiteDatabase db = helper.getWritableDatabase();
                String sql = "";
                if (tipoCategoria.equals("1")) {
                    sql = "SELECT Arquivo FROM Video where Random = " + valor + " LIMIT 1";

                } else {
                    sql = "SELECT Arquivo FROM Comercial where Random = " + valor + " LIMIT 1";
                }

                cursor = db.rawQuery(sql, new String[]{});

                while ((cursor.getCount() > 0)) {
                    LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 70 Ja existem o random " + valor);
                    validarRandom(tipoCategoria);
                    return;
                }
                valorRandom = valor;
            } catch (SQLiteCantOpenDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                valorRandom = valor;
            } catch (SQLiteReadOnlyDatabaseException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                valorRandom = valor;
            } catch (SQLiteDatabaseCorruptException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                valorRandom = valor;
            } catch (SQLiteDatabaseLockedException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                valorRandom = valor;
            } catch (NullPointerException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                valorRandom = valor;
            } catch (InvalidParameterException e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                valorRandom = valor;
            } catch (Exception e) {
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                valorRandom = valor;
            }
        } else {
            RegistrarLog.imprimirMsg("Log", "Banco não foi encontrado : validarRandom()");
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Banco não foi encontrado : validarRandom()");
            valorRandom = valor;
        }
    }

    //---------------------------POPULAR O BANCO APOS COMUNICACAO ---------------------------------------------------------------------//
    public static boolean insertCategoria(String caminho) {
        if (arquivoBanco.exists()) {
            db = helper.getWritableDatabase();
            db.beginTransaction();
            if (null != caminho && !caminho.trim().replaceAll("\\s", "").isEmpty()) {
                try {
                    List<CategoriaExp> listaCategoria = expUtils.lerCategoria(caminho);
                    if (null != listaCategoria && listaCategoria.size() > 0) {
                        for (CategoriaExp c : listaCategoria) {
                            try {
                                ContentValues values = new ContentValues();
                                values.put("Codigo", c.codigo);
                                values.put("Categoria", c.categoria.trim());
                                values.put("DataInicio", c.dataInicial);
                                values.put("DataFinal", c.dataFinal);
                                values.put("Tipo", c.tipo);
                                values.put("Tempo", c.tempo);
                                db.replace("Categoria", null, values);
                            } catch (SQLiteCantOpenDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteReadOnlyDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseCorruptException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseLockedException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (NullPointerException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (InvalidParameterException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (Exception e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            }
                        }
                        db.setTransactionSuccessful();
                    } else {
                        return false;
                    }
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } finally {
                    db.endTransaction();
                }
                return true;

            } else {
                return false;
            }
        } else {
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Banco não foi encontrado : insertCategoria()");
            return false;
        }
    }

    public static boolean insertComercial(String caminho) {
        if (arquivoBanco.exists()) {
            db = helper.getWritableDatabase();
            db.beginTransaction();
            if (null != caminho && !caminho.trim().replaceAll("\\s", "").isEmpty()) {
                try {
                    List<ComercialExp> listaComercial = expUtils.lerComercial(caminho);
                    if (null != listaComercial && listaComercial.size() > 0) {
                        for (ComercialExp c : listaComercial) {
                            try {
                                ContentValues values = new ContentValues();
                                values.put("Arquivo", c.arquivo.trim());
                                values.put("Cliente", c.cliente.trim());
                                values.put("Titulo", c.titulo.trim());
                                values.put("TipoInterprete", c.tipoInterprete);
                                values.put("Categoria", c.categoria);
                                values.put("PeriodoInicial", c.dataInicial);
                                values.put("PeriodoFinal", c.dataFinal);
                                values.put("Semana1", c.semana1);
                                values.put("Horario1", c.horario1);
                                values.put("Semana2", c.semana2);
                                values.put("Horario2", c.horario2);
                                values.put("Semana3", c.semana3);
                                values.put("Horario3", c.horario3);
                                values.put("Semana4", c.semana4);
                                values.put("Horario4", c.horario4);
                                values.put("Semana5", c.semana5);
                                values.put("Horario5", c.horario5);
                                values.put("Semana6", c.semana6);
                                values.put("Horario6", c.horario6);
                                values.put("Semana7", c.semana7);
                                values.put("Horario7", c.horario7);
                                values.put("Semana8", c.semana8);
                                values.put("Horario8", c.horario8);
                                values.put("Semana9", c.semana9);
                                values.put("Horario9", c.horario9);
                                values.put("Semana10", c.semana10);
                                values.put("Horario10", c.horario10);
                                values.put("Semana11", c.semana11);
                                values.put("Horario11", c.horario11);
                                values.put("Semana12", c.semana12);
                                values.put("Horario12", c.horario12);
                                values.put("Semana13", c.semana13);
                                values.put("Horario13", c.horario13);
                                values.put("Semana14", c.semana14);
                                values.put("Horario14", c.horario14);
                                values.put("Semana15", c.semana15);
                                values.put("Horario15", c.horario15);
                                values.put("Semana16", c.semana16);
                                values.put("Horario16", c.horario16);
                                values.put("Semana17", c.semana17);
                                values.put("Horario17", c.horario17);
                                values.put("Semana18", c.semana18);
                                values.put("Horario18", c.horario18);
                                values.put("Semana19", c.semana19);
                                values.put("Horario19", c.horario19);
                                values.put("Semana20", c.semana20);
                                values.put("Horario20", c.horario20);
                                values.put("Semana21", c.semana21);
                                values.put("Horario21", c.horario21);
                                values.put("Semana22", c.semana22);
                                values.put("Horario22", c.horario22);
                                values.put("Semana23", c.semana23);
                                values.put("Horario23", c.horario23);
                                values.put("Semana24", c.semana24);
                                values.put("Horario24", c.horario24);
                                values.put("DiaSemana", c.diaSemana);
                                values.put("DiasAlternados", c.diasAlternados);
                                values.put("Data", c.data);
                                values.put("UltimaExecucao", c.ultimaExecucao);
                                values.put("TempoTotal", c.tempoTotal);
                                values.put("Qtde", c.qtdePlayer);
                                values.put("DataVencto", c.dataVencimento);
                                values.put("Dependencia1", c.dependencia1);
                                values.put("Dependencia2", c.dependencia2);
                                values.put("Dependencia3", c.dependencia3);

                                db.replace("Comercial", null, values);

                            } catch (SQLiteCantOpenDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteReadOnlyDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseCorruptException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseLockedException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (NullPointerException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (InvalidParameterException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (Exception e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            }
                        }
                        db.setTransactionSuccessful();
                    } else {
                        return false;
                    }
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } finally {
                    db.endTransaction();
                }
                return true;
            } else {
                return false;
            }
        } else {
            RegistrarLog.imprimirMsg("Log", "Banco não foi encontrado : insertComercial()");
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Banco não foi encontrado : insertComercial()");
            return false;
        }
    }

    public static boolean insertProgramacao(String caminho) {
        if (arquivoBanco.exists()) {
            db = helper.getWritableDatabase();
            db.beginTransaction();
            if (null != caminho && !caminho.trim().replaceAll("\\s", "").isEmpty()) {
                try {
                    List<ProgramacaoExp> listaProgramacoes = expUtils.lerProgramacao(caminho);
                    if (null != listaProgramacoes && listaProgramacoes.size() > 0) {
                        for (ProgramacaoExp p : listaProgramacoes) {
                            try {
                                ContentValues values = new ContentValues();
                                values.put("Descricao", p.descricao.trim());
                                values.put("Dia", p.diaInicial);
                                values.put("Mes", p.mesInicial);
                                values.put("Ano", p.anoInicial);
                                values.put("Diaf", p.diaFinal);
                                values.put("Mesf", p.mesFinal);
                                values.put("Anof", p.anoFinal);
                                values.put("DiaSemana", p.diaDaSemana);
                                values.put("HoraInicio", p.horarioInicio);
                                values.put("HoraFinal", p.horarioFinal);
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

                            } catch (SQLiteCantOpenDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteReadOnlyDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseCorruptException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseLockedException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (NullPointerException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (InvalidParameterException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (Exception e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            }
                        }
                        db.setTransactionSuccessful();
                    } else {
                        return false;
                    }
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } finally {
                    db.endTransaction();
                }
                return true;
            } else {
                return false;
            }
        } else {
            RegistrarLog.imprimirMsg("Log", "Banco não foi encontrado : insertProgramacao()");
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Banco não foi encontrado : insertProgramacao()");
            return false;
        }
    }

    public static boolean insertVideo(String caminho) {
        if (arquivoBanco.exists()) {
            db = helper.getWritableDatabase();
            db.beginTransaction();
            if (null != caminho && !caminho.trim().replaceAll("\\s", "").isEmpty()) {
                try {
                    List<VideoExp> listaVideos = expUtils.lerVideo(caminho);
                    if (null != listaVideos && listaVideos.size() > 0) {
                        for (VideoExp v : listaVideos) {
                            try {
                                ContentValues values = new ContentValues();
                                values.put("Arquivo", v.arquivo.trim());
                                values.put("Interprete", v.interprete.trim());
                                values.put("TipoInterprete", v.tipoInterprete);
                                values.put("Titulo", v.titulo.trim());
                                values.put("Categoria1", v.categoria1);
                                values.put("Categoria2", v.categoria2);
                                values.put("Categoria3", v.categoria3);
                                values.put("Crossover", (v.crossover == true) ? 1 : 0);
                                values.put("DataVenctoCrossOver", v.dataVencimentoCrossover);
                                values.put("DiasExecucao", v.diasExecucao1);
                                values.put("DiasExecucao2", v.diasExecucao2);
                                values.put("Afinidade1", v.afinadade1.trim());
                                values.put("Afinidade2", v.afinadade2.trim());
                                values.put("Afinidade3", v.afinadade3.trim());
                                values.put("Afinidade4", v.afinadade4.trim());
                                values.put("Gravadora", v.gravadora);
                                values.put("AnoGravacao", v.anoGravacao);
                                values.put("Velocidade", v.velocidade);
                                values.put("Data", v.data);
                                values.put("UltimaExecucaoData", v.ultimaExecucaoData);
                                values.put("TempoTotal", v.tempoTotal);
                                values.put("Qtde", v.quantidadePlayerTotal);
                                values.put("DataVencto", v.dataVencimento);
                                values.put("FrameInicio", v.frameInicio);
                                values.put("FrameFinal", v.frameFinal);
                                values.put("Msg", v.mensagem);
                                db.replace("Video", null, values);

                            } catch (SQLiteCantOpenDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteReadOnlyDatabaseException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseCorruptException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (SQLiteDatabaseLockedException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (NullPointerException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (InvalidParameterException e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            } catch (Exception e) {
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                                AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                                continue;
                            }
                        }
                        db.setTransactionSuccessful();
                    } else {
                        return false;
                    }
                } catch (NullPointerException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } catch (InvalidParameterException e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } catch (Exception e) {
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e);
                    AndroidImprimirUtils.imprimirErro(BancoDAO.class, e, 90);
                    return false;
                } finally {
                    db.endTransaction();
                }
                return true;
            } else {
                return false;
            }
        } else {
            RegistrarLog.imprimirMsg("Log", "Banco não foi encontrado : insertVideo()");
            LogUtils.registrar(90, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 Banco não foi encontrado : insertVideo()");
            return false;
        }
    }
}

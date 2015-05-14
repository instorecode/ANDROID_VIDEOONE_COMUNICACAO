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

import com.br.instore.exp.bean.CategoriaExp;
import com.br.instore.exp.bean.ComercialExp;
import com.br.instore.exp.bean.ProgramacaoExp;
import com.br.instore.utils.Banco;
import com.br.instore.utils.ExpUtils;
import com.utils.RegistrarLog;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.List;

public class BancoDAO {

    private ExpUtils expUtils = new ExpUtils();
    private Cursor cursor;
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public BancoDAO(Context context) {
        this.helper = new DatabaseHelper(context);
    }

    public SQLiteDatabase getDb() {
        File banco = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/videoOne/").concat("videoOneDs.db-journal"));
        if (banco.exists()) {
            banco.delete();
            RegistrarLog.imprimirMsg("Log", "BANCO EXCLUIDO");
        }
        if (null == db) {
            db = helper.getReadableDatabase();
        }
        return db;
    }

    public void close() {
        helper.close();
    }

    public String quantidadeComerciaisNoBanco() {
        SQLiteDatabase db = helper.getWritableDatabase();
        cursor = db.rawQuery("SELECT Arquivo FROM Comercial", new String[]{});
        String comerciaisNoBanco = "";
        comerciaisNoBanco = String.valueOf(cursor.getCount());
        return comerciaisNoBanco;
    }

    public String quantidadeVideoNoBanco() {
        SQLiteDatabase db = getDb();
        cursor = db.rawQuery("SELECT Arquivo FROM Video", new String[]{});
        String videoNoBanco = "";
        videoNoBanco = String.valueOf(cursor.getCount());
        return videoNoBanco;
    }

    private void insertCategoria(String caminho) {
        RegistrarLog.imprimirMsg("Log", "Insert Categoria");
        SQLiteDatabase db = getDb();
        if (null != caminho && !caminho.trim().replaceAll("\\s", "").isEmpty()) {
            try {
                List<CategoriaExp> listaCategoria = expUtils.lerCategoria(caminho);
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
                        RegistrarLog.imprimirMsg("Log", "Banco não pode ser aberto, não foi possivel atualizar a tabela Categoria");
                        continue;
                    } catch (SQLiteReadOnlyDatabaseException e) {
                        RegistrarLog.imprimirMsg("Log", "Banco só pode ser lido, não foi possivel atualizar a tabela Categoria");
                        continue;
                    } catch (SQLiteDatabaseCorruptException e) {
                        RegistrarLog.imprimirMsg("Log", "Banco está corrompido, não foi possivel atualizar a tabela Categoria");
                        continue;
                    } catch (SQLiteDatabaseLockedException e) {
                        RegistrarLog.imprimirMsg("Log", "Banco está bloqueado, não foi possivel atualizar a tabela Categoria");
                        continue;
                    } catch (NullPointerException e) {
                        RegistrarLog.imprimirMsg("Log", "Null Point, não foi possivel atualizar a tabela Categoria");
                        continue;
                    } catch (InvalidParameterException e) {
                        RegistrarLog.imprimirMsg("Log", "Paremtro invalido, não foi possivel atualizar a tabela Categoria");
                        continue;
                    } catch (Exception e) {
                        RegistrarLog.imprimirMsg("Log", "Erro, não foi possivel atualizar a tabela Categoria");
                        continue;
                    }
                }
                db.setTransactionSuccessful();
            } catch (NullPointerException e) {
                RegistrarLog.imprimirMsg("Log", "1 " + e.getMessage());
                return;
            } catch (InvalidParameterException e) {
                RegistrarLog.imprimirMsg("Log", "2 " + e.getMessage());
                return;
            } catch (Exception e) {
                RegistrarLog.imprimirMsg("Log", "3 " + e.getMessage());
                return;
            } finally {
                db.endTransaction();
            }
        }
        RegistrarLog.imprimirMsg("Log", "Fim Insert Categoria");
    }

    private void insertComercial(String caminho) {
        RegistrarLog.imprimirMsg("Log", "Insert Comercial");
        SQLiteDatabase db = getDb();

        if (null != caminho && !caminho.trim().replaceAll("\\s", "").isEmpty()) {
            try {
                List<ComercialExp> listaComercial = expUtils.lerComercial(caminho);
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
                    } catch (SQLiteCantOpenDatabaseException e) {
                        RegistrarLog.imprimirMsg("Log", "Banco não pode ser aberto, não foi possivel atualizar a tabela Comercial");
                        continue;
                    } catch (SQLiteReadOnlyDatabaseException e) {
                        RegistrarLog.imprimirMsg("Log", "Banco só pode ser lido, não foi possivel atualizar a tabela Comercial");
                        continue;
                    } catch (SQLiteDatabaseCorruptException e) {
                        RegistrarLog.imprimirMsg("Log", "Banco está corrompido, não foi possivel atualizar a tabela Comercial");
                        continue;
                    } catch (SQLiteDatabaseLockedException e) {
                        RegistrarLog.imprimirMsg("Log", "Banco está bloqueado, não foi possivel atualizar a tabela Comercial");
                        continue;
                    } catch (NullPointerException e) {
                        RegistrarLog.imprimirMsg("Log", "Null Point, não foi possivel atualizar a tabela Comercial");
                        continue;
                    } catch (InvalidParameterException e) {
                        RegistrarLog.imprimirMsg("Log", "Paremtro invalido, não foi possivel atualizar a tabela Comercial");
                        continue;
                    } catch (Exception e) {
                        RegistrarLog.imprimirMsg("Log", "Erro, não foi possivel atualizar a tabela Comercial");
                        continue;
                    }
                }
                db.setTransactionSuccessful();
            } catch (NullPointerException e) {
                RegistrarLog.imprimirMsg("Log", "1 " + e.getMessage());
                return;
            } catch (InvalidParameterException e) {
                RegistrarLog.imprimirMsg("Log", "2 " + e.getMessage());
                return;
            } catch (Exception e) {
                RegistrarLog.imprimirMsg("Log", "3 " + e.getMessage());
                return;
            } finally {
                db.endTransaction();
            }
        }
        RegistrarLog.imprimirMsg("Log", "Fim Insert Comercial");
    }

    private void insertProgramacao(String caminho) {
        RegistrarLog.imprimirMsg("Log", "Insert Programacao");
        SQLiteDatabase db = getDb();
        if (null != caminho && !caminho.trim().replaceAll("\\s", "").isEmpty()) {
            try {
                List<ProgramacaoExp> listaProgramacoes = expUtils.lerProgramacao(caminho);
                for(ProgramacaoExp p : listaProgramacoes){
                    try {
                        ContentValues values = new ContentValues();
                        values.put("Descricao", p.descricao.trim());
                        values.put("Dia", p.dataInicial.split("-")[0]);
                        values.put("Mes", p.dataInicial.split("-")[1]);
                        values.put("Ano", p.dataInicial.split("-")[2]);
                        values.put("Diaf", p.dataFinal.split("-")[0]);
                        values.put("Mesf", p.dataFinal.split("-")[1]);
                        values.put("Anof", p.dataFinal.split("-")[2]);
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
                        RegistrarLog.imprimirMsg("Log", "Banco não pode ser aberto, não foi possivel atualizar a tabela Programacao");
                        continue;
                    } catch (SQLiteReadOnlyDatabaseException e) {
                        RegistrarLog.imprimirMsg("Log", "Banco só pode ser lido, não foi possivel atualizar a tabela Programacao");
                        continue;
                    } catch (SQLiteDatabaseCorruptException e) {
                        RegistrarLog.imprimirMsg("Log", "Banco está corrompido, não foi possivel atualizar a tabela Programacao");
                        continue;
                    } catch (SQLiteDatabaseLockedException e) {
                        RegistrarLog.imprimirMsg("Log", "Banco está bloqueado, não foi possivel atualizar a tabela Programacao");
                        continue;
                    } catch (NullPointerException e) {
                        RegistrarLog.imprimirMsg("Log", "Null Point, não foi possivel atualizar a tabela Programacao");
                        continue;
                    } catch (InvalidParameterException e) {
                        RegistrarLog.imprimirMsg("Log", "Paremtro invalido, não foi possivel atualizar a tabela Programacao");
                        continue;
                    } catch (Exception e) {
                        RegistrarLog.imprimirMsg("Log", "Erro, não foi possivel atualizar a tabela Programacao");
                        continue;
                    }
                }
            } catch (NullPointerException e) {
                RegistrarLog.imprimirMsg("Log", "1 " + e.getMessage());
                return;
            } catch (InvalidParameterException e) {
                RegistrarLog.imprimirMsg("Log", "2 " + e.getMessage());
                return;
            } catch (Exception e) {
                RegistrarLog.imprimirMsg("Log", "3 " + e.getMessage());
                return;
            } finally {
                db.endTransaction();
            }
        }
        RegistrarLog.imprimirMsg("Log", "Fim Insert Programacao");
    }
}

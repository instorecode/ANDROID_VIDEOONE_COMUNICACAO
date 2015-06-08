package com.tarefas;

import android.os.Environment;
import com.banco.BancoDAO;
import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.LogUtils;
import com.utils.RegistrarLog;

import java.io.File;

public class TaskBanco implements Runnable {

    private final File arquivoBanco = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/videoOne/").concat("videoOneDs.db"));

    @Override
    public void run() {
        RegistrarLog.imprimirMsg("Log", "INICIO TaskBanco");
        if(arquivoBanco.exists()) {
            BancoDAO.criarViewComercial();
            BancoDAO.criarViewComercialDetermidos();
            BancoDAO.criarViewProgramacao();
            BancoDAO.criarViewVideo();
            BancoDAO.excluirComercialDoBanco();
            BancoDAO.excluirVideosDoBanco();
            BancoDAO.programacoes();
            BancoDAO.criarArquivoPlaylist();
            BancoDAO.comerciaisDeterminados();
            BancoDAO.controladorComercialDependencia();
            BancoDAO.criarPlaylistDeterminados();
            BancoDAO.close();
        } else {
            RegistrarLog.imprimirMsg("Log", "Banco não foi encontrado : TaskVideoAndComerciais");
            LogUtils.registrar(21, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 21 Banco não foi encontrado : TaskVideoAndComerciais");
        }
        RegistrarLog.imprimirMsg("Log", "FIM TaskBanco");
    }
}

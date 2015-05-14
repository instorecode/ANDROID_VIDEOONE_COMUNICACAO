package com.comunicacao;


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
                    RegistrarLog.imprimirMsg("O horario é valido");
                    if (!hashId.equals(validarHoraAndDia.hashId())) {
                        RegistrarLog.imprimirMsg("hash é diferente do hash anterior");
                        tentativasRealizadas = 0;
                    }

                    System.out.println("é um dia valido, Maximo tentativas = " + maximoTentativas);
                    System.out.println("é um dia valido, Tentativas realizadas " + tentativasRealizadas);

                    if (tentativasRealizadas < maximoTentativas) {
                        RegistrarLog.imprimirMsg(" Rodando Thread ");
                        logUtils.registrar(" Rodando Thread");
                        conectarEnderecoFtp();
                        moverVideos();
                        hashId = validarHoraAndDia.hashId();
                    }

                } else {
                    // executar o emergencia
                    System.out.println("Não é um dia valido, Maximo tentativas = " + maximoTentativas);
                    System.out.println("Não é um dia valido, Tentativas realizadas " + tentativasRealizadas);
                    RegistrarLog.imprimirMsg(" Não é válido o dia");
                    tentativasRealizadas = 0;
                }

            } else {
                logUtils.registrar(" Arquivo de configurações foi excluido");
                RegistrarLog.imprimirMsg("ERRO::", "Arquivo de configurações foi excluido");
            }
        }
    }


*/
}

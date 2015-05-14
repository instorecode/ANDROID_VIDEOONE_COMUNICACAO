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

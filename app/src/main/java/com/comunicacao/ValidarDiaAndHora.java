package com.comunicacao;

import com.utils.AndroidImprimirUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidarDiaAndHora {

    private Date horarioInicial;
    private Date horarioFinal;
    private Date horarioAtual = new Date();
    private String diaStr;
    private boolean valid = false;
    private File file;
    private Dias diaAtual;
    private enum Dias {
        SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO, DOMINGO
    }

    public ValidarDiaAndHora(String path) throws Exception{
        if(null != path && !path.replaceAll("\\s","").trim().isEmpty()) {
            this.file = new File(path);
        } else {
            throw new Exception("Caminho do properties está errado");
        }
    }

    private class Horario {
        private Date horarioInicial;
        private Date horarioFinal;

        public Date getHorarioInicial() {
            return horarioInicial;
        }

        public void setHorarioInicial(Date horarioInicial) {
            this.horarioInicial = horarioInicial;
        }

        public Date getHorarioFinal() {
            return horarioFinal;
        }

        public void setHorarioFinal(Date horarioFinal) {
            this.horarioFinal = horarioFinal;
        }
    }

    private List<Horario> procurarHorarioValidoInterno() {
        List<Horario> ret = new ArrayList<Horario>();

        List<String> list = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while (null != (line = br.readLine())) {
                Pattern pattern = Pattern.compile("^conexao[0-9]+=");
                Matcher m = pattern.matcher(line);
                if (m.find()) {
                    line = line.replaceAll("conexao[0-9]+=", "");
                    line = line.replaceAll("\\\\", "");
                    list.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
            AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
        } catch (IOException e) {
            AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
            AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
        } catch (NullPointerException e){
            AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
            AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
        } catch (InvalidParameterException e){
            AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
            AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
        } catch (Exception e){
            AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
            AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
        }

        horarioInicial = null;
        horarioFinal = null;
        horarioAtual = new Date();
        setValid(false);

        Calendar c = Calendar.getInstance();
        c.setTime(horarioAtual);
        int iday = c.get(Calendar.DAY_OF_WEEK);

        switch (iday) {
            case 1:
                diaAtual = Dias.DOMINGO;
                diaStr = "7";
                break;
            case 2:
                diaAtual = Dias.SEGUNDA;
                diaStr = "1";
                break;
            case 3:
                diaAtual = Dias.TERCA;
                diaStr = "2";
                break;
            case 4:
                diaAtual = Dias.QUARTA;
                diaStr = "3";
                break;
            case 5:
                diaAtual = Dias.QUINTA;
                diaStr = "4";
                break;
            case 6:
                diaAtual = Dias.SEXTA;
                diaStr = "5";
                break;
            case 7:
                diaAtual = Dias.SABADO;
                diaStr = "6";
                break;
        }

        for (String value : list) {

            String h1str = value.substring(0, 2);
            String m1str = value.substring(3, 5);

            String h2str = value.substring(6, 8);
            String m2str = value.substring(9, 11);

            try {
                horarioInicial = new SimpleDateFormat("ddMMyyyyHHmmss").parse(new SimpleDateFormat("ddMMyyyy").format(horarioAtual).concat(h1str.concat(m1str).concat("00")));
            } catch (ParseException e) {
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
            }

            try {
                horarioFinal = new SimpleDateFormat("ddMMyyyyHHmmss").parse(new SimpleDateFormat("ddMMyyyy").format(horarioAtual).concat(h2str.concat(m2str).concat("00")));
            } catch (ParseException e) {
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
            }

            if (value.substring(12, value.length()).contains(diaStr)) {
                Horario h = new Horario();
                h.setHorarioInicial(horarioInicial);
                h.setHorarioFinal(horarioFinal);
                ret.add(h);
                setValid(true);
            }
        }

       /* if (!isValid()) {
            horarioInicial = null;
            horarioFinal = null;
        }*/

        horarioInicial = null;
        horarioFinal = null;

        return ret;
    }

    public void procurarHorarioValido() {
        List<Horario> list = procurarHorarioValidoInterno();

        for (Horario h : list) {
            if ((horarioAtual == h.horarioInicial || horarioAtual.after(h.horarioInicial)) && horarioAtual.before(h.horarioFinal)) {
                horarioInicial = h.horarioInicial;
                horarioFinal = h.horarioFinal;
            }
        }

        for (Horario h : list) {
            if (null != horarioInicial && null != horarioFinal) {
                if (h.horarioFinal.after(horarioFinal)) {
                    if (h.horarioInicial.before(horarioFinal)) {
                        if (h.horarioInicial.before(horarioInicial)) {
                            horarioInicial = h.horarioInicial;
                        }

                        horarioFinal = h.horarioFinal;
                    }
                }
                if ((h.horarioFinal.before(horarioFinal) && h.horarioFinal.after(horarioInicial)) || h.horarioFinal == horarioFinal) {
                    if (h.horarioInicial.before(horarioInicial)) {
                        horarioInicial = h.horarioInicial;
                    }
                }
            }
        }

        if (null == horarioInicial || null == horarioFinal) {
            setValid(false);
        }

        hashId();
    }

    public String hashId() {
        String hash = "";
        if (valid) {
            hash = new SimpleDateFormat("ddMMyyyyHHmmss").format(horarioInicial).concat(new SimpleDateFormat("ddMMyyyyHHmmss").format(horarioFinal));
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                md.update(hash.getBytes("UTF-8"));
                hash = "MD5:" + new BigInteger(1, md.digest()).toString(16).toUpperCase();
            } catch (NoSuchAlgorithmException e) {
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
            } catch (UnsupportedEncodingException e) {
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
            } catch (NullPointerException e){
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
            } catch (InvalidParameterException e){
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
            } catch (Exception e){
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e);
                AndroidImprimirUtils.imprimirErro(ValidarDiaAndHora.class, e, 90);
            }
        }
        return hash;
    }

    public boolean isValid() {
        return valid;
    }

    private void setValid(boolean valid) {
        this.valid = valid;
    }

    public Date getHorarioInicial() {
        return horarioInicial;
    }

    public Date getHorarioFinal() {
        return horarioFinal;
    }

    public Dias getDiaAtual() {
        return diaAtual;
    }

}

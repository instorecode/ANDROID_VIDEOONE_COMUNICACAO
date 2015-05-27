package com.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 31/03/2015.
 */
public class ComercialDet {

    public String arquivo;
    public String cliente;
    public String titulo;
    public String categoria;
    public String dataInicial;
    public String dataFinal;
    public String semana1;
    public String semana2;
    public String semana3;
    public String semana4;
    public String semana5;
    public String semana6;
    public String semana7;
    public String semana8;
    public String semana9;
    public String semana10;
    public String semana11;
    public String semana12;
    public String semana13;
    public String semana14;
    public String semana15;
    public String semana16;
    public String semana17;
    public String semana18;
    public String semana19;
    public String semana20;
    public String semana21;
    public String semana22;
    public String semana23;
    public String semana24;
    public String horario1;
    public String horario2;
    public String horario3;
    public String horario4;
    public String horario5;
    public String horario6;
    public String horario7;
    public String horario8;
    public String horario9;
    public String horario10;
    public String horario11;
    public String horario12;
    public String horario13;
    public String horario14;
    public String horario15;
    public String horario16;
    public String horario17;
    public String horario18;
    public String horario19;
    public String horario20;
    public String horario21;
    public String horario22;
    public String horario23;
    public String horario24;
    public String diaSemana;
    public boolean diasAlternados = true;
    public String data;
    public String ultimaExecucao;
    public String tempoTotal;
    public int random;
    public int qtdePlayer;
    public String dataVencimento;
    public String dependencia1;
    public String dependencia2;
    public String dependencia3;

    public List<ComercialDependencia> listaDependencias = new ArrayList<ComercialDependencia>();

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}

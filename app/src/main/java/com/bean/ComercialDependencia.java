package com.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ComercialDependencia {

    public String arquivo;
    public String cliente;
    public String titulo;
    public String categoria;
    public String dataInicial;
    public String dataFinal;
    public String data;
    public String dataVencimento;

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}

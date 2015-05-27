package com.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Playlist {
    private String horaInicialProgramacao;
    private String horaFinalProgramacao;
    private String video;
    private String titulo;
    private String categoria;
    private String velocidade;

    public String getHoraInicialProgramacao() {
        return horaInicialProgramacao;
    }

    public void setHoraInicialProgramacao(String horaInicialProgramacao) {
        this.horaInicialProgramacao = horaInicialProgramacao;
    }

    public String getHoraFinalProgramacao() {
        return horaFinalProgramacao;
    }

    public void setHoraFinalProgramacao(String horaFinalProgramacao) {
        this.horaFinalProgramacao = horaFinalProgramacao;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(String velocidade) {
        this.velocidade = velocidade;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}

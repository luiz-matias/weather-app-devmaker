package com.devmaker.luizmatias.weatherapp_devmaker;

/**
 * Created by Luiz Matias on 08/03/2018.
 */

public class Cidade {

    private int id; //id da cidade, recebido pela API
    private String nome; //nome da cidade
    private String foto; //foto da cidade (link da foto)
    private Clima clima; //clima da cidade

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Clima getClima() {
        return clima;
    }

    public void setClima(Clima clima) {
        this.clima = clima;
    }
}

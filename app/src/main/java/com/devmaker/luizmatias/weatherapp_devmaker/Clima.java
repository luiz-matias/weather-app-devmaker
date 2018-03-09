package com.devmaker.luizmatias.weatherapp_devmaker;

/**
 * Created by Luiz Matias on 08/03/2018.
 */

public class Clima {

    private String descricao; //descrição do clima
    private double temperatura; //temperatura em celsius
    private double pressao; //pressão em hPa
    private double umidade; //porcentagem de umidade
    private double temperatura_minima; //temperatura minima em celsius
    private double temperatura_maxima; //temperatura maxima em celsius
    private double nuvens; //porcentagem de nuvens no céu
    private double vento; //velocidade do vento, em m/s

    public double getVento() {
        return vento;
    }

    public void setVento(double vento) {
        this.vento = vento;
    }

    public double getNuvens() {
        return nuvens;
    }

    public void setNuvens(double nuvens) {
        this.nuvens = nuvens;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public double getPressao() {
        return pressao;
    }

    public void setPressao(double pressao) {
        this.pressao = pressao;
    }

    public double getUmidade() {
        return umidade;
    }

    public void setUmidade(double umidade) {
        this.umidade = umidade;
    }

    public double getTemperatura_minima() {
        return temperatura_minima;
    }

    public void setTemperatura_minima(double temperatura_minima) {
        this.temperatura_minima = temperatura_minima;
    }

    public double getTemperatura_maxima() {
        return temperatura_maxima;
    }

    public void setTemperatura_maxima(double temperatura_maxima) {
        this.temperatura_maxima = temperatura_maxima;
    }
}

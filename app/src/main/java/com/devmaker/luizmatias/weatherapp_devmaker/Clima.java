package com.devmaker.luizmatias.weatherapp_devmaker;

/**
 * Created by Luiz Matias on 08/03/2018.
 */

public class Clima {

    private String descricao; //descrição do clima
    private float temperatura; //temperatura em celsius
    private float pressao; //pressão em hPa
    private float umidade; //porcentagem de umidade
    private float temperatura_minima; //temperatura minima em celsius
    private float temperatura_maxima; //temperatura maxima em celsius

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(float temperatura) {
        this.temperatura = temperatura;
    }

    public float getPressao() {
        return pressao;
    }

    public void setPressao(float pressao) {
        this.pressao = pressao;
    }

    public float getUmidade() {
        return umidade;
    }

    public void setUmidade(float umidade) {
        this.umidade = umidade;
    }

    public float getTemperatura_minima() {
        return temperatura_minima;
    }

    public void setTemperatura_minima(float temperatura_minima) {
        this.temperatura_minima = temperatura_minima;
    }

    public float getTemperatura_maxima() {
        return temperatura_maxima;
    }

    public void setTemperatura_maxima(float temperatura_maxima) {
        this.temperatura_maxima = temperatura_maxima;
    }
}

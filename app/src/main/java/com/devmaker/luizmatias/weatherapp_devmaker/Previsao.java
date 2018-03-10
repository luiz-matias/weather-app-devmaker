package com.devmaker.luizmatias.weatherapp_devmaker;

/**
 * Created by Luiz Matias on 08/03/2018.
 */

public class Previsao {

    private String timestamp; //timestamp do horário da previsao, no formato "ano-mes-dia hora:minuto:segundo", no horário UTC
    private Cidade cidade; //objeto da cidade com a previsão do tempo

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }
}

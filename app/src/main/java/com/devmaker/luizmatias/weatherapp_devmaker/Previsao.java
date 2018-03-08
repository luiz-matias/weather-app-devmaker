package com.devmaker.luizmatias.weatherapp_devmaker;

/**
 * Created by Luiz Matias on 08/03/2018.
 */

public class Previsao {

    private long timestamp; //timestamp do horário da previsao
    private Cidade cidade; //objeto da cidade com a previsão do tempo

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }
}

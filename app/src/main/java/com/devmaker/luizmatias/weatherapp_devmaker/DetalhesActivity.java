package com.devmaker.luizmatias.weatherapp_devmaker;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DetalhesActivity extends AppCompatActivity {

    private TextView textViewNome, textViewDescricao, textViewTemperatura, textViewTemperaturaMaxima, textViewTemperaturaMinima, textViewNuvens, textViewChuva, textViewVelocidadeVento;
    private TextView textViewTemperatura1, textViewTemperatura2, textViewTemperaturaMaxima1, textViewTemperaturaMaxima2, textViewTemperaturaMinima1, textViewTemperaturaMinima2;
    private TextView textViewDataPrevisao1, textViewDataPrevisao2;
    private Button buttonTentarNovamente;
    private SwipeRefreshLayout swipeRefreshLayoutAtualizar;
    private LinearLayout linearLayoutSemInternet;
    private ScrollView scrollViewInformacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);
        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setReturnTransition(new Explode());

        //referencia as views
        Toolbar toolbar = findViewById(R.id.toolbarDetalhes);
        setSupportActionBar(toolbar);

        //dia atual, views
        textViewNome = findViewById(R.id.textViewNome);
        textViewDescricao = findViewById(R.id.textViewDescricao);
        textViewTemperatura = findViewById(R.id.textViewTemperatura);
        textViewTemperaturaMaxima = findViewById(R.id.textViewTemperaturaMaxima);
        textViewTemperaturaMinima = findViewById(R.id.textViewTemperaturaMinima);
        textViewNuvens = findViewById(R.id.textViewNuvens);
        textViewChuva = findViewById(R.id.textViewChuva);
        textViewVelocidadeVento = findViewById(R.id.textViewVelocidadeVento);

        //previsão dos próximos dois dias, views
        textViewDataPrevisao1 = findViewById(R.id.textViewDataPrevisao1);
        textViewDataPrevisao2 = findViewById(R.id.textViewDataPrevisao2);
        textViewTemperatura1 = findViewById(R.id.textViewTemperatura1);
        textViewTemperatura2 = findViewById(R.id.textViewTemperatura2);
        textViewTemperaturaMaxima1 = findViewById(R.id.textViewTemperaturaMaxima1);
        textViewTemperaturaMaxima2 = findViewById(R.id.textViewTemperaturaMaxima2);
        textViewTemperaturaMinima1 = findViewById(R.id.textViewTemperaturaMinima1);
        textViewTemperaturaMinima2 = findViewById(R.id.textViewTemperaturaMinima2);


    }

    private class requestPrevisao extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayoutAtualizar.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return Requests.GET(params[0]);
        }

        @Override
        protected void onPostExecute(String retorno) {
            super.onPostExecute(retorno);
            Log.d("retorno", "onPostExecute: retorno:\n"+retorno);
            if(retorno != null) {
                try{

                    //trata informações obtidas e as alimenta em um array de previsões do tempo
                    linearLayoutSemInternet.setVisibility(View.GONE);
                    scrollViewInformacoes.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                    scrollViewInformacoes.startAnimation(animation);

                    JSONObject object = new JSONObject(retorno);

                    JSONArray jsonPrevisoes = object.getJSONArray("list");
                    ArrayList<Previsao> previsoes = new ArrayList<>();

                    for(int i = 0; i < jsonPrevisoes.length(); i++){

                        Previsao previsao = new Previsao();
                        Cidade cidade = new Cidade();
                        Clima clima = new Clima();

                        cidade.setId(object.getJSONObject("city").getInt("id"));
                        cidade.setNome(object.getJSONObject("city").getString("name"));
                        cidade.setCoordenadas(new LatLng(object.getJSONObject("city").getJSONObject("coord").getDouble("lat"), object.getJSONObject("city").getJSONObject("coord").getDouble("lon")));

                        previsao.setCidade(cidade);
                        JSONObject jsonClima = jsonPrevisoes.getJSONObject(i).getJSONObject("main");

                        clima.setTemperatura(jsonClima.getDouble("temp"));
                        clima.setPressao(jsonClima.getDouble("pressure"));
                        clima.setTemperatura_minima(jsonClima.getDouble("temp_min"));
                        clima.setTemperatura_maxima(jsonClima.getDouble("temp_max"));
                        clima.setUmidade(jsonClima.getDouble("humidity"));
                        clima.setNuvens(jsonPrevisoes.getJSONObject(i).getJSONObject("clouds").getDouble("all"));
                        clima.setVento(jsonPrevisoes.getJSONObject(i).getJSONObject("wind").getDouble("speed"));

                        cidade.setClima(clima);

                        previsao.setCidade(cidade);

                        previsao.setTimestamp(jsonPrevisoes.getJSONObject(i).getInt("dt"));


                        previsoes.add(previsao);
                    }

                    //a partir de todas as previsões, resume as previsões dos próximos dois dias
                    ArrayList<Previsao> previsoesDias = getResumoDias(previsoes);



                }catch (Exception e) {
                    //erro na requisição
                    scrollViewInformacoes.clearAnimation();
                    linearLayoutSemInternet.setVisibility(View.VISIBLE);
                    scrollViewInformacoes.setVisibility(View.GONE);

                    e.printStackTrace();
                }
            }else{
                //erro na requisição
                scrollViewInformacoes.clearAnimation();
                linearLayoutSemInternet.setVisibility(View.VISIBLE);
                scrollViewInformacoes.setVisibility(View.GONE);

            }

            swipeRefreshLayoutAtualizar.setRefreshing(false);
        }
    }

    private ArrayList<Previsao> getResumoDias(ArrayList<Previsao> previsoes) {

        ArrayList<Previsao> previsoesDiarias = new ArrayList<>();

        try{

            for(int i = 0; i < previsoes.size(); i++){

                Previsao previsao = previsoes.get(i);

                //conversão de timestamp UTC para um calendar formatado para o horário de Curitiba
                SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(simpleDateFormat.format(new java.util.Date (previsao.getTimestamp()-10800))));

                //todo algoritmo

            }

        }catch (ParseException e){

        }

        return previsoesDiarias;
    }

}

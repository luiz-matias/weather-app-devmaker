package com.devmaker.luizmatias.weatherapp_devmaker;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
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
    private RecyclerView recyclerViewPrevisoes;
    private Button buttonTentarNovamente;
    private SwipeRefreshLayout swipeRefreshLayoutAtualizar;
    private LinearLayout linearLayoutSemInternet;
    private NestedScrollView scrollViewInformacoes;
    private LinearLayout linearLayoutVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);
        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setReturnTransition(new Explode());

        //referencia as views
        buttonTentarNovamente = findViewById(R.id.buttonTentarNovamente);
        swipeRefreshLayoutAtualizar = findViewById(R.id.swipeRefreshLayoutAtualizar);
        linearLayoutSemInternet = findViewById(R.id.linearLayoutSemInternet);
        scrollViewInformacoes = findViewById(R.id.scrollViewInformacoes);
        textViewNome = findViewById(R.id.textViewNome);
        textViewDescricao = findViewById(R.id.textViewDescricao);
        textViewTemperatura = findViewById(R.id.textViewTemperatura);
        textViewTemperaturaMaxima = findViewById(R.id.textViewTemperaturaMaxima);
        textViewTemperaturaMinima = findViewById(R.id.textViewTemperaturaMinima);
        textViewNuvens = findViewById(R.id.textViewNuvens);
        textViewChuva = findViewById(R.id.textViewChuva);
        textViewVelocidadeVento = findViewById(R.id.textViewVelocidadeVento);
        recyclerViewPrevisoes = findViewById(R.id.recyclerViewPrevisoes);
        linearLayoutVoltar = findViewById(R.id.linearLayoutVoltar);

        //ação de voltar
        linearLayoutVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //alimenta informações do tempo atual
        final Intent intent = getIntent();
        textViewNome.setText(intent.getExtras().getString("nome"));
        String descricao = intent.getExtras().getString("descricao");
        String descricao_formatada = descricao.substring(0, 1).toUpperCase() + descricao.substring(1);
        textViewDescricao.setText(descricao_formatada);
        textViewTemperatura.setText(String.format("%.0f", intent.getExtras().getDouble("temperatura")));
        textViewTemperaturaMaxima.setText(String.format("%.0f", intent.getExtras().getDouble("temperatura_maxima")));
        textViewTemperaturaMinima.setText(String.format("%.0f", intent.getExtras().getDouble("temperatura_minima")));
        textViewChuva.setText(String.format("%.0f", intent.getExtras().getDouble("umidade")));
        textViewNuvens.setText(String.format("%.0f", intent.getExtras().getDouble("nuvens")));
        textViewVelocidadeVento.setText(String.format("%.1f", intent.getExtras().getDouble("vento")));

        //recebe o id da cidade
        final int id = intent.getExtras().getInt("id");

        //seta as cores do swipe refresh layout
        swipeRefreshLayoutAtualizar.setColorSchemeColors(getApplicationContext().getResources().getColor(R.color.colorAccent));

        //ao atualizar, faz um fade out das informações da tela e inicia a requisição
        swipeRefreshLayoutAtualizar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        linearLayoutSemInternet.setVisibility(View.GONE);
                        scrollViewInformacoes.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                new requestPrevisao().execute("http://api.openweathermap.org/data/2.5/forecast?id="+id+"&units=metric&lang=pt");
                scrollViewInformacoes.startAnimation(animation);

            }
        });

        //ação de carregar novamente
        buttonTentarNovamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new requestPrevisao().execute("http://api.openweathermap.org/data/2.5/forecast?id="+id+"&units=metric&lang=pt");
            }
        });


        //Inicia animação de fade out na primeira requisição
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayoutSemInternet.setVisibility(View.GONE);
                scrollViewInformacoes.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        scrollViewInformacoes.startAnimation(animation);

        new requestPrevisao().execute("http://api.openweathermap.org/data/2.5/forecast?id="+id+"&units=metric&lang=pt");
    }

    //método que configura e apresenta informações do recycler view
    private void loadList(ArrayList<Previsao> previsoes) {
        PrevisoesAdapter previsoesAdapter = new PrevisoesAdapter(getApplicationContext(), previsoes);
        recyclerViewPrevisoes.setOnFlingListener(null);
        recyclerViewPrevisoes.setNestedScrollingEnabled(false);
        recyclerViewPrevisoes.setAdapter(previsoesAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recyclerViewPrevisoes);


        recyclerViewPrevisoes.setLayoutManager(layoutManager);
        recyclerViewPrevisoes.setItemAnimator(new DefaultItemAnimator());
    }

    //request que recebe todas as previsões das próximas horas e dias da cidade
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
                        clima.setDescricao(jsonPrevisoes.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description"));

                        cidade.setClima(clima);

                        previsao.setCidade(cidade);

                        previsao.setTimestamp(jsonPrevisoes.getJSONObject(i).getString("dt_txt"));


                        previsoes.add(previsao);
                    }

                    //alimenta as previsões no recycler view
                    loadList(previsoes);

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

}


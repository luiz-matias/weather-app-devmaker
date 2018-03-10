package com.devmaker.luizmatias.weatherapp_devmaker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class ListFragment extends Fragment {

    private LinearLayout linearLayoutSemInternet;
    private RelativeLayout relativeLayoutCidades;
    private RecyclerView recyclerViewCidades;
    private Button buttonTentarNovamente;
    private SwipeRefreshLayout swipeRefreshLayoutAtualizar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_list, container, false);

        //referencia elementos do layout
        recyclerViewCidades = (RecyclerView) view.findViewById(R.id.recyclerViewCidades);
        linearLayoutSemInternet = (LinearLayout) view.findViewById(R.id.linearLayoutSemInternet);
        relativeLayoutCidades = (RelativeLayout) view.findViewById(R.id.relativeLayoutCidades);
        buttonTentarNovamente = (Button) view.findViewById(R.id.buttonTentarNovamente);
        swipeRefreshLayoutAtualizar = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutAtualizar);

        getActivity().getWindow().setExitTransition(new Fade());

        //seta a cor do swype refresh
        swipeRefreshLayoutAtualizar.setColorSchemeColors(getContext().getResources().getColor(R.color.colorAccent));

        //ao atualizar, faz um fade out das informações da tela e inicia a requisição
        swipeRefreshLayoutAtualizar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        linearLayoutSemInternet.setVisibility(View.GONE);
                        relativeLayoutCidades.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                relativeLayoutCidades.startAnimation(animation);
                new requestCidades().execute("http://api.openweathermap.org/data/2.5/group?id=3464975,6323121,3460495&units=metric&lang=pt");
            }
        });

        //inicia a requisição quando o usuário tentar novamente
        buttonTentarNovamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new requestCidades().execute("http://api.openweathermap.org/data/2.5/group?id=3464975,6323121,3460495&units=metric&lang=pt");
            }
        });

        //Inicia animação de fade out na primeira requisição
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayoutSemInternet.setVisibility(View.GONE);
                relativeLayoutCidades.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        relativeLayoutCidades.startAnimation(animation);
        new requestCidades().execute("http://api.openweathermap.org/data/2.5/group?id=3464975,6323121,3460495&units=metric&lang=pt");

        return view;
    }

    //método que configura e apresenta informações do recycler view
    private void loadList(ArrayList<Cidade> cidades) {
        CidadesAdapter cidadesAdapter = new CidadesAdapter(getContext(), getActivity(), cidades);
        recyclerViewCidades.setAdapter(cidadesAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerViewCidades.setLayoutManager(layoutManager);
        recyclerViewCidades.setItemAnimator(new DefaultItemAnimator());
    }

    //Asynctask da requisição
    private class requestCidades extends AsyncTask<String, Void, String> {

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

                    //trata informações obtidas e as alimenta em um array de cidades

                    linearLayoutSemInternet.setVisibility(View.GONE);
                    relativeLayoutCidades.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    relativeLayoutCidades.startAnimation(animation);

                    JSONObject object = new JSONObject(retorno);

                    JSONArray jsonCidades = object.getJSONArray("list");
                    ArrayList<Cidade> cidades = new ArrayList<>();

                    for(int i = 0; i < jsonCidades.length(); i++){

                        Cidade cidade = new Cidade();
                        cidade.setId(jsonCidades.getJSONObject(i).getInt("id"));
                        cidade.setNome(jsonCidades.getJSONObject(i).getString("name"));
                        cidade.setCoordenadas(new LatLng(jsonCidades.getJSONObject(i).getJSONObject("coord").getDouble("lat"), jsonCidades.getJSONObject(i).getJSONObject("coord").getDouble("lon")));

                        Clima clima = new Clima();
                        clima.setDescricao(jsonCidades.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description"));

                        JSONObject jsonClima = jsonCidades.getJSONObject(i).getJSONObject("main");

                        clima.setTemperatura(jsonClima.getDouble("temp"));
                        clima.setPressao(jsonClima.getDouble("pressure"));
                        clima.setTemperatura_minima(jsonClima.getDouble("temp_min"));
                        clima.setTemperatura_maxima(jsonClima.getDouble("temp_max"));
                        clima.setUmidade(jsonClima.getDouble("humidity"));
                        clima.setVento(jsonCidades.getJSONObject(i).getJSONObject("wind").getDouble("speed"));
                        clima.setNuvens(jsonCidades.getJSONObject(i).getJSONObject("clouds").getDouble("all"));

                        cidade.setClima(clima);

                        //Simulação de que a API está retornando fotos, para melhor demonstração do design e processamento de imagem
                        switch (cidade.getNome()){
                            case "Curitiba":
                                cidade.setFoto("https://www.promoview.com.br/uploads/2017/07/images/Julho/09.07/Air_Promo_cria_novo_conceito_para_o_lancamento_do____Outubro_Rosa_Curitiba_2017_____2.jpg");
                                break;
                            case "Florianópolis":
                                cidade.setFoto("https://www.redesulhospedagens.com.br/sistema/admin/media/uploads/cidades/ponte-cartao-postal-floripa.jpg");
                                break;
                            case "Ivaiporã":
                                cidade.setFoto("http://diocesedeapucarana.com.br/portal/userfiles/paroquias/1351055279_a54573b85f_b.jpg");
                                break;
                            default:
                                cidade.setFoto("");
                                break;
                        }

                        cidades.add(cidade);
                    }

                    //carrega o recycler view
                    loadList(cidades);



                }catch (Exception e) {
                    //erro na requisição
                    relativeLayoutCidades.clearAnimation();
                    linearLayoutSemInternet.setVisibility(View.VISIBLE);
                    relativeLayoutCidades.setVisibility(View.GONE);

                    e.printStackTrace();
                }
            }else{
                //erro na requisição
                relativeLayoutCidades.clearAnimation();
                linearLayoutSemInternet.setVisibility(View.VISIBLE);
                relativeLayoutCidades.setVisibility(View.GONE);

            }

            swipeRefreshLayoutAtualizar.setRefreshing(false);
        }
    }

}

package com.devmaker.luizmatias.weatherapp_devmaker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private View view;
    private GoogleMap map;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottom_sheet;
    private FloatingActionButton fabDetalhes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_map, container, false);

        //instancia componentes do layout
        bottom_sheet = view.findViewById(R.id.bottom_sheet);
        fabDetalhes = view.findViewById(R.id.fabDetalhes);

        //referencia o behavior a view do bottom sheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        //adiciona callback de controle para o fab manter-se sincronizado com o bottom sheet
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    fabDetalhes.setVisibility(View.GONE);
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    fabDetalhes.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        //inicia o bottom sheet e o fab como oculto
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        fabDetalhes.setVisibility(View.GONE);

        //prepara o mapa
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //mostra localização do usuário no mapa
            map.setMyLocationEnabled(true);
        }

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL); //mapa de tipo normal
        map.setBuildingsEnabled(false); //sem construções 3D ao aproximar o zoom
        map.getUiSettings().setMapToolbarEnabled(false); //sem toolbar de rota/google maps ao clicar em um marker

        //posiciona a câmera no Paraná
        CameraPosition camera_position = new CameraPosition.Builder().target(new LatLng(-24.690195, -51.478545)).zoom(6).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(camera_position), 1, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
            }

            @Override
            public void onCancel() {
            }
        });

        //click no marker do mapa
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //alimenta o bottom sheet com informações, e depois o expande
                Cidade cidade = (Cidade) marker.getTag();
                TextView textViewNome = view.findViewById(R.id.textViewNome);
                TextView textViewDescricao = view.findViewById(R.id.textViewDescricao);
                TextView textViewTemperatura = view.findViewById(R.id.textViewTemperatura);

                textViewNome.setText(cidade.getNome());
                textViewNome.setText(cidade.getNome());
                String descricao = cidade.getClima().getDescricao();
                String descricao_formatada = descricao.substring(0, 1).toUpperCase() + descricao.substring(1);
                textViewDescricao.setText(descricao_formatada);
                textViewTemperatura.setText(String.format("%.0f", cidade.getClima().getTemperatura()));

                fabDetalhes.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                //inicia o click do FAB para abrir a tela de detalhes com informações de previsão do tempo
                fabDetalhes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Ação de detalhes do clima", Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
            }
        });

        //quando o usuário fecha o info window do marker, oculta o bottom sheet e o fab
        map.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                fabDetalhes.setVisibility(View.GONE);
            }
        });

        //realiza requisição das cidades de Curitiba, Florianópolis e Ivaiporã
        new requestCidades().execute("http://api.openweathermap.org/data/2.5/group?id=3464975,6323121,3460495&units=metric&lang=pt");

    }

    //adiciona os markers das cidades, após o carregamento do request
    private void addMarkers(ArrayList<Cidade> cidades) {

        for (int i = 0; i < cidades.size(); i++) {
            Cidade cidade = cidades.get(i);
            map.addMarker(new MarkerOptions()
                    .position(cidade.getCoordenadas())
                    .title(cidade.getNome())
            ).setTag(cidade);
        }

    }

    //Asynctask que faz a requisição
    private class requestCidades extends AsyncTask<String, Void, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            //inicia um progress dialog pra requisição
            progress = new ProgressDialog(getContext());
            progress.setMessage("Carregando cidades...");
            progress.setTitle("Aguarde");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCanceledOnTouchOutside(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return Requests.GET(params[0]);
        }

        @Override
        protected void onPostExecute(String retorno) {
            super.onPostExecute(retorno);

            //verifica o retorno
            if (retorno != null) {
                try {

                    //alimenta as informações obtidas em um array de cidades
                    JSONObject object = new JSONObject(retorno);

                    JSONArray jsonCidades = object.getJSONArray("list");
                    ArrayList<Cidade> cidades = new ArrayList<>();

                    for (int i = 0; i < jsonCidades.length(); i++) {

                        //alimenta informações da cidade
                        Cidade cidade = new Cidade();
                        cidade.setId(jsonCidades.getJSONObject(i).getInt("id"));
                        cidade.setNome(jsonCidades.getJSONObject(i).getString("name"));
                        cidade.setCoordenadas(new LatLng(jsonCidades.getJSONObject(i).getJSONObject("coord").getDouble("lat"), jsonCidades.getJSONObject(i).getJSONObject("coord").getDouble("lon")));

                        //alimenta informações do clima da cidade
                        Clima clima = new Clima();
                        clima.setDescricao(jsonCidades.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description"));

                        JSONObject jsonClima = jsonCidades.getJSONObject(i).getJSONObject("main");

                        clima.setTemperatura(jsonClima.getDouble("temp"));
                        clima.setPressao(jsonClima.getDouble("pressure"));
                        clima.setTemperatura_minima(jsonClima.getDouble("temp_min"));
                        clima.setTemperatura_maxima(jsonClima.getDouble("temp_max"));
                        clima.setUmidade(jsonClima.getDouble("humidity"));

                        cidade.setClima(clima);

                        cidades.add(cidade);
                    }

                    //adiciona markers das cidades no mapa
                    addMarkers(cidades);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Houve um problema ao realizar a requisição!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Houve um problema ao realizar a requisição!", Toast.LENGTH_LONG).show();
            }

            progress.dismiss();
        }
    }

}

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

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private View view;
    private GoogleMap map;
    private LatLng coordenadas_usuario = new LatLng(-1,-1);
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottom_sheet;
    private FloatingActionButton fabDetalhes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_map, container, false);

        bottom_sheet = view.findViewById(R.id.bottom_sheet);
        fabDetalhes = view.findViewById(R.id.fabDetalhes);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        fabDetalhes.setVisibility(View.GONE);

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

        //inicializa LocationManager
        LocationManager locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);

        //determina o melhor provedor de gps
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if(provider != null) {
            locationManager.requestLocationUpdates(provider, 60*1000, 20, this);
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

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

                fabDetalhes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Ação de detalhes do clima", Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
            }
        });

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

    //adiciona markers das cidades, após o carregamento do request
    private void addMarkers(ArrayList<Cidade> cidades) {

        for(int i = 0; i < cidades.size(); i++){
            Cidade cidade = cidades.get(i);
            map.addMarker(new MarkerOptions()
                    .position(cidade.getCoordenadas())
                    .title(cidade.getNome())
            ).setTag(cidade);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        coordenadas_usuario = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class requestCidades extends AsyncTask<String, Void, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {

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
            Log.d("retorno", "onPostExecute: retorno:\n"+retorno);
            if(retorno != null) {
                try{
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

                        cidade.setClima(clima);

                        //Simulador para fotos dos estabelecimentos
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

                    addMarkers(cidades);

                }catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Houve um problema ao realizar a requisição!", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getContext(), "Houve um problema ao realizar a requisição!", Toast.LENGTH_LONG).show();
            }

            progress.dismiss();
        }
    }

}

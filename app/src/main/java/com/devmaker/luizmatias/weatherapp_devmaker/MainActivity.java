package com.devmaker.luizmatias.weatherapp_devmaker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private TabLayout tabLayoutAbas;
    private ViewPager viewPagerFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //referencia e utiliza a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        //referencia tablayout e viewpager
        tabLayoutAbas = findViewById(R.id.tabLayoutAbas);
        viewPagerFragments = findViewById(R.id.viewPagerFragments);

        //antes de terminar a configuração da inicialização das fragments, pede permissão para acessar a localização
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            inicializaApp();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }





    }

    //inicializa o sistema de fragments com o tablayout
    public void inicializaApp(){

        //inicia o adapter do viewpager
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), 2, getApplicationContext());
        viewPagerFragments.setAdapter(adapter);
        //configura listener de posição do viewpager
        viewPagerFragments.addOnPageChangeListener(this);
        //configura sincronização entre o tablayout e o viewpager
        tabLayoutAbas.setupWithViewPager(viewPagerFragments);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        viewPagerFragments.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    inicializaApp();

                } else {
                    Toast.makeText(getApplicationContext(), "Permissão negada, não será possível mostrar sua localização.", Toast.LENGTH_LONG).show();
                    inicializaApp();
                }

                return;
            }
        }
    }
}

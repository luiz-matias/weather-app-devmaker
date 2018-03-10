package com.devmaker.luizmatias.weatherapp_devmaker;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Luiz Matias on 07/03/2018.
 */

public class Requests {

    private static String API_KEY = "9c86c1dbd23759616d140286f15459c3";

    //método para realizar requisições GET para a Open Weather API
    public static String GET(String url){
        try{

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url+"&APPID="+API_KEY)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();

        }catch (IOException e){

            return e.getMessage();

        }
    }

}

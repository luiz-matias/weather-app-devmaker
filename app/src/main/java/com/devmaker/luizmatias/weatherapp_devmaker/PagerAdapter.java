package com.devmaker.luizmatias.weatherapp_devmaker;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Luiz Matias on 08/03/2018.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    int tabCount;
    Context context;

    public PagerAdapter(FragmentManager fm, int tabCount, Context context){
        super(fm);
        this.tabCount = tabCount;
        this.context = context;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        CharSequence string;
        switch (position){
            case 0:
                string = context.getString(R.string.mapTab);
                break;
            case 1:
                string = context.getString(R.string.listTab);
                break;
            default:
                string = "";
                break;
        }

        return string;
    }

    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                MapFragment mapTab = new MapFragment();
                return mapTab;
            case 1:
                ListFragment listTab = new ListFragment();
                return listTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}

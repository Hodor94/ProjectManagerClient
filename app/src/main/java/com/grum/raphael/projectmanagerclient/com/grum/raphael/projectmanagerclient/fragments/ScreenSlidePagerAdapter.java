package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.grum.raphael.projectmanagerclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raphael on 20.11.2017.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;
    private Context context;

    public ScreenSlidePagerAdapter(Context context, FragmentManager fragmentManager,
                                   List<Fragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

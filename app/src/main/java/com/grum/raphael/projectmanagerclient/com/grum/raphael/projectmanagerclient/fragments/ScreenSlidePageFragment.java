package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.grum.raphael.projectmanagerclient.R;

/**
 * Created by Raphael on 20.11.2017.
 */

public class ScreenSlidePageFragment extends Fragment {

    PieChart pieChart;

    public ScreenSlidePageFragment(PieChart pieChart) {
        this.pieChart = pieChart;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.diagaram_container, container,
                false);
        FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.chart_container);
        frameLayout.addView(pieChart);
        return rootView;
    }
}

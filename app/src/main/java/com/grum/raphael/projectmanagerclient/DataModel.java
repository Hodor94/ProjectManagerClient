package com.grum.raphael.projectmanagerclient;

/**
 * Created by grumraph on 14.08.17.
 */

public class DataModel {

    private int icon;
    private String name;

    public DataModel(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}

package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

/**
 * Created by Raphael on 01.12.2017.
 */

public class ConnectChatWithIdentifier {
    private String name;
    private int positionInList;

    public ConnectChatWithIdentifier(String name, int positionInList) {
        this.name = name;
        this.positionInList = positionInList;
    }

    public int getPositionInList() {
        return positionInList;
    }

    public void setPositionInList(int positionInList) {
        this.positionInList = positionInList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

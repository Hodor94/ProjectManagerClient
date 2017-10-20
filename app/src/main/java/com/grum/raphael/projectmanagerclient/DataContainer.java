package com.grum.raphael.projectmanagerclient;

import java.util.HashMap;

/**
 * Created by Raphael on 24.08.2017.
 */

public class DataContainer {
    private String token;
    private String username;
    private String teamName;
    private String userRole;
    private String adminOfProject;

    public DataContainer() {
    }

    public DataContainer(String token, String username, String teamName, String userRole,
                         String adminOfProject, HashMap<String, String> colors) {
        this.token = token;
        this.username = username;
        this.teamName = teamName;
        this.userRole = userRole;
        this.adminOfProject = adminOfProject;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getAdminOfProject() {
        return adminOfProject;
    }

    public void setAdminOfProject(String adminOfProject) {
        this.adminOfProject = adminOfProject;
    }

    public boolean isEmpty() {
        boolean result = false;
        if (username == null && teamName == null && userRole == null && adminOfProject == null
                && token == null) {
            result = true;
        }
        return result;

    }
}

package com.grum.raphael.projectmanagerclient;

/**
 * Created by Raphael on 24.08.2017.
 */

public class DataContainer {
    String token;
    String username;
    String teamName;
    String userRole;
    String adminOfProject;

    public DataContainer() {

    }

    public DataContainer(String token, String username, String teamName, String userRole,
                         String adminOfProject) {
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

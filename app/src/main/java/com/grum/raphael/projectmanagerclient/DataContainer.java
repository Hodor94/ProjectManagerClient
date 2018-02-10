package com.grum.raphael.projectmanagerclient;

import java.util.HashMap;

/**
 * This class is used to save the user'S data needed during the client runs.
 */
public class DataContainer {
    private String token;
    private String username;
    private String teamName;
    private String userRole;
    private String adminOfProject;

    /**
     * Creates an instance of DataContainer.
     */
    public DataContainer() {
    }

    /**
     * Creates an instance of DataContainer with all data set.
     *
     * @param token The user token.
     * @param username The username of the user using the client.
     * @param teamName The name of the team the user belongs to.
     * @param userRole The role of the user in the team.
     * @param adminOfProject The name of the project the user is manager of.
     */
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

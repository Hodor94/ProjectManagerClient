package com.grum.raphael.projectmanagerclient;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * This class is used to connect securely with the server via SSH tunnel.
 */
public class SSHSession {

    private static int port = 22;
    private static JSch secureChannel = new JSch();

    /**
     * Creates a SSH tunnel between this client and the server.
     *
     * @return A {@see Session} between the server and this client.
     */
    public static Session createSession() {
        String host = "132.231.12.158";
        String user = "grum";
        String password = "OoHeedei1eiw8gaa";
        try {
            Session session = secureChannel.getSession(user, host, port);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            return session;
        } catch (JSchException e) {
            return null;
        }
    }
}

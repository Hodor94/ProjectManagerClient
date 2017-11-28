package com.grum.raphael.projectmanagerclient;

import java.util.Calendar;

/**
 * Created by Raphael on 27.11.2017.
 */

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private long messageTime;
    private String team;
    private String receiver;

    public ChatMessage(String messageText, String messageUser, String team, String receiver) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        // Current date in milliseconds
        messageTime = Calendar.getInstance().getTimeInMillis();
    }

    public ChatMessage() {

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}

package com.example.whosin.model.Objects;

public class MessageChat {
    private String sender;
    private String context;
    private String senderID;
    private String hour;
    private String imageUrl;

    public MessageChat(String sender, String context, String senderID, String hour, String imageUrl) {
        this.sender = sender;
        this.context = context;
        this.senderID = senderID;
        this.hour =  hour;
        this.imageUrl = imageUrl;
    }

    public MessageChat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "MessageChat{" +
                "sender='" + sender + '\'' +
                ", context='" + context + '\'' +
                ", senderID='" + senderID + '\'' +
                '}';
    }
}

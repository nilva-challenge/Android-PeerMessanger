package com.example.nilvabtchallenge.model;

public class MessageChatBody {
    //if int 0 send and 1 receive

    private int type;
    private String message,name;

    public MessageChatBody(int type, String message,String name) {
        this.type = type;
        this.message = message;
        this.name=name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

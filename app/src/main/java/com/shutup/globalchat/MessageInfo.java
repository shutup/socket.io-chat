package com.shutup.globalchat;

/**
 * Created by shutup on 15-8-29.
 */
public class MessageInfo {
    private String name;
    private String msg;
    private String time;
    private int type;

    public MessageInfo(String name, String msg, String time, int type) {
        this.name = name;
        this.msg = msg;
        this.time = time;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

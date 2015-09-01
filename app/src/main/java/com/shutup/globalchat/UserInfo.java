package com.shutup.globalchat;

/**
 * Created by shutup on 15-8-28.
 */
public class UserInfo {
    private String nickName;
    private String roomName;

    public UserInfo(String nickName, String roomName) {
        this.nickName = nickName;
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getNickName() {
        return nickName;
    }
}

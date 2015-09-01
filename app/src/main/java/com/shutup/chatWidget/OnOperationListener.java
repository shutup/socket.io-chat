package com.shutup.chatWidget;


public interface OnOperationListener {

    void send(String content);

    void selectedFace(String content);

    void selectedFuncation(int index);


}

package com.shutup.globalchat;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.shutup.chatWidget.Message;
import com.shutup.chatWidget.MessageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by shutup on 15-9-1.
 */
public class SocketIOAssistant implements Constants {

    private static SocketIOAssistant socketIOAssistant;
    private Socket mSocket;
    private UserInfo mUserInfo;
    private Activity context;

    private MessageAdapter adapter;
    /**
     * init user joined listener
     */
    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.d(TAG, data.getString("name") + " join the room");
                        addInfo(data.getString("name") + " join the room");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };
    /**
     * init user left listener
     */
    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.d(TAG, data.getString("name") + " left the room");
                        addInfo(data.getString("name") + " left the room");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    /**
     * init global msg receive listener
     */
    private Emitter.Listener onGlobalMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject msg = (JSONObject) args[0];
            try {
                String msgStr = msg.getString("msg");
                Log.d("global msg", "get :" + msgStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    /**
     * init private msg receive listener
     * 收到房间的消息
     */
    private Emitter.Listener onPrivateMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        JSONObject msg = (JSONObject) data.get("data");
                        String nameStr = data.getString("name");
                        String timeStr = data.getString("time");
                        adapter.getData().add(Message.fromJSON(msg));
                        adapter.notifyDataSetChanged();

                        Log.d("room msg", nameStr + " said: " + msg.getString("msg"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    /**
     * init connect status listener
     */
    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "connected to server");
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addInfo("已连接");
                }
            });

        }
    };
    /**
     * init connect error listener
     */
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "connected to server error");
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addInfo("连接失败");
                }
            });
        }
    };
    /**
     * init disconnect  listener
     */
    private Emitter.Listener onDisConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "disconnect from server");
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addInfo("断开连接");
                }
            });
        }
    };

    public SocketIOAssistant(Context context, UserInfo userInfo, MessageAdapter adapter) {
        this.context = (Activity) context;
        this.mUserInfo = userInfo;
        this.adapter = adapter;
        try {
            mSocket = IO.socket(serverUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        init();
    }

    public static SocketIOAssistant getSocketIOAssistant(Context context, UserInfo userInfo, MessageAdapter adapter) {
        if (socketIOAssistant == null) {
            socketIOAssistant = new SocketIOAssistant(context, userInfo, adapter);
        }
        return socketIOAssistant;
    }

    public void init() {
        if (!mSocket.connected()) {
            //add socket listeners
            mSocket.on(receiveGlobalMessage, onGlobalMessage);
            mSocket.on(mUserInfo.getRoomName(), onPrivateMessage);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT, onConnected);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisConnect);
            mSocket.on(userJoined, onUserJoined);
            mSocket.on(userLeft, onUserLeft);
            mSocket.connect();
            addMe();
            joinRoom();
        }
    }

    public void destory() {
        leaveRoom();
        if (mSocket.connected()) {
            mSocket.disconnect();
        }
        mSocket.off();
    }

    /**
     * add info
     *
     * @param info
     */
    private void addInfo(String info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

    /**
     * add user
     */
    private void addMe() {
        JSONObject data = new JSONObject();
        try {
            data.put("name", mUserInfo.getNickName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(addUser, data);
    }

    /**
     * leave Group
     */
    private void leaveRoom() {
        JSONObject data = new JSONObject();
        try {
            data.put("room", mUserInfo.getRoomName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(leaveGroup, data);
    }

    /**
     * join group
     */
    private void joinRoom() {
        JSONObject data = new JSONObject();
        try {
            data.put("room", mUserInfo.getRoomName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(joinGroup, data);
    }

    /**
     * setup msg
     * 发送远程服务器
     */
    public void attemptSend(Message message) {

        JSONObject data = new JSONObject();
        Message msg = new Message(message);
        msg.setIsSend(false);
        JSONObject json = msg.toJSON();
        String stime = Utils.getCurrentTime();
        try {
            data.put("data", json);
            data.put("time", stime);
            data.put("name", mUserInfo.getNickName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit(mUserInfo.getRoomName(), data);
    }
}

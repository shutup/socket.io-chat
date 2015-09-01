package com.shutup.globalrandomchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements Constants {

    /**
     * declare needed var
     */
    private Socket mSocket;
    private TextView mRoomName;
    private EditText mInputMessageView;
    private Button mSendBtn;
    private ListView mContents;
    private ArrayList<MessageInfo> msgs = new ArrayList<>();
    private MyAdapter myAdapter;
    private UserInfo mUserInfo;
    /**
     * init btn listener
     */
    private View.OnClickListener onSendBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            attemptSend();
        }
    };
    /**
     * init user joined listener
     */
    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
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
            runOnUiThread(new Runnable() {
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
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject msg = (JSONObject) args[0];
            try {
                String msgStr = msg.getString("msg");
                Log.d("chat msg", "get :" + msgStr);
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject msg = (JSONObject) args[0];
                    try {
                        String msgStr = msg.getString("msg");
                        String nameStr = msg.getString("name");
                        String timeStr = msg.getString("time");
                        addMessage(nameStr, msgStr, timeStr, OTHER);
                        Log.d("room msg", nameStr + " said: " + msgStr);

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
            runOnUiThread(new Runnable() {
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
            runOnUiThread(new Runnable() {
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addInfo("断开连接");
                }
            });
        }
    };

    {
        try {
            mSocket = IO.socket(serverUrl);
        } catch (URISyntaxException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.conversation);
        if (!mSocket.connected()) {
            mSocket.connect();
        }
        initView();
        initEvents();
        addMe();
        joinRoom();

    }

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
     * add info
     *
     * @param info
     */
    private void addInfo(String info) {
        Toast.makeText(ChatActivity.this, info, Toast.LENGTH_SHORT).show();
    }

    /**
     * add message
     *
     * @param name
     * @param msg
     * @param stime
     * @param type
     */
    private void addMessage(String name, String msg, String stime, int type) {
        MessageInfo messageInfo = new MessageInfo(name, msg, stime, type);
        msgs.add(messageInfo);
        myAdapter.notifyDataSetChanged();
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

    private void initEvents() {

        //add btn listener
        mSendBtn.setOnClickListener(onSendBtnClickListener);
        //add socket listeners
//        mSocket.on(receiveMessage, onNewMessage);
        mSocket.on(mUserInfo.getRoomName(), onPrivateMessage);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT, onConnected);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisConnect);
        mSocket.on(userJoined, onUserJoined);
        mSocket.on(userLeft, onUserLeft);
    }


    private void initView() {
        //get user info
        Bundle data = getIntent().getExtras();
        mUserInfo = new UserInfo(data.getString(loginNickName), data.getString(loginRoomName));
        mRoomName = (TextView) findViewById(R.id.roomName);
        mRoomName.setText(mUserInfo.getRoomName());
        mInputMessageView = (EditText) findViewById(R.id.conversation_editText);
        mSendBtn = (Button) findViewById(R.id.conversation_send_btn);
        mContents = (ListView) findViewById(R.id.conversation_listView);
        //去除分割线
        mContents.setDivider(null);
        myAdapter = new MyAdapter(this, msgs);
        mContents.setAdapter(myAdapter);
    }

    /**
     * 校验数据无误后，发送到本地聊天框和远程服务器
     */
    private void attemptSend() {
        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(ChatActivity.this, "发送内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject data = new JSONObject();
        String stime = Utils.getCurrentTime();
        try {
            data.put("msg", message);
            data.put("time", stime);
            data.put("name", mUserInfo.getNickName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mInputMessageView.setText("");
        mSocket.emit(mUserInfo.getRoomName(), data);
        addMessage(mUserInfo.getNickName(), message, stime, ME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveRoom();
        if (mSocket.connected()) {
            mSocket.disconnect();
        }
        mSocket.off();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

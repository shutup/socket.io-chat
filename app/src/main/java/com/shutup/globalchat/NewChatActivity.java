package com.shutup.globalchat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.shutup.chatWidget.Message;
import com.shutup.chatWidget.MessageAdapter;
import com.shutup.chatWidget.MessageInputToolBox;
import com.shutup.chatWidget.OnOperationListener;
import com.shutup.chatWidget.Option;
import com.shutup.globalrandomchat.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NewChatActivity extends AppCompatActivity implements Constants {

    private MessageInputToolBox box;
    private ListView listView;
    private MessageAdapter adapter;

    private SocketIOAssistant socketIOAssistant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initListView();
        initSocketIO();
        initMessageInputToolBox();


    }

    private void initSocketIO() {
        Bundle data = getIntent().getExtras();
        socketIOAssistant = SocketIOAssistant.getSocketIOAssistant(NewChatActivity.this, new UserInfo(data.getString(loginNickName), data.getString(loginRoomName)), adapter);
    }


    /**
     * init MessageInputToolBox
     */
    @SuppressLint("ShowToast")
    private void initMessageInputToolBox() {
        box = (MessageInputToolBox) findViewById(R.id.messageInputToolBox);
        box.setOnOperationListener(new OnOperationListener() {

            @Override
            public void send(String content) {
                System.out.println("===============" + content);
                Message message = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", content, true, true, new Date());
                //add to local
                addMessage(message);
                //send to server
                socketIOAssistant.attemptSend(message);

            }

            @Override
            public void selectedFace(String content) {

                System.out.println("===============" + content);
                Message message = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS, "Tomcat", "avatar", "Jerry", "avatar", content, true, true, new Date());
                //add to local
                addMessage(message);
                //send to server
                socketIOAssistant.attemptSend(message);

            }


            @Override
            public void selectedFuncation(int index) {

                System.out.println("===============" + index);

                switch (index) {
                    case 0:
                        //do some thing
                        break;
                    case 1:
                        //do some thing
                        break;

                    default:
                        break;
                }
                Toast.makeText(NewChatActivity.this, "Do some thing here, index :" + index, Toast.LENGTH_SHORT).show();

            }

        });

        ArrayList<String> faceNameList = new ArrayList<String>();
        for (int x = 1; x <= 10; x++) {
            faceNameList.add("big" + x);
        }

        ArrayList<String> faceNameList1 = new ArrayList<String>();
        for (int x = 1; x <= 7; x++) {
            faceNameList1.add("cig" + x);
        }


        ArrayList<String> faceNameList2 = new ArrayList<String>();
        for (int x = 1; x <= 24; x++) {
            faceNameList2.add("dig" + x);
        }

        Map<Integer, ArrayList<String>> faceData = new HashMap<Integer, ArrayList<String>>();
        faceData.put(R.mipmap.em_cate_magic, faceNameList2);
        faceData.put(R.mipmap.em_cate_rib, faceNameList1);
        faceData.put(R.mipmap.em_cate_duck, faceNameList);
        box.setFaceData(faceData);


        List<Option> functionData = new ArrayList<Option>();
        for (int x = 0; x < 2; x++) {
            Option takePhotoOption = new Option(this, "Take", R.drawable.take_photo);
            Option galleryOption = new Option(this, "Gallery", R.drawable.gallery);
            functionData.add(galleryOption);
            functionData.add(takePhotoOption);
        }
        box.setFunctionData(functionData);
    }


    private void initListView() {
        listView = (ListView) findViewById(R.id.messageListview);

        List<Message> messages = new ArrayList<Message>();

        adapter = new MessageAdapter(this, messages);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                box.hide();
                return false;
            }
        });

    }

    private void addMessage(Message msg) {
        adapter.getData().add(msg);
        listView.setSelection(listView.getBottom());
    }

    private void createReplayMsg(Message message) {

        final Message reMessage = new Message(message.getType(), 1, "Tom", "avatar", "Jerry", "avatar",
                message.getType() == 0 ? "Re:" + message.getContent() : message.getContent(),
                false, true, new Date()
        );
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * (new Random().nextInt(3) + 1));
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            adapter.getData().add(reMessage);
                            listView.setSelection(listView.getBottom());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

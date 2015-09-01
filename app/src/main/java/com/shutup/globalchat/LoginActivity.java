package com.shutup.globalchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shutup.globalrandomchat.R;

public class LoginActivity extends AppCompatActivity implements Constants {

    private EditText nickNameEdit;
    private EditText roomNameEdit;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEvent();
    }

    private void initEvent() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    Intent intent = new Intent(LoginActivity.this, NewChatActivity.class);
                    intent.putExtra(loginNickName, nickNameEdit.getText().toString());
                    intent.putExtra(loginRoomName, roomNameEdit.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void initView() {
        nickNameEdit = (EditText) findViewById(R.id.loginNameEdit);
        roomNameEdit = (EditText) findViewById(R.id.loginRoomEdit);
        loginBtn = (Button) findViewById(R.id.loginBtn);
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(nickNameEdit.getText().toString())) {
            Toast.makeText(LoginActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(roomNameEdit.getText().toString())) {
            Toast.makeText(LoginActivity.this, "请输入房间号", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}

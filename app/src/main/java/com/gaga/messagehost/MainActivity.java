package com.gaga.messagehost;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by 临园 on 2016/4/20.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("管理助手");
        toolbar.setSubtitle(R.string.mainactivity_name);
        toolbar.setLogo(R.drawable.logomine);
        setSupportActionBar(toolbar);

        //创建按钮监听事件
        findViewById(R.id.btn_MaintainMain).setOnClickListener(this);
        findViewById(R.id.btn_AdminMain).setOnClickListener(this);
        findViewById(R.id.btn_ExprotMain).setOnClickListener(this);
        findViewById(R.id.btn_LibMain).setOnClickListener(this);
        findViewById(R.id.btn_HelpMain).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //分别跳转到不同的ACTIVITY
            case R.id.btn_MaintainMain:
                startActivity(new Intent(MainActivity.this,ShowConfigActivity.class));
            break;
            case R.id.btn_AdminMain:
                startActivity(new Intent(MainActivity.this,RepairMessageActivity.class));
            break;
            case R.id.btn_ExprotMain:
                startActivity(new Intent(MainActivity.this,InExportActivity.class));
                break;
            case R.id.btn_LibMain:
                startActivity(new Intent(MainActivity.this,StorageActivity.class));
                break;
            case R.id.btn_HelpMain:
                startActivity(new Intent(MainActivity.this,HelpActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

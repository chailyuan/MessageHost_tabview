package com.gaga.messagehost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by re on 2016/5/19.
 */
public class ShowItemEditActivity extends Activity implements View.OnClickListener{

    private TextView name;
    private EditText content;
    private Button btn1,btn2;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_item_edit);

        name = (TextView)findViewById(R.id.tv_name);
        content = (EditText)findViewById(R.id.et_content);
        btn1 = (Button)findViewById(R.id.exitBtn0);
        btn2 = (Button)findViewById(R.id.exitBtn1);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

        //获取从父窗口传来的消息
        //里面应该有name和info两项内容
        intent = getIntent();
        String str1 = intent.getStringExtra("name");
        String str2 = intent.getStringExtra("content");

        name.setText(str1);
        content.setText(str2);

        System.out.println(str1);
        System.out.println(str2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exitBtn0:
                //保存
                //101表明有数据更新，102表明没有更新
                intent.putExtra("content",content.getText().toString());
                setResult(101, intent);
                finish();
                break;

            default:
                setResult(102,intent);
                finish();
                break;
        }
    }
}

package com.gaga.messagehost;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

/**
 * create by 临园
 * 登录对话框
 */
public class LoginActivity extends AppCompatActivity {
    private String userName = null,password = null;
    private MyDataBase dbSingle = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        MoveDataBase util = new MoveDataBase(this);
        // 判断数据库是否存在
        boolean dbExist = util.checkDataBase();

        if (dbExist) {
            Log.i("tag", "The database is exist.");
        } else {// 不存在就把raw里的数据库写入手机
            try {
                util.copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }


        //打开数据库
        dbSingle = MyDataBase.GetDb(this);

        InsertDataBase();

        findViewById(R.id.tv_forgotpassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //忘记密码
                Toast.makeText(LoginActivity.this, "请使用管理员账号登录重置密码！", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.login_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = ((EditText) findViewById(R.id.login_username)).getText().toString();
                password = ((EditText) findViewById(R.id.login_password)).getText().toString();

                if (userName.equals("")){
                    Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (userName.equals("admin")&&password.equals("admin")){
                    //默认的管理员账号登录，此账号登录只能进入注册界面
                    ((EditText)findViewById(R.id.login_username)).setText("");
                    ((EditText)findViewById(R.id.login_password)).setText("");
                    startActivity(new Intent(LoginActivity.this, NewUserActivity.class));
                    return;
                }
                Cursor lCursor = dbSingle.dbReader.rawQuery("SELECT * FROM "+ MyDataBase.TABLENAME_USER+" WHERE "+ MyDataBase.USER_NAME +"=?",
                        new String[]{userName});
                if (lCursor.moveToNext()) {
                    //表明存在数据
                    int pass = lCursor.getColumnIndex(MyDataBase.USER_PASSWORD);
                    String strValue=lCursor.getString(pass);
                    lCursor.close();

                    if(strValue.equals(password)){
                        //密码正确
                        //暂存当前用户名

                        GotoMainActivity();
                    }else{
                        //密码错误
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //不存在当前用户
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    //密码正确，转到主界面
    private void GotoMainActivity(){
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }
    //测试用，插入一条数据
    private void InsertDataBase(){
        Cursor lCursor = dbSingle.dbReader.rawQuery("SELECT * FROM "+ MyDataBase.TABLENAME_MAINTAIN+" WHERE "+ MyDataBase.MT_ID_EQUIPMENTS+"=?",
                new String[]{MyDataBase.MT_ID_EQUIPMENTS});

        if (!lCursor.moveToNext()){

            ContentValues values = new ContentValues();
            for (int i=0;i< MyDataBase.MT_ALL_TITLE.length;i++){
                values.put(MyDataBase.MT_ALL_TITLE[i], MyDataBase.MT_ALL_TITLE[i]);
            }
            values.put(MyDataBase.MT_CODE, MyDataBase.MT_CODE);
            dbSingle.dbWriter.insert(MyDataBase.TABLENAME_MAINTAIN, null, values);
        }
        lCursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

package com.gaga.messagehost;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by re on 2016/5/21.
 */
public class NewUserActivity extends AppCompatActivity {
    private MyDataBase dbSingle = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newuser);

        dbSingle = MyDataBase.GetDb(this);

        findViewById(R.id.btn_newuser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText)findViewById(R.id.et_username)).getText().toString();
                String password = ((EditText)findViewById(R.id.et_password)).getText().toString();
                String repeatpassword = ((EditText)findViewById(R.id.et_repeatpassword)).getText().toString();

                if (name.equals("")||password.equals("")||repeatpassword.equals("")){
                    //不能为空
                    Toast.makeText(NewUserActivity.this,"不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name.equals("admin")){
                    //不能为管理员账号，请重新输入
                    Toast.makeText(NewUserActivity.this,"不能使用管理员账号",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(repeatpassword)){
                    //两次密码不一样
                    Toast.makeText(NewUserActivity.this,"两次输入的密码不一样，请检查",Toast.LENGTH_SHORT).show();
                    return;
                }
                //输入正确，录入数据库
                //先检查有没有当前用户
                ContentValues cv = new ContentValues();
                Cursor cursor = dbSingle.dbReader.query(MyDataBase.TABLENAME_USER, null, MyDataBase.USER_NAME + " =?", new String[]{name}, null, null, null, null);
                if (cursor.getCount()!=0){
                    dbSingle.dbWriter.delete(MyDataBase.TABLENAME_USER,MyDataBase.USER_NAME+"=?",new String[]{name});
                }

                cv.clear();
                cv.put(MyDataBase.USER_NAME, name);
                cv.put(MyDataBase.USER_PASSWORD, password);
                long a = dbSingle.dbWriter.insert(MyDataBase.TABLENAME_USER, null, cv);
                if (a>=0){
                    //插入成功
                    Toast.makeText(NewUserActivity.this,"注册成功，请重新登录",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}

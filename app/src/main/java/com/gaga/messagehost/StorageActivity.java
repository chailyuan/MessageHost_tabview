package com.gaga.messagehost;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 临园 on 2016/4/24.
 * 出入库管理
 */
public class StorageActivity extends AppCompatActivity implements View.OnClickListener{
    private MyDataBase dbSingle = null;


    private Switch swStorage = null;
    private EditText etStorageCode = null;
    private EditText etStoragePosition = null;
    private EditText etStorageStyle = null;
    private Button btnSave = null;

    private String thisCode = null;

    private TextView tvStorage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_storage);

        dbSingle = MyDataBase.GetDb(this);

        etStoragePosition = (EditText)findViewById(R.id.et_StoragePosition);
        etStorageStyle = (EditText)findViewById(R.id.et_StorageStyle);
        btnSave = (Button)findViewById(R.id.btn_StorageSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //需要在两个数据库中修改，第一个库是装备状态，第二个库是装备位置和装备类别
                ContentValues cv = new ContentValues();
                cv.put(MyDataBase.RP_POSITION, etStoragePosition.getText().toString());
                cv.put(MyDataBase.RP_TYPE_EQUIPMENTS, etStorageStyle.getText().toString());
                String whereClause = MyDataBase.RP_CODE+"=?";

                dbSingle.dbWriter.update(MyDataBase.TABLENAME_REPARE, cv, whereClause, new String[]{etStorageCode.getText().toString()});

                cv.clear();
                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日");
                final Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String strDate = formatter.format(curDate);

                if (swStorage.isChecked()){
                    cv.put(MyDataBase.MT_OUTSTORAGE,strDate);
                }else {
                    cv.put(MyDataBase.MT_INSTORAGE,strDate);
                }
                whereClause = MyDataBase.MT_CODE+"=?";
                dbSingle.dbWriter.update(MyDataBase.TABLENAME_MAINTAIN, cv, whereClause, new String[]{etStorageCode.getText().toString()});
                Toast.makeText(StorageActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
            }
        });

        tvStorage = (TextView)findViewById(R.id.tv_Storage);
        etStorageCode = (EditText)findViewById(R.id.et_storageCode);
        swStorage = (Switch)findViewById(R.id.sw_storage);
        swStorage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选中状态，设置在用
                    //直接更新数据库数据
                    ContentValues cv = new ContentValues();
                    cv.put(MyDataBase.MT_STATUS, "在用");
                    String whereClause = MyDataBase.MT_CODE+"=?";
                    dbSingle.dbWriter.update(MyDataBase.TABLENAME_MAINTAIN, cv, whereClause, new String[]{etStorageCode.getText().toString()});
                    tvStorage.setText("在用");
                } else {
                    //关闭装填，设置库存
                    ContentValues cv = new ContentValues();
                    cv.put(MyDataBase.MT_STATUS, "库存");
                    String whereClause = MyDataBase.MT_CODE+"=?";
                    dbSingle.dbWriter.update(MyDataBase.TABLENAME_MAINTAIN, cv, whereClause, new String[]{etStorageCode.getText().toString()});
                    tvStorage.setText("库存");
                }
            }
        });
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String strDate = formatter.format(curDate);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER){
            //回车事件
            String code = etStorageCode.getText().toString().replace("\n", "");
            etStorageCode.setText(code);

            if (code.equals("")) {
                //返回的数据是空的
                Toast.makeText(this,"请输入条形码！",Toast.LENGTH_SHORT).show();
                swStorage.setEnabled(false);
                etStorageStyle.setEnabled(false);
                etStoragePosition.setEnabled(false);
                btnSave.setEnabled(false);

            } else {
                //返回的数据不空
                //隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etStorageCode.getWindowToken(), 0);


                Cursor cursor = dbSingle.dbReader.query(MyDataBase.TABLENAME_MAINTAIN, null, MyDataBase.MT_CODE + " =?", new String[]{code}, null, null, null, null);

                if (cursor.moveToNext()) {
                    //查询到了数据
                    swStorage.setEnabled(true);
                    etStorageStyle.setEnabled(true);
                    etStoragePosition.setEnabled(true);
                    btnSave.setEnabled(true);
                    thisCode = code;

                    String status = cursor.getString(cursor.getColumnIndex(MyDataBase.MT_STATUS));

                    cursor.close();

                    cursor = dbSingle.dbReader.query(MyDataBase.TABLENAME_REPARE, null, MyDataBase.RP_CODE + " =?", new String[]{code}, null, null, null, null);
                    if (cursor.moveToNext()) {
                        String position = cursor.getString(cursor.getColumnIndex(MyDataBase.RP_POSITION));
                        String type = cursor.getString(cursor.getColumnIndex(MyDataBase.RP_TYPE_EQUIPMENTS));
                        etStoragePosition.setText(position);
                        etStorageStyle.setText(type);
                    }

                    if (status.equals("在用")){
                        swStorage.setChecked(true);
                    }else if (status.equals("库存")){
                        swStorage.setChecked(false);
                    }
                }else {

                    swStorage.setEnabled(false);
                    etStorageStyle.setEnabled(false);
                    etStoragePosition.setEnabled(false);
                    btnSave.setEnabled(false);

                    Toast.makeText(this,"未查询到数据！请检查",Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            }
            return true;


        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }
}

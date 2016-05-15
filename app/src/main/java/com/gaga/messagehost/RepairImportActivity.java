package com.gaga.messagehost;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by re on 2016/5/2.
 */
public class RepairImportActivity extends AppCompatActivity {
    private EditText etCode,etRiqi,etXianxiang,etBanfa,etRenyuan;
    private Button btnSaveMess;

    private MyDataBase dbSingle = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_repairimport);
        dbSingle = MyDataBase.GetDb(this);

        etCode = (EditText) findViewById(R.id.et_repairimportCode);
        etRiqi = (EditText) findViewById(R.id.et_RPriqi);
        etXianxiang = (EditText) findViewById(R.id.et_RPxianxiang);
        etBanfa = (EditText) findViewById(R.id.et_RPbanfa);
        etRenyuan = (EditText) findViewById(R.id.et_RPrenyaun);

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日");
        final Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String strDate = formatter.format(curDate);

        etRiqi.setText(strDate);


        btnSaveMess= (Button) findViewById(R.id.btn_RPSaveRPMess);
        btnSaveMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strCode,strRiqi,strXianxiang,strBanfa,strRenyuan;
                strCode = etCode.getText().toString();
                strBanfa = etBanfa.getText().toString();
                strRenyuan = etRenyuan.getText().toString();
                strRiqi = etRiqi.getText().toString();
                strXianxiang = etXianxiang.getText().toString();

                if (strCode.equals("")
                        ||strRiqi.equals("")
                        ||strXianxiang.equals("")
                        ||strBanfa.equals("")
                        ||strRenyuan.equals("")){
                    //不能为空，请输入完整
                    Toast.makeText(RepairImportActivity.this,"请输入完整",Toast.LENGTH_SHORT).show();
                }else {
                    //向数据库中插入新的维修信息,如果维修信息表中无当前条形码对应的信息怎么办
                    //先查詢配置表中是否有此信息

                    ContentValues cv = new ContentValues();
                    String strEtCode = etCode.getText().toString();
                    Cursor cursor = dbSingle.dbReader.query(MyDataBase.TABLENAME_REPARE, null, MyDataBase.RP_CODE + " =?", new String[]{strEtCode}, null, null, null, null);

                    if (cursor.getCount()!=0){
                        cursor.moveToNext();
                        cv.put(MyDataBase.RP_ID_EQUIPMENTS, cursor.getString(cursor.getColumnIndex(MyDataBase.RP_ID_EQUIPMENTS)));
                        cv.put(MyDataBase.RP_TYPE_EQUIPMENTS,cursor.getString(cursor.getColumnIndex(MyDataBase.RP_TYPE_EQUIPMENTS)));
                        cv.put(MyDataBase.RP_POSITION,cursor.getString(cursor.getColumnIndex(MyDataBase.RP_POSITION)));
                        cv.put(MyDataBase.RP_CODE,strCode);
                        cv.put(MyDataBase.RP_DATE,strRiqi);
                        cv.put(MyDataBase.RP_PHENOMENON,strXianxiang);
                        cv.put(MyDataBase.RP_METHOD,strBanfa);
                        cv.put(MyDataBase.RP_SERVICE_PERSON, strRenyuan);
                        long a = dbSingle.dbWriter.insert(MyDataBase.TABLENAME_REPARE,null,cv);
                        if (a>=0){
                            Toast.makeText(RepairImportActivity.this,"插入成功",Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                    }else {
                        cursor = dbSingle.dbReader.query(MyDataBase.TABLENAME_MAINTAIN, null, MyDataBase.MT_CODE + " =?", new String[]{strEtCode}, null, null, null, null);
                        if (cursor.getCount()==0){
                            //不存在
                        }else {
                            cursor.moveToNext();
                            cv.put(MyDataBase.RP_ID_EQUIPMENTS, cursor.getString(cursor.getColumnIndex(MyDataBase.MT_ID_EQUIPMENTS)));
                            cv.put(MyDataBase.RP_CODE,strCode);
                            cv.put(MyDataBase.RP_DATE,strRiqi);
                            cv.put(MyDataBase.RP_PHENOMENON, strXianxiang);
                            cv.put(MyDataBase.RP_METHOD, strBanfa);
                            cv.put(MyDataBase.RP_SERVICE_PERSON,strRenyuan);
                            long a = dbSingle.dbWriter.insert(MyDataBase.TABLENAME_REPARE,null,cv);

                            if (a>=0){
                                Toast.makeText(RepairImportActivity.this,"插入成功",Toast.LENGTH_SHORT).show();
                            }
                        }
                        cursor.close();
                    }

                }
            }
        });

    }
}

package com.gaga.messagehost;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by re on 2016/5/2.
 * 维修信息查询
 */
public class RepairCheckActivity extends AppCompatActivity {
    private MyDataBase dbSingle = null;
    private EditText etCode;
    private ListView lvRpCheck;
    private List<Map<String, Object>> mData;

    private TextView tvClass,tvCode;
    private Button btnSavePostion;
    private EditText etPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_repaircheck);

        dbSingle = MyDataBase.GetDb(this);

        lvRpCheck = (ListView)findViewById(R.id.lv_repaircheck);
        etCode = (EditText)findViewById(R.id.et_repaircheckCode);
        etCode.requestFocus();
        tvClass = (TextView)findViewById(R.id.tv_RPCheckClass);
        tvCode = (TextView)findViewById(R.id.tv_RPCheckCode);
        btnSavePostion = (Button)findViewById(R.id.btn_SavePosition);
        etPosition = (EditText)findViewById(R.id.et_RPCheckPosition);
        btnSavePostion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在這裡保存位置信息
                Cursor cursor = dbSingle.dbReader.query(MyDataBase.TABLENAME_REPARE, null, MyDataBase.RP_CODE + " =?", new String[]{etCode.getText().toString()}, null, null, null, null);
                ContentValues cv = new ContentValues();
                cv.put(MyDataBase.RP_POSITION,etPosition.getText().toString());

                while (cursor.moveToNext()){
                    String whereClause = MyDataBase.RP_CODE + "=?";
                    int a =  dbSingle.dbWriter.update(MyDataBase.TABLENAME_REPARE, cv, whereClause, new String[]{etCode.getText().toString()});
                }
                cursor.close();
            }
        });

        mData = InitmData();

        MyAdapter myAdapter = new MyAdapter(this);
        lvRpCheck.setAdapter(myAdapter);

    }
    private List<Map<String,Object>> InitmData(){
        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        Map<String,Object> map = null;

        map = new HashMap<String, Object>();
        map.put("name","");
        map.put("content", "");
        list.add(map);

        return list;
    }

    private List<Map<String, Object>> GetData(Cursor cursor) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;




        if (cursor.getCount() == 0){

            map = new HashMap<String, Object>();
            map.put("name", "无记录");
            map.put("content", "");
            list.add(map);

            Toast.makeText(this,"未查詢到!",Toast.LENGTH_SHORT).show();
            //置空
            etPosition.setText("");
            tvClass.setText("");
            tvCode.setText("");
            return list;
        }

        int count = 1;
        while (cursor.moveToNext()) {

            map = new HashMap<String, Object>();
            map.put("name", "记录"+count+"：");
            map.put("content","");
            count++;
            list.add(map);

            tvClass.setText(cursor.getString(1).toString());
            tvCode.setText(cursor.getString(2).toString());
            etPosition.setText(cursor.getString(8).toString());

            for (int i = 2; i < MyDataBase.RP_ALL_TITLE.length-2; i++) {
                map = new HashMap<String, Object>();
                map.put("name", MyDataBase.RP_ALL_CHINESE[i] + ':');
                map.put("content", cursor.getString(i + 1).toString());
                list.add(map);
            }

        }
        return list;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER){
            //回车事件的响应
            String code = etCode.getText().toString().replace("\n", "");
            etCode.setText(code);



            if (code.equals("")) {
                //返回的数据是空的
            } else {
                //返回的数据不空
                //隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etCode.getWindowToken(), 0);

                Cursor cursor = dbSingle.dbReader.query(MyDataBase.TABLENAME_REPARE, null, MyDataBase.RP_CODE + " =?", new String[]{code}, null, null, null, null);

                mData = GetData(cursor);
                cursor.close();

            }
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    //提取出来方便点
    public final class ViewHolder {
        public TextView title;
        public TextView info;
    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {

                holder = new ViewHolder();

                //可以理解为从vlist获取view  之后把view返回给ListView

                convertView = mInflater.inflate(R.layout.layout_repaircheck_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.tv_RPName);
                holder.info = (TextView) convertView.findViewById(R.id.tv_RPCheck_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText((String) mData.get(position).get("name"));
            holder.info.setText((String) mData.get(position).get("content"));

            return convertView;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}
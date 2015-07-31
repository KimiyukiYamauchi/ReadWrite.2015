package com.example.yamauchi.readwriteyo;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        int wc = LinearLayout.LayoutParams.WRAP_CONTENT;

        Button btnw = new Button(this);
        btnw.setText("Write");
        btnw.setTag("W");
        btnw.setLayoutParams(new LinearLayout.LayoutParams(wc,wc));
        btnw.setOnClickListener(this); // イベントリスナーの登録
        layout.addView(btnw);

        Button btnr = new Button(this);
        btnr.setText("Read");
        btnr.setTag("R");
        btnr.setLayoutParams(new LinearLayout.LayoutParams(wc,wc));
        btnr.setOnClickListener(this); // イベントリスナーの登録処理
        layout.addView(btnr);

        EditText et = new EditText(this);
        et.setText("???");
        et.setTag("et");
        layout.addView(et);

        TextView tv = new TextView(this);
        tv.setText("???");
        tv.setTag("tv");
        layout.addView(tv);
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

    @Override
    public void onClick(View v) {
        if((String)v.getTag() == "W"){
            View parent = (View)v.getParent();
            EditText et = (EditText)parent.findViewWithTag("et");
            TextView tv  = (TextView)parent.findViewWithTag("tv");
            try {
                //writeToFile(this, et.getText().toString()); // ファイルへの書き込み処理
                writeToTable(et.getText().toString(), false); // テーブルに追加
                /*SharedPreferences prefs
                        = getSharedPreferences("myprefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("text", et.getText().toString());*/
                /*editor.putString("Title", "Javaの絵本");
                editor.putInt("Pages", 213);
                editor.putInt("Price", 1580);*/
                //editor.commit();
            } catch (Exception e) {
                tv.setText("ERROR:" + e.getMessage());
            }
        }else if((String)v.getTag() == "R"){
            View parent = (View)v.getParent();
            TextView tv  = (TextView)parent.findViewWithTag("tv");
            try {
				//tv.setText(readFromFile(this)); // ファイルの読み込み処理
                tv.setText(readFromTable());	// テーブルからの読み出し
                //tv.setText(readFromPrefs()); // プレファレンスからの読み込み
            } catch (Exception e) {
                tv.setText("ERROR:" + e.getMessage());
            }
        }
    }


    private String readFromPrefs(){
        StringBuffer sb = new StringBuffer();

        SharedPreferences prefs
                = getSharedPreferences("myprefs", MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();
        for(Map.Entry<String, ?> entry : map.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            sb.append(key + "|" + value.toString() + "\n");
        }
        return  sb.toString();
    }
    private void writeToFile(Context c, String s) throws Exception{
        s = s + "\n";
        byte [] data = s.getBytes();
        OutputStream stream = null;
        try {
            String path = getFilesDir().toString();
            Log.v("writeToFile", path);
            stream = c.openFileOutput	// ファイルのオープン
                    ("test.txt", Context.MODE_APPEND);
            stream.write(data); // 書き込み
            stream.close();	// ファイルのクローズ

        } catch (Exception e) {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException e1) {
                    throw e1;
                }
            }
        }
    }

    private String readFromFile(Context c) throws Exception{
        byte[] data = new byte[100];
        InputStream stream = null;
        ByteArrayOutputStream stream2 = null;
        try {
            String path = getFilesDir().toString();
            Log.v("readFromFile", path);
            stream = c.openFileInput("test.txt");

            stream2 = new ByteArrayOutputStream();

            int size = stream.read(data);
            while(size > 0){
                stream2.write(data, 0, size);
                size = stream.read(data);
            }
            stream2.close();
            stream.close();

            String s = new String(stream2.toByteArray());
            return s;
        } catch (Exception e) {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException e1) {
                    throw e1;
                }
            }
        }
        return "";
    }

    private void writeToTable(String data, boolean overwrite){
        ContentValues vals = new ContentValues();
        vals.put("data", data);
        String whereClause = "id = 1";
        String [] whereArgs = null;

        DBHelper dbh = new DBHelper(this);
        SQLiteDatabase db = dbh.getWritableDatabase();

        try{
            if(overwrite){
                db.update(DBHelper.TABLENAME, vals, whereClause, whereArgs);
            }else{
                db.insert(DBHelper.TABLENAME, "", vals);
            }
        }finally{
            db.close();
        }
    }

    private String readFromTable(){
        String[] columns = {"id", "data"};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;

        StringBuffer sb = new StringBuffer();
        Cursor cur = null;

        DBHelper dbh = new DBHelper(this);
        SQLiteDatabase db = dbh.getReadableDatabase();
        try{
            cur = db.query(DBHelper.TABLENAME, columns, selection,
                    selectionArgs, groupBy, having, orderBy);
            while(cur.moveToNext()){
                sb.append(cur.getInt(0) + "," + cur.getString(1) + "\n");
            }
        }finally{
            if(cur != null){
                cur.close();
            }
            db.close();
        }
        return sb.toString();
    }
}

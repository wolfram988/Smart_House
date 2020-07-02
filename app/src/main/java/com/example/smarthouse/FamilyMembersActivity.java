package com.example.smarthouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class FamilyMembersActivity extends AppCompatActivity {
    Intent intent; //намерение
    DBHelper dbHelper;
    ListView listView; //список
    NecessaryDBTools necessaryDBTools;//вспомогательный класс
    ArrayList<String> names;
    ArrayList<String> phones;
    Resources res; //ресурсы андроид
    SimpleAdapter adapter;
    public static final String TABLE_NAME = "family";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_members);
        dbHelper = new DBHelper(this);
        setTitle(R.string.family_members_list);//заголовок окна
        necessaryDBTools = new NecessaryDBTools(dbHelper,TABLE_NAME);
        listView = findViewById(R.id.listview);
        updateAdapter();
    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.buttonAdd:

                break;
            case R.id.buttonBack:  //переход на главный экран
                intent = new Intent(FamilyMembersActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonClear:
                break;
        }
    }
    public void updateAdapter(){
        names = new ArrayList<>();
        names = necessaryDBTools.getFromSQLite("name");
        phones = new ArrayList<>();
        phones = necessaryDBTools.getFromSQLite("phone");
        adapter = new SimpleAdapter(this,
                necessaryDBTools.putMapstoList(names,phones),
                android.R.layout.simple_list_item_2,
                new String[]{"Name", "Phone"},
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
    } //обновление данных в списке и устанока адаптера списка
}

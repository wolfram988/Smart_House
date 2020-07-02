package com.example.smarthouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SocketSettingsActivity extends AppCompatActivity {
    Intent intent; //намерение
    ListView listView; //список

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_settings);
        setTitle(R.string.socket_settings);//заголовок окна
        listView = findViewById(R.id.listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.settings_list));
        listView.setAdapter(adapter);
    }
    public void onClick(View v){
        switch (v.getId()){

            case R.id.buttonBack: //переход на главный экран
                intent = new Intent(SocketSettingsActivity.this,MainActivity.class);
                startActivity(intent);
                    break;

        }
    }
}

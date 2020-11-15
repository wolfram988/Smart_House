package com.example.smarthouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper; //помошник для работы с sqlite
    NecessaryDBTools necessaryDBTools;//доп. класс для работы с базой данных
    Spinner spinner; //выпадающий список
    Intent intent;//намерение
    String numberText; //номер телефона(строка)
    String password; //пароль
    String name;
    SMSCommand SC; // класс для работы с командами и смс
    ArrayAdapter adapter; //адаптер выпадающего списка
    ArrayList<String> phones; //телефоны
    ArrayList<String> passwords; //пароли
    ArrayList<String> adminFlags;//флаги на ввод пароля
    ArrayList<String> names;
    int adminFlag; //флаг на ввод пароля
    int requestCode = 0;
    boolean isCorrect;
    public static final String TABLE_NAME = "numbers";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.main_menu);//заголовок окна
        spinner = findViewById(R.id.spinner);
        dbHelper = new DBHelper(this);
        necessaryDBTools = new NecessaryDBTools(dbHelper,TABLE_NAME);
        phones = necessaryDBTools.getFromSQLite("phone");
        passwords = necessaryDBTools.getFromSQLite("password");
        adminFlags = necessaryDBTools.getFromSQLite("adminflag");
        names = necessaryDBTools.getFromSQLite("name");
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,names);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        while ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED)){
            requestCode++;
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS},
                    requestCode);
        } //проверка разрешений

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        phones = necessaryDBTools.getFromSQLite("phone");
        passwords = necessaryDBTools.getFromSQLite("password");
        adminFlags = necessaryDBTools.getFromSQLite("adminflag");
        names = necessaryDBTools.getFromSQLite("name");
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,names);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public void onClick(View v){
        isCorrect = true;
        try {
            numberText = phones.get(spinner.getSelectedItemPosition());
            password = passwords.get(spinner.getSelectedItemPosition());
            adminFlag = Integer.parseInt(adminFlags.get(spinner.getSelectedItemPosition()));
            name = names.get(spinner.getSelectedItemPosition());
        }
        catch (Exception ignored){}
        SC = new SMSCommand(numberText,password);
        try {
            SC.isCorrect();
        }
        catch (NullPointerException e){
            isCorrect = false;
        }
        if(v.getId() == R.id.buttonNumbers){
            intent = new Intent(MainActivity.this, SocketsActivity.class);
            startActivity(intent);}
        else {
            if (isCorrect) { //проверка на правильность номера
                switch (v.getId()) {
                    case R.id.buttonOn: //включение
                        SC.sendCommand(getString(R.string.C_On), adminFlag);
                        break;
                    case R.id.buttonOff:  //выключение
                        SC.sendCommand(getString(R.string.C_Off), adminFlag);
                        break;
                    case R.id.buttonSettings: //переход в активность с настройками розетки
                        intent = new Intent(MainActivity.this, SocketSettingsActivity.class);
                        intent.putExtra("phone_number", numberText);
                        intent.putExtra("password",password);
                        intent.putExtra("admin_flag",adminFlag);
                        startActivity(intent);
                        break;
                    case R.id.buttonFamilyMembers: //переход в активность с членами семьи
                        intent = new Intent(MainActivity.this, FamilyMembersActivity.class);
                        intent.putExtra("phone_number", numberText);
                        intent.putExtra("password",password);
                        intent.putExtra("admin_flag",adminFlag);
                        intent.putExtra("socketname",name);
                        startActivity(intent);
                        break;
                }
            } else {
                if (numberText == null) {
                    Log.d("onClick", "error : incorrect number");
                    Toast.makeText(getApplicationContext(), "Неверный номер", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}

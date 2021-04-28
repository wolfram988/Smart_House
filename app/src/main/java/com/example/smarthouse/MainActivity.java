package com.example.smarthouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
    Dialog dialogConfirm;
    NecessaryDBTools necessaryDBTools;//доп. класс для работы с базой данных
    Spinner spinner; //выпадающий список
    SharedPreferences sharedPreferences;
    Intent intent;//намерение
    String numberText; //номер телефона(строка)
    String password; //пароль
    String name;
    String command_buffer;
    SMSCommand SC; // класс для работы с командами и смс
    ArrayAdapter adapter; //адаптер выпадающего списка
    ArrayList<String> phones; //телефоны
    ArrayList<String> passwords; //пароли
    ArrayList<String> adminFlags;//флаги на ввод пароля
    ArrayList<String> names;
    int adminFlag; //флаг на ввод пароля
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
        if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS},
                    0);
        } //проверка разрешений


    }
    public void onResume() {
        super.onResume();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
        else if (v.getId() == R.id.buttonSettings){
            intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        else {
            if (isCorrect) { //проверка на правильность номера
                dialogConfirm = new Dialog(this);
                switch (v.getId()) {
                    case R.id.buttonOn: //включение
                         if(sharedPreferences.getBoolean("confirm",true)){
                             dialogConfirm.setContentView(R.layout.dialog_confirm);
                             command_buffer = getString(R.string.C_On);
                             dialogConfirm.show();
                         }
                         else{
                         SC.sendCommand(getString(R.string.C_On), adminFlag);
                         }
                        break;
                    case R.id.buttonOff:  //выключение
                        if(sharedPreferences.getBoolean("confirm",true)){
                            dialogConfirm.setContentView(R.layout.dialog_confirm);
                            command_buffer = getString(R.string.C_Off);
                            dialogConfirm.show();
                        }
                        else{
                        SC.sendCommand(getString(R.string.C_Off), adminFlag);}
                        break;
                    case R.id.buttonFamilyMembers: //переход в активность с членами семьи
                        intent = new Intent(MainActivity.this, FamilyMembersActivity.class);
                        intent.putExtra("phone_number", numberText);
                        intent.putExtra("password",password);
                        intent.putExtra("admin_flag",adminFlag);
                        intent.putExtra("socketname",name);
                        startActivity(intent);
                        break;
                    case R.id.buttonStatus:
                        if(sharedPreferences.getBoolean("confirm",true)){
                            dialogConfirm.setContentView(R.layout.dialog_confirm);
                            command_buffer = getString(R.string.C_GetSocketState);
                            dialogConfirm.show();
                        }
                        else{
                        SC.sendCommand(getString(R.string.C_GetSocketState),adminFlag);}
                        break;
                }
            } else {
                if (numberText == null) {
                    Log.d("onClick", "error : null number");
                    Toast.makeText(getApplicationContext(), "Не выбрана розетка!", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
    public void onConfDClick(View view){
        switch (view.getId()){
            case R.id.button_yes:
                SC.sendCommand(command_buffer,adminFlag);
                dialogConfirm.cancel();
            case R.id.button_no:
                dialogConfirm.cancel();
        }
    }
}

package com.example.smarthouse;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SocketsActivity extends AppCompatActivity {
    DBHelper dbHelper; //помощник для работы с SQLite
    NecessaryDBTools necessaryDBTools; //доп класс для работы с SQLite
    ContentValues contentValues; //помощник для работы с данными для SQLite
    SMSCommand smsCommand; //доп класс для работы с номерами и смс
    ListView listView; //список
    SimpleAdapter adapter; //адаптер списка
    ArrayList<String> names; //имена
    ArrayList<String> phones; //имена
    Resources res; //ресурсы андроид
    Intent intent; //намерение
    Dialog dialogAdd,dialogDelete; //диалоговые окна
    Switch d_adminSwitch;//переключатель ввода пароля
    int adminFlag = 0; //переменная ввода пароля
    int deletePosition;
    EditText d_editPassword,d_editName, d_editPhone;//поля ввода пароля,имени,телефона
    final String TABLE_NAME = "numbers";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers);
        setTitle(R.string.sockets_list);//заголовок окна
        dbHelper = new DBHelper(this);
        smsCommand = new SMSCommand();
        necessaryDBTools = new NecessaryDBTools(dbHelper,TABLE_NAME);
        res = getResources();
        listView = findViewById(R.id.listview);
        dialogDelete = new Dialog(this);
        dialogDelete.setContentView(R.layout.dialog_delete_socket);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                deletePosition = position;
                TextView textView = dialogDelete.findViewById(R.id.title);
                textView.setTextSize(18);
                dialogDelete.show();

                return true;
            }
        });
        updateAdapter();
    } //действия при запуске приложения
    public void onClick(View v){
        contentValues = new ContentValues();
        dialogAdd = new Dialog(this);
        switch (v.getId()) {

            case R.id.buttonAdd:
                dialogAdd.setContentView(R.layout.dialog_add_socket);
                d_adminSwitch = dialogAdd.findViewById(R.id.switch1);
                d_editPassword = dialogAdd.findViewById(R.id.editPassword);
                d_editPassword.setVisibility(View.GONE);
                d_editName = dialogAdd.findViewById(R.id.editName);
                d_editPhone = dialogAdd.findViewById(R.id.editPhone);
                d_adminSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                           d_editPassword.setVisibility(View.VISIBLE);
                            adminFlag = 1;
                            Log.d("LOG_TAG", "onCheckedChanged: " + adminFlag);
                        } else {
                            d_editPassword.setVisibility(View.GONE);
                            adminFlag = 0;
                            Log.d("LOG_TAG", "onCheckedChanged: " + adminFlag);
                        }
                    }
                });
                dialogAdd.show();
                break;
            case R.id.buttonBack:
                intent = new Intent(SocketsActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonClear:
                necessaryDBTools.clearSQLiteTable(TABLE_NAME);
                updateAdapter();
                break;
            case R.id.buttonSettings:
                break;
        } //1344
    }//обработчик кнопок
    public void onDialogAddClick(View v) {
        switch (v.getId()){
            case R.id.buttonDAdd: //кнопка добавить
                //добавление розетки без пароля
                if(adminFlag == 0) {
                    if (!d_editName.getText().toString().equals("")
                            && !d_editPhone.getText().toString().equals("")) { //проверка на заполненность полей
                        if(smsCommand.isCorrect(d_editPhone.getText().toString())){ //проврка валидности номера телефона
                            necessaryDBTools.putSocketToSQLite(
                                    d_editName.getText().toString(),
                                    d_editPhone.getText().toString(),
                                    null, adminFlag);
                        updateAdapter();
                        dialogAdd.cancel();}
                        else{
                            Toast.makeText(this,"Неверный номер",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Log.d("NumbersActivity", "onDialogClick: error");
                        Toast.makeText(this,"Не все поля заполнены",Toast.LENGTH_SHORT).show();
                    }

                }

                //добавление розетки с паролем
                else if(adminFlag == 1){
                    if (!d_editName.getText().toString().equals("") &&
                            !d_editPhone.getText().toString().equals("") &&
                            !d_editPassword.getText().toString().equals("")) {
                        if(smsCommand.isCorrect(d_editPhone.getText().toString())){
                            necessaryDBTools.putSocketToSQLite(
                                d_editName.getText().toString(),
                                d_editPhone.getText().toString(),
                                d_editPassword.getText().toString(),
                                adminFlag);
                        updateAdapter();
                        dialogAdd.cancel();}
                        else{
                            Toast.makeText(this,"Неверный номер",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Log.d("NumbersActivity", "onDialogClick: error");
                        Toast.makeText(this,"Не все поля заполнены",Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.buttonDCancel: //кнопка отмена
                dialogAdd.cancel();
                break;
        }
    } //обработчик кнопок в диалоговом окне
    public void onDialogDeleteClick(View v){
        switch (v.getId()){
            case R.id.buttonDYes:
              necessaryDBTools.deleteFromSQLite(names.get(deletePosition));
              updateAdapter();
              dialogDelete.cancel();
                break;
            case R.id.buttonDNo:
                dialogDelete.cancel();
                break;
        }
    } //обработчиик кнопок в диалоговом окне удаления номера
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

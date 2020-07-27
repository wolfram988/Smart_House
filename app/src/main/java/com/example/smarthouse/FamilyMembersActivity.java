package com.example.smarthouse;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class FamilyMembersActivity extends AppCompatActivity {
    Intent intent; //намерение
    DBHelper dbHelper;
    ListView listView; //список
    NecessaryDBTools necessaryDBTools;//вспомогательный класс
    ArrayList<String> names;
    ArrayList<String> phones;
    Resources res; //ресурсы андроид
    SimpleAdapter adapter;
    String phone,password, socketname;
    SMSCommand smsCommand;
    int adminFlag;
    Dialog dialogAdd,dialogDelete;
    EditText d_editName,d_editPhone;

    public static final String TABLE_NAME = "family";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_members);
        dbHelper = new DBHelper(this);
        setTitle(R.string.family_members_list);//заголовок окна
        necessaryDBTools = new NecessaryDBTools(dbHelper,TABLE_NAME);
        smsCommand = new SMSCommand();
        listView = findViewById(R.id.listview);
        intent = getIntent();
        phone = intent.getStringExtra("phone_number");
        Log.i("family", "phone: "+phone);
        adminFlag = intent.getIntExtra("admin_flag",5);
        Log.i("family", "adminflag: "+adminFlag);
        socketname = intent.getStringExtra("socketname");
        Log.i("family","socket_name: "+ socketname);
        if(adminFlag == 1){
            password = intent.getStringExtra("password");
            Log.i("family", "password: "+password);
        }
        else
        {password = "";}
        updateAdapter();
    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.buttonAdd:
                dialogAdd = new Dialog(this);
                dialogAdd.setContentView(R.layout.dialog_add_family_member);
                d_editName = dialogAdd.findViewById(R.id.editName);
                d_editPhone = dialogAdd.findViewById(R.id.editPhone);
                dialogAdd.show();
                break;
            case R.id.buttonBack:  //переход на главный экран
                intent = new Intent(FamilyMembersActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonClear:
                necessaryDBTools.clearFamilyMembers(TABLE_NAME,socketname);
                updateAdapter();
                break;
        }
    }//обработка нажатий

    public void onDialogAddClick(View view){
        switch (view.getId()){
            case R.id.buttonDAdd:
                if(!d_editName.equals("") && !d_editPhone.equals("")){
                    if(smsCommand.isCorrect(d_editPhone.getText().toString())){
                        if(!names.contains(d_editName.getText().toString())){
                            necessaryDBTools.putFamilyMemberToSQLite(d_editName.getText().toString(),d_editPhone.getText().toString(), socketname);
                            dialogAdd.hide();
                            updateAdapter();
                        }
                        else{
                            Toast.makeText(this,"Член семьи с таким именем уже существует",Toast.LENGTH_SHORT);
                        }
                    }
                    else {
                        Toast.makeText(this,"Неверный номер телефона",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this,"Не все поля заполнены",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.buttonDCancel:
                dialogAdd.hide();
                break;
        }
    }
    public void updateAdapter(){
        names = new ArrayList<>();
        names = necessaryDBTools.getFamilyMembersFromSQLite("name",socketname);
        phones = new ArrayList<>();
        phones = necessaryDBTools.getFamilyMembersFromSQLite("phone",socketname);
        adapter = new SimpleAdapter(this,
                necessaryDBTools.putMapstoList(names,phones),
                android.R.layout.simple_list_item_2,
                new String[]{"Name", "Phone"},
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
    } //обновление данных в списке и устанока адаптера списка
}

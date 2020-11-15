package com.example.smarthouse;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
    Dialog dialogAdd,dialogDelete,dialogEdit;
    String selectedFamilyMember, selectedPhone;
    EditText editNewName,editNewPhone;

    public static final String TABLE_NAME = "family";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_members);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFamilyMember = names.get(i);
                selectedPhone = phones.get(i);
                showPopupMenu(view);

                return true;
            }
        });
    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.buttonAdd:
                dialogAdd = new Dialog(this);
                dialogAdd.setContentView(R.layout.dialog_add_family_member);
                dialogAdd.show();
                break;
            case R.id.buttonClear:
                necessaryDBTools.clearFamilyMembers(TABLE_NAME,socketname);
                updateAdapter();
                break;
        }
    }//обработка нажатий
    private void showPopupMenu(View v){
        final PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popupmenu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_edit:

                        dialogEdit = new Dialog(FamilyMembersActivity.this);
                        dialogEdit.setContentView(R.layout.dialog_edit_family_member);
                        editNewName = dialogEdit.findViewById(R.id.editName);
                        editNewPhone = dialogEdit.findViewById(R.id.editPhone);
                        editNewName.setText(selectedFamilyMember);
                        editNewPhone.setText(selectedPhone);
                        editNewPhone.setEnabled(false);
                        dialogEdit.show();
                        popupMenu.dismiss();

                        return true;

                    case R.id.menu_delete:

                        dialogDelete = new Dialog(FamilyMembersActivity.this);
                        dialogDelete.setContentView(R.layout.dialog_delete_family_member);
                        TextView textView = dialogDelete.findViewById(R.id.title);
                        textView.setTextSize(18);
                        dialogDelete.show();
                        popupMenu.dismiss();

                        return true;

                    default: return false;
                }
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    public void onDialogAddClick(View view){
        switch (view.getId()){
            case R.id.buttonDApply:
                EditText d_editName,d_editPhone;
                d_editName = dialogAdd.findViewById(R.id.editName);
                d_editPhone = dialogAdd.findViewById(R.id.editPhone);
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
    public void onDialogDeleteClick(View view){
        switch (view.getId()){
            case R.id.buttonDApply:
                necessaryDBTools.deleteFromSQLite(selectedFamilyMember);
                updateAdapter();
                dialogDelete.hide();
                break;
            case R.id.buttonDNo:
                dialogDelete.hide();
                break;
        }
    }
    public void onDialogEditClick(View view){
        switch (view.getId()){
            case R.id.buttonDApply:
                
                necessaryDBTools.edit("name",selectedFamilyMember,editNewName.getText().toString());
                updateAdapter();
                dialogEdit.hide();
                break;
            case R.id.buttonDCancel:
                dialogEdit.hide();
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package com.example.smarthouse;

//import android.telephony.SmsManager;
import android.util.Log;

public class SMSCommand {
    String TAG = "SMSComand"; //заголовок для лога
    String number; //номер телефона
    String password; //пароль
    SMSCommand(String number,String password){
        this.number = number;
        this.password = password;
    } //конструктор класса
    SMSCommand(){} //конструктор класса для работы с функциями

    public void sendCommand(String command,int admin){
        TAG = "LOG_TAG";
        if (admin == 0){
            Log.d(TAG, "sendCommand: "+ command+" number: "+number);
            //SmsManager.getDefault().sendTextMessage(number, null, command, null, null);
        }
        else if (admin == 1){
            Log.d(TAG, "sendCommand: "+ command + password+ "#" + " number: " + number);
        //SmsManager.getDefault().sendTextMessage(number, null, command+password+"#", null, null);
            }
    }
    public boolean isCorrect() {
        return (number.matches("^\\+[\\(\\-]?(\\d[\\(\\)\\-]?){10}\\d$") ||
                //ИЛИ один раз откр.скобочка, 9 раз цифры, 1 раз скобочки и - скобочки по одному разу и в конце 10-я цифра
                number.matches("^\\(?(\\d[\\-\\(\\)]?){10}\\d$"));
    }
    public boolean isCorrect(String number) {
        return (number.matches("^\\+[\\(\\-]?(\\d[\\(\\)\\-]?){10}\\d$") ||
                number.matches("^\\(?(\\d[\\-\\(\\)]?){10}\\d$"));
    }


}

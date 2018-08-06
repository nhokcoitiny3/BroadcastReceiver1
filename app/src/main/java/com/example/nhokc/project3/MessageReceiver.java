package com.example.nhokc.project3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MessageReceiver extends BroadcastReceiver {
    private List<String> prefixList = new ArrayList<>();
    private MyDatabaseHelper myDatabaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        myDatabaseHelper = new MyDatabaseHelper(context);
        prefixList = myDatabaseHelper.getAllPrefix();
        proccessMessage(context, intent);

    }

    public void proccessMessage(Context context, Intent intent) {
        String sms_extra = "pdus";
        Bundle bundle = intent.getExtras();
        Object[] objArr = (Object[]) bundle.get(sms_extra);
        String sms = "";
        for (int i = 0; i < objArr.length; i++) {
            SmsMessage smsMsg = SmsMessage.
                    createFromPdu((byte[]) objArr[i]);
            String body = smsMsg.getMessageBody();
            String address = smsMsg.getDisplayOriginatingAddress();
            sms += address + ":\n" + body + "\n";
            for (String s : prefixList) {
                if (s.equals(address)) {
                    deleteSms(context, address);
                    Intent speechIntent = new Intent();
                    speechIntent.setClass(context,MainActivity.class);
                    speechIntent.putExtra("KEY", "ok");
                    speechIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(speechIntent);
                //    abortBroadcast();
                }
            }
        }
    }

    public void deleteSms(Context context, String phoneNumber) {
        Cursor c = context.getContentResolver().query(
                Uri.parse("content://sms/inbox"), new String[]{
                        "_id", "thread_id", "address", "person", "date", "body"}, null, null, null);
        try {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                long threadId = c.getLong(1);
                String address = c.getString(2);
                if (address.equals(phoneNumber)) {
                    Log.d("", "Deleting SMS with id: " + id);
                    context.getContentResolver().delete(Uri.parse("content://sms/inbox"),"thread_id=?",new String[]{String.valueOf(threadId)});
                   //Thread.sleep(10000);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}

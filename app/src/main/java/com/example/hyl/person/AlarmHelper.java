package com.example.hyl.person;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmHelper {

    private Context c;
    private AlarmManager mAlarmManager;

    public AlarmHelper(Context c) {
        this.c = c;
        mAlarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
    }

    public void openAlarm(int id, String title, String content, long time) {
        Intent intent = new Intent();
        intent.setClass(c, CallAlarm.class);
        //等待的Intent

        //避免了闹钟的覆盖
        PendingIntent pi = PendingIntent.getBroadcast(c, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);//更新之前PendingIntent的消息
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);//硬件闹钟，当闹钟发射时唤醒手机休眠；
    }
}

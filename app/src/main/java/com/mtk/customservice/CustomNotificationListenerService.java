package com.mtk.customservice;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.mtk.data.PreferenceData;
import com.mtk.util.NotifiUtils;
import com.vdurmont.emoji.EmojiParser;


/**
 * Created by Dandy on 08-03-2018.
 */

public class CustomNotificationListenerService extends NotificationListenerService {

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        // Implement what you want here
    }
    @Override
    public void onNotificationPosted(final StatusBarNotification sbn){
        //String Category = sbn.getNotification().category;
        //String[] PackagedCategory = { CATEGORY_CALL,CATEGORY_EMAIL,CATEGORY_MESSAGE,CATEGORY_REMINDER,CATEGORY_SOCIAL,CATEGORY_ALARM};
        //Set<String> Categoryhash = new HashSet<String>(Arrays.asList(PackagedCategory));
        boolean isAllowed = !PreferenceData.isNotificationPrivate();//Categoryhash.contains(Category);
        if(isAllowed) {
            sendToIp(sbn);
        }
    }

    public void  sendToIp(StatusBarNotification sbn)   {

        String text = " ";
        String title = " ";
        String packagename=getApplicationInfo().packageName;
        Bundle extras = extras = sbn.getNotification().extras;
        try {
            text = extras.getCharSequence("android.text").toString();
            title = extras.getString("android.title");
            if(text==null){text = " ";}
            if(title==null){title = " ";}
        }
        catch (Exception e)
        {
            Log.i("Notification error",e.toString());
            text = " ";
            title = " ";
            //skip notification
        }
        if(sbn.getPackageName()!=null)
        {
        packagename=sbn.getPackageName();
        }
        if(!" ".equals(title) && !" ".equals(text)) {
            NotifiUtils.sendAppNotificationToWatch(getApplicationContext(), EmojiParser.parseToAliases(title, EmojiParser.FitzpatrickAction.REMOVE), EmojiParser.parseToAliases(text, EmojiParser.FitzpatrickAction.REMOVE), packagename);
        }
    }
}
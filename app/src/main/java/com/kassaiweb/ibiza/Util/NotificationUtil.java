package com.kassaiweb.ibiza.Util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kassaiweb.ibiza.Notification.Notification;

public class NotificationUtil {

    public static void sendNotification(String title, String body, String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference messageRef = database.getReference("messages").push();
        messageRef.setValue(new Notification(title, body, userId));
    }
}

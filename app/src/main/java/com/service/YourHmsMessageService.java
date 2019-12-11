package com.service;

import android.util.Log;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

public class YourHmsMessageService extends HmsMessageService {
String TAG="hmsmsg";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle HCM messages here.
        // Check if message contains a data payload.
        if (remoteMessage.getData().length() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Job.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received HCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}

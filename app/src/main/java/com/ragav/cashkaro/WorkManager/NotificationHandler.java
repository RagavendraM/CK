package com.ragav.cashkaro.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ragav.cashkaro.ListActivity;

import com.ragav.cashkaro.R;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;


public class NotificationHandler extends Worker {

    public NotificationHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("WorkManager","doWork");
        createNotification();
        return Result.success();
    }

    public static void oneTimeRequest(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.e("WorkManager","oneTimeRequest");
            LocalTime alarmTimeMorning = LocalTime.of(10,00);
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            LocalTime nowTime = now.toLocalTime();
            if(nowTime==alarmTimeMorning || nowTime.isAfter(alarmTimeMorning)){
                  now = now.plusDays(1);
            }
            now = now.withHour(alarmTimeMorning.getHour()).withMinute(alarmTimeMorning.getMinute()); // .withSecond(alarmTime.second).withNano(alarmTime.nano)
            Duration duration = Duration.between(LocalDateTime.now(), now);

            OneTimeWorkRequest workRequestMorning = new OneTimeWorkRequest.Builder(NotificationHandler.class).setInitialDelay(duration.getSeconds(),TimeUnit.SECONDS).build();
            WorkManager.getInstance().enqueueUniqueWork("MorningTask", ExistingWorkPolicy.REPLACE,workRequestMorning);

            Log.e("WorkManager","oneTimeRequest");
            LocalTime alarmTimeEvening = LocalTime.of(10,00);
            LocalDateTime now1 = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            LocalTime nowTime1 = now1.toLocalTime();
            if(nowTime1==alarmTimeEvening || nowTime1.isAfter(alarmTimeEvening)){
                now = now.plusDays(1);
            }
            now = now.withHour(alarmTimeEvening.getHour()).withMinute(alarmTimeEvening.getMinute()); // .withSecond(alarmTime.second).withNano(alarmTime.nano)
            Duration duration1 = Duration.between(LocalDateTime.now(), now);

            OneTimeWorkRequest workRequestEvening = new OneTimeWorkRequest.Builder(NotificationHandler.class).setInitialDelay(duration.getSeconds(),TimeUnit.SECONDS).build();
            WorkManager.getInstance().enqueueUniqueWork("EveningTask", ExistingWorkPolicy.REPLACE,workRequestEvening);
        }

    }

    public void createNotification(){

        //intent to open our activity
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);

        //notifications
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"TASK")
//                .setSmallIcon(R.drawable.ic_share)
                .setContentTitle("Event Reminder")
                .setContentText("Good Morning..Please check out latest updates")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_share))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        //show notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel("TASK" , "CASH" , importance) ;
            assert notificationManagerCompat != null;
            notificationManagerCompat.createNotificationChannel(notificationChannel) ;
        }
            Log.e("Ragav","notific triggereed");
        notificationManagerCompat.notify(1,builder.build());
    }

}

package com.gaga.messagehost;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class PCMessageReceiver extends BroadcastReceiver {
    protected ActivityManager mActivityManager;

    public PCMessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        boolean isTop =  isTopActivity(getTopTask(),"com.gaga.messagehost","com.gaga.messagehost.InExportActivity");
        System.out.println("是否在前台？ " + isTop);

        if (!isTop){
            intent.setClass(context,InExportActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else {
            //动态发送广播给导入导出activity，用他来进行消息的显示
            Intent i = new Intent(SynchronizationService.BROADCAST_MESSAGE);
            i.putExtra(SynchronizationService.BROADCAST_PC, intent.getStringExtra("order"));
            context.sendBroadcast(i);
        }
    }





    public ActivityManager.RunningTaskInfo getTopTask() {
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            return tasks.get(0);
        }

        return null;
    }

    public boolean isTopActivity(
            ActivityManager.RunningTaskInfo topTask,
            String packageName,
            String activityName) {
        if (topTask != null) {
            ComponentName topActivity = topTask.topActivity;

            if (topActivity.getPackageName().equals(packageName) &&
                    topActivity.getClassName().equals(activityName)) {
                return true;
            }
        }

        return false;
    }
}

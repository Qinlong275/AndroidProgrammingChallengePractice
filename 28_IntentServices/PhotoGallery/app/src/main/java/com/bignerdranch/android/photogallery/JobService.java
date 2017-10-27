package com.bignerdranch.android.photogallery;

/**
 * Created by 秦龙 on 2017/10/4.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by Leo on 2017/7/22.
 */

public class JobService extends android.app.job.JobService {

    private static final String TAG = "JobService";

    private PollTask mCurrentTask;

    @Override
    public boolean onStartJob(JobParameters parms){ //服务启动后执行
        mCurrentTask = new PollTask();
        mCurrentTask.execute(parms); //开启AsyncTask
        Log.i(TAG,"Start Job");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if(mCurrentTask != null){
            mCurrentTask.cancel(true);
        }
        Log.i(TAG,"Stop Job");
        return false;
    }


    public static void setServiceAlarm(Context context,Boolean shouldStartAlarm){ //Fragment中启动服务需要调用的方法.用于在Fragment启动JobService
        final int JOB_ID = 1;
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if(shouldStartAlarm){
            scheduler.cancel(JOB_ID);
        }else{
            JobInfo jobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(context, JobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(1000*60*15)
                    .setPersisted(true)
                    .build();
            scheduler.schedule(jobInfo);
        }
    }

    //判断是否已经计划好了任务
    public static boolean isHasBeenScheduled(Context context){

        final int JOB_ID = 1;

        JobScheduler scheduler = (JobScheduler)context.getSystemService(JOB_SCHEDULER_SERVICE);

        boolean hasBeenScheduled = false;

        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == JOB_ID) {
                hasBeenScheduled = true;
            }
        }

        return hasBeenScheduled;
    }

    public JobService(){
        super();
    }


    //用于执行
    private class PollTask extends AsyncTask<JobParameters,Void ,List<GalleryItem>> {
        //在doInBackground()方法中获取到最新的结果集。
        @Override
        protected List<GalleryItem> doInBackground(JobParameters... params) {
            JobParameters jobParams = params[0];

            //不需要手动判断网络连接状态了

            String query = QueryPreferences.getStoredQuery(JobService.this); //获取查询值

            List<GalleryItem> items;

            //获取最新结果集
            if(query == null){
                items = new FlickrFetchr().fetchRecentPhotos();
            }else{
                items = new FlickrFetchr().searchPhotos(query);
            }

            jobFinished(jobParams,false);

            return items; //将items返回出去， onPostExcute()方法会接受到
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            String lastResultId = QueryPreferences.getLastResultId(JobService.this);
            //获取第一条结果
            String resultId = items.get(0).getId();
            //确认是否不同于上一次结果ID ，不同的话弹出Notification。
            if(resultId.equals(lastResultId)){
                Log.i(TAG, "Got a old result" + resultId);
            }else{
                Log.i(TAG, "Got a new result" + resultId);

                Resources resources = getResources();
                Intent i = PhotoGalleryActivity.newIntent(JobService.this);
                PendingIntent pi = PendingIntent.getActivity(JobService.this,0,i,0);

                Notification notification = new NotificationCompat.Builder(JobService.this)
                        .setTicker(resources.getString(R.string.new_pictures_title))
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentTitle(resources.getString(R.string.new_pictures_title))
                        .setContentText(resources.getString(R.string.new_pictures_text))
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(JobService.this);
                notificationManager.notify(0,notification);
            }
            //将第一条结果存入SharedPreferences
            QueryPreferences.setLastResultId(JobService.this,resultId);
        }

    }
}

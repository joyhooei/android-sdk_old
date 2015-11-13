package org.weilan;

import java.util.Timer;  
import java.util.TimerTask;  

import android.app.Activity;  
import android.content.Intent;  
import android.os.Bundle;  
import android.os.Handler;  
import android.os.Message;  
import android.view.MotionEvent;  

import ${PACKAGE_NAME}.R;

public class SplashActivity extends Activity {  
    private long startTime;  
    private boolean touched=false;  
    private Timer timer ;  

    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        this.setContentView(R.layout.splash);
        //开启 定时器  
        timer = new Timer(true);  
        startTime = System.currentTimeMillis();  
        timer.schedule(task, 2000);  
    }


    private final TimerTask task = new TimerTask() {  
        public void run() {  
            Message message = new Message();  
            message.what = 0;  
            timerHandler.sendMessage(message);  
            timer.cancel();  
            this.cancel();  
        }  
    };


    private final Handler timerHandler = new Handler() {  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
                case 0:  
                    SplashActivity.this.finish();  
                    // 跳转到新的 activity  
                    Intent intent = new Intent(SplashActivity.this, poem.class);
                    SplashActivity.this.startActivity(intent);  
                    break;  
            }  
            super.handleMessage(msg);  
        }  
    };  
}  

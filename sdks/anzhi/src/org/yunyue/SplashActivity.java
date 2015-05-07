package org.yunyue;

import java.util.Timer;  
import java.util.TimerTask;  

import android.app.Activity;  
import android.content.Intent;  
import android.os.Bundle;  
import android.os.Handler;  
import android.os.Message;  
import android.view.MotionEvent;  

import com.yunyue.nzgl.anzhi.R;

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
        timer.schedule(task, 0, 1);  
    }


    private final TimerTask task = new TimerTask() {  
        public void run() {  
            if (task.scheduledExecutionTime() - startTime == 2000 || touched) {  
                Message message = new Message();  
                message.what = 0;  
                timerHandler.sendMessage(message);  
                timer.cancel();  
                this.cancel();  
            }  

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


    /** 
     * 点击直接跳转 
     */  
    public boolean onTouchEvent(MotionEvent event) {  
        if (event.getAction() == MotionEvent.ACTION_DOWN) {  
            touched = true;  
        }  
        return true;  
    }  
}  

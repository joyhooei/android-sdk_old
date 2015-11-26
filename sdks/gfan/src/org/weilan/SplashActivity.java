package org.weilan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mappn.sdk.pay.GfanPay;
import com.mappn.sdk.pay.GfanPayInitCallback;

public class SplashActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GfanPay.getInstance(getApplicationContext()).init(this,new GfanPayInitCallback() {

            @Override
            public void onSuccess() {
                //闪屏关闭成功
                SplashActivity.this.finish();
                // 跳转到新的 activity
                Intent intent = new Intent(SplashActivity.this, poem.class);
                SplashActivity.this.startActivity(intent);
            }

        @Override
            public void onError() {
                Toast.makeText(getApplicationContext(), "初始化失败，请退出重试", Toast.LENGTH_SHORT).show();
                SplashActivity.this.finish();
            }
        });

    }
}

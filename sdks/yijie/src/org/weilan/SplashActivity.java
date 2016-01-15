package org.weilan;

import android.content.Intent;
import android.graphics.Color;

import com.snowfish.cn.ganga.helper.SFOnlineSplashActivity;

public class SplashActivity extends SFOnlineSplashActivity {
    public int getBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public void onSplashStop() {
        Intent intent = new Intent(this, poem.class);
        startActivity(intent);
        this.finish();
    }
}


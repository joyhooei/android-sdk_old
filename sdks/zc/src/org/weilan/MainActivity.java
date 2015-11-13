package org.weilan;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.HandlerThread;
import android.widget.Toast;
import android.util.Log;
import com.tendcloud.appcpa.TalkingDataAppCpa;

public class MainActivity extends poem {
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        TalkingDataAppCpa.init(this.getApplicationContext(), getMetaData("TD_APPID"), getMetaData("TD_CHANNEL"));
	}

    @Override
    public void doTalkDataRegister(String username){
        Log.v("sdk", "talkingdata onRegister username: " + username);
        //注册成功
        TalkingDataAppCpa.onRegister(username);
    }

    @Override
    public void doTalkDataLogin(String username ){
        Log.v("sdk", "talkingdata onLogin username: " + username);
        //登录成功
        TalkingDataAppCpa.onLogin(username);
    }

    @Override
    public void doTalkDataCreateRole(String rolename ){
        Log.v("sdk", "talkingdata onCreateRole rolename: " + rolename);
        //创建角色成功
        TalkingDataAppCpa.onCreateRole(rolename);
    }

}

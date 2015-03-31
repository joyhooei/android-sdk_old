package com.wandoujia.mariosdk.plugin.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnMessageReceivedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnPayFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnSinglePayFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.model.MessageEntity;
import com.wandoujia.mariosdk.plugin.api.model.model.PayResult;


/**
 * @author liuxv@wandoujia.com
 */
public class MarioPluginDemoActivity extends Activity {

  private WandouGamesApi wandouGamesApi;
  private static final String TAG = "MarioPluginDemoActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    wandouGamesApi = MarioPluginApplication.getWandouGamesApi();

    wandouGamesApi.init(this);

    findViewById(R.id.call_activity).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!wandouGamesApi.isLoginned()) {}
        wandouGamesApi.startLeaderboardActivity(null);
      }
    });

    findViewById(R.id.call_manager).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        double score = Math.random() * 10000;
        wandouGamesApi.submitScore(0, score);
        Toast.makeText(MarioPluginDemoActivity.this, "score is " + score, Toast.LENGTH_SHORT)
            .show();


        wandouGamesApi.startAchievementActivity(null);
      }
    });

    findViewById(R.id.call_account).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
       wandouGamesApi.login();
      }
    });



    wandouGamesApi.registerMessageListener(new OnMessageReceivedListener() {
      @Override
      public void onMessageReceived(MessageEntity entity) {
        Toast.makeText(MarioPluginDemoActivity.this, entity.getMessageContent(), Toast.LENGTH_LONG)
            .show();

      }
    });

    final EditText orderDescInput = (EditText) findViewById(R.id.desc);
    final EditText orderPriceInput = (EditText) findViewById(R.id.money);

    findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          String orderDesc = orderDescInput.getText().toString();
          long orderPriceInFen =
              (long) (Float.parseFloat(orderPriceInput.getText().toString()) * 100);
          wandouGamesApi.pay(MarioPluginDemoActivity.this, orderDesc, orderPriceInFen,
              "GameOutTradeNo007", new OnPayFinishedListener() {
                @Override
                public void onPaySuccess(PayResult payResult) {

                }

                @Override
                public void onPayFail(PayResult payResult) {

                }
              });
        } catch (NumberFormatException exception) {
          Log.w(TAG, "Price input parse error: " + exception.toString());
        }
      }
    });


    findViewById(R.id.pay_single).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          String orderDesc = orderDescInput.getText().toString();
          long orderPriceInFen =
              (long) (Float.parseFloat(orderPriceInput.getText().toString()) * 100);
          wandouGamesApi.singlePay(MarioPluginDemoActivity.this, orderDesc, orderPriceInFen,
              "GameOutTradeNo008", new OnSinglePayFinishedListener() {
                @Override
                public void onPaySuccess(PayResult payResult) {

                }

                @Override
                public void onPayFail(PayResult payResult) {

                }

                @Override
                public void onMobilePay(PayResult payResult) {

                }
              });
        } catch (NumberFormatException exception) {
          Log.w(TAG, "Price input parse error: " + exception.toString());
        }
      }
    });

    findViewById(R.id.game_gift).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        wandouGamesApi.startGameGiftActivity("com.gameloft.android.GAND.GloftSMIF");
      }
    });

    findViewById(R.id.game_account).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        wandouGamesApi.startAccountActivity();
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    wandouGamesApi.onResume(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    wandouGamesApi.onPause(this);
  }

}

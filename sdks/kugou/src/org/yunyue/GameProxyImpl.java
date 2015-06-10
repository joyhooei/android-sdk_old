package org.yunyue;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

public class GameProxyImpl extends GameProxy{

    public boolean supportLogin() {
        return false;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return false;
    }

}

package com.xugaoxiang.mqttclientandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xugaoxiang.mqttclientandroid.mqtt.MqttManager;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    public static final String URL = "tcp://10.10.10.48:1883";

    private String userName = "admin";

    private String password = "password";

    private String clientId = null;

    private static final String TOPIC = "shopping";
    private static final String TOPIC_MESSAGE = "What a nice day! Go shopping with me?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);
        clientId = getAddressMacByIf();

        if (clientId == null) {
            clientId = getSerialNum();
        }

        //Toast.makeText(getApplicationContext(), "Client id is " + clientId, Toast.LENGTH_LONG).show();

        //连接
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = MqttManager.getInstance().creatConnect(URL, userName, password, clientId);
                        Log.d(TAG,"isConnected: " + b);
                    }
                }).start();
            }
        });

        //订阅
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MqttManager.getInstance().subscribe(TOPIC, 2);
                    }
                }).start();
            }
        });

        //发布
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MqttManager.getInstance().publish(TOPIC, 2, TOPIC_MESSAGE.getBytes());
                    }
                }).start();
            }
        });

        //断开连接
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MqttManager.getInstance().disConnect();
                        } catch (MqttException e) {

                        }
                    }
                }).start();

            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 订阅接收到的消息
     * 这里的Event类型可以根据需要自定义, 这里只做基础的演示
     *
     * @param message
     */
    @Subscribe
    public void onEvent(MqttMessage message) {
        Log.d(TAG,message.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    public String getAddressMacByIf () {
        try  {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                //Toast.makeText(this, nif.getName() + nif.getHardwareAddress(), Toast.LENGTH_LONG).show();
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }
                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02x", b));
                    }
                    return res1.toString();
                }
            }
        } catch (Exception e) {
            Log.e("MobileAccess", "Error lecture propritiete Address MAC ");
        }
        return null;
    }

    public String getSerialNum() {
        String serialNum = null;

        try {
            Class <?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serialNum = (String)get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(this, "serialNo is " + serialNum, Toast.LENGTH_LONG).show();
        return serialNum;
    }
}

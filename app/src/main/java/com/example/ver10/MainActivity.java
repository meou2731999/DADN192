package com.example.ver10;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ver10.ui.home.HomeFragment;
import com.example.ver10.ui.home.HomeViewModel;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;


    MQTTHelper mqttHelper;
    public void startMQTT(){

        mqttHelper = new MQTTHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
            }

            @Override
            public void connectionLost(Throwable throwable) {
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString().substring(1,mqttMessage.toString().length()-1));

                JSONObject jsonObject = new JSONObject(mqttMessage.toString().substring(1,mqttMessage.toString().length()-1));
                String device_id = jsonObject.getString("device_id");
                JSONArray valuesArray = jsonObject.getJSONArray("values");

                int temparature = Integer.parseInt(valuesArray.getString(0));
                int humidity = Integer.parseInt(valuesArray.getString(1));

                Log.w("Showdata",device_id + ": temp = " + temparature + ", humi = " + humidity);

                //homeFragment.setValue(temparature,humidity);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public void sendDataToMQTT(final String ID, final String value1, final String value2){
//        //final Timer aTimer = new Timer();
//        //TimerTask aTask = new TimerTask() {
//            //@Override
//            //public void run() {
//
//            }
//        };
//        //aTimer.schedule(aTask, 10000, 10000);
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);

        String data = "[{\"device_id\":\"Speaker\", \"values\":[\"" + value1 + "\",\"" + value2 + "\"]}]";
        byte[] b = data.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish("Topic/Speaker", msg);
            Log.e("publish","[{\"device_id\":\"" + ID + "\", \"values\":[\"" + value1 + "\",\"" + value2 + "\"]}]");

        }catch (MqttException e){
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_area, R.id.nav_setting, R.id.nav_notify)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        startMQTT();
        //sendDataToMQTT("Speaker","1","1000");
        FragmentManager fragmentManager = getSupportFragmentManager();
        HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentById(R.id.fragmentHome);
        //homeFragment.setValue(15,25);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

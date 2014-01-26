package com.famanson.motivator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;

public class Alarm extends BroadcastReceiver
{
    private long DEFAULT_POINTS_GOAL = 1000;

    private String dbPath = "/data/data/com.misfitwearables.prometheus/databases/shine.db";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) < 18) {
            // Exit if it is not after working hours
            return;
        }
        Log.i("MOTIVATOR", "Received.");
        long now = calendar.getTimeInMillis();

        Process p;
        try {
            //p = Runtime.getRuntime().exec("su -c 'sqlite3 /data/data/com.misfitwearables.prometheus/databases/shine.db \"select progressDataJson from goal;\"'");
            p = Runtime.getRuntime().exec(new String[] {"su", "-c", "sqlite3",
                    dbPath,
                    "\"select progressDataJson, startTime from goal;\""});
            p.waitFor();

            String line, todayStat = null;
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while((line=input.readLine()) != null){
                // split line into the json and the timestamp
                List<String> results = Lists.newArrayList(Splitter.on("|").split(line));
                long stamp = Long.valueOf(results.get(1))*1000;
                long delta = Math.abs(now - stamp);
                if (delta < 24*3600*1000) {
                    // Here is the latest stat
                    todayStat = results.get(0);
                    break;
                }
            }

            if (todayStat != null) {
                ObjectMapper mapper = new ObjectMapper();
                Stat stat = mapper.readValue(todayStat, Stat.class);
                if (stat.getPoints() > DEFAULT_POINTS_GOAL) {
                    makeToast(context, "You've reached your goal, enjoy your internet");
                    turnData(context, true);
                } else {
                    makeToast(context, "You've failed your goal, no internet till that's done");
                    turnData(context, false);
                }
            } else {
                makeToast(context, "You've failed your goal, no internet till that's done");
            }

            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    void turnData(Context context, boolean on) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class iConnectivityManagerClass =  Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(iConnectivityManager, on);

        // And wifi
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(on);
    }

    private void makeToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
package edu.temple.stockmarketapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;


public class UpdateStockService extends IntentService {

    FileOutputStream fos;
    private static final String tag = "debuggg";
    ObjectOutputStream oos;
    File path2, symbolListFile;
    String filename, link;
    FileInputStream fip;
    ObjectInputStream oip;
    List<String> symbolList;
    JSONObject obj;
    RequestQueue queue;
    int i;

    public UpdateStockService() {
        super("UpdateStockService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            queue = Volley.newRequestQueue(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        path2 = Environment.getExternalStorageDirectory();
                        filename = path2.getAbsolutePath() + "/symbol-list.txt";
                        symbolListFile = new File(filename);

                            try {
                                fip = new FileInputStream(filename);
                                oip = new ObjectInputStream(fip);
                                symbolList = (List<String>) oip.readObject();


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                        path2 = Environment.getExternalStorageDirectory();


                        for (i = 0; i<symbolList.size(); i++) {
                            link = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?" +
                                    "symbol=" + symbolList.get(i);

                            JsonObjectRequest json = new JsonObjectRequest
                                    (Request.Method.GET, link, null, new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject response) {

                                            obj = response;
                                            filename = path2.getAbsolutePath() + "/stock-" + Integer.toString(i) + ".txt";
                                            if (obj.has("Name")) {
                                                try {
                                                    fos = new FileOutputStream(filename);
                                                    oos = new ObjectOutputStream(fos);
                                                    oos.writeChars("Name: " + obj.optString("Name") + "\n" + "Current Price: " + obj.optDouble("LastPrice") + "\n");
                                                    //Log.d(tag, "inside onresponse");
                                                    oos.close();
                                                    fos.close();
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                Log.d(tag,"Not found");
                                            }

                                        }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println(" Error.");
                            }
                        });
                        queue.add(json);
                    }


                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            // Restore interrupt status.
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }).start();



        }
    }


}

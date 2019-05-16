package edu.temple.stockmarketapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StockFragment.OnFragmentInteractionListener {
    private static final String tag = "debuggg";
    EditText symbol;
    FloatingActionButton findButton;
    StockFragment stockFragment;
    DetailsFragment detailsFragment;
    String link,filename;
    RequestQueue queue;
    JSONObject obj;
    String member,line;
    Intent intent;
    FileOutputStream fos;
    ObjectOutputStream oos;
    File path, file;
    TextView display;
    BufferedReader br;
    StringBuilder text;
    ImageView chart;
    Drawable d;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        stockFragment = new StockFragment();
        detailsFragment = new DetailsFragment();
        intent = new Intent(getApplicationContext(), UpdateStockService.class);
        startService(intent);

        getFragmentManager().beginTransaction().add(R.id.stockList,stockFragment).addToBackStack(null).commit();
        getFragmentManager().beginTransaction().replace(R.id.stockDetails,detailsFragment).addToBackStack(null).commit();

        queue = Volley.newRequestQueue(this);

        findButton = (FloatingActionButton) findViewById(R.id.findStockButton);
        symbol = (EditText) findViewById(R.id.symbol);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (symbol.getText().toString() != "") {
                    link = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?" +
                            "symbol=" + symbol.getText().toString();



                    JsonObjectRequest json = new JsonObjectRequest
                            (Request.Method.GET, link, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {

                                    obj = response;
                                    if (obj.has("Name")) {
                                        stockFragment.emptyMessage.setVisibility(View.INVISIBLE);
                                        stockFragment.stockList.setVisibility(View.VISIBLE);
                                        member = obj.optString("Symbol");
                                        stockFragment.stockAndSymbolList.add(member);
                                        stockFragment.myAdapter.notifyDataSetChanged();


                                        try {
                                            path = Environment.getExternalStorageDirectory();
                                            filename = path.getAbsolutePath() + "/symbol-list.txt";
                                            fos = new FileOutputStream(filename);

                                            oos = new ObjectOutputStream(fos);
                                            oos.writeObject(stockFragment.stockAndSymbolList);
                                            //Log.d(tag, "inside onresponse");
                                            oos.close();
                                            fos.close();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

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
            }
        });
    }

    public void showDetails(int i) {


        path = Environment.getExternalStorageDirectory();
        filename = path.getAbsolutePath() + "/stock-" + Integer.toString(i) + ".txt";
        file = new File(filename);
        display = (TextView) findViewById(R.id.details) ;
        Log.d(tag, "skrt");
        try {
            br = new BufferedReader(new FileReader(file));
            text = new StringBuilder();
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }

            String url = "http://www.google.com/finance/chart?q=" + stockFragment.stockAndSymbolList.get(i).toString() + "&p=1d";
            try {
                InputStream is = (InputStream) new URL(url).getContent();
                d = Drawable.createFromStream(is, "src name");
                chart = findViewById(R.id.chart);
                chart.setImageDrawable(d);
            } catch (Exception e) {
                e.printStackTrace();
            }

            display.setText(text.toString());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package edu.temple.stockmarketapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


public class StockFragment extends Fragment {
    private static final String tag = "debugg:";
    GridView stockList;
    CustomAdapter myAdapter;
    public OnFragmentInteractionListener mListener;
    public List<String> stockAndSymbolList;
    TextView emptyMessage;
    File symbolListFile,path;
    FileInputStream fip;
    ObjectInputStream oip;
    String filename;

    public StockFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement the interface");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_stock, container, false);

        stockList = (GridView) v.findViewById(R.id.stockList);
        emptyMessage = (TextView) v.findViewById(R.id.emptyMessage);

        path = Environment.getExternalStorageDirectory();
        filename = path.getAbsolutePath() + "/symbol-list.txt";
        symbolListFile = new File(filename);
        if (symbolListFile.exists()) {
            emptyMessage.setVisibility(View.INVISIBLE);
            stockList.setVisibility(View.VISIBLE);
            try {
                Log.d(tag, "here");
                fip = new FileInputStream(filename);
                oip = new ObjectInputStream(fip);
                stockAndSymbolList = (List<String>) oip.readObject();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(tag, "there");
            stockAndSymbolList = new ArrayList<String>();
        }

        myAdapter = new CustomAdapter(getActivity(),stockAndSymbolList);
        stockList.setAdapter(myAdapter);
        stockList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mListener.showDetails(i);
                Log.d(tag,"huncho");

            }
        });
        return v;
    }

    public interface OnFragmentInteractionListener {
        public void showDetails(int i);
    }
}

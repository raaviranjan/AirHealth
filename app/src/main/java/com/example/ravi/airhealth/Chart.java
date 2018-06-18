package com.example.ravi.airhealth;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Ravi on 07-Jun-18.
 */

public class Chart extends Fragment {
    WebView mWebView1;
    TextView tvLoc;
    String channel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.chart,viewGroup,false);

        SharedPreferences preferences = getActivity().getSharedPreferences("SHAR_PREF_NAME", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("chart", true);
        editor.apply();

        mWebView1 = view.findViewById(R.id.webview1);
        tvLoc = view.findViewById(R.id.tvLoc);
        Bundle bundle = getArguments();
        tvLoc.setText(bundle.getString("CurrentLoc"));
        channel = bundle.getString("ChannelID");

        mWebView1.getSettings().setJavaScriptEnabled(true);
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Please wait...", true);
        mWebView1.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                pd.show();
                super.onPageStarted(view, url, favicon);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                pd.dismiss();
                super.onPageFinished(view, url);
            }

    });

        mWebView1.loadUrl("http://www.airhealth.info/chart-view/pollution/"+channel);

        return view;

    }
}

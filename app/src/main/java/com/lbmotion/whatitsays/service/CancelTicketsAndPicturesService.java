package com.lbmotion.whatitsays.service;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lbmotion.whatitsays.R;
import com.lbmotion.whatitsays.managers.SendTicketsAndPictures;

/**
 * Created by witman on 20/03/2018.
 */

public class CancelTicketsAndPicturesService extends Activity {

    private Button buttonStop,buttonClose;
    private TextView message;
    private String msg;
    private boolean allowStop = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      requestWindowFeature(Window.FEATURE_NO_TITLE);
//      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_tickets_and_pictures_service);
        (new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    Thread.sleep(0);} catch (InterruptedException ie) {}
                int t = SendTicketsAndPictures.getNumberOfTicketsWaiting();
                int p = SendTicketsAndPictures.getNumberOfPicturesWaiting();
                if(t+p == 0)
                    allowStop = true;
                msg = getString(R.string.check_picture_text);
                msg = msg.replace("X",t+"");
                msg = msg.replace("Y",p+"");
                return Boolean.valueOf(true);
            }
            @Override
            protected void onPostExecute(Boolean result) {
                message.setText(msg);
//              if(allowStop)
//                  buttonStop.setEnabled(true);
                buttonClose.setEnabled(true);
            }
        }).execute();
        message = (TextView) findViewById(R.id.check_picture_text);
        message.setText(R.string.loading);
/*
        buttonStop = (Button) findViewById(R.id.stop_running);
        buttonStop.setEnabled(false);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UCApp.doExitNow = true;
                Parameters.update("cold_startup","True");
                SendTicketsAndPictures.stopIt();
                finish();
                System.exit(0);
            }
        });
*/
        buttonClose = (Button) findViewById(R.id.close_sreen);
        buttonClose.setEnabled(false);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendTicketsAndPictures.stopIt();
                finish();
            }
        });
    }
}



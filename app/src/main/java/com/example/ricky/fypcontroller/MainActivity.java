package com.example.ricky.fypcontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Button btnF, btnB,btnL,btnR,btnConn;
    EditText textIP;
    TextView msg;
    InetAddress ip;
    private Thread thread;
    private Socket socket;
    private BufferedWriter bw;
    private BufferedReader br;
    private String tmp;

    private View.OnTouchListener DpadListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    if(v==btnF)
                    {
                        msg.setText("f");
                    }
                    else if(v==btnB)
                    {
                        msg.setText("b");
                    }
                    else if(v==btnR)
                    {
                        msg.setText("r");
                    }
                    else if(v==btnL)
                    {
                        msg.setText("l");
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }
            return true;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnF = (Button) findViewById(R.id.btnF);
        btnB = (Button) findViewById(R.id.btnB);
        btnL = (Button) findViewById(R.id.btnL);
        btnR = (Button) findViewById(R.id.btnR);
        btnConn = (Button) findViewById(R.id.btnConn);
        textIP = (EditText) findViewById(R.id.textIP);
        msg = (TextView) findViewById(R.id.msg);
        btnConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread = new Thread(Connection);
                thread.start();
            }
        })  ;

        btnF.setOnTouchListener(DpadListener);
        btnB.setOnTouchListener(DpadListener);
        btnL.setOnTouchListener(DpadListener);
        btnR.setOnTouchListener(DpadListener);


    }

    private Runnable Connection=new Runnable() {
        @Override
        public void run() {
            try
            {
                ip = InetAddress.getByName(textIP.getText().toString());
                int port = 7689;
                socket = new Socket(ip,port);
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while(socket.isConnected())
                {
                    tmp = br.readLine();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    };

}

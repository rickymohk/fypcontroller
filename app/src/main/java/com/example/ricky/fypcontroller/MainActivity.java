package com.example.ricky.fypcontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.Math.floor;

public class MainActivity extends AppCompatActivity {
    Button btnF, btnB,btnL,btnR,btnConn,btnCam,btnCenter;
    EditText textIP;
    TextView msg;
    InetAddress ip;
    private Thread thread;
    private Socket socket;
    private OutputStream os;
    private String tmp;
    private byte dpadState;
    private String currentMotion;
    String status;
    float dX,dY;

    private View.OnTouchListener DpadListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    if(v==btnF)
                    {
                        dpadState |= 1;
                    }
                    else if(v==btnB)
                    {
                        dpadState |= 2;
                    }
                    else if(v==btnR)
                    {
                        dpadState |= 4;
                    }
                    else if(v==btnL)
                    {
                        dpadState |= 8;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(v==btnF)
                    {
                        dpadState &= ~1;
                    }
                    else if(v==btnB)
                    {
                        dpadState &= ~2;
                    }
                    else if(v==btnR)
                    {
                        dpadState &= ~4;
                    }
                    else if(v==btnL)
                    {
                        dpadState &= ~8;
                    }
                    break;
            }
            String motion = dpadState==0?"s":dpadState==1?"f":dpadState==2?"b":dpadState==4?"r":dpadState==8?"l":currentMotion;
            if(!motion.equals(currentMotion))
            {
                currentMotion = motion;
                msg.setText(currentMotion);
                if(status.equals("Connected!"))
                {
                    try
                    {
                        os.write(currentMotion.getBytes());
                    }
                    catch (Exception e)
                    {
                        msg.setText(e.getMessage());
                    }
                }

            }

            return true;
        }
    };
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnF = (Button) findViewById(R.id.btnF);
        btnB = (Button) findViewById(R.id.btnB);
        btnL = (Button) findViewById(R.id.btnL);
        btnR = (Button) findViewById(R.id.btnR);
        btnConn = (Button) findViewById(R.id.btnConn);
        btnCam = (Button) findViewById(R.id.btnCam);
        btnCenter = (Button) findViewById(R.id.btnCenter);
        textIP = (EditText) findViewById(R.id.textIP);
        msg = (TextView) findViewById(R.id.msg);
        btnConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                thread = new Thread(Connection);
                thread.start();
            }
        })  ;
        currentMotion = "s";
        dpadState = 0;
        status = "init";
        btnF.setOnTouchListener(DpadListener);
        btnB.setOnTouchListener(DpadListener);
        btnL.setOnTouchListener(DpadListener);
        btnR.setOnTouchListener(DpadListener);
        btnCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parent = (View)btnCam.getParent();
                double width = parent.getWidth()-btnCam.getWidth();
                double height = parent.getHeight()-btnCam.getHeight();
                double centerX = width/2;
                double centerY = height/2;
                btnCam.animate().x((float)centerX).y((float)centerY).setDuration(500).start();
                msg.setText("c");
                if(status.equals("Connected!"))
                {
                    try
                    {
                        os.write("c".getBytes());
                    }
                    catch (Exception e)
                    {
                        msg.setText(e.getMessage());
                    }
                }
            }
        });
        btnCam.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View parent = (View)v.getParent();
                double width = parent.getWidth()-v.getWidth();
                double height = parent.getHeight()-v.getHeight();
                double centerX = width/2;
                double centerY = height/2;
                double scaleX = -1024/width;
                double scaleY = 1024/height;

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if((event.getRawX() + dX)>0 && (event.getRawX() + dX)<width)
                        {
                            v.animate().x(event.getRawX() + dX).setDuration(0).start();
                        }
                        if((event.getRawY() + dY)>0 && (event.getRawY() + dY)<height)
                        {
                            v.animate().y(event.getRawY() + dY).setDuration(0).start();
                        }
                        break;
                    default:
                        return false;
                }



                int angleX = (int)Math.floor((v.getX()-centerX)*scaleX);
                int angleY = (int)Math.floor((v.getY()-centerY)*scaleY);
                msg.setText(angleX+","+angleY);
                if(status.equals("Connected!"))
                {
                    String cmd = "m" + angleX + "\n" + angleY + "\n";
                    try
                    {
                        os.write(cmd.getBytes());
                    }
                    catch (Exception e)
                    {
                        msg.setText(e.getMessage());
                    }
                }
                return true;
            }
        });

    }

    private Runnable Connection=new Runnable() {
        @Override
        public void run() {
            try
            {

                ip = InetAddress.getByName(textIP.getText().toString());
                int port = 7689;
                status = "Connecting to " + textIP.getText().toString();
                runOnUiThread(UpdateUI);
                socket = new Socket(ip,port);
                try
                {
                    if(socket.isConnected()) {
                        status = "Connected!";
                        runOnUiThread(UpdateUI);
                        os = socket.getOutputStream();
                    }
                }
                catch(Exception e)
                {
                    status = e.getMessage();
                    runOnUiThread(UpdateUI);
                }
            }
            catch(Exception e)
            {
               status = e.getMessage();
                runOnUiThread(UpdateUI);
            }
        }
    };

    private Runnable UpdateUI = new Runnable() {
        @Override
        public void run() {
            msg.setText(status);
        }
    };

}

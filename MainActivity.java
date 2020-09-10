package com.yoctoverse.www.csvirtualg;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.widget.Switch;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.EditText;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import android.view.KeyEvent;
import android.widget.Toast;
import io.github.controlwear.virtual.joystick.android.JoystickView;




public class MainActivity extends AppCompatActivity implements  SensorEventListener {
    private Socket socket;

    private static final int SERVERPORT = 8001;


    static  float scaleh = 2F;
    static float scalev = 2F;
    static float z1 = 0F;
    static float y1 = 0F;
    static float z2 = 0F;
    static float y2 = 0F;

    static float zz1 = 0F;
    static float yy1 = 0F;
    static float zz2 = 0F;
    static float yy2 = 0F;
    static float zz3 = 0F;
    static float yy3 = 0F;

    static float zz0 = 0F;
    static float yy0 = 0F;

    private  float[] mAccelerometerReading = new float[3];
    private float[] mMagnetometerReading = new float[3];

    private  float[] mRotationMatrix = new float[9];
    private float[] mOrientationAngles = new float[3];





    private static   String SERVER_IP = "192.168.1.6";
    private SensorManager sensorManager;
    public void scalerh(View view)
    {
        String  tex;
        EditText textt=  findViewById(R.id.scaleeh);
        tex = textt.getText().toString();

        scaleh  =  Float.valueOf(tex);
    }
    public void scalerv(View view)
    {
        String  tex;
        EditText textt=  findViewById(R.id.scaleev);
        tex = textt.getText().toString();

        scalev  =  Float.valueOf(tex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


        }

    }

    public void starter(View view)
    {

        EditText textt=  findViewById(R.id.address);
        SERVER_IP = textt.getText().toString();
        try
        {

            new Thread(new ClientThread()).start();
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_GAME);
          //  sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR),SensorManager.SENSOR_DELAY_GAME);
        }
        catch(Exception e)
        {
            textt.setText(("Please Enter correct ip"));
        }
    }
    public void stopper(View view)
    {
        try
        {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


            char f = 's';
            if(out == null){}
            else
            { out.println(f);}

        }

        catch (IOException e)
        {

            e.printStackTrace();

        }

        try
        {


            socket.close();

            sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));

        }
        catch(Exception e)
        {

        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }
    public static byte [] float2ByteArray (float value)
    {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {

        if(event.sensor.getType() ==Sensor.TYPE_GYROSCOPE)
        {
            float x = event.values[1];
            float y = event.values[0];
            float z = event.values[2];

            Switch simpleSwitch = (Switch) findViewById(R.id.switch1);
            Boolean switchState = simpleSwitch.isChecked();

            if(switchState)
            {
                y=0;
                z=0;
            }

            y  = (y + yy1+ yy2 + yy3)/4;
            z = (z + zz1 + zz2  + zz3)/4;




            yy3 = yy2;
            yy2 = yy1;
            yy1 = y;

             zz3 = zz2;
             zz2 = zz1;
             zz1 = z;

            y = scalev *y/100;
            z = scaleh*z/100;
            int zz,yy;
            zz = (int) (100*z);
            yy = (int) (100*y);
            TextView a = findViewById(R.id.hh);
            String mytext1=Float.toString(z);
            a.setText(mytext1);
            TextView g = findViewById(R.id.vv);
            String mytext2=Float.toString(y);
            g.setText(mytext2);
            byte [] b ;
            byte [] t ;

            b = float2ByteArray(y);
            t = float2ByteArray(z);
            byte[] c = new byte[b.length + t.length];

            System.arraycopy(b, 0, c, 0, t.length);
            System.arraycopy(t, 0, c, b.length, t.length);


            char rec ='m';
            byte [] h1 = new byte[1];
            h1[0] =  (byte) rec;

            byte []d = new byte[h1.length + c.length];

            System.arraycopy(h1, 0, d, 0, h1.length);
            System.arraycopy(c, 0, d, h1.length, c.length);


            try
            {
                //    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                DataOutputStream  sout = new DataOutputStream(socket.getOutputStream());

                //      sout.writeInt(num);
                // out.println(mytext3);
                // out.print(zVal);
                if(sout ==null){}
                else {
                    sout.write(d);
                }
                try {


                    Thread.sleep(7);
                }
                catch (Exception e){}

              //sout.close();
            }
            catch (IOException e)
            {

                e.printStackTrace();

            }
        }



        }

// Remove this and create a

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HEADSETHOOK){

         //   Toast.makeText(getApplicationContext(),"Fire",Toast.LENGTH_SHORT).show();
            try
            {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


                char f = 'F';
                if(out == null){}
                else
                { out.println(f);}


            }

            catch (IOException e)
            {

                e.printStackTrace();

            }


            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            try
            {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


                char f = 'w';
                if(out == null){}
                else
                { out.println(f);}


            }

            catch (IOException e)
            {

                e.printStackTrace();

            }


            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            try
            {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


                char f = 'x';
                if(out == null){}
                else
                { out.println(f);}


            }

            catch (IOException e)
            {

                e.printStackTrace();

            }


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HEADSETHOOK){


            try
            {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


                char f = 'f';
                if(out == null){}
                else
                { out.println(f);}

            }

            catch (IOException e)
            {

                e.printStackTrace();

            }

            return true;


        }
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            try
            {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


                char f = 'W';
                if(out == null){}
                else
                { out.println(f);}


            }

            catch (IOException e)
            {

                e.printStackTrace();

            }


            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            try
            {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


                char f = 'X';
                if(out == null){}
                else
                { out.println(f);}


            }

            catch (IOException e)
            {

                e.printStackTrace();

            }


            return true;
        }

        return super.onKeyDown(keyCode, event);
    }




    public void reloader(View view)
    {
        try
        {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


            char f = 'r';
            if(out == null){}
            else
            { out.println(f);}

        }

        catch (IOException e)
        {

            e.printStackTrace();

        }


    }


    class ClientThread implements Runnable
    {
        @Override
        public void run()
        {

            try
            {

                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                try
                {
                    socket = new Socket(serverAddr, SERVERPORT);
                }
                catch ( IOException e1)
                {
                    e1.printStackTrace();
                }

            }
            catch (UnknownHostException   e1)
            {

                e1.printStackTrace();


            }
        }


    }



}




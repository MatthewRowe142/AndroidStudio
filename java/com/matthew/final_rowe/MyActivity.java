package com.matthew.final_rowe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private shake mShake;

    private LayoutInflater layoutInflater;
    private RelativeLayout mainLayout;
    private ImageView mBallImage;
    private ImageView nBallImage;
    private Ball mBall;
    private Ball nBall;

    public boolean bounce = false;

    private Thread movementThread;

    static int TOP;
    static int BOTTOM;
    static int LEFT;
    static int RIGHT;

    private TextView x_axis;
    private TextView y_axis;
    private TextView z_axis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mainLayout = (RelativeLayout) findViewById (R.id.relativeLayout);
        x_axis = (TextView) findViewById(R.id.textView2);
        y_axis = (TextView) findViewById(R.id.textView4);
        z_axis = (TextView) findViewById(R.id.textView6);

        mBall = new Ball();
        nBall = new Ball();
        initializeBall();
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBallImage = (ImageView) layoutInflater.inflate(R.layout.ball_item,null);
        mBallImage.setX(25.0f);
        mBallImage.setY(25.0f);
        mainLayout.addView(mBallImage, 0);
        nBallImage = (ImageView) layoutInflater.inflate(R.layout.ball_item,null);
        nBallImage.setX(25.0f);
        nBallImage.setY(25.0f);
        mainLayout.addView(nBallImage, 0);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        movementThread = new Thread(BallMovement);
        mShake = new shake(new shake.OnShakeListener(){
            @Override
            public void onShake(){
                goCrazy();
            }
        });
    }
    public void goCrazy(){
        bounce = true;
        mBall.setVelocityX((3)*mBall.getVelocityX());
        mBall.setVelocityX((4)*mBall.getVelocityX());
        mBall.setVelocityX((5)*mBall.getVelocityX());
        nBall.setVelocityX((3)*nBall.getVelocityX());
        nBall.setVelocityX((4)*nBall.getVelocityX());
        nBall.setVelocityX((5)*nBall.getVelocityX());
        bounce=false;
    }

    private void initializeBall(){
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        mBall.setX(50.0f);
        mBall.setY(50.0f);
        mBall.setWidth(70);
        mBall.setVelocityX(0.0f);
        mBall.setVelocityY(0.0f);
        nBall.setX(55.0f);
        nBall.setY(400.0f);
        nBall.setWidth(70);
        nBall.setVelocityX(0.0f);
        nBall.setVelocityY(0.0f);
        TOP = 0;
        BOTTOM = (int) (screenHeight - 3*mBall.getWidth());
        LEFT = 0 ;
        RIGHT = (int) (screenWidth - 3*mBall.getWidth());
    }

    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(mShake,sensorAccelerometer,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        movementThread.start();
    }
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this, sensorAccelerometer);
        sensorManager.unregisterListener(mShake, sensorAccelerometer);
    }
    protected void onStop(){
        super.onStop();
        finish();
    }
    @Override
    public void onDestroy(){
        finish();
        super.onDestroy();
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mBall.setVelocityX(sensorEvent.values[0]);
            mBall.setVelocityY(sensorEvent.values[1]);
            nBall.setVelocityX(sensorEvent.values[0]);
            nBall.setVelocityY(sensorEvent.values[1]);
            x_axis.setText(" " + sensorEvent.values[0]);
            y_axis.setText(" " + sensorEvent.values[1]);
            z_axis.setText(" " + sensorEvent.values[2]);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }
    private Runnable BallMovement = new Runnable() {
        private static final int DELAY = 20;
        public void run(){
            try {
                while(true) {

                    mBall.setX(mBall.getX() + mBall.getVelocityX());
                    mBall.setY(mBall.getY() - mBall.getVelocityY());

                    if(!bounce) {
                        if (mBall.getY() < TOP + 1) {
                            mBall.setY(TOP);
                            mBall.setVelocityY(0);
                        } else if (mBall.getY() > BOTTOM - 1) {
                            mBall.setY(BOTTOM);
                            mBall.setVelocityY(0);
                        }

                        if (mBall.getX() < LEFT + 1) {
                            mBall.setX(LEFT);
                            mBall.setVelocityX(0);
                        } else if (mBall.getX() > RIGHT - 1) {
                            mBall.setX(RIGHT);
                            mBall.setVelocityX(0);
                        }
                    }else{
                        if (mBall.getY() < TOP + 1) {
                            mBall.setVelocityY((-5)*mBall.getVelocityY());
                        } else if (mBall.getY() > BOTTOM - 1) {
                            mBall.setVelocityY((-5)*mBall.getVelocityY());
                        }

                        if (mBall.getX() < LEFT + 1) {
                            mBall.setVelocityX((-5)*mBall.getVelocityX());
                        } else if (mBall.getX() > RIGHT - 1) {
                            mBall.setVelocityX((-5)*mBall.getVelocityX());
                        }
                    }

                    Thread.sleep(DELAY);

                    threadHandler.sendEmptyMessage(0);
                    nBall.setX(nBall.getX() + nBall.getVelocityX());
                    nBall.setY(nBall.getY() - nBall.getVelocityY());


                    if(!bounce){
                        if (nBall.getY() < TOP+1) {
                        nBall.setY(TOP);
                        nBall.setVelocityY(0);
                        }
                        else if (nBall.getY() > BOTTOM-1) {
                            nBall.setY(BOTTOM);
                            nBall.setVelocityY(0);
                        }

                        if (nBall.getX() < LEFT+1) {
                            nBall.setX(LEFT);
                            nBall.setVelocityX(0);
                        }
                        else if (nBall.getX() > RIGHT-1) {
                            nBall.setX(RIGHT);
                            nBall.setVelocityX(0);
                        }
                    }else{
                        if (nBall.getY() < TOP + 1) {
                            nBall.setVelocityY((-5)*nBall.getVelocityY());
                        } else if (nBall.getY() > BOTTOM - 1) {
                            nBall.setVelocityY((-5)*nBall.getVelocityY());
                        }

                        if (nBall.getX() < LEFT + 1) {
                            nBall.setVelocityX((-5)*nBall.getVelocityX());
                        } else if (mBall.getX() > RIGHT - 1) {
                            nBall.setVelocityX((-5)*nBall.getVelocityX());
                        }
                    }

                    if(Math.sqrt(Math.pow((nBall.getY()-mBall.getY()),2)+Math.pow((nBall.getX()-mBall.getX()),2))<200){
                        if(!(mBall.getY() > BOTTOM-1 || mBall.getY() < TOP+1)){
                            mBall.setVelocityY((-3) * mBall.getVelocityY());
                        }
                        if(!(mBall.getX() < LEFT+1 || mBall.getX() > RIGHT-1)) {
                            mBall.setVelocityX((-3) * mBall.getVelocityX());
                        }
                        if(!(nBall.getY() > BOTTOM-1 || nBall.getY() < TOP+1)){
                            nBall.setVelocityY((-3) * nBall.getVelocityY());
                        }
                        if(!(nBall.getX() < LEFT+1 || nBall.getX() > RIGHT-1)) {
                            nBall.setVelocityX((-3) * nBall.getVelocityX());
                        }
                    }
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    };
    public Handler threadHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            mBallImage.setX(mBall.getX());
            mBallImage.setY(mBall.getY());
            nBallImage.setX(nBall.getX());
            nBallImage.setY(nBall.getY());
        }
    };

    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
package com.matthew.final_rowe;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
public class shake implements SensorEventListener{
    private long mTimeOfLastShake;
    private static final float SHAKE_THRESHOLD_GRAVITY = 22.0f;
    private static final int SHAKE_TIME_LAPSE = 500;
    private OnShakeListener mShakeListener;

    public shake(OnShakeListener shakeListener){
        mShakeListener = shakeListener;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            float gForceX = x - SensorManager.GRAVITY_EARTH;
            float gForceY = y - SensorManager.GRAVITY_EARTH;
            float gForceZ = z - SensorManager.GRAVITY_EARTH;

            double value = Math.pow(gForceX,2.0)+Math.pow(gForceY,2.0)+Math.pow(gForceZ,2.0);
            float gForce = (float) Math.sqrt(value);
            if(gForce> SHAKE_THRESHOLD_GRAVITY){
                final long now = System.currentTimeMillis();
                if(mTimeOfLastShake+SHAKE_TIME_LAPSE>now){
                    return;
                }
                mTimeOfLastShake = now;
                mShakeListener.onShake();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public interface OnShakeListener{
        public void onShake();

    }
}

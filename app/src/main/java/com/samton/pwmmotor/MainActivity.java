package com.samton.pwmmotor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SocketRequest.IResultCallBack {

    /**
     * 请求对象
     */
    private SocketRequest mRequest = null;

    private PwmMotor pwmMotor = null;

    private String TAG = "Test";

    private EditText mDuty = null;

    private EditText mPeriod = null;

    private int periodNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pwmMotor = new PwmMotor();
        mDuty = (EditText) findViewById(R.id.mDuty);
        mPeriod = (EditText) findViewById(R.id.mPeriod);
        findViewById(R.id.mOpenBtn).setOnClickListener(this);
        findViewById(R.id.mCloseBtn).setOnClickListener(this);

        findViewById(R.id.mChange0Btn).setOnClickListener(this);
        findViewById(R.id.mChange2Btn).setOnClickListener(this);
        findViewById(R.id.mChange3Btn).setOnClickListener(this);

        findViewById(R.id.mChange0BtnInTime).setOnClickListener(this);
        findViewById(R.id.mChange2BtnInTime).setOnClickListener(this);
        findViewById(R.id.mChange3BtnInTime).setOnClickListener(this);

        findViewById(R.id.mLeft2Middle).setOnClickListener(this);
        findViewById(R.id.mLeft2Right).setOnClickListener(this);
        findViewById(R.id.mMiddle2Left).setOnClickListener(this);
        findViewById(R.id.mMiddle2Right).setOnClickListener(this);
        findViewById(R.id.mRight2Middle).setOnClickListener(this);
        findViewById(R.id.mRight2Left).setOnClickListener(this);

        findViewById(R.id.mSetDuty5).setOnClickListener(this);
        findViewById(R.id.mSetDuty15).setOnClickListener(this);
        findViewById(R.id.mSetDuty25).setOnClickListener(this);

        findViewById(R.id.mDanceBtn).setOnClickListener(this);


        new Thread(new Runnable() {
            @Override
            public void run() {
                // 初始化连接对象
                mRequest = new SocketRequest("192.168.100.10", 50000);
                mRequest.setCallBack(MainActivity.this);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        String duty = mDuty.getText().toString().trim();
        String period = mPeriod.getText().toString().trim();
        if (TextUtils.isEmpty(duty)) {
            return;
        }
        int dutyNumber = Integer.valueOf(duty) * 100000;
        periodNumber = Integer.valueOf(period) * 1000000;

        final int mDutyNumber = 5 * 100000;
        final int mPeriodNumber = periodNumber;
        switch (v.getId()) {
            // 打开设备
            case R.id.mOpenBtn:
                if (pwmMotor.openDevices()) {
                    findViewById(R.id.mOpenBtn).setEnabled(false);
                    Toast.makeText(MainActivity.this, "打开设备成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "打开设备失败", Toast.LENGTH_SHORT).show();
                }
                break;
            // 关闭设备
            case R.id.mCloseBtn:
                pwmMotor.closeDevices();
                break;
            // 设置等闲比
            case R.id.mSetDuty5:
                mDuty.setText(5 + "");
                break;
            // 设置等闲比
            case R.id.mSetDuty15:
                mDuty.setText(15 + "");
                break;
            // 设置等闲比
            case R.id.mSetDuty25:
                mDuty.setText(25 + "");
                break;
            // 调节设备
            case R.id.mChange0Btn:
                Log.e(TAG, "0结果为" + pwmMotor.config(0, dutyNumber, periodNumber));
                break;
            // 调节设备
            case R.id.mChange2Btn:
//                pwmMotor.rightArmUp();
                Log.e(TAG, "2结果为" + pwmMotor.config(2, dutyNumber, periodNumber));
                break;
            // 调节设备
            case R.id.mChange3Btn:
//                pwmMotor.leftArmUp();
                Log.e(TAG, "3结果为" + pwmMotor.config(3, dutyNumber, periodNumber));
                break;
            // 调节设备
            case R.id.mChange0BtnInTime:
                Toast.makeText(this, "按下面的按钮！", Toast.LENGTH_SHORT).show();
                break;
            // 调节设备
            case R.id.mChange2BtnInTime:
                pwmMotor.rightArmDown();
                break;
            // 调节设备
            case R.id.mChange3BtnInTime:
                pwmMotor.leftArmDown();
                break;

            case R.id.mLeft2Middle:
                // pwmMotor.headLeft2Middle();
                break;
            case R.id.mLeft2Right:
                // pwmMotor.headLeft2Right();
                break;
            case R.id.mMiddle2Left:
                // pwmMotor.headMiddle2Left();
                break;
            case R.id.mMiddle2Right:
                // pwmMotor.headMiddle2Right();
                break;
            case R.id.mRight2Middle:
                // pwmMotor.headRight2Middle();
                break;
            case R.id.mRight2Left:
                // pwmMotor.headRight2Left();
                break;
            // 跳舞
            case R.id.mDanceBtn:
                dance();
                break;

        }
    }

    /**
     * 跳舞方法
     */
    private void dance() {
        new Step1().start();
    }

    @Override
    public void onResult(String result) {

    }

    @Override
    public void onError(String errorMsg) {

    }

    /**
     * 旋转
     *
     * @param angle 角度
     */
    private void circle(String angle) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Command", "WheelCycle");
            jsonObject.put("Radius", 0);
            jsonObject.put("Angle", angle);
            jsonObject.put("Speed", "0.5");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRequest.sendCommand(jsonObject.toString());
    }

    private class Step1 extends Thread {
        @Override
        public void run() {
            super.run();
            pwmMotor.leftArmUp();
            // pwmMotor.headMiddle2Left();
            try {
                sleep(1500);
                // 开启第二步舞蹈
                new Step2().start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Step2 extends Thread {
        @Override
        public void run() {
            super.run();
            pwmMotor.leftArmDown();
            pwmMotor.rightArmUp();
            // pwmMotor.headLeft2Right();
            try {
                sleep(1500);
                // 开启第三步舞蹈
                new Step3().start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Step3 extends Thread {
        @Override
        public void run() {
            super.run();
            // 底盘转动360
            circle("359");
            try {
                sleep(3000);
                // 开启第四步舞蹈
                new Step4().start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Step4 extends Thread {
        @Override
        public void run() {
            super.run();
            pwmMotor.rightArmDown();
            pwmMotor.leftArmUp();
            // pwmMotor.headRight2Left();
            try {
                sleep(1500);
                // 开启第五步
                new Step5().start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Step5 extends Thread {
        @Override
        public void run() {
            super.run();
            // 底盘转动-360
            circle("-359");
            try {
                sleep(3000);
                // 开启第六步
                new Step6().start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Step6 extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                // pwmMotor.headLeft2Right();
                pwmMotor.leftArmUp();
                pwmMotor.rightArmUp();
                // 底盘向右60 向左120 然后恢复0
                circle("60");
                sleep(2000);
                // pwmMotor.headRight2Middle();
                pwmMotor.leftArmDown();
                pwmMotor.rightArmDown();
                circle("-120");
                sleep(2000);
                circle("60");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

package com.littletree.mypersonalconnectionmap.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.littletree.mypersonalconnectionmap.R;
import com.littletree.mypersonalconnectionmap.utils.SingleTextWatcher;

public class MainActivity extends AppCompatActivity {
    Button btn_commit;
    EditText et_secondnum;
    EditText et_thirdnum;
    EditText et_anglenum;
    EditText et_firstAvatarDiameter;
    EditText et_secondAvatarDiameter;
    EditText et_thirdAvatarDiameter;

    int secondNum = 5;
    int thirdNum = 20;
    int angleNum = 120;

    int diameter_one = 85;
    int diameter_two = 50;
    int diameter_three = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_commit = findViewById(R.id.btn_commit);
        et_secondnum = findViewById(R.id.et_secondnum);
        et_thirdnum = findViewById(R.id.et_thirdnum);
        et_anglenum = findViewById(R.id.et_anglenum);
        et_firstAvatarDiameter = findViewById(R.id.et_firstAvatarDiameter);
        et_secondAvatarDiameter = findViewById(R.id.et_secondAvatarDiameter);
        et_thirdAvatarDiameter = findViewById(R.id.et_thirdAvatarDiameter);

        initlimit();
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //生成人脉图
                if (0!=secondNum&&0!=thirdNum&&0!=angleNum&&0!=diameter_one&&0!=diameter_two&&0!=diameter_three){
                    PersonalConnectionMapActivity.start(MainActivity.this,secondNum,thirdNum,angleNum,diameter_one,diameter_two,diameter_three);
                }else {
                    Toast.makeText(MainActivity.this, "请正确填写数据", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initlimit(){
        SingleTextWatcher ScoreTextWatcher = new SingleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString()) || ".".equals(s.toString())) {
                    secondNum = 0 ;
                } else {
                    secondNum = Integer.parseInt(s.toString());
                    if (secondNum > 8) {
                        secondNum = 8;
                        et_secondnum.setText("8");
                    }
                }
            }
        };
        et_secondnum.addTextChangedListener(ScoreTextWatcher);

        SingleTextWatcher ScoreTextWatcher2 = new SingleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString()) || ".".equals(s.toString())) {
                    thirdNum = 0 ;
                } else {
                    thirdNum = Integer.parseInt(s.toString());
                    if (thirdNum > 1000) {
                        thirdNum = 1000;
                        et_thirdnum.setText("1000");
                    }
                }

            }
        };
        et_thirdnum.addTextChangedListener(ScoreTextWatcher2);

        SingleTextWatcher ScoreTextWatcher3 = new SingleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString()) || ".".equals(s.toString())) {
                    angleNum = 120 ;
                } else {
                    angleNum = Integer.parseInt(s.toString());
                    if (angleNum > 150) {
                        angleNum = 150;
                        et_anglenum.setText("150");
                    }
                }

            }
        };
        et_anglenum.addTextChangedListener(ScoreTextWatcher3);

        SingleTextWatcher ScoreTextWatcher4 = new SingleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString()) || ".".equals(s.toString())) {
                    diameter_one = 85 ;
                } else {
                    diameter_one = Integer.parseInt(s.toString());
                    if (diameter_one > 180) {
                        diameter_one = 180;
                        et_firstAvatarDiameter.setText("180");
                    }
                }

            }
        };
        et_firstAvatarDiameter.addTextChangedListener(ScoreTextWatcher4);

        SingleTextWatcher ScoreTextWatcher5 = new SingleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString()) || ".".equals(s.toString())) {
                    diameter_two = 50 ;
                } else {
                    diameter_two = Integer.parseInt(s.toString());
                    if (diameter_two > 100) {
                        diameter_two = 100;
                        et_secondAvatarDiameter.setText("100");
                    }
                }

            }
        };
        et_secondAvatarDiameter.addTextChangedListener(ScoreTextWatcher5);

        SingleTextWatcher ScoreTextWatcher6 = new SingleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString()) || ".".equals(s.toString())) {
                    diameter_three = 30 ;
                } else {
                    diameter_three = Integer.parseInt(s.toString());
                    if (diameter_three > 60) {
                        diameter_three = 60;
                        et_thirdAvatarDiameter.setText("60");
                    }
                }

            }
        };
        et_thirdAvatarDiameter.addTextChangedListener(ScoreTextWatcher6);
    }
}
package com.littletree.mypersonalconnectionmap.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.littletree.mypersonalconnectionmap.HVScrollView;

import com.littletree.mypersonalconnectionmap.R;
import com.littletree.mypersonalconnectionmap.customview.ImageLayout;
import com.littletree.mypersonalconnectionmap.dialog.Dialog_twobutton_notitle;
import com.littletree.mypersonalconnectionmap.entity.AtmanRelation;
import com.littletree.mypersonalconnectionmap.utils.PUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @ProjectName: MyPersonalConnectionMap
 * @Package: com.littletree.mypersonalconnectionmap
 * @ClassName: PersonalConnectionMapActivity
 * @Author: littletree
 * @CreateDate: 2022/7/19/019 15:45
 */
public class PersonalConnectionMapActivity extends AppCompatActivity {
    HVScrollView hvScrollView;  //滑动view
    ImageLayout shaderImage;

    private float scale = 1;
    private float preScale = 1;// 默认前一次缩放比例为1

    private int mSecondNum; //第二层数量
    private int mThirdNum;  //第三层数量
    private int mAngleNum;  //角度

    private int mDiameter_one;  //第一层头像直径
    private int mDiameter_two;  //第二层头像直径
    private int mDiameter_three;  //第三层头像直径

    private boolean moreFinger = false;  //判断是否多手指
    private ScaleGestureDetector mScaleGestureDetector = null;  //缩放手势类
    private GestureDetector mLongPressGestureListener = null;   //长按手势

    private List<String> picurllist = new ArrayList<>();  //实体数据

    private Dialog_twobutton_notitle dialog_twobutton_notitle;   //保存dialog
    private Dialog_twobutton_notitle.Builder dialog_twobutton_notitlebuilder;
    private TextView save_dialog_title;
    private TextView save_btn_one;
    private TextView save_btn_two;

    public SVProgressHUD mSvp;  //加载框

    public static void start(Context context,int mSecondNum,int mThirdNum,int mAngleNum,int mDiameter_one,int mDiameter_two,int mDiameter_three) {
        Intent starter = new Intent(context, PersonalConnectionMapActivity.class);
        starter.putExtra("secondNum",mSecondNum);
        starter.putExtra("thirdNum",mThirdNum);
        starter.putExtra("angleNum",mAngleNum);
        starter.putExtra("diameter_one",mDiameter_one);
        starter.putExtra("diameter_two",mDiameter_two);
        starter.putExtra("diameter_three",mDiameter_three);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalconnectionmap);
        hvScrollView = findViewById(R.id.hvScrollView);
        shaderImage = findViewById(R.id.shaderImage);

        mSecondNum = getIntent().getIntExtra("secondNum",0);
        mThirdNum = getIntent().getIntExtra("thirdNum",0);
        mAngleNum = getIntent().getIntExtra("angleNum",0);
        mDiameter_one = getIntent().getIntExtra("diameter_one",0);
        mDiameter_two = getIntent().getIntExtra("diameter_two",0);
        mDiameter_three = getIntent().getIntExtra("diameter_three",0);

        mScaleGestureDetector = new ScaleGestureDetector(PersonalConnectionMapActivity.this,
                new ScaleGestureListener());

        mLongPressGestureListener = new GestureDetector(PersonalConnectionMapActivity.this,
                new LongPressGestureListener());

        shaderImage.setOnitemclicklistener(new ImageLayout.OnItemClickListener() {
            @Override
            public void avatarclick(int degree) {
                Toast.makeText(PersonalConnectionMapActivity.this, String.format("第%d层头像被点击",degree), Toast.LENGTH_SHORT).show();
            }
        });

        initgesture();
        initPiclist();
        initData();
        initdialog();
    }

    private void initData(){
        List<AtmanRelation> atmanRelationList = new ArrayList<>();
        AtmanRelation firstatmanRelation = new AtmanRelation();
        firstatmanRelation.setDegree(0);
        firstatmanRelation.setType("");
        firstatmanRelation.setVisitorAvatar(getRandomPicUrl());
        atmanRelationList.add(firstatmanRelation);

        for (int x=0;x<mSecondNum;x++){
            AtmanRelation secondatmanRelation = new AtmanRelation();
            secondatmanRelation.setDegree(1);
            secondatmanRelation.setType(String.valueOf(x+1));
            atmanRelationList.add(secondatmanRelation);
        }

        for (int x=0;x<mThirdNum;x++){
            AtmanRelation secondatmanRelation = new AtmanRelation();
            secondatmanRelation.setDegree(2);

            secondatmanRelation.setType(String.valueOf(((int) (Math.random() * (mSecondNum+1)))));
            secondatmanRelation.setVisitorAvatar(getRandomPicUrl());
            secondatmanRelation.setVisitorName(String.valueOf(x+1));
            atmanRelationList.add(secondatmanRelation);
        }

        hvScrollView.post(new Runnable() {
            @Override
            public void run() {
                shaderImage.setVisibility(View.GONE);
                shaderImage.setmAngleNum(mAngleNum);
                shaderImage.setmDiameter(mDiameter_one,mDiameter_two,mDiameter_three);
                shaderImage.getmaxXYRange(atmanRelationList);
                shaderImage.changeViewRangle(hvScrollView.getWidth(),hvScrollView.getHeight());

                shaderImage.setSourceList(atmanRelationList);
                tryScale(scale, 0.9f);
                mhandler.sendEmptyMessage(11);
            }
        });

    }

    private void initgesture(){
        hvScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (moreFinger) {
                    mScaleGestureDetector.onTouchEvent(motionEvent);
                }
                mLongPressGestureListener.onTouchEvent(motionEvent);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        //第一个手指按下事件
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        //第二个手指按下事件
                        moreFinger = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        moreFinger = false;
                        mhandler.sendEmptyMessage(12);//手指放开来个回弹效果
                        break;
                }
                return moreFinger;
            }
        });
    }

    private void initdialog(){
        dialog_twobutton_notitlebuilder = new Dialog_twobutton_notitle.Builder(PersonalConnectionMapActivity.this);
        dialog_twobutton_notitle = dialog_twobutton_notitlebuilder.create();
        save_dialog_title = dialog_twobutton_notitlebuilder.getDialog_title();
        save_btn_one = dialog_twobutton_notitlebuilder.getBtn_one();
        save_btn_two = dialog_twobutton_notitlebuilder.getBtn_two();

        save_dialog_title.setText("是否保存图片？");
        save_btn_one.setText("取消");
        save_btn_two.setText("确认");

        save_btn_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_twobutton_notitle.dismiss();
            }
        });

        save_btn_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_twobutton_notitle.dismiss();
                ScaleAndSave();
            }
        });
    }

    private void ScaleAndSave() {
        mhandler.sendEmptyMessageDelayed(13, 400);
    }

    private Handler mhandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 11:
                    shaderImage.setVisibility(View.VISIBLE);
                    hvScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            hvScrollView.scrollTo(shaderImage.getChangedX() - PUtil.getScreenW(PersonalConnectionMapActivity.this) / 2, shaderImage.getChangedY() - PUtil.getScreenH(PersonalConnectionMapActivity.this) / 2 + 300);

                        }
                    });
                    break;
                case 12:
                    if (scale < 0.3f) {
                        tryScale(scale, 0.3f);
                    } else if (scale > 1f) {
                        tryScale(scale, 1f);
                    }
                    break;
                case 13:
                    showTip();
                    tryScale(scale, 1f);
                    mhandler.sendEmptyMessageDelayed(14, 400);
                    break;
                case 14:
                    if (null != hvScrollView && 1 == hvScrollView.getChildCount()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap cacheBitmapFromView = getBitmapByView(hvScrollView,shaderImage.aiMaxWidthLength(),shaderImage.aiMaxHighLength());
                                saveImageToGallery(PersonalConnectionMapActivity.this,cacheBitmapFromView);
                            }
                        }).start();
                    }
                    break;
                case 15:
                    dismissTip();
                    break;
                case 16:
                    dismissTip();
                    Toast.makeText(PersonalConnectionMapActivity.this, "图片保存失败", Toast.LENGTH_SHORT).show();
                    break;
                case 17:
                    dismissTip();
                    Toast.makeText(PersonalConnectionMapActivity.this, "图片已经保存至相册", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //缩放手势
    public class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float previousSpan = detector.getPreviousSpan();
            float currentSpan = detector.getCurrentSpan();
            if (currentSpan < previousSpan) {
                // 缩小
                scale = preScale - (previousSpan - currentSpan) / 1000;
                if (scale < 0.2f) {
                    scale = 0.2f;
                }
            } else {
                // 放大
                scale = preScale + (currentSpan - previousSpan) / 1000;
                if (scale > 1.0f) {
                    scale = 1.0f;
                }
            }

            // 缩放view
            shaderImage.setScaleX(scale);
            shaderImage.setScaleY(scale);
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            preScale = scale;//记录本次缩放比例
        }
    }

    //长按手势
    public class LongPressGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            //保存图片dialog
            dialog_twobutton_notitle.show();
        }
    }

    public void tryScale(float current, float target) {

        ValueAnimator animator_shadow = ValueAnimator.ofFloat(current, target);
        animator_shadow.setDuration(200);
        animator_shadow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                scale = (float) valueAnimator.getAnimatedValue();
                preScale = scale;
                shaderImage.setScaleX(scale);
                shaderImage.setScaleY(scale);
            }
        });
        animator_shadow.start();

    }

    private void initPiclist(){
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202107%2F17%2F20210717232533_2edcf.thumb.1000_0.jpg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=d59bf85f17e199a1de8e03bec10f35fb");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202105%2F29%2F20210529001057_aSeLB.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=2c91553fb2afcd0856a4b1e9af25b9ad");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202107%2F09%2F20210709142454_dc8dc.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=cd7af904cd749fbba908f4180dc56a41");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202111%2F26%2F20211126105820_273b6.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=0701a10c9dbcac9479ac3aaf5c776583");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202009%2F23%2F20200923185609_rQUdj.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=7aa65f6fd48505e00a81c9c7228ef3c7");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F201908%2F19%2F20190819150344_ALnaX.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=1de4504cfa4dfb8fe038f5b96ec0b9b0");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202108%2F21%2F20210821060555_67898.thumb.1000_0.jpg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=a5ee09911da37588b72e9a7056e5c97d");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F30%2F20200330091314_yNVUZ.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=7a7ca6594834f5c88cb296d34eac7974");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic_source%2F30%2F90%2F40%2F309040a0602c672cebc6ab3a1bbbc8cd.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=a4b09804f41faf0adac5be102748a0a4");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202107%2F24%2F20210724145000_0fcf1.thumb.1000_0.jpg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=820d1951ab604c0720869dedca9c6239");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic_source%2Fcb%2F35%2F54%2Fcb355440daa8fbeb34cce74a5fee1fd1.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=38d2113514e16704eeff44e7aaaf6c7b");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2F6a%2F6c%2Ff5%2F6a6cf5bf4d8c084bce791092060bd2d7.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=06a8099535e9a129062357c9fce5cc59");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F26%2F20200326194416_vWGTk.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=42bd74091c163bd9a4fa6493aa2047b1");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2Fa3%2Fdd%2F26%2Fa3dd26ee5649ee4841d24495bf381885.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=910a7a9d4db0d4f45e42b9d29baa8cbe");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202102%2F26%2F20210226143822_d462c.thumb.1000_0.png&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=43b7c6ec40bc794acba7312ad5d1dd5c");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202006%2F18%2F20200618233934_tjntn.jpg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=c8bd89f3e7b0f13947502d82313182be");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F25%2F20200325132316_jockg.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660814798&t=7b0d1cca3f22156734a216285fda8eed");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2F5c%2F25%2F22%2F5c252287d2b39d9eac7dcad60d7a3b0e.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660815148&t=9b260cf98ad187571c34f2bc8fc76c95");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fhbimg.huabanimg.com%2F3f731a19ad377739ef2662d98cbcd77af60db6084e78c-xZ4SaM_fw658&refer=http%3A%2F%2Fhbimg.huabanimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660815148&t=266fe643362f08e1c6e04578dac0c753");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202004%2F10%2F20200410102736_cqtxe.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660815148&t=9976211128d6a94a47329d066d81d70c");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202010%2F16%2F20201016210823_22b6e.thumb.400_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660815148&t=7f385553fb475e0ea37a8122be776269");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202005%2F07%2F20200507150245_petgq.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660815148&t=5cdc3a6ffe84c0bd790876923f879f7d");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202006%2F21%2F20200621031631_lggnn.thumb.400_0.png&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660815148&t=57970c29fd47c8759248dec7320351ff");
        picurllist.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F201911%2F08%2F20191108195943_uscfc.thumb.400_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660815203&t=13c58fdd293ae8eb550d7693eeaf8ad0");
    }

    private String getRandomPicUrl(){
        return picurllist.get((int) (Math.random()*(picurllist.size())));
    }

    public static Bitmap getBitmapByView(FrameLayout scrollView, int mallpicwidth, int mallpichigh) {
        Bitmap bitmap = null;

        bitmap = Bitmap.createBitmap(mallpicwidth, mallpichigh,
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    @SuppressLint("CheckResult")
    public void saveImageToGallery(final Context context, final Bitmap bmp) {
        XXPermissions.with(PersonalConnectionMapActivity.this).permission( Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> list, boolean b) {
                        if (b){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    saveImgBitmap(PersonalConnectionMapActivity.this,bmp);
                                }
                            }).start();
                        }

                    }

                    @Override
                    public void noPermission(List<String> list, boolean b) {
                        mhandler.sendEmptyMessage(15);
                        Toast.makeText(context, "权限申请失败", Toast.LENGTH_SHORT).show();
                        XXPermissions.gotoPermissionSettings(PersonalConnectionMapActivity.this);
                    }
                });
    }

    public void saveImgBitmap(Context mcontext, Bitmap cachebmp) {
        try {
            // 首先保存图片 创建文件夹
            File appDir = new File(Environment.getExternalStorageDirectory(), "connectionmap");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            //图片文件名称
            String fileName = "connectionmap" + System.currentTimeMillis() + ".png";
            File file = new File(appDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                cachebmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 其次把文件插入到系统图库
            // 最后通知图库更新

            if (file.exists()){
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                mcontext.sendBroadcast(intent);
                mhandler.sendEmptyMessage(17);
            }
        } catch (Exception e) {
            mhandler.sendEmptyMessage(16);
            e.printStackTrace();
        }
    }

    /**
     * 显示加载框
     */
    public void showTip() {
        if (null == mSvp) {
            mSvp = new SVProgressHUD(PersonalConnectionMapActivity.this);
        }
        if (!mSvp.isShowing()) {
            mSvp.showWithStatus("正在保存...");
        }
    }

    /**
     * 隐藏加载框
     */
    public void dismissTip() {
        if (null == mSvp) {
            mSvp = new SVProgressHUD(PersonalConnectionMapActivity.this);
        }

        if (mSvp.isShowing()) {
            mSvp.dismiss();
        }
    }
}

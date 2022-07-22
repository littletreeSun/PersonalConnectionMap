package com.littletree.mypersonalconnectionmap.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.littletree.mypersonalconnectionmap.R;
import com.littletree.mypersonalconnectionmap.utils.PUtil;
import com.littletree.mypersonalconnectionmap.entity.PointLeo;

import java.util.ArrayList;

/**
 * @ProjectName: MyPersonalConnectionMap
 * @Package: com.littletree.mypersonalconnectionmap
 * @ClassName: ShaderImageView
 * @Author: littletree
 * @CreateDate: 2022/7/19/019 16:48
 */
public class LineImageView extends View {


    //test
    private Paint mPaint;
    private Paint PointPaint;  //8个点
    private ArrayList<PointLeo> points;

    public LineImageView(Context context) {
        this(context, null);
    }

    public LineImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.relationship_line));
        mPaint.setStyle(Paint.Style.STROKE);//设置填充样式
        mPaint.setStrokeWidth(1);//设置画笔宽度

        PointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        PointPaint.setColor(getResources().getColor(R.color.relationship_line));
        PointPaint.setStyle(Paint.Style.STROKE);//设置填充样式
        PointPaint.setStrokeWidth(PUtil.dip2px(getContext(), 5));//设置画笔宽度
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (points != null) {
            for (int i = 0; i < points.size(); i++) {
                PointLeo pointLeo = points.get(i);
                if (1==pointLeo.getEnddegree()){
                    if ("1".equals(pointLeo.getTypestr())){   //咨询视频
                        mPaint.setColor(getResources().getColor(R.color.relationship_zixun));
                        PointPaint.setColor(getResources().getColor(R.color.relationship_zixun));
                    }else if ("2".equals(pointLeo.getTypestr())){   //名片
                        mPaint.setColor(getResources().getColor(R.color.relationship_card));
                        PointPaint.setColor(getResources().getColor(R.color.relationship_card));
                    }else if ("3".equals(pointLeo.getTypestr())){   //宣传册
                        mPaint.setColor(getResources().getColor(R.color.relationship_xuanchuance));
                        PointPaint.setColor(getResources().getColor(R.color.relationship_xuanchuance));
                    }else if ("4".equals(pointLeo.getTypestr())){   //商品
                        mPaint.setColor(getResources().getColor(R.color.relationship_goods));
                        PointPaint.setColor(getResources().getColor(R.color.relationship_goods));
                    }else if ("5".equals(pointLeo.getTypestr())){   //优惠券
                        mPaint.setColor(getResources().getColor(R.color.relationship_youhuijuan));
                        PointPaint.setColor(getResources().getColor(R.color.relationship_youhuijuan));
                    } else if ("6".equals(pointLeo.getTypestr())) {   //小程序
                        mPaint.setColor(getResources().getColor(R.color.relationship_xiaochengxu));
                        PointPaint.setColor(getResources().getColor(R.color.relationship_xiaochengxu_back));
                    } else if ("7".equals(pointLeo.getTypestr())) {   //娱乐
                        mPaint.setColor(getResources().getColor(R.color.relationship_yule));
                        PointPaint.setColor(getResources().getColor(R.color.relationship_yule_back));
                    } else if ("8".equals(pointLeo.getTypestr())) {   //海报
                        mPaint.setColor(getResources().getColor(R.color.relationship_haibao));
                        PointPaint.setColor(getResources().getColor(R.color.relationship_haibao_back));
                    }
                    canvas.drawCircle((pointLeo.getX1()+pointLeo.getX())/2,(pointLeo.getY1()+pointLeo.getY())/2, 5,PointPaint);
                }else {
                    mPaint.setColor(getResources().getColor(R.color.relationship_line));
                }
                PathEffect pathEffect = new DashPathEffect(new float[]{10, 10}, 2);
                mPaint.setPathEffect(pathEffect);

                Path linePath = new Path();
                linePath.moveTo(pointLeo.getX(), pointLeo.getY());
                linePath.lineTo(pointLeo.getX1(), pointLeo.getY1());
                canvas.drawPath(linePath, mPaint);
            }
        }

    }


    public void setLines(ArrayList<PointLeo> points) {
        this.points = points;
        invalidate();
    }



}

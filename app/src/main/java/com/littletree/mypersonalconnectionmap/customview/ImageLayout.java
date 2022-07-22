package com.littletree.mypersonalconnectionmap.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coorchice.library.SuperTextView;
import com.littletree.mypersonalconnectionmap.R;
import com.littletree.mypersonalconnectionmap.RoundImageView;
import com.littletree.mypersonalconnectionmap.entity.AtmanRelation;
import com.littletree.mypersonalconnectionmap.entity.RelationBean;
import com.littletree.mypersonalconnectionmap.utils.PUtil;
import com.littletree.mypersonalconnectionmap.entity.PointLeo;
import com.littletree.mypersonalconnectionmap.entity.RectPoint;

import java.util.ArrayList;
import java.util.List;

import static com.littletree.mypersonalconnectionmap.RoundImageView.RoundMode.ROUND_VIEW;

/**
 * @ProjectName: MyPersonalConnectionMap
 * @Package: com.littletree.mypersonalconnectionmap
 * @ClassName: ImageLayout
 * @Author: littletree
 * @CreateDate: 2022/7/19/019 16:43
 */
public class ImageLayout extends FrameLayout {
    FrameLayout layouPoints;  //用于添加头像的布局
    private LineImageView lineImageView;  //画线以及中心头像的小圆(项目ui图的需要)

    private List<AtmanRelation> sourceList = new ArrayList<>(); //包含1,2,3层的实体类(接口返回的数据)
    private ArrayList<PointLeo> points = new ArrayList<>();//用于连线的。
    private ArrayList<RectPoint> rects = new ArrayList<>();//用于判断重叠的
    private ArrayList<RelationBean> reList_2 = new ArrayList<>();//第二度关系的集合，目的是 为了绘制完一度后再绘制二度

    private int width_degress_one = PUtil.dip2px(getContext(), 85);  //第一层头像直径
    private int width_degress_two = PUtil.dip2px(getContext(), 50);  //第二层头像直径
    private int width_degress_three = PUtil.dip2px(getContext(), 30);   //第三层头像直径
    private Context mcontext;

    private int mAngleNum = 120;   //第三层扩散的角度

    private int LastCenterX;  //理论中心点为1500dp处
    private int LastCenterY;  //理论中心点为1500dp处

    private OnItemClickListener onitemclicklistener;

    //用于计算边缘
    int minX;
    int minY;
    int maxX;
    int maxY;
    ArrayList<RectPoint> myxyrects = new ArrayList<>();//用于判断重叠的
    List<AtmanRelation> XYsourceList = new ArrayList<>();  //用于计算的
    private ArrayList<RelationBean> XYreList_2 = new ArrayList<>();//第二度关系的集合，目的是 为了绘制完一度后再绘制二度

    public ImageLayout(Context context) {
        this(context, null);
    }

    public ImageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mcontext = context;
        initView(context, attrs);
    }

    public void setmAngleNum(int mAngleNum) {
        this.mAngleNum = mAngleNum;
    }

    public void setmDiameter(int mDiameter_one,int mDiameter_two,int mDiameter_three) {
        width_degress_one = PUtil.dip2px(getContext(), mDiameter_one);
        width_degress_two = PUtil.dip2px(getContext(), mDiameter_two);
        width_degress_three = PUtil.dip2px(getContext(), mDiameter_three);
    }

    public void setSourceList(List<AtmanRelation> sourceList) {
        this.sourceList = sourceList;
        addPoint();
    }

    private void initView(Context context, AttributeSet attrs) {
        View imgPointLayout = inflate(context, R.layout.layout_imgview_point, this);
        layouPoints = imgPointLayout.findViewById(R.id.layouPoints);
        lineImageView = imgPointLayout.findViewById(R.id.lineImageView);
    }

    public ArrayList<AtmanRelation> getDegree(int Degree) {//获取几度关系
        ArrayList<AtmanRelation> arrayList = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            if (sourceList.get(i).getDegree() == Degree) {
                arrayList.add(sourceList.get(i));
            }
        }
        return arrayList;
    }

    public ArrayList<AtmanRelation> getsonList(AtmanRelation atmanRelation) {//获取这个节点下 所有的集合
        ArrayList<AtmanRelation> arrayList = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            if (sourceList.get(i).getDegree() == (atmanRelation.getDegree()+1) && sourceList.get(i).getType().equals(atmanRelation.getType())) {
                arrayList.add(sourceList.get(i));
            }
        }
        return arrayList;
    }

    public void addPoint() {
        points.clear();
        rects.clear();
        reList_2.clear();
        layouPoints.removeAllViews();

        //找到自己这个点。并把中心点带入
        ArrayList<AtmanRelation> myAtmanList = getDegree(0);
        myAtmanList.get(0).setY_center(LastCenterX);
        myAtmanList.get(0).setX_center(LastCenterY);
        myAtmanList.get(0).setRectPoint(new RectPoint(LastCenterX - width_degress_one / 2, LastCenterY - width_degress_one / 2
                , LastCenterX + width_degress_one / 2, LastCenterY + width_degress_one / 2));

        showAvatar(LastCenterX - width_degress_one / 2, LastCenterY- width_degress_one / 2, myAtmanList.get(0));

        /***
         * 如果是一度关系
         */
        int circle = width_degress_one;//当前一度关系的长度
        ArrayList<AtmanRelation> oneAtmanList = getDegree(1);
        int number = oneAtmanList.size();//当前一度关系的个数
        int x_center = LastCenterX;
        int y_center = LastCenterY;
        for (int i = 0; i < oneAtmanList.size(); i++) {
            AtmanRelation itemBean = oneAtmanList.get(i);
            //相隔的角度就是
            double jiaod = 360 / ((double) oneAtmanList.size());
            double currentJD = 0 + jiaod * (i + 1);

            int X1 = 0;
            int Y1 = 0;

            if (currentJD != 0 && currentJD != 180 && currentJD != 360) {//别面有错误出现
                X1 = x_center + (int) (circle * (Math.cos(Math.PI * currentJD / 180)));
                Y1 = y_center + (int) (circle * (Math.sin(Math.PI * currentJD / 180)));
            } else {
                if (currentJD == 0 || currentJD == 360) {
                    X1 = x_center + circle;
                    Y1 = y_center;
                } else {
                    Y1 = y_center;
                    X1 = x_center - circle;
                }

            }

            int trueX1 = 0;
            int trueY1 = 0;
            //当前点的所在区域则是
            RectPoint leoPoint;

            trueX1 = X1 - width_degress_two / 2;//减去自身控件的长度
            trueY1 = Y1 - width_degress_two / 2;//减去自身控件的长度
            leoPoint = new RectPoint(trueX1, trueY1, trueX1 + width_degress_two, trueY1 + width_degress_two);

            if (checkHaveRect(leoPoint)) {//检查是否有重叠，true为有重叠。要增加半径的长度
                repetAdd(x_center, y_center, circle, currentJD, 1, itemBean);
            } else {//false 没有重叠，则直接添加
                showAvatar(trueX1, trueY1, itemBean);

                points.add(new PointLeo(x_center, y_center, X1, Y1, itemBean.getType(), itemBean.getDegree(), true));

                // 把当前的中心点坐标  和当前点的所在区域存储起来。以便放置证据的时候 不覆盖 判断
                itemBean.setX_center(X1);
                itemBean.setY_center(Y1);
                itemBean.setRectPoint(new RectPoint(trueX1 - PUtil.dip2px(getContext(), 16), trueY1 - PUtil.dip2px(getContext(), 16),
                        trueX1 + PUtil.dip2px(getContext(), 16), trueY1 + PUtil.dip2px(getContext(), 16)));

                rects.add(leoPoint);

                /**
                 * 这里是添加成功关系以后，进行的下一层 关系的绘制
                 * */

                ArrayList<AtmanRelation> sonList = getsonList(itemBean);//一度节点下二度的关系集合
                if (sonList.size() > 0) {
                    int numberNex = sonList.size();//下一层关系有几个。（注意这里下一层的一度关系是不包括父类的。而且算角度的时候要加个1）
                    int circle2 = width_degress_three;
                    reList_2.add(new RelationBean(circle2, numberNex, X1, Y1, currentJD, 2, sonList));//倒数第二个参数，第几度关系 加180度是因为要向外
                }

            }

            if (i == number - 1) {
                if (reList_2.size() > 0) {
                    for (int j = 0; j < reList_2.size(); j++) {
                        RelationBean relationBean = reList_2.get(j);
                        if (relationBean.getNumberNex() > 0) {
                            addNexPoint(relationBean.getCircle(), relationBean.getNumberNex(), relationBean.getX1(), relationBean.getY1(), relationBean.getTrueJD(), relationBean.getRelation(), relationBean.getAtmanRelations(), j);
                        }
                    }
                }
            }

        }
        lineImageView.setLines(points);
    }

    public void addNexPoint(int circleNex, double numberNex, int x_centerNex, int y_centerNex, double nowJD, int relation, ArrayList<AtmanRelation> atmanRelations, int index) {
        int circle = circleNex;//半径的长度
        double number = numberNex + 1;//当前关系个数。当然要加上父类那条线
        int x_center = x_centerNex;//目前坐标中心点
        int y_center = y_centerNex;//目前坐标重新点
        double trueJD = nowJD % 360 - mAngleNum/2;
        for (int i = 0; i < numberNex; i++) {
            AtmanRelation itemBean = atmanRelations.get(i);
            //相隔的角度就是
            double jiaod = mAngleNum / number;
            double currentJD = trueJD + jiaod * (i + 1);

            int X1 = 0;
            int Y1 = 0;
            if (currentJD != 0 && currentJD != 180 && currentJD != 360) {//别面有错误出现
                X1 = x_center + (int) (circle * (Math.cos(Math.PI * currentJD / 180)));
                Y1 = y_center + (int) (circle * (Math.sin(Math.PI * currentJD / 180)));
            } else {
                if (currentJD == 0 || currentJD == 360) {
                    X1 = x_center + circle;
                    Y1 = y_center;
                } else {
                    Y1 = y_center;
                    X1 = x_center - circle;
                }

            }

            int trueX1 = 0;
            int trueY1 = 0;
            //当前点的所在区域则是
            RectPoint leoPoint;

            if (0 == itemBean.getDegree()) {
                trueX1 = X1 - width_degress_one / 2;//减去自身控件的长度
                trueY1 = Y1 - width_degress_one / 2;//减去自身控件的长度
                leoPoint = new RectPoint(trueX1 - width_degress_one, trueY1 - width_degress_one,
                        trueX1 + width_degress_one, trueY1 + width_degress_one);
            } else if (1 == itemBean.getDegree()) {
                trueX1 = X1 - width_degress_two / 2;//减去自身控件的长度
                trueY1 = Y1 - width_degress_two / 2;//减去自身控件的长度
                leoPoint = new RectPoint(trueX1, trueY1,
                        trueX1 + width_degress_two, trueY1 + width_degress_two);
            } else {
                trueX1 = X1 - width_degress_three / 2;//减去自身控件的长度
                trueY1 = Y1 - width_degress_three / 2;//减去自身控件的长度
                leoPoint = new RectPoint(trueX1, trueY1,
                        trueX1 + width_degress_three, trueY1 + width_degress_three + PUtil.dip2px(getContext(), 20));
            }

            if (checkHaveRect(leoPoint)) {//检查是否有重叠，true为有重叠。要增加半径的长度

                repetAdd(x_center, y_center, circle, currentJD, relation, itemBean);

            } else {//false 没有重叠，则直接添加
                showAvatar(trueX1, trueY1, itemBean);

                points.add(new PointLeo(x_center, y_center, X1, Y1, itemBean.getType(), itemBean.getDegree(), true));

                itemBean.setX_center(X1);
                itemBean.setY_center(Y1);
                itemBean.setRectPoint(new RectPoint(trueX1 - PUtil.dip2px(getContext(), 8), trueY1 - PUtil.dip2px(getContext(), 8),
                        trueX1 + PUtil.dip2px(getContext(), 8), trueY1 + PUtil.dip2px(getContext(), 8)));
                rects.add(leoPoint);
            }
        }


    }


    //这里是检查重叠区域，如果有重叠则一直增加半径 直至不重叠位置
    public void repetAdd(int x_center, int y_center, int circle, double currentJD, int relation, AtmanRelation itemBean) {
        int circleAdd = circle + PUtil.dip2px(getContext(), 14);
        int X1 = 0;
        int Y1 = 0;
        if (currentJD != 0 && currentJD != 180 && currentJD != 360) {//别面有错误出现
            X1 = x_center + (int) (circleAdd * (Math.cos(Math.PI * currentJD / 180)));
            Y1 = y_center + (int) (circleAdd * (Math.sin(Math.PI * currentJD / 180)));
        } else {
            if (currentJD == 0 || currentJD == 360) {
                X1 = x_center + circleAdd;
                Y1 = y_center;
            } else {
                Y1 = y_center;
                X1 = x_center - circleAdd;
            }

        }

        int trueX1 = 0;
        int trueY1 = 0;
        //当前点的所在区域则是
        RectPoint leoPoint;

        if (0 == itemBean.getDegree()) {
            trueX1 = X1 - width_degress_one / 2;//减去自身控件的长度
            trueY1 = Y1 - width_degress_one / 2;//减去自身控件的长度
            leoPoint = new RectPoint(trueX1 - width_degress_one, trueY1 - width_degress_one,
                    trueX1 + width_degress_one, trueY1 + width_degress_one);
        } else if (1 == itemBean.getDegree()) {
            trueX1 = X1 - width_degress_two / 2;//减去自身控件的长度
            trueY1 = Y1 - width_degress_two / 2;//减去自身控件的长度
            leoPoint = new RectPoint(trueX1, trueY1,
                    trueX1 + width_degress_two, trueY1 + width_degress_two);
        } else {
            trueX1 = X1 - width_degress_three / 2;//减去自身控件的长度
            trueY1 = Y1 - width_degress_three / 2;//减去自身控件的长度
            leoPoint = new RectPoint(trueX1, trueY1,
                    trueX1 + width_degress_three, trueY1 + width_degress_three + PUtil.dip2px(getContext(), 20));
        }


        if (checkHaveRect(leoPoint)) {//检查是否有重叠，true为有重叠。要增加半径的长度
            repetAdd(x_center, y_center, circleAdd, currentJD, relation, itemBean);
        } else {//false 没有重叠，则直接添加
            showAvatar(trueX1, trueY1, itemBean);

            points.add(new PointLeo(x_center, y_center, X1, Y1, itemBean.getType(), itemBean.getDegree(), true));

            itemBean.setX_center(X1);
            itemBean.setY_center(Y1);
            itemBean.setRectPoint(new RectPoint(trueX1 - PUtil.dip2px(getContext(), 8), trueY1 - PUtil.dip2px(getContext(), 8),
                    trueX1 + PUtil.dip2px(getContext(), 8), trueY1 + PUtil.dip2px(getContext(), 8)));
            rects.add(leoPoint);

            if (relation == 1) {//当前是一度关系的节点，才能添加二度
                ArrayList<AtmanRelation> sonList = getsonList(itemBean);//一度节点下二度的关系集合
                if (sonList.size() > 0) {
                    int numberNex = sonList.size();//下一层关系有几个。（注意这里下一层的一度关系是不包括父类的。而且算角度的时候要加个1）
                    int circle2 = PUtil.dip2px(getContext(), 10);
                    reList_2.add(new RelationBean(circle2, numberNex, X1, Y1, currentJD, 0, sonList));//倒数第二个参数，第几度关系
                }
            }
        }
    }


    //检查是否有重叠区域
    public boolean checkHaveRect(RectPoint rectPoint) {
        for (int i = 0; i < rects.size(); i++) {
            RectPoint currenRect = rects.get(i);
            if (rectPoint.getLeft_x() > currenRect.getRigth_x()
                    || rectPoint.getLeft_top_y() > currenRect.getRight_bottom_y()
                    || rectPoint.getRight_bottom_y() < currenRect.getLeft_top_y()
                    || rectPoint.getRigth_x() < currenRect.getLeft_x()
            ) {

            } else {
                return true;//只要不符合上面条件  则有重叠
            }

        }

        return false;//没有包含在内
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void showAvatar(int trueX1, int trueY1, AtmanRelation itemBean) {
        int viewnum = 0;

        if (0 == itemBean.getDegree() || 1 == itemBean.getDegree()) {
            SuperTextView imageView_bottom_yy = new SuperTextView(getContext());
            LayoutParams leoParams_bottom_yy = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            leoParams_bottom_yy.leftMargin = trueX1;
            leoParams_bottom_yy.topMargin = trueY1;
            imageView_bottom_yy.setGravity(Gravity.CENTER);
            imageView_bottom_yy.setTextColor(getResources().getColor(R.color.white));
            imageView_bottom_yy.setTextSize(12f);

            TextView textview_name = new TextView(getContext());
            LayoutParams Params_textview_name = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Params_textview_name.leftMargin = trueX1;
            Params_textview_name.height = 80;
            textview_name.setGravity(Gravity.CENTER_HORIZONTAL);
            textview_name.setTextSize(8f);
            textview_name.setMaxLines(1);

            textview_name.setTextColor(getResources().getColor(R.color.black));
            textview_name.setText(itemBean.getVisitorName());

            if (0 == itemBean.getDegree()) {
                viewnum = width_degress_one;
                leoParams_bottom_yy.width = viewnum;
                leoParams_bottom_yy.height = viewnum;

                Params_textview_name.width = viewnum;
                Params_textview_name.topMargin = trueY1 + viewnum + 5;
                textview_name.setVisibility(GONE);

                imageView_bottom_yy.setUrlImage(itemBean.getVisitorAvatar());
                imageView_bottom_yy.setStrokeColor(getResources().getColor(R.color.relationship_avatar_back));
                imageView_bottom_yy.setStrokeWidth(PUtil.dip2px(getContext(), 5));

            } else if (1 == itemBean.getDegree()) {
                viewnum = width_degress_two;
                leoParams_bottom_yy.width = viewnum;
                leoParams_bottom_yy.height = viewnum;
                Params_textview_name.width = viewnum;
                Params_textview_name.topMargin = trueY1 + viewnum + 10;
                textview_name.setVisibility(GONE);
                if ("1".equals(itemBean.getType())) {   //资讯视频
                    imageView_bottom_yy.setText("资讯\n视频");
                    imageView_bottom_yy.setSolid(getResources().getColor(R.color.relationship_zixun));
                    imageView_bottom_yy.setStrokeColor(getResources().getColor(R.color.relationship_zixun_back));
                    imageView_bottom_yy.setStrokeWidth(PUtil.dip2px(getContext(), 4));
                } else if ("2".equals(itemBean.getType())) {   //名片
                    imageView_bottom_yy.setText("名片");
                    imageView_bottom_yy.setSolid(getResources().getColor(R.color.relationship_card));
                    imageView_bottom_yy.setStrokeColor(getResources().getColor(R.color.relationship_card_back));
                    imageView_bottom_yy.setStrokeWidth(PUtil.dip2px(getContext(), 4));
                } else if ("3".equals(itemBean.getType())) {   //宣传册
                    imageView_bottom_yy.setText("宣传册");
                    imageView_bottom_yy.setSolid(getResources().getColor(R.color.relationship_xuanchuance));
                    imageView_bottom_yy.setStrokeColor(getResources().getColor(R.color.relationship_xuanchuance_back));
                    imageView_bottom_yy.setStrokeWidth(PUtil.dip2px(getContext(), 4));
                } else if ("4".equals(itemBean.getType())) {   //商品
                    imageView_bottom_yy.setText("商品");
                    imageView_bottom_yy.setSolid(getResources().getColor(R.color.relationship_goods));
                    imageView_bottom_yy.setStrokeColor(getResources().getColor(R.color.relationship_goods_back));
                    imageView_bottom_yy.setStrokeWidth(PUtil.dip2px(getContext(), 4));
                } else if ("5".equals(itemBean.getType())) {   //优惠券
                    imageView_bottom_yy.setText("优惠券");
                    imageView_bottom_yy.setSolid(getResources().getColor(R.color.relationship_youhuijuan));
                    imageView_bottom_yy.setStrokeColor(getResources().getColor(R.color.relationship_youhuijuan_back));
                    imageView_bottom_yy.setStrokeWidth(PUtil.dip2px(getContext(), 4));
                } else if ("6".equals(itemBean.getType())) {   //小程序
                    imageView_bottom_yy.setText("小程序");
                    imageView_bottom_yy.setSolid(getResources().getColor(R.color.relationship_xiaochengxu));
                    imageView_bottom_yy.setStrokeColor(getResources().getColor(R.color.relationship_xiaochengxu_back));
                    imageView_bottom_yy.setStrokeWidth(PUtil.dip2px(getContext(), 4));
                } else if ("7".equals(itemBean.getType())) {   //娱乐
                    imageView_bottom_yy.setText("娱乐");
                    imageView_bottom_yy.setSolid(getResources().getColor(R.color.relationship_yule));
                    imageView_bottom_yy.setStrokeColor(getResources().getColor(R.color.relationship_yule_back));
                    imageView_bottom_yy.setStrokeWidth(PUtil.dip2px(getContext(), 4));
                } else if ("8".equals(itemBean.getType())) {   //海报
                    imageView_bottom_yy.setText("海报");
                    imageView_bottom_yy.setSolid(getResources().getColor(R.color.relationship_haibao));
                    imageView_bottom_yy.setStrokeColor(getResources().getColor(R.color.relationship_haibao_back));
                    imageView_bottom_yy.setStrokeWidth(PUtil.dip2px(getContext(), 4));
                }
            }

            imageView_bottom_yy.setCorner(450f);

            imageView_bottom_yy.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onitemclicklistener.avatarclick(itemBean.getDegree()+1);
                }
            });

            layouPoints.addView(imageView_bottom_yy, leoParams_bottom_yy);
            layouPoints.addView(textview_name, Params_textview_name);
            imageView_bottom_yy.setAlpha(1.0f);

        } else {
            RoundImageView imageView_bottom_yy = new RoundImageView(getContext());
            LayoutParams leoParams_bottom_yy = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            leoParams_bottom_yy.leftMargin = trueX1;
            leoParams_bottom_yy.topMargin = trueY1;

            TextView textview_name = new TextView(getContext());
            LayoutParams Params_textview_name = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Params_textview_name.leftMargin = trueX1;
            Params_textview_name.height = 40;
            textview_name.setGravity(Gravity.CENTER_HORIZONTAL);
            textview_name.setTextSize(8f);
            textview_name.setMaxLines(1);
            textview_name.setTextColor(getResources().getColor(R.color.black));
            textview_name.setText(itemBean.getVisitorName());

            viewnum = width_degress_three;
            leoParams_bottom_yy.width = viewnum;
            leoParams_bottom_yy.height = viewnum;
            Params_textview_name.width = viewnum;
            Params_textview_name.topMargin = trueY1 + viewnum + 1;

            imageView_bottom_yy.setRoundMode(ROUND_VIEW);
            imageView_bottom_yy.setBorderWidth(PUtil.dip2px(getContext(), 1));
            imageView_bottom_yy.setBorderColor(mcontext.getResources().getColor(R.color.relationship_avatar_back));
            imageView_bottom_yy.setBackground(mcontext.getResources().getDrawable(R.drawable.bg_ai_person));

            Glide.with(mcontext).load(itemBean.getVisitorAvatar()).into(imageView_bottom_yy);

            imageView_bottom_yy.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onitemclicklistener.avatarclick(itemBean.getDegree()+1);
                }
            });

            layouPoints.addView(imageView_bottom_yy, leoParams_bottom_yy);
            layouPoints.addView(textview_name, Params_textview_name);
            imageView_bottom_yy.setAlpha(1.0f);
        }
    }

    public interface OnItemClickListener{
        void avatarclick(int degree);
    }

    public OnItemClickListener getOnitemclicklistener() {
        return onitemclicklistener;
    }

    public void setOnitemclicklistener(OnItemClickListener onitemclicklistener) {
        this.onitemclicklistener = onitemclicklistener;
    }

    //画图之前优先计算最小最大值
    public void getmaxXYRange(List<AtmanRelation> sourceList) {
        XYsourceList = sourceList;
        myxyrects.clear();

        minX = 0;
        minY = 0;
        maxX = 0;
        maxY = 0;
        /*
         * 如果是一度关系
         */
        int circle = width_degress_one;//当前一度关系的长度
        ArrayList<AtmanRelation> oneAtmanList = getXYDegree(((ArrayList) sourceList), 1);
        int number = oneAtmanList.size();//当前一度关系的个数
        int x_center = 0;
        int y_center = 0;
        for (int i = 0; i < oneAtmanList.size(); i++) {
            AtmanRelation itemBean = oneAtmanList.get(i);
            //相隔的角度就是
            double jiaod = 360 / ((double) oneAtmanList.size());
            double currentJD = 0 + jiaod * (i + 1);

            int X1 = 0;
            int Y1 = 0;

            if (currentJD != 0 && currentJD != 180 && currentJD != 360) {//别面有错误出现
                X1 = x_center + ((int) (circle * (Math.cos(Math.PI * currentJD / 180))));
                Y1 = y_center + ((int) (circle * (Math.sin(Math.PI * currentJD / 180))));
            } else {
                if (currentJD == 0 || currentJD == 360) {
                    X1 = x_center + circle;
                    Y1 = y_center;
                } else {
                    Y1 = y_center;
                    X1 = x_center - circle;
                }

            }

            int trueX1 = 0;
            int trueY1 = 0;
            //当前点的所在区域则是
            RectPoint leoPoint;

            if (0 == itemBean.getDegree()) {
                trueX1 = X1 - width_degress_one / 2;//减去自身控件的长度
                trueY1 = Y1 - width_degress_one / 2;//减去自身控件的长度
                leoPoint = new RectPoint(trueX1 - width_degress_one, trueY1 - width_degress_one,
                        trueX1 + width_degress_one, trueY1 + width_degress_one);
            } else if (1 == itemBean.getDegree()) {
                trueX1 = X1 - width_degress_two / 2;//减去自身控件的长度
                trueY1 = Y1 - width_degress_two / 2;//减去自身控件的长度
                leoPoint = new RectPoint(trueX1, trueY1,
                        trueX1 + width_degress_two, trueY1 + width_degress_two);
            } else {
                trueX1 = X1 - width_degress_three / 2;//减去自身控件的长度
                trueY1 = Y1 - width_degress_three / 2;//减去自身控件的长度
                leoPoint = new RectPoint(trueX1, trueY1,
                        trueX1 + width_degress_three, trueY1 + width_degress_three + PUtil.dip2px(getContext(), 20));
            }

            if (checkXYHaveRect(leoPoint)) {//检查是否有重叠，true为有重叠。要增加半径的长度
                getXYrepetAdd(x_center, y_center, circle, currentJD, 1, itemBean);
            } else {//false 没有重叠，则直接添加
                if (minX > trueX1) {
                    minX = trueX1;
                }
                if (minY > trueY1) {
                    minY = trueY1;
                }
                if (maxX < (trueX1 + width_degress_three )) {
                    maxX = trueX1 + width_degress_three ;
                }
                if (maxY < (trueY1 + width_degress_three + PUtil.dip2px(getContext(), 10))) {
                    maxY = trueY1 + width_degress_three + PUtil.dip2px(getContext(), 10);
                }

                // 把当前的中心点坐标  和当前点的所在区域存储起来。以便放置证据的时候 不覆盖 判断
                itemBean.setX_center(X1);
                itemBean.setY_center(Y1);
                itemBean.setRectPoint(new RectPoint(trueX1 - PUtil.dip2px(getContext(), 16), trueY1 - PUtil.dip2px(getContext(), 16),
                        trueX1 + PUtil.dip2px(getContext(), 16), trueY1 + PUtil.dip2px(getContext(), 16)));

                myxyrects.add(leoPoint);

                /**
                 * 这里是添加成功关系以后，进行的下一层 关系的绘制
                 * */

                ArrayList<AtmanRelation> sonList = getsonXYList(itemBean);//一度节点下二度的关系集合
                if (sonList.size() > 0) {
                    int numberNex = sonList.size();//下一层关系有几个。（注意这里下一层的一度关系是不包括父类的。而且算角度的时候要加个1）
                    int circle2 = width_degress_three;
                    XYreList_2.add(new RelationBean(circle2, numberNex, X1, Y1, currentJD, 2, sonList));//倒数第二个参数，第几度关系
                }

            }

            if (i == number - 1) {
                if (XYreList_2.size() > 0) {
                    for (int j = 0; j < XYreList_2.size(); j++) {
                        RelationBean relationBean = XYreList_2.get(j);
                        if (relationBean.getNumberNex() > 0) {
                            addXYNexPoint(relationBean.getCircle(), relationBean.getNumberNex(), relationBean.getX1(), relationBean.getY1(), relationBean.getTrueJD(), relationBean.getRelation(), relationBean.getAtmanRelations(), j);
                        }
                    }
                }
            }
        }

    }

    //检查是否有重叠区域
    public boolean checkXYHaveRect(RectPoint rectPoint) {
        for (int i = 0; i < myxyrects.size(); i++) {
            RectPoint currenRect = myxyrects.get(i);
            if (rectPoint.getLeft_x() > currenRect.getRigth_x()
                    || rectPoint.getLeft_top_y() > currenRect.getRight_bottom_y()
                    || rectPoint.getRight_bottom_y() < currenRect.getLeft_top_y()
                    || rectPoint.getRigth_x() < currenRect.getLeft_x()
            ) {


            } else {
                return true;//只要不符合上面条件  则有重叠
            }
        }

        return false;//没有包含在内
    }

    public void changeViewRangle(int layoutwidth, int layouthigh) {
        int mlastWidth = 0;
        int mlastHigh = 0;

        if (maxX > layoutwidth / 2 || Math.abs(minX) > layoutwidth / 2) {
            if (maxX >= Math.abs(minX)) {
                mlastWidth = maxX * 2;
            } else {
                mlastWidth = Math.abs(minX) * 2;
            }
        } else {
            mlastWidth = layoutwidth;
        }

        if (maxY > layouthigh / 2 || Math.abs(minY) > layouthigh / 2) {
            if (maxY >= Math.abs(minY)) {
                mlastHigh = maxY * 2;
            } else {
                mlastHigh = Math.abs(minY) * 2;
            }
        } else {
            mlastHigh = layouthigh;
        }

        RelativeLayout.LayoutParams layoutParams1;
        layoutParams1 = new RelativeLayout.LayoutParams(mlastWidth, mlastHigh);
        layoutParams1.addRule(RelativeLayout.CENTER_IN_PARENT);
        layouPoints.setLayoutParams(layoutParams1);

        RelativeLayout.LayoutParams layoutParams2;
        layoutParams2 = new RelativeLayout.LayoutParams(mlastWidth, mlastHigh);
        layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
        lineImageView.setLayoutParams(layoutParams2);

        LastCenterX = mlastWidth / 2;
        LastCenterY = mlastHigh / 2;
    }

    public ArrayList<AtmanRelation> getXYDegree(ArrayList<AtmanRelation> mshareRoadRelationArrayList, int Degree) {//获取几度关系
        ArrayList<AtmanRelation> arrayList = new ArrayList<>();
        for (int i = 0; i < mshareRoadRelationArrayList.size(); i++) {
            if (mshareRoadRelationArrayList.get(i).getDegree() == Degree) {
                arrayList.add(mshareRoadRelationArrayList.get(i));
            }
        }
        return arrayList;
    }

    //这里是检查重叠区域，如果有重叠则一直增加半径 直至不重叠位置
    public void getXYrepetAdd(int x_center, int y_center, int circle, double currentJD, int relation, AtmanRelation itemBean) {
        int circleAdd = circle + PUtil.dip2px(getContext(), 14);
        int X1 = 0;
        int Y1 = 0;
        if (currentJD != 0 && currentJD != 180 && currentJD != 360) {//别面有错误出现
            X1 = x_center + ((int) (circleAdd * (Math.cos(Math.PI * currentJD / 180))));
            Y1 = y_center + ((int) (circleAdd * (Math.sin(Math.PI * currentJD / 180))));

//            if(currentJD>90&&currentJD<270){
//                X1 = X1 - 1;
//            }
//            if(currentJD>180&&currentJD<360){
//                Y1 = Y1 - 1;
//            }
        } else {
            if (currentJD == 0 || currentJD == 360) {
                X1 = x_center + circleAdd;
                Y1 = y_center;
            } else {
                Y1 = y_center;
                X1 = x_center - circleAdd;
            }

        }

        int trueX1 = 0;
        int trueY1 = 0;
        //当前点的所在区域则是
        RectPoint leoPoint;

        if (0 == itemBean.getDegree()){
            trueX1 = X1 - width_degress_one / 2;//减去自身控件的长度
            trueY1 = Y1 - width_degress_one / 2;//减去自身控件的长度
            leoPoint = new RectPoint(trueX1 - width_degress_one, trueY1 - width_degress_one,
                    trueX1 + width_degress_one, trueY1 + width_degress_one);
        } else if (1 == itemBean.getDegree()) {
            trueX1 = X1 - width_degress_two / 2;//减去自身控件的长度
            trueY1 = Y1 - width_degress_two / 2;//减去自身控件的长度
            leoPoint = new RectPoint(trueX1, trueY1,
                    trueX1 + width_degress_two, trueY1 + width_degress_two);
        } else {
            trueX1 = X1 - width_degress_three / 2;//减去自身控件的长度
            trueY1 = Y1 - width_degress_three / 2;//减去自身控件的长度
            leoPoint = new RectPoint(trueX1, trueY1,
                    trueX1 + width_degress_three, trueY1 + width_degress_three + PUtil.dip2px(getContext(), 20));
        }

        if (checkXYHaveRect(leoPoint)) {//检查是否有重叠，true为有重叠。要增加半径的长度
            getXYrepetAdd(x_center, y_center, circleAdd, currentJD, relation, itemBean);
        } else {//false 没有重叠，则直接添加
            itemBean.setX_center(X1);
            itemBean.setY_center(Y1);
            itemBean.setRectPoint(new RectPoint(trueX1 - PUtil.dip2px(getContext(), 8), trueY1 - PUtil.dip2px(getContext(), 8),
                    trueX1 + PUtil.dip2px(getContext(), 8), trueY1 + PUtil.dip2px(getContext(), 8)));
            myxyrects.add(leoPoint);

            if (minX > trueX1) {
                minX = trueX1;
            }
            if (minY > trueY1) {
                minY = trueY1;
            }
            if (maxX < (trueX1 + width_degress_three )) {
                maxX = trueX1 + width_degress_three ;
            }
            if (maxY < (trueY1 + width_degress_three + PUtil.dip2px(getContext(), 10))) {
                maxY = trueY1 + width_degress_three + PUtil.dip2px(getContext(), 10);
            }

            if (relation == 1) {//当前是一度关系的节点，才能添加二度
                ArrayList<AtmanRelation> sonList = getsonXYList(itemBean);//一度节点下二度的关系集合
                if (sonList.size() > 0) {
                    int numberNex = sonList.size();//下一层关系有几个。（注意这里下一层的一度关系是不包括父类的。而且算角度的时候要加个1）
                    int circle2 = PUtil.dip2px(getContext(), 10);
                    XYreList_2.add(new RelationBean(circle2, numberNex, X1, Y1, currentJD, 0, sonList));//倒数第二个参数，第几度关系
                }
            }
        }
    }

    public void addXYNexPoint(int circleNex, double numberNex, int x_centerNex, int y_centerNex, double nowJD, int relation, ArrayList<AtmanRelation> atmanRelations, int index) {
        int circle = circleNex;//半径的长度
        double number = numberNex + 1;//当前关系个数。当然要加上父类那条线
        int x_center = x_centerNex;//目前坐标中心点
        int y_center = y_centerNex;//目前坐标重新点
        double trueJD = nowJD % 360 - mAngleNum/2;
        for (int i = 0; i < numberNex; i++) {
            AtmanRelation itemBean = atmanRelations.get(i);
            //相隔的角度就是
            double jiaod = mAngleNum / number;
            double currentJD = trueJD + jiaod * (i + 1);
            int X1 = 0;
            int Y1 = 0;
            if (currentJD != 0 && currentJD != 180 && currentJD != 360) {//别面有错误出现
                X1 = x_center + ((int) (circle * (Math.cos(Math.PI * currentJD / 180))));
                Y1 = y_center + ((int) (circle * (Math.sin(Math.PI * currentJD / 180))));
            } else {
                if (currentJD == 0 || currentJD == 360) {
                    X1 = x_center + circle;
                    Y1 = y_center;
                } else {
                    Y1 = y_center;
                    X1 = x_center - circle;
                }

            }

            int trueX1 = 0;
            int trueY1 = 0;
            //当前点的所在区域则是
            RectPoint leoPoint;

            if (0 == itemBean.getDegree()) {
                trueX1 = X1 - width_degress_one / 2;//减去自身控件的长度
                trueY1 = Y1 - width_degress_one / 2;//减去自身控件的长度
                leoPoint = new RectPoint(trueX1 - width_degress_one, trueY1 - width_degress_one,
                        trueX1 + width_degress_one, trueY1 + width_degress_one);
            } else if (1 == itemBean.getDegree()) {
                trueX1 = X1 - width_degress_two / 2;//减去自身控件的长度
                trueY1 = Y1 - width_degress_two / 2;//减去自身控件的长度
                leoPoint = new RectPoint(trueX1, trueY1,
                        trueX1 + width_degress_two, trueY1 + width_degress_two);
            } else {
                trueX1 = X1 - width_degress_three / 2;//减去自身控件的长度
                trueY1 = Y1 - width_degress_three / 2;//减去自身控件的长度
                leoPoint = new RectPoint(trueX1, trueY1,
                        trueX1 + width_degress_three, trueY1 + width_degress_three + PUtil.dip2px(getContext(), 20));
            }

            if (checkXYHaveRect(leoPoint)) {//检查是否有重叠，true为有重叠。要增加半径的长度
                getXYrepetAdd(x_center, y_center, circle, currentJD, relation, itemBean);
            } else {//false 没有重叠，则直接添加
                itemBean.setX_center(X1);
                itemBean.setY_center(Y1);
                itemBean.setRectPoint(new RectPoint(trueX1 - PUtil.dip2px(getContext(), 8), trueY1 - PUtil.dip2px(getContext(), 8),
                        trueX1 + PUtil.dip2px(getContext(), 8), trueY1 + PUtil.dip2px(getContext(), 8)));
                myxyrects.add(leoPoint);

                if (minX > trueX1) {
                    minX = trueX1;
                }
                if (minY > trueY1) {
                    minY = trueY1;
                }
                if (maxX < (trueX1 + width_degress_three)) {
                    maxX = trueX1 + width_degress_three;
                }
                if (maxY < (trueY1 + width_degress_three + PUtil.dip2px(getContext(), 10))) {
                    maxY = trueY1 + width_degress_three + PUtil.dip2px(getContext(), 10);
                }
            }
        }
    }

    public ArrayList<AtmanRelation> getsonXYList(AtmanRelation atmanRelation) {//获取这个节点下 所有的集合
        ArrayList<AtmanRelation> arrayList = new ArrayList<>();
        for (int i = 0; i < XYsourceList.size(); i++) {
            if (XYsourceList.get(i).getDegree() == (atmanRelation.getDegree()+1) && XYsourceList.get(i).getType().equals(atmanRelation.getType())) {
                arrayList.add(XYsourceList.get(i));
            }
        }
        return arrayList;
    }

    public int getChangedX() {
        return LastCenterX;
    }

    public int getChangedY() {
        return LastCenterY;
    }

    public int aiMaxWidthLength() {
        return 2*LastCenterX;
    }

    public int aiMaxHighLength() {
        return 2*LastCenterY;
    }
}

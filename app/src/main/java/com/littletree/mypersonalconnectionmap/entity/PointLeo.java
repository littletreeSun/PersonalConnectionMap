package com.littletree.mypersonalconnectionmap.entity;

/**
 * @ProjectName: MyPersonalConnectionMap
 * @Package: com.littletree.mypersonalconnectionmap
 * @ClassName: PointLeo
 * @Author: littletree
 * @CreateDate: 2022/7/19/019 16:50
 */
public class PointLeo {
    private int x;
    private int y;
    private int x1;
    private int y1;
    private String typestr;  //自加 类型
    private int enddegree;  //自加 到达的级别
    private boolean isClick;

    public PointLeo(int x, int y, int x1, int y1,String typestr,int enddegree,boolean isClick) {
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;
        this.typestr = typestr;
        this.enddegree = enddegree;
        this.isClick = isClick;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public String getTypestr() {
        return typestr;
    }

    public void setTypestr(String typestr) {
        this.typestr = typestr;
    }

    public int getEnddegree() {
        return enddegree;
    }

    public void setEnddegree(int enddegree) {
        this.enddegree = enddegree;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }
}

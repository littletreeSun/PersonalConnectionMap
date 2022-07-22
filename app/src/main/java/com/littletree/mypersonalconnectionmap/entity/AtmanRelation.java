package com.littletree.mypersonalconnectionmap.entity;

import java.io.Serializable;

/**
 * @ProjectName: MyPersonalConnectionMap
 * @Package: com.littletree.mypersonalconnectionmap
 * @ClassName: AtmanRelation
 * @Author: littletree
 * @CreateDate: 2022/7/19/019 16:54
 */
public class AtmanRelation implements Serializable {
    private int degree;//当前第几度关系
    private String type;
    private String visitorAvatar;
    private String visitorId;
    private String visitorName;

    private int x_center;//当前点的x中心点
    private int y_center;//当前点的y中心点
    private RectPoint rectPoint;//当前点所在的区域。用于放置图片证据的时候进行判断不覆盖

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVisitorAvatar() {
        return visitorAvatar;
    }

    public void setVisitorAvatar(String visitorAvatar) {
        this.visitorAvatar = visitorAvatar;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public int getX_center() {
        return x_center;
    }

    public void setX_center(int x_center) {
        this.x_center = x_center;
    }

    public int getY_center() {
        return y_center;
    }

    public void setY_center(int y_center) {
        this.y_center = y_center;
    }

    public RectPoint getRectPoint() {
        return rectPoint;
    }

    public void setRectPoint(RectPoint rectPoint) {
        this.rectPoint = rectPoint;
    }
}

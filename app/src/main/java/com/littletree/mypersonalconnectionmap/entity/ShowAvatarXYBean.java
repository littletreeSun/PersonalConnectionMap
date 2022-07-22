package com.littletree.mypersonalconnectionmap.entity;

import java.io.Serializable;

/**
 * @ProjectName: MyPersonalConnectionMap
 * @Package: com.littletree.mypersonalconnectionmap
 * @ClassName: ShowAvatarXYBean
 * @Author: littletree
 * @CreateDate: 2022/7/19/019 17:09
 */
public class ShowAvatarXYBean implements Serializable {
    private int showX;
    private int showY;

    public ShowAvatarXYBean(int showX, int showY) {
        this.showX = showX;
        this.showY = showY;
    }

    public int getShowX() {
        return showX;
    }

    public void setShowX(int showX) {
        this.showX = showX;
    }

    public int getShowY() {
        return showY;
    }

    public void setShowY(int showY) {
        this.showY = showY;
    }
}

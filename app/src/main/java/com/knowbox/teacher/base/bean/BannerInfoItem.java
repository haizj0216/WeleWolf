/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.teacher.base.bean;

import java.io.Serializable;

/**
 * banner信息
 *
 * @author wangming
 */
public class BannerInfoItem implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 606422874060350059L;

    public String image;
    public String url;

    public BannerInfoItem(String image, String url) {
        this.image = image;
        this.url = url;
    }
}

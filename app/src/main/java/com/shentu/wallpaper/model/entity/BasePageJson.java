package com.shentu.wallpaper.model.entity;

import java.io.Serializable;
import java.util.List;

public class BasePageJson<T> implements Serializable{

    public int count;
    public String next;
    public Object previous;
    public List<T> results;

}

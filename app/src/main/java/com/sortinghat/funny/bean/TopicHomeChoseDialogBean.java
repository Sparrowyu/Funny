package com.sortinghat.funny.bean;

import java.io.Serializable;

/**
 * Created by wzy on 2021/7/1
 */
public class TopicHomeChoseDialogBean implements Serializable {

    private String id;
    private String name;

    private boolean isSelect;
    private boolean isClicked = false;//是否被选中

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

}

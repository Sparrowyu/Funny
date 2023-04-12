package com.sortinghat.funny.bean;

import java.util.Objects;

/**
 * Created by wzy on 2021/08/03
 */
public class ItemBean {

    private String menuId;
    private String menuName;
    private int iconResource;

    public ItemBean() {
    }

    public ItemBean(String menuName, int iconResource) {
        this.menuName = menuName;
        this.iconResource = iconResource;
    }

    public ItemBean(String menuId, String menuName, int iconResource) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.iconResource = iconResource;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ItemBean itemBean = (ItemBean) o;
        return Objects.equals(menuId, itemBean.menuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId);
    }
}
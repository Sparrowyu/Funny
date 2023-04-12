package com.sortinghat.funny.bean;

import java.util.List;

public class HomeReportBean {
    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private String dataString;
        private boolean isCheck;
        private boolean isTitle;
        private int id;

        public String getDataString() {
            return dataString;
        }

        public void setDataString(String dataString) {
            this.dataString = dataString;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        public boolean isTitle() {
            return isTitle;
        }

        public void setTitle(boolean title) {
            isTitle = title;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

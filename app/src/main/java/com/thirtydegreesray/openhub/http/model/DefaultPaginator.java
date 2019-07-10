package com.thirtydegreesray.openhub.http.model;

public class DefaultPaginator extends BasePaginator {

    private int curPage;

    public DefaultPaginator() {

        curPage = 1;
    }

    public int getCurPage() {

        return curPage;
    }

    public void setCurPage(final int curPage) {

        this.curPage = curPage;
    }
}

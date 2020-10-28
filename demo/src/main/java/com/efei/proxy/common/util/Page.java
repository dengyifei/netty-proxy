package com.efei.proxy.common.util;

import java.util.List;
import java.util.Map;

public class Page<T> {
    int  pageNumber;
    int curPage;
    int totalPage;
    int rows;
    List<T> data;

    public int getPageNumber(){
        return pageNumber;
    }

    public int getCurPage(){
        return curPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}

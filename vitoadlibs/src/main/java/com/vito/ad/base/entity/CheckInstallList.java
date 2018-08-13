package com.vito.ad.base.entity;

import java.util.ArrayList;
import java.util.List;

public class CheckInstallList {
    private List<Integer> checkInstallList;

    public List<Integer> getCheckInstallList() {
        if (checkInstallList==null)
            checkInstallList = new ArrayList<>();
        return checkInstallList;
    }

    public void setCheckInstallList(List<Integer> checkInstallList) {
        this.checkInstallList = checkInstallList;
    }
}

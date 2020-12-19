package com.view;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Objects;

public class StateListItem {


    public String iStateId;
    public String vStateCode;
    public String vState;


    public StateListItem(String iStateId, String vStateCode, String vState) {
        this.iStateId = iStateId;
        this.vStateCode = vStateCode;
        this.vState = vState;
    }

    @Override
    public String toString() {
        return  vState ;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateListItem that = (StateListItem) o;
        return Objects.equals(vState, that.vState);
    }

    @Override
    public int hashCode() {

        return Objects.hash(vState);
    }
}

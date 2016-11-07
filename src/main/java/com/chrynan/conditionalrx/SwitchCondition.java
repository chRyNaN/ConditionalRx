package com.chrynan.conditionalrx;

import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckeenan on 11/2/16.
 */
public class SwitchCondition<T, R> {

    private final List<R> caseConditions;
    private final Action1<T> action;
    private final boolean doBreak;
    private boolean isDefault;

    public SwitchCondition(List<R> caseConditions, Action1<T> action, boolean isDefault, boolean doBreak) {
        this.caseConditions = caseConditions;
        this.action = action;
        this.isDefault = isDefault;
        this.doBreak = doBreak;
    }

    public List<R> getCaseConditions() {
        return caseConditions;
    }

    public Action1<T> getAction() {
        return action;
    }

    public boolean doBreak() {
        return doBreak;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}

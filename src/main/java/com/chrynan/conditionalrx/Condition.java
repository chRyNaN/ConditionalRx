package com.chrynan.conditionalrx;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by ckeenan on 10/25/16.
 */
public class Condition<T> {

    private Func1<T, Boolean> condition;
    private Action1<T> action;

    public Condition(Func1<T, Boolean> condition, Action1<T> action) {
        this.condition = condition;
        this.action = action;
    }

    public Func1<T, Boolean> getCondition() {
        return condition;
    }

    public Action1<T> getAction() {
        return action;
    }
}

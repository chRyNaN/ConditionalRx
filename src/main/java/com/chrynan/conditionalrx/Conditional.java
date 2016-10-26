package com.chrynan.conditionalrx;

import rx.functions.Func1;

/**
 * Created by ckeenan on 10/25/16.
 */
public class Conditional<T> {

    public static <T> IfOperator.IfConditionBuilder<T> ifThis(Func1<T, Boolean> condition) {
        return new IfOperator.IfConditionBuilder<>(condition);
    }

    private Conditional() {

    }

}

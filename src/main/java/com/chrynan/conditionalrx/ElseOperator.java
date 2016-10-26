package com.chrynan.conditionalrx;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckeenan on 10/25/16.
 */
public class ElseOperator<T> implements Observable.Operator<T, T> {
    private final List<Condition<T>> conditions = new ArrayList<>();
    private final Action1<T> action;

    public ElseOperator(List<Condition<T>> previousConditions, Action1<T> action) {
        this.conditions.addAll(previousConditions);
        this.action = action;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> subscriber) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(T t) {
                boolean performElse = true;

                for (final Condition<T> condition : conditions) {

                    if (condition.getCondition().call(t)) {
                        performElse = false;

                        condition.getAction().call(t);
                    }
                }

                if (performElse) {
                    action.call(t);
                }

                subscriber.onNext(t);
            }
        };
    }

    public static class ElseConditionBuilder<T> {

        private final List<Condition<T>> conditions;

        public ElseConditionBuilder(List<Condition<T>> conditions) {
            this.conditions = conditions;
        }

        public ElseOperator<T> then(Action1<T> action) {
            return new ElseOperator<>(conditions, action);
        }
    }
}

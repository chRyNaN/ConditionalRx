package com.chrynan.conditionalrx;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckeenan on 11/2/16.
 */
public class SwitchOperator<T, R> implements Observable.Operator<T, T> {
    private final Func1<T, R> switchStatementCondition;
    private final List<SwitchCondition<T, R>> caseConditions;
    private boolean isDefault;

    SwitchOperator(Func1<T, R> switchStatementCondition) {
        this.switchStatementCondition = switchStatementCondition;
        this.caseConditions = new ArrayList<>();
    }

    SwitchOperator(Func1<T, R> switchStatementCondition, List<SwitchCondition<T, R>> caseConditions) {
        this.switchStatementCondition = switchStatementCondition;
        this.caseConditions = caseConditions;
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
                final R caseObject = switchStatementCondition.call(t);
                final SwitchCondition<T, R> defaultCondition = getDefaultCondition();

                boolean performedAction = false;

                for (SwitchCondition<T, R> condition : caseConditions) {
                    boolean doPerformAction = false;

                    for (R r : condition.getCaseConditions()) {
                        if (r != null && r.equals(caseObject)) {
                            doPerformAction = true;
                            break;
                        }
                    }

                    if (doPerformAction) {
                        condition.getAction().call(t);

                        performedAction = true;

                        if (condition.doBreak()) {
                            break;
                        }
                    }
                }

                if (defaultCondition != null && !performedAction) {
                    defaultCondition.getAction().call(t);
                }

                subscriber.onNext(t);
            }
        };
    }

    public SwitchCaseBuilder<T, R> caseWithBreak(R caseCondition) {
        return new SwitchCaseBuilder<>(switchStatementCondition, caseConditions, caseCondition, isDefault, true);
    }

    public SwitchCaseBuilder<T, R> caseWithoutBreak(R caseCondition) {
        return new SwitchCaseBuilder<>(switchStatementCondition, caseConditions, caseCondition, isDefault, false);
    }

    public SwitchOperator<T, R> makeDefault() {
        this.isDefault = true;
        return this;
    }

    private SwitchCondition<T, R> getDefaultCondition() {
        for (SwitchCondition<T, R> c : caseConditions) {
            if (c.isDefault()) {
                return c;
            }
        }

        return null;
    }

    public static class SwitchCaseBuilder<T, R> {
        private final Func1<T, R> switchStatementCondition;
        private final List<SwitchCondition<T, R>> previousConditions;
        private final List<R> caseConditions;
        private final boolean doBreak;
        private boolean isDefault;

        SwitchCaseBuilder(Func1<T, R> switchStatementCondition, List<SwitchCondition<T, R>> previousConditions, R caseCondition,
                          boolean isDefault, boolean doBreak) {
            this.switchStatementCondition = switchStatementCondition;
            this.previousConditions = previousConditions;
            this.caseConditions = new ArrayList<>();
            this.caseConditions.add(caseCondition);
            this.doBreak = doBreak;
            this.isDefault = isDefault;
        }

        public SwitchCaseBuilder<T, R> withCase(R caseCondition) {
            caseConditions.add(caseCondition);
            return this;
        }

        public SwitchCaseBuilder<T, R> makeDefault() {
            isDefault = true;
            return this;
        }

        public SwitchOperator<T, R> then(Action1<T> action) {
            if (isDefault) {
                for (SwitchCondition<T, R> condition : previousConditions) {
                    condition.setDefault(false);
                }
            }
            previousConditions.add(new SwitchCondition<>(caseConditions, action, isDefault, doBreak));
            return new SwitchOperator<>(switchStatementCondition, previousConditions);
        }
    }
}

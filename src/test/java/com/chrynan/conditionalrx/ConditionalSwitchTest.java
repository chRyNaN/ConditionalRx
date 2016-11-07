package com.chrynan.conditionalrx;

import com.oracle.tools.packager.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckeenan on 11/3/16.
 */
public class ConditionalSwitchTest {

    private Observable<Integer> observable;
    private TestSubscriber<Integer> testSubscriber;
    private List<Integer> testValues;
    private int count;

    @Before
    public void init() {
        // Setup new test values
        testValues = new ArrayList<>();

        testValues.add(5);
        testValues.add(12);
        testValues.add(25);
        testValues.add(3);
        testValues.add(68);
        testValues.add(4);
    }

    @Test
    public void singleSwitchCaseShouldWork() {
        resetVariables();

        observable.lift(Conditional.switchOn(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return true;
            }
        }).caseWithBreak(true)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count++;
                    }
                })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("singleSwitchCaseShouldWork()", 6);
    }

    @Test
    public void multipleSwitchCasesSingleActionShouldWork() {
        resetVariables();

        observable.lift(Conditional.switchOn(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return integer;
            }
        }).caseWithBreak(5)
                .withCase(25)
                .withCase(68)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count++;
                    }
                })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("multipleSwitchCasesSingleActionShouldWork()", 3);
    }

    @Test
    public void defaultShouldWork() {
        resetVariables();

        observable.lift(Conditional.switchOn(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return integer;
            }
        }).makeDefault()
                .caseWithBreak(12)
                .withCase(3)
                .withCase(4)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count++;
                    }
                })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("defaultShouldWork()", 6);
    }

    @Test
    public void multipleCasesShouldWork() {
        resetVariables();

        observable.lift(Conditional.switchOn(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return integer;
            }
        }).caseWithBreak(5)
                .withCase(25)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count++;
                    }
                }).caseWithBreak(68)
                .withCase(12)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count--;
                    }
                }).makeDefault()
                .caseWithBreak(3)
                .withCase(4)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count = count + 2;
                    }
                })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("multipleCasesShouldWork()", 4);
    }

    @Test
    public void defaultShouldCoverAllCases() {
        resetVariables();

        observable.lift(Conditional.switchOn(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return integer;
            }
        }).makeDefault()
                .caseWithBreak(1)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count++;
                    }
                })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("defaultShouldCoverAllCases()", 6);
    }

    @Test
    public void defaultOutOfOrderShouldWork() {
        resetVariables();

        observable.lift(Conditional.switchOn(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return integer;
            }
        }).caseWithBreak(5)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count++;
                    }
                }).makeDefault()
                .caseWithBreak(68)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count--;
                    }
                })
                .caseWithBreak(4)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count++;
                    }
                })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("defaultOutOfOrderShouldWork()", -2);
    }

    @Test
    public void caseWithoutBreakShouldWork() {
        resetVariables();

        observable.lift(Conditional.switchOn(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return integer;
            }
        }).caseWithoutBreak(5)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count++;
                    }
                }).caseWithoutBreak(12)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count++;
                    }
                }).caseWithoutBreak(25)
                .then(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        count++;
                    }
                })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("caseWithoutBreakShouldWork()", 3);
    }

    private void resetVariables() {
        // Reset count variable
        count = 0;

        // Helps test Observables and their results
        testSubscriber = new TestSubscriber<>();

        // Observerable used to test
        observable = Observable.from(testValues);
    }

    private void assertCodeExecutedSuccessfully() {
        // Make assertions
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(testValues.size());
        testSubscriber.assertReceivedOnNext(testValues);
    }

    private void assertTrue(String methodName, int valueCountShouldBe) {
        Assert.assertTrue("Count field value in " + methodName + " method in ConditionalSwitchTest class is not correct " +
                        "after performing test. Count value should be = " + valueCountShouldBe + " but it is = " + count,
                count == valueCountShouldBe);
    }

}

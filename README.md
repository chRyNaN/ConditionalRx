# ConditionalRx

If, else-if, else, and switch condition operators for RxJava. 

These operators are like a combination of both the `filter` and `doOnNext` operators. They filter on a condition then perform a function for those objects that passed the condition. All objects are emmitted whether they passed the filter condition or not.

## Example:

```java
myObservable.lift(Conditional.ifThis(myObject -> myObject.isTrue())
                      .then(myObject::performFunction)
                      .otherwise(myObject::performOtherFunction));
```

## Note:

This is a new experimental project and is currently not tested.

## TODO:

* Add switch statement methods
* Clean-up code
* Test code
* Add JavaDoc

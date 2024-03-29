package com.example.rxjava2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    /**
     * 基础使用create()
     */
    public void demo1(){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("message");
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("观察者接收到:onSubscribe"+d);
            }

            @Override
            public void onNext(String value) {
                System.out.println("观察者接收到"+value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                System.out.println("观察者接收到：onComplete");
            }
        });
    }
    /**
     * 基础使用 just
     * 使用just( )，将为你创建一个Observable并自动为你调用onNext( )发射数据
     * Consumer和Observer都是观察者
     */
    public void demo2(){
        Observable.just("demo2").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        });
    }
    /**
     * 基础使用 fromIterable
     * 使用fromIterable()，遍历集合，发送每个item。相当于多次回调onNext()方法，每次传入一个item。
     */
    public void demo3(){
        List<String> list=new ArrayList<>();
        for(int i=0;i<10;i++){
            list.add(i+"");
        }
        Observable.fromIterable(list).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        });
    }
    /**
     * 基础使用：defer
     * 当观察者订阅时，才创建Observable，并且针对每个观察者创建都是一个新的Observable。
     */
    public void demo4(){
        Observable.defer(new Callable<ObservableSource<?>>() {
            @Override
            public ObservableSource<?> call() throws Exception {
                return Observable.just("Hello","World");
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.println(o);
            }
        });
    }
    /**
     * 基础使用：interal
     * 创建一个按固定时间间隔发射整数序列的Observable，可用作定时器。即按照固定2秒一次调用onNext()方法。
     */
    public void demo5(){
        Observable.interval(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                System.out.println("hello");
            }
        });
    }
    /**
     * 基础使用：range
     * 创建一个发射特定整数序列的Observable，第一个参数为起始值，第二个为发送的个数，如果为0则不发送，负数则抛异常。
     * 上述表示发射1到20的数。即调用20次nNext()方法，依次传入1-20数字。
     */
    public void demo6(){
        Observable.range(5,20).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);
            }
        });
    }

    /**
     * 基础使用：timer
     * 创建一个Observable，它在一个给定的延迟后发射一个特殊的值，即表示延迟2秒后，调用onNext()方法。
     */
    public void demo7(){
        Observable.timer(2,TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long s) throws Exception {
                System.out.println(s);
            }
        });
    }
    /**
     * 中级使用：map
     * map()操作符，就是把原来的Observable对象转换成另一个Observable对象，同时将传输的数据进行一些灵活的操作，
     * 方便Observer获得想要的数据形式。
     */
    public void demo8(){
        Observable.just("hello").map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return s + " world";
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        });
    }
    /**
     * 高级使用：Scheduler
     * 在哪个线程生产事件，就在哪个线程消费事件。如果需要切换线程，就需要用到 Scheduler（调度器）
     * Schedulers.immediate() 在当前线程运行
     * Schedulers.newThread() 启用新线程
     * Schedulers.io() I/O 操作（读写文件、读写数据库、网络信息交互等)有线程池
     * AndroidSchedulers.mainThread() 主线程
     *
     * subscribeOn(): 指定Observable(被观察者)所在的线程，或者叫做事件产生的线程。
     * observeOn(): 指定 Observer(观察者)所运行在的线程，或者叫做事件消费的线程。
     */
    public void demo9(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                System.out.println("所在的线程："+Thread.currentThread().getName());
                System.out.println("发送的数据："+1+"");
                e.onNext(1);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("所在的线程：" + Thread.currentThread().getName());
                System.out.println("接收到的数据:" + "integer:" + integer);
            }


        });

    }
    /**
     * 高级使用：Disposable
     * 在RxJava中,用它来切断Observer(观察者)与Observable(被观察者)之间的连接，当调用它的dispose()方法时,
     * 它就会将Observer(观察者)与Observable(被观察者)之间的连接切断, 从而导致Observer(观察者)收不到事件。
     *
     * Disposable的作用是切断连接，确切地讲是将Observer(观察者)切断，不再接收来自被观察者的事件，而被观察者的事件却仍在继续执行。
     *
     * 当Observable(被观察者)发送了一个onComplete后, Observable(被观察者)中onComplete之后的事件将会继续发送,
     * 而Observer(观察者)收到onComplete事件之后将不再继续接收事件.
     */
    public void demo10(){
        Disposable dis=Observable.just("hello").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("发送的数据"+s);
            }
        });
    }

    /**
     * 高级使用：线程调度器（subscribeOn，subscribeOn）
     * subscribeOn() 指定的是上游发送事件的线程, observeOn() 指定的是下游接收事件的线程。
     * 多次指定上游的线程只有第一次指定的有效, 也就是说多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
     * 多次指定下游的线程是可以的, 也就是说每调用一次observeOn() , 下游的线程就会切换一次.
     *
     * doOnNext:可以拦截下游，对数据进行一次处理
     */
    public void demo11(){
        Observable.just("hello").subscribeOn(Schedulers.newThread()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    Log.i("MALEI",Thread.currentThread().getName());
                    System.out.println("send message "+s);
                }

            }).observeOn(Schedulers.io()).subscribe(new Consumer<String>() {
                @Override
            public void accept(String s) throws Exception {
                Log.i("MALEI",Thread.currentThread().getName());
                System.out.println("send message "+s);
            }
        });
    }

    public Observable<String> demo12(){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("hello");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //demo1();
        //demo2();
        //demo3();
        Observable<String> data=demo12();
        data.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i("TAG",s);
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}

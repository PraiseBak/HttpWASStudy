package com.otc.otc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

interface MyInterface {
    void myMethod();
}

class MyClass implements MyInterface {
    @Override
    public void myMethod() {
        System.out.println("MyClass.myMethod() called");
    }
}

public class ReflectionExample {
    public static void main(String[] args) {
        MyClass myClass = new MyClass();
        MyInterface proxy = (MyInterface) Proxy.newProxyInstance(
                MyInterface.class.getClassLoader(),
                new Class[]{MyInterface.class},
                new MyInvocationHandler(myClass));

        proxy.myMethod(); // 프록시 객체를 통해 메서드 호출
    }

    static class MyInvocationHandler implements InvocationHandler
    {
        private final Object target;

        MyInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Before method call");
            Object result = method.invoke(target, args);
            System.out.println("After method call");
            return result;
        }

    }
}













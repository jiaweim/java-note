package mjw.gson;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

public class InstanceCreatorForB implements InstanceCreator<A.B> {

    private final A a;

    public InstanceCreatorForB(A a) {
        this.a = a;
    }

    public A.B createInstance(Type type) {
        return a.new B();
    }
}
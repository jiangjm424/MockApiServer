package jm.droid.lib.singleton;

public abstract class Singleton<P, T> {

    public Singleton() {
    }

    private volatile T mInstance;

    protected abstract T create(P param);

    public final T get(P param) {
        if (mInstance == null) {
            synchronized (this) {
                if (mInstance == null) mInstance = create(param);
            }
        }
        return mInstance;
    }
}

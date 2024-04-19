package utils;

public class Tuple<T1, T2> {
    public T1 i;
    public T2 j;

    public Tuple(T1 i, T2 j) {
        this.i = i;
        this.j = j;
    }

    public T1 getFirst() {
        return i;
    }

    public T2 getSecond() {
        return j;
    }

    public void setSecond(T2 second) {
        this.j = second;
    }
}

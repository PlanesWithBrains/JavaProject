import java.time.Duration;

public class Pairs <A,B>{// переписанный duration для хранения данных
    public final A _1;
    public final B _2;

    public Pairs(A a,B b) {
        _1 = a;
        _2 = b;
    }
    public boolean equals(Pairs<A,B> that){
        return (_1.equals(that._1));
    }
    public Duration div(long divided){
        return ((Duration)_2).dividedBy(divided);
    }
}

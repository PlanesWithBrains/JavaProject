import java.time.Duration;

public class Pairs <A,B>{// переписанный pair для хранения данных
    public  A _1;
    public  B _2;

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
    public void incrim(){
        if(_2 instanceof Integer){
            Integer temp = Integer.valueOf(((Integer) _2).intValue() + 1);
            _2 = (B)temp;
        }
    }
}

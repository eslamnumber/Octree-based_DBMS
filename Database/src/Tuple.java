import java.io.Serializable;
import java.util.Hashtable;

public class Tuple implements Serializable {
    private Hashtable<String,Object> tuple;
    private String key;
    public Hashtable<String, Object> getTuple() {
        return tuple;
    }

    public void setTuple(Hashtable<String, Object> tuple) {
        this.tuple = tuple;
    }

    public Tuple(Hashtable<String,Object> tuble){
        this.tuple =tuble;
    }
    public Object get(String key){
        return tuple.get(key);
    }



}

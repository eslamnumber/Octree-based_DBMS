import java.io.Serializable;
import java.util.ArrayList;

public class OctPoint implements Serializable {
    ArrayList<OctPoint> points;
    private Object x;
    private Object y;
    private Object z;
    private  String reference;

    private boolean nullify = false;

    public OctPoint(Object x, Object y, Object z){
        points = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.z = z;

    }

    public OctPoint(){
        points = new ArrayList<>();
        nullify = true;
    }

    public Object getX(){
        return x;
    }

    public Object getY(){
        return y;
    }

    public Object getZ(){
        return z;
    }

    public boolean isNullified(){
        return nullify;
    }
    public void insert(OctPoint point,String reference){
        point.reference = reference;
        points.add(point);
    }
    public void remove(OctPoint point){
        for(int i = 0; i < points.size(); i++){
            if(points.get(i)==point){
                points.remove(i);
                return;
            }
        }
    }
    public ArrayList<OctPoint> getPoints(){
        return points;
    }
    public boolean CheckSize(){
        if (points.size()< 5)
            return true;
        return false;
    }
    public int getSize(){
        return points.size();
    }

    public String getValue() {
        return reference;
    }

}

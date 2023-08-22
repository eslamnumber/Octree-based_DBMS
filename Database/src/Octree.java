import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
public class Octree<T> implements Serializable {
    int c=0;

    private OctPoint point;
    private OctPoint topLeftFront, bottomRightBack;

    private Octree<T>[] children = new Octree[8];

    private String object;

    public Octree() {
        point= new OctPoint();
    }

    public Octree(Object x, Object y, Object z, String object) {
        point = new OctPoint(x, y, z);
        this.object = object;
    }

    public Octree(Object x1, Object y1, Object z1, Object x2, Object y2, Object z2) throws OutOfBoundsException, DBAppException {
        if (CompareTo(x2, x1) < 0 || CompareTo(y2, y1) < 0 || CompareTo(z2, z1) < 0) {
            throw new OutOfBoundsException("The bounds are not properly set!");
        }
        point= null;
        topLeftFront = new OctPoint(x1, y1, z1);
        bottomRightBack = new OctPoint(x2, y2, z2);
        for (int i = 0; i <= 7; i++) {
            children[i] = new Octree<>();
        }
    }

    public void insert(Object x, Object y, Object z, String object) throws Exception {
        c++;
//        if (CompareTo(x, topLeftFront.getX()) < 0 || CompareTo(x, bottomRightBack.getX()) > 0
//                || CompareTo(y, topLeftFront.getY()) < 0 || CompareTo(y, bottomRightBack.getY()) > 0
//                || CompareTo(y, topLeftFront.getZ()) < 0 || CompareTo(z, bottomRightBack.getZ()) > 0) {
//            throw new OutOfBoundsException("Insertion point is out of bounds! X: " + x + " Y: " + y + " Z: " + z + " Object Name: " + object.getClass().getName());
//        }

        Object midx = getAverge(topLeftFront.getX(), bottomRightBack.getX());
        Object midy = getAverge(topLeftFront.getY(), bottomRightBack.getY());
        Object midz = getAverge(topLeftFront.getZ(), bottomRightBack.getZ());

        int pos;

        if (CompareTo(x, midx) <= 0) {
            if (CompareTo(y, midy) <= 0) {
                if (CompareTo(z, midz) <= 0)
                    pos = OctLocations.TopLeftFront.getNumber();
                else
                    pos = OctLocations.TopLeftBottom.getNumber();
            } else {
                if (CompareTo(z, midz) <= 0)
                    pos = OctLocations.BottomLeftFront.getNumber();
                else
                    pos = OctLocations.BottomLeftBack.getNumber();
            }
        } else {
            if (CompareTo(y, midy) <= 0) {
                if (CompareTo(z, midz) <= 0)
                    pos = OctLocations.TopRightFront.getNumber();
                else
                    pos = OctLocations.TopRightBottom.getNumber();
            } else {
                if (CompareTo(z, midz) <= 0)
                    pos = OctLocations.BottomRightFront.getNumber();
                else
                    pos = OctLocations.BottomRightBack.getNumber();
            }
        }
        if (children[pos].point == null) {
            children[pos].insert(x, y, z, object);
        }
        else if(children[pos].point.isNullified()){
            children[pos]= new Octree<>(x,y,z,object);
            children[pos].point.insert(new OctPoint(x,y,z),object);
        }

        else if (children[pos].point.CheckSize()) {
            children[pos].point.insert(new OctPoint(x, y, z), object);
        } else {
            Object x_ = children[pos].point.getX();
            Object y_ = children[pos].point.getY();
            Object z_ = children[pos].point.getZ();
            ArrayList<OctPoint> Points = children[pos].point.getPoints();
            children[pos] = null;
            if (pos == OctLocations.TopLeftFront.getNumber()) {
                children[pos] = new Octree<>(topLeftFront.getX(), topLeftFront.getY(), topLeftFront.getZ(), midx, midy, midz);
            } else if (pos == OctLocations.TopRightFront.getNumber()) {
                children[pos] = new Octree<>(midx, topLeftFront.getY(), topLeftFront.getZ(), bottomRightBack.getX(), midy, midz);
            } else if (pos == OctLocations.BottomRightFront.getNumber()) {
                children[pos] = new Octree<>(midx, midy, topLeftFront.getZ(), bottomRightBack.getX(), bottomRightBack.getY(), midz);
            } else if (pos == OctLocations.BottomLeftFront.getNumber()) {
                children[pos] = new Octree<>(topLeftFront.getX(), midy, topLeftFront.getZ(), midx, bottomRightBack.getY(), midz);
            } else if (pos == OctLocations.TopLeftBottom.getNumber()) {
                children[pos] = new Octree<>(topLeftFront.getX(), topLeftFront.getY(), midz, midx, midy, bottomRightBack.getZ());
            } else if (pos == OctLocations.TopRightBottom.getNumber()) {
                children[pos] = new Octree<>(midx, topLeftFront.getY(), midz, bottomRightBack.getX(), midy, bottomRightBack.getZ());
            } else if (pos == OctLocations.BottomRightBack.getNumber()) {
                children[pos] = new Octree<>(midx, midy, midz, bottomRightBack.getX(), bottomRightBack.getY(), bottomRightBack.getZ());
            } else if (pos == OctLocations.BottomLeftBack.getNumber()) {
                children[pos] = new Octree<>(topLeftFront.getX(), midy, midz, midx, bottomRightBack.getY(), bottomRightBack.getZ());
            }
            for (int i = 0; i < Points.size(); i++) {
                children[pos].insert(Points.get(i).getX(),Points.get(i).getY(),Points.get(i).getZ(), Points.get(i).getValue());
            }

            children[pos].insert(x,y,z, object);
        }
    }

    public ArrayList<Object> get(Object x, Object y, Object z) throws Exception {
        if (CompareTo(x ,topLeftFront.getX())<0 || CompareTo(x , bottomRightBack.getX())>0
                || CompareTo(y ,topLeftFront.getY())<0 || CompareTo(y , bottomRightBack.getY())>0
                || CompareTo(y ,topLeftFront.getZ())<0|| CompareTo(z , bottomRightBack.getZ())>0) return null;
        Object midx = getAverge(topLeftFront.getX(),bottomRightBack.getX());
        Object midy = getAverge(topLeftFront.getY(), bottomRightBack.getY());
        Object midz = getAverge(topLeftFront.getZ(),bottomRightBack.getZ());

        int pos;

        if(CompareTo(x, midx)<=0){
            if(CompareTo(y, midy)<=0){
                if(CompareTo(z, midz)<=0)
                    pos = OctLocations.TopLeftFront.getNumber();
                else
                    pos = OctLocations.TopLeftBottom.getNumber();
            }else{
                if(CompareTo(z, midz)<=0)
                    pos = OctLocations.BottomLeftFront.getNumber();
                else
                    pos = OctLocations.BottomLeftBack.getNumber();
            }
        }else{
            if(CompareTo(y, midy)<=0){
                if(CompareTo(z, midz)<=0)
                    pos = OctLocations.TopRightFront.getNumber();
                else
                    pos = OctLocations.TopRightBottom.getNumber();
            }else {
                if(CompareTo(z, midz)<=0)
                    pos = OctLocations.BottomRightFront.getNumber();
                else
                    pos = OctLocations.BottomRightBack.getNumber();
            }
        }

        if(children[pos].point== null)
            return children[pos].get(x, y, z);
        if(children[pos].point.getSize()==0)
            return null;
        ArrayList<Object> objects = new ArrayList<>();
        for(int i=0; i<children[pos].point.getSize(); i++){
            if (CompareTo(((OctPoint)children[pos].point.getPoints().get(i)).getX(), x) == 0 && CompareTo(((OctPoint)children[pos].point.getPoints().get(i)).getY(), y) == 0 && CompareTo(((OctPoint)children[pos].point.getPoints().get(i)).getZ(), z) == 0)
                objects.add((T) ((OctPoint) children[pos].point.getPoints().get(i)).getValue());
        }
        return  objects;
    }

    public boolean remove(Object x, Object y, Object z,Object w) throws Exception {
        if (CompareTo(x ,topLeftFront.getX())<0 || CompareTo(x , bottomRightBack.getX())>0
                || CompareTo(y ,topLeftFront.getY())<0 || CompareTo(y , bottomRightBack.getY())>0
                || CompareTo(y ,topLeftFront.getZ())<0|| CompareTo(z , bottomRightBack.getZ())>0)  return false;
        Object midx = getAverge(topLeftFront.getX(),bottomRightBack.getX());
        Object midy = getAverge(topLeftFront.getY(), bottomRightBack.getY());
        Object midz = getAverge(topLeftFront.getZ(), bottomRightBack.getZ());

        int pos;

        if(CompareTo(x, midx)<=0){
            if(CompareTo(y, midy)<=0){
                if(CompareTo(z, midz)<=0)
                    pos = OctLocations.TopLeftFront.getNumber();
                else
                    pos = OctLocations.TopLeftBottom.getNumber();
            }else{
                if(CompareTo(z, midz)<=0)
                    pos = OctLocations.BottomLeftFront.getNumber();
                else
                    pos = OctLocations.BottomLeftBack.getNumber();
            }
        }else{
            if(CompareTo(y, midy)<=0){
                if(CompareTo(z, midz)<=0)
                    pos = OctLocations.TopRightFront.getNumber();
                else
                    pos = OctLocations.TopRightBottom.getNumber();
            }else {
                if(CompareTo(z, midz)<=0)
                    pos = OctLocations.BottomRightFront.getNumber();
                else
                    pos = OctLocations.BottomRightBack.getNumber();
            }
        }
        if(children[pos].point == null)
            return children[pos].remove(x, y, z,w);
        if(children[pos].point.isNullified())
            return false;
        else {
            boolean flag = false;
            for (int i = 0; i < children[pos].point.getSize(); i++) {
                if (CompareTo(((OctPoint) children[pos].point.getPoints().get(i)).getX(), x) == 0 &&
                        CompareTo(((OctPoint) children[pos].point.getPoints().get(i)).getY(), y) == 0 &&
                        CompareTo(((OctPoint) children[pos].point.getPoints().get(i)).getZ(), z) == 0 &&
                        CompareTo(((OctPoint) children[pos].point.getPoints().get(i)).getValue(), w) == 0)
                    children[pos].point.remove((OctPoint) children[pos].point.getPoints().get(i));
                flag= true;
            }
            if(flag)
                return true;
        }
        return false;
    }

    private static int CompareTo(Object keyOfTheInsert, Object colKeyValue)  throws DBAppException {
        int result;
        switch (keyOfTheInsert.getClass().getSimpleName()) {
            case "String":
                result = ((String) keyOfTheInsert).compareTo((String) colKeyValue);
                break;
            case "Date":
                result = ((Date) keyOfTheInsert).compareTo((Date) colKeyValue);
                break;
            case "Double":
                result = Double.compare((Double) keyOfTheInsert, (Double) colKeyValue);
                break;
            case "Integer":
                result = Integer.compare((Integer) keyOfTheInsert, (Integer) colKeyValue);
                break;
            default:
                throw new DBAppException();
        }
        return result;
    }

    public static String GetMidString(String word1,String word2) {
        int len1=word1.length();//ahmed
        int len2=word2.length();//ali
        String result="";
        int mid=0;
        for(int i=0;i<Math.min(len1,len2);i++){
            mid=((int)(word1.charAt(i)+word2.charAt(i)))/2;
            result+=(char)mid;
        }
        if(len1>len2)
            result+=word1.substring(len2);
        else if(len2>len1)
            result+=word2.substring(len1);

        return result;
    }

    public static String getMidDate(String dateStr1, String dateStr2) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(dateStr1);
        Date date2 = sdf.parse(dateStr2);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        long midMillis = cal1.getTimeInMillis() + (cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 2;
        Calendar midCal = Calendar.getInstance();
        midCal.setTimeInMillis(midMillis);
        return sdf.format(midCal.getTime());
    }

    public static Object getAverge(Object keyOfTheInsert, Object colKeyValue) throws Exception {
        Object result;
        switch (keyOfTheInsert.getClass().getSimpleName()) {
            case "String":
                result = GetMidString((String) keyOfTheInsert,(String) colKeyValue);
                break;
            case "Date":
                result = getMidDate(((Date) keyOfTheInsert).toString(),((Date) colKeyValue).toString());
                break;
            case "Double":
                result = ((Double) keyOfTheInsert + (Double) colKeyValue)/2;
                break;
            case "Integer":
                result = ((Integer)keyOfTheInsert+(Integer) colKeyValue)/2 ;
                break;
            default:
                throw new DBAppException();
        }
        return result;
    }

    public boolean helperRange(Object x, Object minX, Object maxX, String operator) throws DBAppException {
        int compareXMinx = CompareTo(x, minX);
        int compareXMaxx = CompareTo(x, maxX);


        boolean con1;
        switch (operator){
            case "<":
                con1 = compareXMinx > 0;
                break;
            case "<=":
                con1 = compareXMinx >= 0;
                break;
            case ">":
                con1 = compareXMaxx < 0;
                break;
            case ">=":
                con1 = compareXMaxx <= 0;
                break;
            case "!=":
                con1 = true;
                break;
            default:  // =
                con1 = compareXMinx >= 0 && compareXMaxx <= 0;
                break;
        }
        return con1;
    }

    public boolean CheckRange(Object x, Object Comx, String operator) throws DBAppException {
        boolean cond1;
        switch (operator){
            case "<":
                cond1 = CompareTo(Comx, x) < 0;
                break;
            case "<=":
                cond1 = CompareTo(Comx, x) <= 0;
                break;
            case ">":
                cond1 = CompareTo(Comx, x) > 0;
                break;
            case ">=":
                cond1 = CompareTo(Comx, x) >= 0;
                break;
            case "!=":
                cond1 = CompareTo(Comx, x) != 0;
                break;
            default:
                cond1 = CompareTo(Comx, x) == 0;
        }

        return cond1;
    }

    public ArrayList<Object> getRange(Object x, Object y, Object z, String[] operations) throws DBAppException {
        ArrayList res = new ArrayList<>();
        Object minX = topLeftFront.getX();
        Object minY = topLeftFront.getY();
        Object minZ = topLeftFront.getZ();
        Object maxX = bottomRightBack.getX();
        Object maxY = bottomRightBack.getY();
        Object maxZ = bottomRightBack.getZ();

        boolean con1 = helperRange(x, minX, maxX, operations[0]);
        boolean con2 = helperRange(y, minY, maxY, operations[1]);
        boolean con3 = helperRange(z, minZ, maxZ, operations[2]);

        if (con1 && con2 && con3) {
            for (int i = 0; i < children.length; i++) {
                if (children[i].point == null) {
                    ArrayList<Object> temp = children[i].getRange(x, y, z, operations);
                    res.addAll(temp);
                } else {
                    for (int j = 0; j < children[i].point.getSize(); j++) {
                        OctPoint op =  (OctPoint) children[i].point.getPoints().get(j);
                        boolean cond1 = CheckRange(x, op.getX(), operations[0]);
                        boolean cond2 = CheckRange(y, op.getY(), operations[1]);
                        boolean cond3 = CheckRange(z, op.getZ(), operations[2]);
                        if (cond1 && cond2 && cond3)
                            res.add(op.getValue());
                    }
                }
            }
        }
        return res;
    }
    public void serializeObject(String path) throws DBAppException {
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this);
            objectOut.close();
        }catch (Exception e){
            throw new DBAppException(e.getMessage());
        }
    }
    public Object deserializeObject(String path) throws DBAppException {
        Object o;
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            o = objectIn.readObject();
            objectIn.close();
            fileIn.close();
        } catch (Exception e) {
            throw new DBAppException(e.getMessage());
        }
        return o;
    }

    public static void main(String[] args) throws Exception {
        Octree<String> tree = new Octree<>(0,0,0, 30, 30, 30);
        int count = 0;
        tree.insert(4, 4, 4, "ma");
        tree.insert(4, 4, 4, "mb");
        tree.insert(4, 4, 4, "mc");
        tree.insert(6, 6, 6, "b");
        //tree.insert(4, 8, 8, "a");
        tree.insert(9, 9, 9, "c");
        tree.insert(10, 10, 10, "d");
        tree.insert(11, 11, 11, "e");
        tree.insert(12, 12, 12, "f");
        tree.insert(13, 13, 13, "g");
        tree.insert(14, 14, 14, "h");
        tree.insert(15, 15, 15, "i");
        tree.insert(16, 16, 16, "j");
        tree.insert(17, 17, 17, "k");
        tree.insert(18, 18, 18, "l");
        tree.insert(19, 19, 19, "m");
        tree.insert(20, 20, 20, "n");
        tree.insert(21, 21, 21, "o");
        tree.insert(22, 22, 22, "p");
        tree.insert(23, 23, 23, "q");
        tree.insert(24, 24, 24, "r");
        tree.insert(25, 25, 25, "s");
        tree.insert(26, 26, 26, "t");
        tree.remove(15,15,15,"i");
        ArrayList<Object> result = tree.getRange(25,25,25,new String[]{"<","<","<"});
        for(int i =0 ;i<result.size();i++){
            System.out.println(result.get(i)+" ");
        }
    }
}
//for (int i = 0; i < Points.size(); i++) {
//                children[pos].insert(x_,y_,z_, Points.get(i).getValue());
//            }

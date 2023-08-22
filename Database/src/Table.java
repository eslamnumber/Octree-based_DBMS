import java.io.*;
import java.util.*;

public class Table implements Serializable {
    private final String tableName;

    public void setColNameMin(Hashtable<String, Object> colNameMin)  {
        ColNameMin = colNameMin;
    }
    public void setColNameMax(Hashtable<String, Object> colNameMax) {
        ColNameMax = colNameMax;
    }

    public void setColNameType(Hashtable<String, String> colNameType) {
        ColNameType = colNameType;
    }

    public Hashtable<String, Object> getColNameMin() {
        return ColNameMin;
    }

    public Hashtable<String, Object> getColNameMax() {
        return ColNameMax;
    }

    public Hashtable<String, String> getColNameType() {
        return ColNameType;
    }

    private Hashtable<String, Object> ColNameMin;
    private Hashtable<String, Object> ColNameMax;
    private Hashtable<String, String> ColNameType;



    public void setClusteringKeyType(String clusteringKeyType) {
        ClusteringKeyType = clusteringKeyType;
    }

    private String ClusteringKeyType;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;
    private final  Vector<String> filePaths;

    public ArrayList<String[]> getIndexes() {
        return Indexes;
    }

    private final ArrayList<String[]> Indexes;

    public Table(String tableName) {
        this.tableName = tableName;
        filePaths = new Vector<String>();
        Indexes = new ArrayList<>();
        ColNameMax = new Hashtable<>();
        ColNameMin = new Hashtable<>();
        ColNameType = new Hashtable<>();
    }

    public String getTablePath() {
        return "src/Resources/Tables/" + tableName + "/" + tableName + ".class";
    }

    public int getPageCount() {
        return filePaths.size();
    }

    public void AddPage(String s) {
        this.filePaths.add(s);
    }

    public void removePage(String s) {
        this.filePaths.remove(s);
    }

    public Vector<String> getFilePath() throws DBAppException {
        if(filePaths==null)
            throw new DBAppException("Table is not loaded");
        return filePaths;
    }

    public String getTableName() {
        return tableName;
    }

    public int isINDEX(ArrayList<String> arr) {
        for (int i = 0; i < Indexes.size(); i++) {
            String[] index = Indexes.get(i);
            if (hasSameElements(index, arr)) {
                return i;
            }
        }
        return -1;
    }

    public String getPagePath(int x) {
        return "src/Resources/Tables/" + getTableName() + "/" + "page" + x + ".class";
    }

    public static boolean hasSameElements(String[] array, ArrayList<String> list) {
        if (array.length != list.size()) {
            return false;
        }

        ArrayList<String> tempList = new ArrayList<>(Arrays.asList(array));

        for (String element : list) {
            if (!tempList.contains(element)) {
                return false;
            } else {
                tempList.remove(element);
            }
        }

        return true;
    }

    public int getComs() {
        return Indexes.size();
    }

    public String[] GetIndex(int x) {
        return Indexes.get(x);
    }

    public void AddIndex(String[] x) {
        Indexes.add(x);
    }

    private static int CompareTo(Object keyOfTheInsert, Object colKeyValue) throws DBAppException {
        int result;
        if (keyOfTheInsert.getClass().getSimpleName().equals(colKeyValue.getClass().getSimpleName()))
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
                    throw new DBAppException("DataBase Doesnot Support this type");
            }
        else
            throw new DBAppException();
        return result;
    }

    public int FindPage(Object KeyofNewValue, boolean flag) throws DBAppException, IOException {
        int low = 0;
        int high = filePaths.size() - 1;
        if (filePaths.size() == 0)
            return -1;
        return FindPageHelper(KeyofNewValue, key, low, high, flag);
    }

    private int FindPageHelper(Object keyofNewValue, String key, int low, int high, boolean flag) throws DBAppException, IOException {
        int mid = (low + high) / 2;
        Page x = (Page) deserializeObject(filePaths.get(mid));
        int compare = CompareTo(keyofNewValue, x.getMaxClusteringValue().getTuple().get(key));
        x.serializeObject();
        if (low == high)
            return low;
        else if (compare < 0)
            return FindPageHelper(keyofNewValue, key, low, mid - 1, flag);
        else if (compare > 0)
            return FindPageHelper(keyofNewValue, key, mid + 1, high, flag);
        else if (flag)
            throw new DBAppException("Use a unique Key");
        else
            return mid;
    }

    public void AddToTree(Hashtable<String, Object> htblColNameValue, String key, Page page) throws DBAppException {
        if (getComs() != 0) {
            Octree octree;
            try {
                for (int j = 0; j < getComs(); j++) {
                    String[] strColNames = GetIndex(j);
                    octree = (Octree) deserializeObject(getIndexPath(j));
                    octree.insert(htblColNameValue.get(strColNames[0]),
                            htblColNameValue.get(strColNames[1]),
                            htblColNameValue.get(strColNames[2]), htblColNameValue.get(key) + "," + page.getPath());
                    serializeObject(octree,getIndexPath(j));

                }
            } catch (Exception e) {
                throw new DBAppException(e.getMessage());
            }
        }
    }



    public void checkValidity(Hashtable<String, Object> htblColNameValue) throws DBAppException {
        for (String key : htblColNameValue.keySet()) {
            System.out.println();
            if (CompareTo(htblColNameValue.get(key), this.getColNameMax().get(key)) > 0) {
                throw new DBAppException("The value of " + key + " is greater than the maximum value of " + key);
            }
            if (CompareTo(htblColNameValue.get(key), getColNameMin().get(key)) < 0)
                throw new DBAppException("The value of " + key + " is less than the minimum value of " + key);

            if (getColNameType().get(key).equals("java.lang.String") && !(htblColNameValue.get(key) instanceof String))
                throw new DBAppException("The value of " + key + " is not a string");

            if (getColNameType().get(key).equals("java.lang.Integer") && !(htblColNameValue.get(key) instanceof Integer))
                throw new DBAppException("The value of " + key + " is not an integer");

            if (getColNameType().get(key).equals("java.lang.Double") && !(htblColNameValue.get(key) instanceof Double))
                throw new DBAppException("The value of " + key + " is not a double");

            if (getColNameType().get(key).equals("java.util.Date") && !(htblColNameValue.get(key) instanceof Date))
                throw new DBAppException("The value of " + key + " is not a date");

        }
        for (String key : getColNameType().keySet()) {
            if (!htblColNameValue.containsKey(key))
                htblColNameValue.put(key, "null");
        }
    }

    public void serializeObject() throws DBAppException {
        try {
            FileOutputStream fileOut = new FileOutputStream(getTablePath());
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this);
            objectOut.close();
        } catch (Exception e) {
            throw new DBAppException(e.getMessage());
        }
    }

    public void serializeObject(Object o, String s) throws DBAppException {
        try {
            FileOutputStream fileOut = new FileOutputStream(s);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(o);
            objectOut.close();
        } catch (Exception e) {
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

    public void removeFromTree(Tuple overMax, String key, Page page) throws DBAppException {
        if (getComs() != 0) {
            Octree octree;
            try {
                for (int j = 0; j < getComs(); j++) {
                    String[] strColNames = GetIndex(j);
                    octree = (Octree) deserializeObject(getIndexPath(j));
                    octree.remove(overMax.getTuple().get(strColNames[0]),
                            overMax.getTuple().get(strColNames[1]),
                            overMax.getTuple().get(strColNames[2]), overMax.getTuple().get(key) + "," + page.getPath());
                    serializeObject(octree, getIndexPath(j));

                }
            } catch (Exception e) {
                throw new DBAppException(e.getMessage());
            }
        }
    }

    public void FirstInsert(Hashtable<String, Object> htblColNameValue) throws DBAppException, IOException {
        Page page = new Page(getPagePath(getPageCount()));
        filePaths.add(getPagePath(getPageCount()));
        AddToTree(htblColNameValue, getKey(), page);
        page.insert(0, new Tuple(htblColNameValue));
    }

    public void MakeNewPage() throws DBAppException {
        String PagePath = getPagePath(getPageCount());
        Page page = new Page(PagePath);
        filePaths.add(PagePath);
    }

    public Tuple DeleteLastRecord(Page page) throws DBAppException {
        Tuple overMax = page.delete(page.getCount() - 1);//The greater key in the page is the one should be shifted
        removeFromTree(overMax, getKey(), page);
        return overMax;
    }

    public void insertInBeginOfPage(Tuple tuple, int x) throws DBAppException {
        Page page = (Page) deserializeObject(filePaths.get(x + 1));     // get the next page to add
        page.insert(0, tuple);// the greater value removed from previous page should the smallest value in the next page
        AddToTree(tuple.getTuple(), getKey(), page);

    }

    public Page Insert(Hashtable<String, Object> htblColNameValue, int TargetPage) throws DBAppException {
        Page page = (Page) deserializeObject(filePaths.get(TargetPage));
        page.insertIntoPage(htblColNameValue.get(key), key, htblColNameValue);
        AddToTree(htblColNameValue, getKey(), page);
        return page;
    }

    public void insertIntoTable(Hashtable<String, Object> htblColNameValue) throws DBAppException, IOException {
        checkValidity(htblColNameValue);
        int TargetPage = FindPage(htblColNameValue.get(key), true);
        if (TargetPage == -1) {     // if it is the first insert make a new page insert and break
            FirstInsert(htblColNameValue);
        }
        else {
            Page page = Insert(htblColNameValue, TargetPage);
            if (page.Full()) {
                //if the page is full before insertion then insert and  shift
                for (int i = TargetPage; i < filePaths.size() && page.Full(); i++) {
                    Tuple overMax = DeleteLastRecord(page);
                    if (i == filePaths.size() - 1) // if the page which become full is the last page then make a new page
                        MakeNewPage();
                    insertInBeginOfPage(overMax, i);

                }
            }
        }
        serializeObject();
    }
    public String getIndexPath(int x) {
        return "src/Resources/Tables/" + getTablePath() + "/" + "Octree" + x + ".class";
    }
    public void deleteFromTable(Hashtable<String, Object> htblColNameValue) throws DBAppException, IOException {
        checkValidity(htblColNameValue);
        int TargetPage = FindPage(htblColNameValue.get(key), false);
        if (TargetPage != -1) {
            Page page = (Page) deserializeObject(filePaths.get(TargetPage));
            Tuple tuple = page.deleteFromPage(htblColNameValue.get(key), key);
            removeFromTree(tuple, getKey(), page);
            serializeObject();
        }
    }
    public void deleteUsingIndex(Hashtable<String,Object> htblColNameValue) throws Exception {
        ArrayList<String> ColNames = new ArrayList<>();
        ColNames.addAll(htblColNameValue.keySet());
        int index = isINDEX(ColNames);   // to get the index of the index
        String indexPath = getIndexPath(index);
        Octree octree = (Octree) deserializeObject(indexPath);
        ArrayList<Object> values = octree.getRange(htblColNameValue.get(GetIndex(index)[0]), htblColNameValue.get(GetIndex(index)[1]), htblColNameValue.get(GetIndex(index)[2]), new String[]{"=", "=", "="});
        for (String s : ColNames)
            htblColNameValue.remove(s);
        for (int i = 0; i < values.size(); i++) {
            Page page = (Page) deserializeObject(GetPageFromIndex((String) values.get(i)));
            page.deleteFromPage(this,htblColNameValue, GetKeyFromIndex((String) values.get(i)));
            }
        serializeObject();
        }
        public String GetKeyFromIndex(String s ){
            String[] strColNames = s.split(",");
            return strColNames[0];
        }
        public String GetPageFromIndex(String s){
            String[] strColNames = s.split(",");
            return strColNames[1];
        }
    public void deleteLinear(Hashtable<String, Object> htblColNameValue) throws DBAppException {
        for (int i = 0; i < filePaths.size(); i++) {
            Page page = (Page) deserializeObject(filePaths.get(i));
            for (int j = 0; j < page.getCount(); j++) {
                Tuple tuple = page.getTuples().get(j);
                boolean flag = CheckIfValidRowToDelete(htblColNameValue, tuple);
                if (flag) {
                    page.delete(j);
                    removeFromTree(tuple, getKey(), page);
                    j--;
                }
            }
        }
        serializeObject();
    }
    public boolean CheckIfValidRowToDelete(Hashtable<String,Object> htblColNameValue, Tuple tuple) throws DBAppException {
        boolean flag = true;
        for (String Key : htblColNameValue.keySet()) {
            if (tuple.get(Key) == "null" && !htblColNameValue.get(Key).equals("null"))
                flag = false;
            else if (CompareTo(htblColNameValue.get(Key), tuple.get(Key)) != 0)
                flag = false;
        }
        return flag;
    }
    public void update(Hashtable<String,Object> htblColNameValue,Object key) throws DBAppException, IOException {
        checkValidity(htblColNameValue);
        int TargetPage=FindPage(key,false);
        Page page = (Page) deserializeObject(filePaths.get(TargetPage));
        page.updatePage(key,this,htblColNameValue);
        serializeObject();
    }
}


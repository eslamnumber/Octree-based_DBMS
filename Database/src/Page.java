import java.io.*;
import java.util.*;

public class Page implements Serializable {

	private String path;
	private Vector<Tuple> tuples;
	public Page(String path) {
		this.path = path;
		tuples = new Vector<>();
	}
	public String getPath(){
		return path;
	}
	public Tuple getMinClusteringValue() {
		return tuples.get(0);
	}

	public Tuple getMaxClusteringValue() {
		return tuples.get(tuples.size()-1);
	}

	public Vector<Tuple> getTuples() {
		return tuples;
	}
	public void setTuples(Vector<Tuple> Tubles){
		this.tuples =Tubles;
	}
	public int getCount(){
		return tuples.size();
	}
	public Hashtable<String,Object> getTuple(int Row) throws DBAppException {
		if(Row >= tuples.size())
			 throw new DBAppException("Row is out of bounds");
		return tuples.get(Row).getTuple();
	}
	public boolean Full(){
		if(tuples.size()== 101)
			return true;
		return false ;
	}
	public void insert(int x , Tuple tuble) throws DBAppException {
		tuples.add(x,tuble);
		serializeObject();
	}

	public Tuple delete(int Row) throws DBAppException {
		Tuple x = tuples.remove(Row);
		serializeObject();
		return x;

	}
	public int FindRow(Object KeyofTheInsert, String Colkey, boolean flag) throws DBAppException {

		int low = 0;
		int high = getCount() - 1;
		return FindRowHelper( KeyofTheInsert, Colkey, flag, low, high);
	}
	public int FindRowHelper(Object KeyofTheInsert, String Colkey, boolean flag, int low, int high) throws DBAppException {
		if (low > high)
			return low;

		int mid = (low + high) / 2;
		Tuple x = getTuples().get(mid);
		Object ColkeyValue = x.get(Colkey);
		int compare = CompareTo(KeyofTheInsert, ColkeyValue);
		if(low > high&&flag)
			return low;
		else if(low > high)
			throw new DBAppException("Key not found");
		else if (compare < 0)
			return FindRowHelper( KeyofTheInsert, Colkey, flag, low, mid - 1);
		else if (compare > 0)
			return FindRowHelper( KeyofTheInsert, Colkey, flag, mid + 1, high);
		else if (flag)
			throw new DBAppException("Use a unique Key");
		else
			return mid;
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

	public void insertIntoPage(Object KeyOfTheInsert, String ColKey,Hashtable<String,Object> tuple) throws DBAppException {
		int Row = FindRow(KeyOfTheInsert, ColKey, true);
		insert(Row, new Tuple(tuple));
		serializeObject();
	}
	public void deleteFromPage(Table table,Hashtable<String,Object> htblColNameValue,String key) throws DBAppException {
			boolean flag = true;
			int TargetRow = FindRow(htblColNameValue.get(table.getKey()), table.getKey(), false);//Find the row
			for (String s : htblColNameValue.keySet())
				if (CompareTo(htblColNameValue.get(s), getTuple(TargetRow).get(s)) != 0)
					flag = false;
			if (flag) {
				Tuple tuple = delete(TargetRow);
				table.removeFromTree(tuple,table.getKey(), this);
			}
		}

	public Tuple deleteFromPage(Object KeyOfTheInsert, String ColKey) throws DBAppException {
		int Row = FindRow(KeyOfTheInsert, ColKey, false);
		if(Row>=getCount())
			throw new DBAppException("Key Not Found");
		Tuple x = getTuples().get(Row);
		delete(Row);
		if(getCount()==0)
			deletePage();
		else
			serializeObject();
        return x;
	}
	public void deletePage(){
		File file = new File(path);
		file.delete();
	}
	public void serializeObject() throws DBAppException {
		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(this);
			objectOut.close();
		}catch (Exception e){
			throw new DBAppException(e.getMessage());
		}
	}
	public void updatePage(Object KeyOfTheInsert, Table table,Hashtable<String,Object> htblColNameValue) throws DBAppException {
		int Row = FindRow(KeyOfTheInsert, table.getKey(), false);
		Tuple OldRow = getTuples().get(Row);
		TheNewRecordAfterUpdateIs(Row, htblColNameValue);
		ReplaceRecords(table, htblColNameValue, Row, OldRow);
		serializeObject();
	}
	public void ReplaceRecords(Table table,Hashtable<String,Object> htblColNameValue,int Row, Tuple OldRow) throws DBAppException {
		table.removeFromTree(OldRow, table.getKey(), this);
		tuples.set(Row, new Tuple(htblColNameValue));
		table.AddToTree(tuples.get(Row).getTuple(), table.getKey(), this);
	}
	public void TheNewRecordAfterUpdateIs(int Row , Hashtable<String,Object> htblColNameValue) throws DBAppException {
		Tuple OldRow = getTuples().get(Row);
		for (String Key : OldRow.getTuple().keySet()) {// if the attribute in the row isnot found in the htblColumnNameValue then it doesnot Need  to be Updated
			if (htblColNameValue.get(Key).equals("null") == true)   // remove this key that has null values from new row
				htblColNameValue.put(Key, OldRow.get(Key));// Then put the attributes Doesnot need to be updated in htblColNamValue
		}
	}

	}





import java.io.IOException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.FileWriter;
public class DBApp {
	public void init() throws IOException {


		File resourcesDirectory = new File("src/Resources");
		if (!resourcesDirectory.exists()) resourcesDirectory.mkdirs();

		//create directory for tables
		File tableDirectory = new File("src/Resources/Tables");
		if (!tableDirectory.exists()) tableDirectory.mkdirs();

		File dbAppConfig = new File("src/Resources/DBApp.config");
		if (!dbAppConfig.exists()) dbAppConfig.createNewFile();
		File file = new File("src/Resources/" + "Metadata" + ".csv");


	}

	public void createTable(String strTableName,
							String strClusteringKeyColumn,
							Hashtable<String, String> htblColNameType,
							Hashtable<String, String> htblColNameMin,
							Hashtable<String, String> htblColNameMax) throws DBAppException, ParseException, IOException {
		for (String col : htblColNameType.keySet())
			if (!htblColNameType.containsKey(col) || !htblColNameType.containsKey(col))
				throw new DBAppException();

		File tableDirectory = new File("src/Resources/Tables/" + strTableName);
		if (tableDirectory.exists())
			throw new DBAppException("table Exists");
		else
			tableDirectory.mkdir();
		Table table = new Table(strTableName);
		String metadataFilePath =getMetadataPath();
		try (BufferedWriter metadataWriter = new BufferedWriter(new FileWriter(metadataFilePath, true))) {
			metadataWriter.write("Table Name,Column Name,Column Type,ClusteringKey,IndexName,IndexType,min,max");
			metadataWriter.newLine();
			for (String columnName : htblColNameType.keySet()) {
				StringBuilder metadataRowBuilder = new StringBuilder();
				metadataRowBuilder.append(strTableName).append(",");
				metadataRowBuilder.append(columnName).append(",");
				metadataRowBuilder.append(htblColNameType.get(columnName)).append(",");

				if (columnName.equals(strClusteringKeyColumn)) {
					table.setKey(columnName);
					table.setClusteringKeyType(htblColNameType.get(columnName));
					metadataRowBuilder.append("true,");
				} else {
					metadataRowBuilder.append("false,");
				}

				metadataRowBuilder.append("null,null,");
				metadataRowBuilder.append(htblColNameMin.get(columnName)).append(",");
				metadataRowBuilder.append(htblColNameMax.get(columnName));
				metadataWriter.write(metadataRowBuilder.toString());
				metadataWriter.newLine();
			}

		} catch (IOException e) {
			throw new DBAppException();
		}
		getFormat(table, htblColNameType, htblColNameMin, htblColNameMax);
		serializeObject(table,table.getTablePath());
	}

	public static int[] readConfig() {
		Properties prop = new Properties();
		String filePath = "src/Resources/DBApp.config";
		InputStream is = null;
		try {
			is = new FileInputStream(filePath);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		try {
			prop.load(is);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		int[] arr = new int[2];
		arr[0] = Integer.parseInt(prop.getProperty("MaximumRowsCountinPage"));
		arr[1] = Integer.parseInt(prop.getProperty("MaximumKeysCountinIndexBucket"));
		return arr;
	}


	public Object deserializeObject(String path) throws DBAppException  {
		try{FileInputStream fileIn = new FileInputStream(path);
		ObjectInputStream objectIn = new ObjectInputStream(fileIn);
		Object o = objectIn.readObject();
		objectIn.close();
		fileIn.close();
		return o;
		}catch (Exception e){
			throw new DBAppException(e.getMessage());
		}
		//return null;
	}
    public void SetTableColNameType(Table table,Hashtable<String , String > htblColNameType) throws DBAppException {
		Hashtable<String,String> colNameType = new Hashtable<>();
      for(String key:htblColNameType.keySet()){
		colNameType.put(key,htblColNameType.get(key));
	  }
	  table.setColNameType(colNameType);
	}

	public void SetTableColNameMin(Table table,Hashtable<String , String  > htblColNameMin) throws DBAppException{
		Hashtable<String,Object> colNameMin = new Hashtable<>();
		for(String key:htblColNameMin.keySet()){
			colNameMin.put(key,getKeyFormat(htblColNameMin.get(key), table.getColNameType().get(key)));
		}
		table.setColNameMin(colNameMin);
	}
	public void SetTableColNameMax(Table table,Hashtable<String , String  > htblColNameMax) throws DBAppException{
		Hashtable<String,Object> colNameMax = new Hashtable<>();
		for(String key:htblColNameMax.keySet()){
			colNameMax.put(key,getKeyFormat(htblColNameMax.get(key), table.getColNameType().get(key)));
		}
		table.setColNameMax(colNameMax);
	}
	public void getFormat(Table table ,Hashtable<String, String> htblColNameType,
							Hashtable<String, String> htblColNameMin,
							Hashtable<String, String > htblColNameMax) throws  DBAppException {
		SetTableColNameType(table, htblColNameType);
		SetTableColNameMin(table, htblColNameMin);
		SetTableColNameMax(table, htblColNameMax);
	}

	public void insertIntoTable(String strTableName,
								Hashtable<String, Object> htblColNameValue) throws DBAppException, IOException {
		Table table = (Table) deserializeObject(getTablePath(strTableName));
		table.getFilePath();
		table.insertIntoTable(htblColNameValue);
		table.serializeObject();

	}
	public String getTablePath(String s) {
		return "src/Resources/Tables/" + s + "/" + s + ".class";
	}

	public String getMetadataPath() {
		return "src/Resources/" + "Metadata" + ".csv";
	}
	public Object getKeyFromTree(Table table,String s) throws DBAppException {
		String[] arr = s.split(",");
		return getKeyFormat(arr[0], table.getColNameType().get(table.getKey()));

	}

	public String getPagePathFromTree(String s) {
		String[] arr = s.split(",");
		return arr[1];
	}


	public void serializeObject(Object o, String s) throws DBAppException {
		try {
			FileOutputStream fileOut = new FileOutputStream(s);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(o);
			objectOut.close();
		} catch (IOException e) {
			throw new DBAppException(e.getMessage());
		}
	}

	public void deleteFromTable(String strTableName,
								Hashtable<String, Object> htblColNameValue) throws Exception {

		Table table=(Table) deserializeObject(getTablePath(strTableName));
		table.getFilePath();
		ArrayList<String> ColNames = new ArrayList<>();
		ColNames.addAll(htblColNameValue.keySet());
		Object KeyOfNewValue = htblColNameValue.get(table.getKey());
		int index = table.isINDEX(ColNames);   // to get the index of the index
		if (index != -1)
			table.deleteUsingIndex(htblColNameValue);
		else if (KeyOfNewValue != null)    //if the key in the page then search for it
		   table.deleteFromTable(htblColNameValue);

		else
			table.deleteLinear(htblColNameValue);

		table.serializeObject();
	}

	public void updateTable(String strTableName,
							String strClusteringKeyValue,
							Hashtable<String, Object> htblColNameValue) throws DBAppException, IOException {
		Table table = (Table) deserializeObject(getTablePath(strTableName));
		table.getFilePath();
		Object key = getKeyFormat(strClusteringKeyValue, table.getColNameType().get(table.getKey()));
		table.update(htblColNameValue, key);
		serializeObject(table, table.getTablePath());
	}

	public void ChangeMetaData(Table table, String[] strarrColName) throws DBAppException {
		try {
			BufferedReader br = new BufferedReader(new FileReader(getMetadataPath()));
			String line;
			List<String> lines = new ArrayList<>();
			while ((line = br.readLine()) != null) {
				String[] row = line.split(",");
				if (row[0].equals(table.getTableName())) {
					for (String colName : strarrColName) {
						if (row[1].equals(colName)) {
							row[4] = table.getComs() + "";
							row[5] = "Octree";
							line = String.join(",", row);
						}
					}
				}
				lines.add(line);
			}
			Files.write(java.nio.file.Path.of(getMetadataPath()), lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new DBAppException(e.getMessage());
		}
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
			throw new DBAppException("Please ENTER "+colKeyValue.getClass().getSimpleName()+" number!");
		return result;
	}

	public static Object getKeyFormat(String key,String keyType) throws DBAppException {
		switch (keyType) {
			case "java.lang.Integer":
				return Integer.parseInt(key);
			case "java.lang.String":
				return key;
			case "java.lang.Double":
				return Double.parseDouble(key);
			case "java.util.Date":
				try {
					return new SimpleDateFormat("yyyy-MM-dd").parse(key);
				} catch (ParseException e) {
					throw new DBAppException(e.getMessage());
				}
			default:
				 throw new DBAppException("PLEASE ENTER "+keyType+"number!");
		}
	}   
	public void CheckIfValidIndex(Table table , String[] strarrColName) throws DBAppException {
		for (String colName : strarrColName) {
			if (!table.getColNameType().containsKey(colName)) {
				throw new DBAppException("Column " + colName + " does not exist in table " + table.getTableName());
			}
		}
		ArrayList<String> Index = new ArrayList<>();
		for (String colName : strarrColName)
		Index.add(colName);
         int index = table.isINDEX(Index);
		 if(index == -1)
			throw new DBAppException("This Index is already found");
	}
	public void makeAnOctree(Table table, String[] strarrColName) throws OutOfBoundsException, DBAppException{
		String indexPath = table.getIndexPath(table.getComs());
		Octree octree= new Octree(table.getColNameMin().get(strarrColName[0]),
				            table.getColNameMin().get(strarrColName[1]),
				            table.getColNameMin().get(strarrColName[2]),
				            table.getColNameMax().get(strarrColName[0]),
				            table.getColNameMax().get(strarrColName[1]),
				            table.getColNameMax().get(strarrColName[2]));
		for (int i = 0; i < table.getFilePath().size(); i++) {
				Page page = (Page) deserializeObject(table.getFilePath().get(i));
				for (Tuple tuple : page.getTuples()) {
					table.AddToTree(tuple.getTuple(),table.getKey(), page);
				}
					serializeObject(page,page.getPath());
			}
			table.AddIndex(strarrColName);
			serializeObject(octree,indexPath);
			serializeObject(table,table.getTablePath());
	}

	public void createIndex(String strTableName,
							String[] strarrColName) throws DBAppException {

		if (strarrColName.length != 3)
			throw new DBAppException("The number of columns must be 3");
		Table table = (Table) deserializeObject(getTablePath(strTableName));
		CheckIfValidIndex(table, strarrColName);
		ChangeMetaData(table,strarrColName);
		makeAnOctree(table,strarrColName);
	}
	public ArrayList<Tuple> selectFromTable(SQLTerm[] arrSQLTerms,
											String[] strarrOperators) throws DBAppException {
		if (arrSQLTerms.length != strarrOperators.length + 1  )
			throw new DBAppException("ENTER A VALID QUERY");
		String strTableName = arrSQLTerms[0]._strTableName;
		Table table = (Table) deserializeObject(getTablePath(strTableName));
		Vector<String> filenames = table.getFilePath();
		Hashtable<String, String> htblColNameType = table.getColNameType();
		ArrayList<String[]> strColNames;
		    //get the key value of the Table
		     strColNames =table.getIndexes();
			ArrayList<String> ColOnCondition = new ArrayList<>();
			Hashtable<String,String > ColNameOperator = new Hashtable<>();
			Hashtable<String,Object > ColNameValue = new Hashtable<>();
			for (int j = 0; j < arrSQLTerms.length; j++) {
				SQLTerm sqlTerm = arrSQLTerms[j];
				String colName = sqlTerm._strColumnName;
				String operator = sqlTerm._strOperator;
				Object value = sqlTerm._objValue;
				if (sqlTerm._strTableName.equals(strTableName) == false) {
					throw new DBAppException("Select from one table at a time");
				}
				if (htblColNameType.containsKey(colName) == false) {   // check that the user enter a valid column name
					throw new DBAppException("Column name not found");
				}
				if (sqlTerm._objValue.getClass().getName().equals("java.lang.Integer")) {  // check that the user enter a valid value
					if (htblColNameType.get(colName).equals("java.lang.Integer") == false) {
						throw new DBAppException("Please Enter " + sqlTerm._objValue.getClass().getName()+"value for "+colName);
					}
				} else if (sqlTerm._objValue.getClass().getName().equals("java.lang.Double")) {  // check that the user enter a valid value
					if (htblColNameType.get(colName).equals("java.lang.Double") == false) {
						throw new DBAppException("Please Enter " + sqlTerm._objValue.getClass().getName()+"value for "+colName);
					}
				} else if (sqlTerm._objValue.getClass().getName().equals("java.util.Date")) {  // check that the user enter a valid value
					if (htblColNameType.get(colName).equals("java.util.Date") == false) {
						throw new DBAppException("Please Enter " + sqlTerm._objValue.getClass().getName()+"value for "+colName);
					}
				} else if (sqlTerm._objValue.getClass().getName().equals("java.lang.String")) {   // check that the user enter a valid value
					if (htblColNameType.get(colName).equals("java.lang.String") == false) {
						throw new DBAppException("Please Enter " + sqlTerm._objValue.getClass().getName()+"value for "+colName);
					}
				} else {
					throw new DBAppException("Please Enter a  "+ htblColNameType.get(colName) +" "+colName);
				}
				ColNameOperator.put(colName,operator);
				ColNameValue.put(colName,value);
				ColOnCondition.add(colName);    // add The columns appear ON the query to the array list Just once
			}
			int Index = table.isINDEX(ColOnCondition);
			boolean flag = true;
			for (int i = 0; i < strarrOperators.length; i++)
				if (!strarrOperators[i].equalsIgnoreCase("and")) {
					flag = false;
					break;
				}
		ArrayList<Tuple> TargetTuples = new ArrayList<>();
		if (!flag||Index==-1) {
	    	for (int m = 0; m < filenames.size(); m++) {
			Page page =  (Page) deserializeObject(filenames.get(m));
			for (int i = 0; i < page.getCount(); i++) {
				Tuple tuble = page.getTuples().get(i);
				TargetTuples.add(tuble);
				String Operation = "";                                      // and or  // or and
				for (int k = 0; k < arrSQLTerms.length; k++) {// student name = eslam  and name = ahmed or name = eslamms
					if (isaTargetRecord(arrSQLTerms[k]._strOperator, CompareTo(tuble.getTuple().get(arrSQLTerms[k]._strColumnName),arrSQLTerms[k]._objValue)))
						Operation += "1";
					else
						Operation += "0";

					if (k < strarrOperators.length) {
						if (strarrOperators[k].equalsIgnoreCase("AND"))
							Operation += "*";
						else if (strarrOperators[k].equalsIgnoreCase("OR"))
							Operation += "+";
						else if (strarrOperators[k].equalsIgnoreCase("XOR"))
							Operation += "^";
						else
							throw new DBAppException("Enter a valid operator (AND,OR,XOR)");
					}
				}
				if (evaluateOperation(Operation) == 0)
					TargetTuples.remove(tuble);
			}
		}
			}
			else {
				String indexPath = table.getIndexPath(Index);
				Octree octree= (Octree) deserializeObject(indexPath);
				String[] Operator=new String[3];
				for(int i=0;i<3;i++) {
					Operator[i] = ColNameOperator.get(strColNames.get(Index)[i]);
				}
   			    ArrayList<String> values;
				values=octree.getRange(ColNameValue.get(strColNames.get(Index)[0]),ColNameValue.get(strColNames.get(Index)[1]),ColNameValue.get(strColNames.get(Index)[2]),Operator);

			for(int i =0;i<values.size();i++) {

					Page page = (Page) deserializeObject(getPagePathFromTree(values.get(i)));
					int TargetRow = page.FindRow(getKeyFromTree(table,values.get(i)),table.getKey(), false);  //Find the row
					TargetTuples.add(page.getTuples().get(Math.abs(TargetRow)));
						serializeObject(page,page.getPath());
				        serializeObject(octree,indexPath);

				}
				for(int i=0;i<TargetTuples.size();i++)
				{
				for(String Key1:ColNameOperator.keySet()) {
					if(!isaTargetRecord(ColNameOperator.get(Key1),CompareTo(TargetTuples.get(i).getTuple().get(Key1),ColNameValue.get(Key1)))) {
						TargetTuples.remove(i);
						i--;
						break;
							}
				}
				}
			}
		return TargetTuples;
	}
	public void Print(String StrTablename) throws DBAppException {
		String tableDirectory = getTablePath(StrTablename);
		Table table;
		table = (Table) deserializeObject(tableDirectory);
		Vector<String> filepaths = table.getFilePath();
		for (String pagePath : filepaths) {
			Page page;
			page = (Page) deserializeObject(pagePath);
			for (Tuple tuble : page.getTuples()) {
				for (Map.Entry<String, Object> field : tuble.getTuple().entrySet()) {
					System.out.print(field.getKey() + ": " + field.getValue() + " ");
				}
				System.out.println();
			}
		}
	}
	public static double evaluateOperation(String expression) {
		Stack<Integer > stack = new Stack<>();
		int  currentNumber = 0;
		char currentOperator = '+';

		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (Character.isDigit(c)) {
				currentNumber = currentNumber * 10 + (c - '0');
			} else if (c == '^') {
				if (currentOperator == '+') {
					stack.push(currentNumber);
				} else if (currentOperator == '*') {
					stack.push(stack.pop() * currentNumber);
				}
				currentNumber = 0;
				currentOperator = c;
			} else if (c == '*' || c == '+') {
				if (currentOperator == '+') {
					stack.push(currentNumber);
				} else if (currentOperator == '*') {
					stack.push(stack.pop() * currentNumber);
				} else if (currentOperator == '^') {
					stack.push((stack.pop()) ^ currentNumber);
				}
				currentNumber = 0;
				currentOperator = c;
			}
		}
		if (currentOperator == '+') {
			stack.push(currentNumber);
		} else if (currentOperator == '*') {
			stack.push(stack.pop() * currentNumber);
		} else if (currentOperator == '^') {
			stack.push(stack.pop() ^ currentNumber);
		}

		int  result = 0;
		while (!stack.isEmpty()) {
			result += stack.pop();
		}
		return result;
	}

    public boolean isaTargetRecord(String Operator, int CompareValue)  {
		if (Operator.equals("=")&&CompareValue!=0) {
			return false;
		}
		else if (Operator.equals("!=")&&CompareValue==0) {
			return false;
		}
		else if (Operator.equals(">")&&CompareValue<=0) {
			return false;
		}
		else if (Operator.equals(">=")&&CompareValue<0) {
			return false;
		}
		else if (Operator.equals("<")&&CompareValue>=0) {
			return false;
		}
		else if (Operator.equals("<=")&&CompareValue>0) {
			return false;
		}
		return true;
	}
}


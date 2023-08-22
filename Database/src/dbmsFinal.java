import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
	public ArrayList<Tuple> selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws DBAppException {
    // Check the input array lengths
    if (arrSQLTerms.length != strarrOperators.length + 1) {
        throw new DBAppException("ENTER A VALID QUERY");
    }

    String strTableName = arrSQLTerms[0]._strTableName;
    Table table = deserializeTable(strTableName);
    ArrayList<String[]> strColNames = table.getIndexes();

    ArrayList<String> ColOnCondition = new ArrayList<>();
    Hashtable<String, String> ColNameOperator = new Hashtable<>();
    Hashtable<String, Object> ColNameValue = new Hashtable<>();

    // Extract conditions and validate them
    extractAndValidateConditions(arrSQLTerms, table, ColOnCondition, ColNameOperator, ColNameValue);

    // Check if index exists for the selected columns
    int Index = table.isINDEX(ColOnCondition);
    boolean useIndex = shouldUseIndex(strarrOperators, Index);

    ArrayList<Tuple> TargetTuples = new ArrayList<>();

    if (!useIndex) {
        // Perform a linear scan through data files
        TargetTuples = linearScan(table, ColOnCondition, strarrOperators);
    } else {
        // Use the index for a more efficient query
        TargetTuples = useIndex(table, ColOnCondition, ColNameOperator, ColNameValue, Index);
    }

    return TargetTuples;
}

private void extractAndValidateConditions(SQLTerm[] arrSQLTerms, Table table,
        ArrayList<String> ColOnCondition, Hashtable<String, String> ColNameOperator,
        Hashtable<String, Object> ColNameValue) throws DBAppException {
    for (SQLTerm sqlTerm : arrSQLTerms) {
        String colName = sqlTerm._strColumnName;
        String operator = sqlTerm._strOperator;
        Object value = sqlTerm._objValue;

        // Validate the column name
        if (!table.getColNameType().containsKey(colName)) {
            throw new DBAppException("Column name not found: " + colName);
        }

        // Validate the value type
        validateValueType(colName, value, table.getColNameType());

        ColNameOperator.put(colName, operator);
        ColNameValue.put(colName, value);
        ColOnCondition.add(colName);
    }
}

private void validateValueType(String colName, Object value, Hashtable<String, String> colNameType)
        throws DBAppException {
    String expectedType = colNameType.get(colName);
    String valueType = value.getClass().getName();

    if (!expectedType.equals(valueType)) {
        throw new DBAppException("Please Enter " + expectedType + " value for " + colName);
    }
}

private boolean shouldUseIndex(String[] strarrOperators, int index) {
    boolean allAndOperators = true;
    for (String operator : strarrOperators) {
        if (!operator.equalsIgnoreCase("and")) {
            allAndOperators = false;
            break;
        }
    }
    return !allAndOperators || index == -1;
}

private ArrayList<Tuple> linearScan(Table table, ArrayList<String> ColOnCondition, String[] strarrOperators) throws DBAppException {
    ArrayList<Tuple> TargetTuples = new ArrayList<>();
    Vector<String> filenames = table.getFilePath();

    for (String filename : filenames) {
        Page page = deserializePage(filename);

        for (int i = 0; i < page.getCount(); i++) {
            Tuple tuple = page.getTuples().get(i);

            if (evaluateConditions(ColOnCondition, strarrOperators, tuple)) {
                TargetTuples.add(tuple);
            }
        }
    }

    return TargetTuples;
}

private ArrayList<Tuple> useIndex(Table table, ArrayList<String> ColOnCondition,
        Hashtable<String, String> ColNameOperator, Hashtable<String, Object> ColNameValue, int index) throws DBAppException {
    String indexPath = table.getIndexPath(index);
    Octree octree = deserializeOctree(indexPath);

    String[] operators = new String[3];
    for (int i = 0; i < 3; i++) {
        operators[i] = ColNameOperator.get(table.getIndexColumnName(index, i));
    }

    ArrayList<String> values = octree.getRange(
            ColNameValue.get(table.getIndexColumnName(index, 0)),
            ColNameValue.get(table.getIndexColumnName(index, 1)),
            ColNameValue.get(table.getIndexColumnName(index, 2)),
            operators);

    ArrayList<Tuple> TargetTuples = new ArrayList<>();

    for (String value : values) {
        String pagePath = getPagePathFromTree(value);
        Page page = deserializePage(pagePath);
        int targetRow = page.FindRow(getKeyFromTree(table, value), table.getKey(), false);

        if (targetRow >= 0 && evaluateConditions(ColOnCondition, ColNameOperator, ColNameValue, page.getTuples().get(Math.abs(targetRow)))) {
            TargetTuples.add(page.getTuples().get(Math.abs(targetRow)));
            serializeObject(page, pagePath);
            serializeObject(octree, indexPath);
        }
    }

    return TargetTuples;
}

private boolean evaluateConditions(ArrayList<String> ColOnCondition, String[] strarrOperators, Tuple tuple) {
    String operation = "";
    for (int k = 0; k < ColOnCondition.size(); k++) {
        String colName = ColOnCondition.get(k);
        Object value = tuple.getTuple().get(colName);
        String operator = ColNameOperator.get(colName);

        if (isaTargetRecord(operator, CompareTo(value, ColNameValue.get(colName)))) {
            operation += "1";
        } else {
            operation += "0";
        }

        if (k < strarrOperators.length) {
            String strOperator = strarrOperators[k];
            if (strOperator.equalsIgnoreCase("AND")) {
                operation += "*";
            } else if (strOperator.equalsIgnoreCase("OR")) {
                operation += "+";
            } else if (strOperator.equalsIgnoreCase("XOR")) {
                operation += "^";
            }
        }
    }

    return evaluateOperation(operation) == 1;
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


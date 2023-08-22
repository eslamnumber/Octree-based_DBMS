import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;


public class Tests  {

//    public static void main(String[] args) throws Exception {
//        DBApp db = new DBApp();
//        db.init();
//        //createStudentTable(db);
////        insertCoursesRecords(db,200);
////        insertPCsRecords(db,200);
//        //insertStudentRecords(db,200);
////        insertTranscriptsRecords(db,200);
//        //db.createIndex("courses",new String[]{"course_id","date_added","hours"});
//       // db.createIndex("students",new String[]{"gpa","id","first_name"} );
////        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
////        htblColNameValue.put("id", new String("43-123456"));
////        htblColNameValue.put("gpa",2.2);
////        htblColNameValue.put("first_name", new String("eslammms"));
////        htblColNameValue.put("last_name",new String("eslam"));
////        htblColNameValue.put("dob",new Date(1999-1900, 8-1, 4));
////
////db.updateTable("students","43-123456",htblColNameValue);
//        //db.insertIntoTable("students",htblColNameValue);
//       // db.deleteFromTable("students", htblColNameValue);
////        htblColNameValue.put("name", "eslam");
//////        htblColNameValue.put("age", 33);
//
//        db.Print("students");
//
////
//
//	        SQLTerm[] arrSQLTerms;
//	        arrSQLTerms = new SQLTerm[3];
//	        arrSQLTerms[0] = new SQLTerm();
//	        arrSQLTerms[0]._strTableName = "students";
//	        arrSQLTerms[0]._strColumnName= "id";
//	        arrSQLTerms[0]._strOperator = "!=";
//	        arrSQLTerms[0]._objValue ="43-123456";
//
//	        arrSQLTerms[1] = new SQLTerm();
//	        arrSQLTerms[1]._strTableName = "students";
//	        arrSQLTerms[1]._strColumnName= "gpa";
//	        arrSQLTerms[1]._strOperator = ">";
//	        arrSQLTerms[1]._objValue = 2.2;
//
//        arrSQLTerms[2] = new SQLTerm();
//        arrSQLTerms[2]._strTableName = "students";
//        arrSQLTerms[2]._strColumnName= "first_name";
//        arrSQLTerms[2]._strOperator = "=";
//        arrSQLTerms[2]._objValue = "Mahy";
//	        String[]strarrOperators = new String[2];
//	        strarrOperators[0] = "OR";
//        strarrOperators[1] = "AND";
//        ArrayList<Tuple> resultSet=null;
//        resultSet = db.selectFromTable(arrSQLTerms, strarrOperators);
//        for (int i = 0; i < resultSet.size(); i++) {
//            System.out.print(resultSet.get(i).getTuple().get("id") + " " + resultSet.get(i).getTuple().get("first_name") + " " + resultSet.get(i).getTuple().get("gba") + resultSet.get(i).getTuple().get("dob") + " " + resultSet.get(i).getTuple().get("last_name"));
//            System.out.println();
//        }
//////           }
//
//
//
////	      String table = "students";
////	        row.put("first_name", "fooooo");
////	        row.put("last_name", "baaaar");
////	        Date dob = new Date(1992 - 1900, 9 - 1, 8);
////	        row.put("dob", dob);
////	        row.put("gpa", 1.1);
////	        dbApp.updateTable(table, clusteringKey, row);
////        createCoursesTable(db);
////        createPCsTable(db);
////        createTranscriptsTable(db);
////        createStudentTable(db);
////        insertPCsRecords(db,200);
////        insertTranscriptsRecords(db,200);
////        insertStudentRecords(db,200);
////        insertCoursesRecords(db,200);
//
//
//    }


        public static void main(String[] args) throws DBAppException, IOException, ClassNotFoundException, ParseException, IOException {
       DBApp dpApp= new DBApp();
        dpApp.init();
        Hashtable<String, String> htblColNameType = new Hashtable<>();
        Hashtable<String, String> htblColNameMin = new Hashtable<>();
        Hashtable<String, String> htblColNameMax = new Hashtable<>();
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put("id",8);
        htblColNameValue.put("name", "eslammw");
        htblColNameValue.put("age", 33);
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("age", "java.lang.Integer");
        htblColNameMin.put("id", "0");
        htblColNameMax.put("id", "1000");
       htblColNameMin.put("name", "a");
        htblColNameMax.put("name", "zzzzzzzzzzzz");
        htblColNameMin.put("age", "18");
        htblColNameMax.put("age", "40");
        String strClusteringKeyColumn = "id";
        SQLTerm[] arrSQLTerms=new SQLTerm[3];
        arrSQLTerms[0]=new SQLTerm();
       arrSQLTerms[0]._strTableName = "students";
        arrSQLTerms[0]._strColumnName = "name";
        arrSQLTerms[0]._strOperator = "=";
        arrSQLTerms[0]._objValue = "eslam";
        arrSQLTerms[1]=new SQLTerm();
        arrSQLTerms[1]._strTableName = "students";
        arrSQLTerms[1]._strColumnName = "age";
        arrSQLTerms[1]._strOperator = "=";
        arrSQLTerms[1]._objValue = 33;
        arrSQLTerms[2]=new SQLTerm();
        arrSQLTerms[2]._strTableName = "students";
        arrSQLTerms[2]._strColumnName = "id";
        arrSQLTerms[2]._strOperator = "<";
        arrSQLTerms[2]._objValue = 5;


        String[] strarrOperators = new String[2];
        strarrOperators[0] = "AND";
        strarrOperators[1] = "AND";
// select * from Student where name = “John Noor” or gpa = 1.5;
         ArrayList<Tuple> resultSet = new ArrayList<>();
        try {
           //dpApp.createTable("students", strClusteringKeyColumn, htblColNameType, htblColNameMin, htblColNameMax);
           //   dpApp.createIndex("students", new String[]{"name", "age", "id"});
          //resultSet = dpApp.selectFromTable(arrSQLTerms, strarrOperators);
       dpApp.insertIntoTable("students", htblColNameValue);
            //dpApp.deleteFromTable("students",htblColNameValue);
            //dpApp.updateTable("students","5",htblColNameValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dpApp.Print("students");



        //System.out.println(x.getClass().getName());


    }
//String strTableName,
// String strClusteringKeyColumn,
// Hashtable<String,String> htblColNameType,
// Hashtable<String,String> htblColNameMin,
// Hashtable<String,String> htblColNameMax
/*
"java.lang.Integer"
"java.lang.String"
"java.lang.Double"
"java.util.Date"
(Note: date acceptable format is "YYYY-MM-DD")*/
//public void insertIntoTable(String strTableName,
//								Hashtable<String,Object> htblColNameValue)
private static void  insertCoursesRecords(DBApp dbApp, int limit) throws Exception {
    BufferedReader coursesTable = new BufferedReader(new FileReader("C:\\Users\\ESLAM GAMAL\\Desktop\\Database\\src\\courses_table.csv"));
    String record;
    Hashtable<String, Object> row = new Hashtable<>();
    int c = limit;
    if (limit == -1) {
        c = 1;
    }
    while ((record = coursesTable.readLine()) != null && c > 0) {
        String[] fields = record.split(",");


        int year = Integer.parseInt(fields[0].trim().substring(0, 4));
        int month = Integer.parseInt(fields[0].trim().substring(5, 7));
        int day = Integer.parseInt(fields[0].trim().substring(8));

        Date dateAdded = new Date(year - 1900, month - 1, day);

        row.put("date_added", dateAdded);

        row.put("course_id", fields[1]);
        row.put("course_name", fields[2]);
        row.put("hours", Integer.parseInt(fields[3]));

        dbApp.insertIntoTable("courses", row);
        row.clear();

        if (limit != -1) {
            c--;
        }
    }

    coursesTable.close();
}

    private static void  insertStudentRecords(DBApp dbApp, int limit) throws Exception {
        BufferedReader studentsTable = new BufferedReader(new FileReader("C:\\Users\\ESLAM GAMAL\\Desktop\\Database\\src\\students_table.csv"));
        String record;
        int c = limit;
        if (limit == -1) {
            c = 1;
        }

        Hashtable<String, Object> row = new Hashtable<>();
        while ((record = studentsTable.readLine()) != null && c > 0) {
            String[] fields = record.split(",");

            row.put("id", fields[0]);
            row.put("first_name", fields[1]);
            row.put("last_name", fields[2]);

            int year = Integer.parseInt(fields[3].trim().substring(0, 4));
            int month = Integer.parseInt(fields[3].trim().substring(5, 7));
            int day = Integer.parseInt(fields[3].trim().substring(8));

            Date dob = new Date(year - 1900, month - 1, day);
            row.put("dob", dob);

            double gpa = Double.parseDouble(fields[4].trim());

            row.put("gpa", gpa);

            dbApp.insertIntoTable("students", row);
            row.clear();
            if (limit != -1) {
                c--;
            }
        }
        studentsTable.close();
    }
    private static void insertTranscriptsRecords(DBApp dbApp, int limit) throws Exception {
        BufferedReader transcriptsTable = new BufferedReader(new FileReader("C:\\Users\\ESLAM GAMAL\\Desktop\\Database\\src\\transcripts_table.csv"));
        String record;
        Hashtable<String, Object> row = new Hashtable<>();
        int c = limit;
        if (limit == -1) {
            c = 1;
        }
        while ((record = transcriptsTable.readLine()) != null && c > 0) {
            String[] fields = record.split(",");

            row.put("gpa", Double.parseDouble(fields[0].trim()));
            row.put("student_id", fields[1].trim());
            row.put("course_name", fields[2].trim());

            String date = fields[3].trim();
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8));

            Date dateUsed = new Date(year - 1900, month - 1, day);
            row.put("date_passed", dateUsed);

            dbApp.insertIntoTable("transcripts", row);
            row.clear();

            if (limit != -1) {
                c--;
            }
        }

        transcriptsTable.close();
    }
    private static void insertPCsRecords(DBApp dbApp, int limit) throws Exception {
        BufferedReader pcsTable = new BufferedReader(new FileReader("C:\\Users\\ESLAM GAMAL\\Desktop\\Database\\src\\pcs_table.csv"));
        String record;
        Hashtable<String, Object> row = new Hashtable<>();
        int c = limit;
        if (limit == -1) {
            c = 1;
        }
        while ((record = pcsTable.readLine()) != null && c > 0) {
            String[] fields = record.split(",");

            row.put("pc_id", Integer.parseInt(fields[0].trim()));
            row.put("student_id", fields[1].trim());

            dbApp.insertIntoTable("pcs", row);
            row.clear();

            if (limit != -1) {
                c--;
            }
        }

        pcsTable.close();
    }
    private static void createTranscriptsTable(DBApp dbApp) throws Exception {
        // Double CK
        String tableName = "transcripts";

        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("gpa", "java.lang.Double");
        htblColNameType.put("student_id", "java.lang.String");
        htblColNameType.put("course_name", "java.lang.String");
        htblColNameType.put("date_passed", "java.util.Date");

        Hashtable<String, String> minValues = new Hashtable<>();
        minValues.put("gpa", "0.7");
        minValues.put("student_id", "43-0000");
        minValues.put("course_name", "AAAAAA");
        minValues.put("date_passed", "1990-01-01");

        Hashtable<String, String> maxValues = new Hashtable<>();
        maxValues.put("gpa", "5.0");
        maxValues.put("student_id", "99-9999");
        maxValues.put("course_name", "zzzzzz");
        maxValues.put("date_passed", "2020-12-31");

        dbApp.createTable(tableName, "gpa", htblColNameType, minValues, maxValues);
    }

    private static void createStudentTable(DBApp dbApp) throws Exception {
        // String CK
        String tableName = "students";

        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.String");
        htblColNameType.put("first_name", "java.lang.String");
        htblColNameType.put("last_name", "java.lang.String");
        htblColNameType.put("dob", "java.util.Date");
        htblColNameType.put("gpa", "java.lang.Double");

        Hashtable<String, String> minValues = new Hashtable<>();
        minValues.put("id", "43-0000");
        minValues.put("first_name", "AAAAAA");
        minValues.put("last_name", "AAAAAA");
        minValues.put("dob", "1990-01-01");
        minValues.put("gpa", "0.7");

        Hashtable<String, String> maxValues = new Hashtable<>();
        maxValues.put("id", "99-9999");
        maxValues.put("first_name", "zzzzzz");
        maxValues.put("last_name", "zzzzzz");
        maxValues.put("dob", "2000-12-31");
        maxValues.put("gpa", "5.0");

        dbApp.createTable(tableName, "id", htblColNameType, minValues, maxValues);
    }
    private static void createPCsTable(DBApp dbApp) throws Exception {
        // Integer CK
        String tableName = "pcs";

        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("pc_id", "java.lang.Integer");
        htblColNameType.put("student_id", "java.lang.String");


        Hashtable<String, String> minValues = new Hashtable<>();
        minValues.put("pc_id", "0");
        minValues.put("student_id", "43-0000");

        Hashtable<String, String> maxValues = new Hashtable<>();
        maxValues.put("pc_id", "20000");
        maxValues.put("student_id", "99-9999");

        dbApp.createTable(tableName, "pc_id", htblColNameType, minValues, maxValues);
    }
    private static void createCoursesTable(DBApp dbApp) throws Exception {
        // Date CK
        String tableName = "courses";

        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("date_added", "java.util.Date");
        htblColNameType.put("course_id", "java.lang.String");
        htblColNameType.put("course_name", "java.lang.String");
        htblColNameType.put("hours", "java.lang.Integer");


        Hashtable<String, String> minValues = new Hashtable<>();
        minValues.put("date_added", "1901-01-01");
        minValues.put("course_id", "0000");
        minValues.put("course_name", "AAAAAA");
        minValues.put("hours", "1");

        Hashtable<String, String> maxValues = new Hashtable<>();
        maxValues.put("date_added", "2020-12-31");
        maxValues.put("course_id", "9999");
        maxValues.put("course_name", "zzzzzz");
        maxValues.put("hours", "24");

        dbApp.createTable(tableName, "date_added", htblColNameType, minValues, maxValues);
    }}


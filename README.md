🗄️ Mini Database Engine with Octree Indexing

This project is a miniature Java-based database engine developed for the Database II course (CSEN604) at the German University in Cairo (GUC).

📚 Overview  
The engine supports basic database functionalities, including:  
- Creating tables and inserting records  
- Updating and deleting data  
- Searching with and without index support  
- Meta-data management  
- Page-based storage  
- Octree indexing for multi-dimensional data  

This implementation is file-based (not in-memory), with a focus on learning how databases handle indexing, storage, and query optimization.

🛠️ Technologies Used  
- Java  
- Java Serialization  
- Octree Data Structure  
- CSV for Metadata  
- File I/O and Dynamic Class Loading  

📦 Features  
- Page-based table storage (each page is a serialized `.class` file)  
- Deferred page loading for memory efficiency  
- Metadata managed in `metadata.csv`  
- Clustering key and sorted data  
- Support for `Integer`, `String`, `Double`, and `Date` column types  
- Octree indexing on 3 columns for fast multidimensional search  
- Select queries with AND, OR, and XOR operators  
- Configuration through `DBApp.config` file  

🔍 Core Methods in DBApp.java  
- `createTable(...)`  
- `createIndex(...)`  
- `insertIntoTable(...)`  
- `updateTable(...)`  
- `deleteFromTable(...)`  
- `selectFromTable(...)`  
- `init()`  

🧠 How Octree Indexing Works  
- Indexes are created on exactly three columns.  
- Octree nodes store references to disk-based page locations.  
- Queries benefit from Octree when the indexed columns are used in the selection.  

📁 Example Configuration  
`DBApp.config`:  
MaximumRowsCountinTablePage = 200
MaximumEntriesinOctreeNode = 16

📊 Example Metadata Entry  

🧪 Example Usage  
```java
DBApp dbApp = new DBApp();  
dbApp.createTable("Student", "id", htblColNameType, htblMin, htblMax);  
dbApp.insertIntoTable("Student", htblColNameValue);  
dbApp.createIndex("Student", new String[] {"gpa", "age", "score"});  

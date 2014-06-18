//package root;


/**
 * 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * @author sec
 *
 */
public class adwords {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner advertisersPtr = null;
		Scanner keywordsPtr = null;
		Scanner sysinPtr = null;

		String value0 = null;
		String value1 = null;
		String username = null;
		String password = null;
		int t1num = 0;
		int t2num = 0;
		int t3num = 0;
		int t4num = 0;
		int t5num = 0;
		int t6num = 0;
		int flag = 0;
		String resp = null;
		String greedy_1_query = "SELECT QID, RANK, ADVERTISERID, BALANCE, BUDGET FROM GREEDY_FIRST ORDER BY QID ASC, RANK ASC";
		String greedy_2_query = "SELECT QID, RANK, ADVERTISERID, BALANCE, BUDGET FROM GREEDY_SECOND ORDER BY QID ASC, RANK ASC";
		String balance_1_query = "SELECT QID, RANK, ADVERTISERID, BALANCE, BUDGET FROM BALANCE_FIRST ORDER BY QID ASC, RANK ASC";
		String balance_2_query = "SELECT QID, RANK, ADVERTISERID, BALANCE, BUDGET FROM BALANCE_SECOND ORDER BY QID ASC, RANK ASC";
		String generalized_1_query = "SELECT QID, RANK, ADVERTISERID, BALANCE, BUDGET FROM GENERALIZED_FIRST ORDER BY QID ASC, RANK ASC";
		String generalized_2_query = "SELECT QID, RANK, ADVERTISERID, BALANCE, BUDGET FROM GENERALIZED_SECOND ORDER BY QID ASC, RANK ASC";

		Connection con = null;
		StringTokenizer st = null;

		try {
			sysinPtr=new Scanner(new FileReader("system.in"));	
			while (sysinPtr.hasNextLine() && sysinPtr.hasNext() != false ){
				String line = sysinPtr.nextLine();
				if (username == null || password == null){
					st = new StringTokenizer(line," = ");
					while (st.hasMoreTokens() && flag == 0) {
						value0 = st.nextToken();
						value1 = st.nextToken();
						if (value0.equals("username")){
							username = value1;
						}else if (value0.equals("password")){
							password = value1;
							flag = 1;
						}
					}

				}else{		
					st = new StringTokenizer(line," num_ads = ");
					while (st.hasMoreTokens()) {
						value0 = st.nextToken();
						value1 = st.nextToken();
						if (value0.equals("TASK1:")){
							t1num = Integer.parseInt(value1);
						}else if (value0.equals("TASK2:")){
							t2num = Integer.parseInt(value1);
						}else if (value0.equals("TASK3:")){
							t3num = Integer.parseInt(value1);
						}else if (value0.equals("TASK4:")){
							t4num = Integer.parseInt(value1);
						}else if (value0.equals("TASK5:")){
							t5num = Integer.parseInt(value1);
						}else if (value0.equals("TASK6:")){
							t6num = Integer.parseInt(value1);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File Read Error");
			e.printStackTrace();
		}
		System.out.println("File read successfully");
		sysinPtr.close();
		createFile();
		try {

			con = DriverManager.getConnection("jdbc:oracle:thin:"+username+"/"+password+"@oracle.cise.ufl.edu:1521:orcl", username, password);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (con != null) {
			try{

				File advertisers_edited = new File("Advertisers_edited.dat");
				advertisers_edited.createNewFile();
				BufferedWriter advertisers_edited_bw = new BufferedWriter(new FileWriter(advertisers_edited, true));
				try {
					advertisersPtr=new Scanner(new FileReader("Advertisers.dat"));
				} catch (FileNotFoundException e) {
					System.out.println("File Read Error");
					e.printStackTrace();
				}
				insertFile(advertisersPtr,"Advertisers",advertisers_edited_bw);
				advertisersPtr.close();
				advertisers_edited_bw.close();

				File TEMP = new File("temp1.sql");
				TEMP.createNewFile();
				BufferedWriter TEMP_bw = new BufferedWriter(new FileWriter(TEMP, true));

				try {
					keywordsPtr=new Scanner(new FileReader("Keywords.dat"));
				} catch (FileNotFoundException e) {
					System.out.println("File Read Error");
					e.printStackTrace();
				}
				System.out.println("File read successfully");
				insertFile(keywordsPtr,"TEMP",TEMP_bw);
				TEMP_bw.close();
				keywordsPtr.close();

				File insert = new File("insert.sql");
				insert.createNewFile();
				BufferedWriter insert_bw = new BufferedWriter(new FileWriter(insert, true));
				insert_bw.write("INSERT INTO TEMP2 (SELECT DISTINCT WORDS FROM TEMP);");
				insert_bw.newLine();
				insert_bw.write("DROP TABLE TEMP;");
				insert_bw.newLine();
				insert_bw.write("INSERT INTO WORDS_DICTIONARY (SELECT WORDS, INDEX_SEQ.NEXTVAL FROM TEMP2);");
				insert_bw.newLine();
				insert_bw.write("DROP TABLE TEMP2;");
				insert_bw.newLine();
				insert_bw.write("INSERT INTO ADVERTISERS_INDEX(SELECT ADVERTISERID,INDEX_NUMBER, 1 "
						+ "FROM KEYWORDS, WORDS_DICTIONARY "
						+ "WHERE WORDS = KEYWORD "
						+ "GROUP BY ADVERTISERID, INDEX_NUMBER);");
				insert_bw.newLine();
				insert_bw.write("CREATE UNIQUE INDEX KEYWORDS_ADID ON KEYWORDS (KEYWORD, ADVERTISERID)"
						+ " PCTFREE 10 INITRANS 2 MAXTRANS 255 NOLOGGING COMPUTE STATISTICS "
						+ " STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645"
						+ " PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) TABLESPACE CISETS;");
				insert_bw.newLine();
				insert_bw.write("CREATE UNIQUE INDEX QID_QUERY ON QUERIES (QID, QUERY) "
						+ " PCTFREE 10 INITRANS 2 MAXTRANS 255 NOLOGGING COMPUTE STATISTICS "
						+ " STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645"
						+ " PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) TABLESPACE CISETS ;");
				insert_bw.newLine();
				insert_bw.write("  CREATE BITMAP INDEX WORDS_INDEX_NUM ON WORDS_DICTIONARY (WORDS, INDEX_NUMBER) "
						+ " PCTFREE 10 INITRANS 2 MAXTRANS 255 NOLOGGING COMPUTE STATISTICS "
						+ " STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 "
						+ " PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)"
						+ " TABLESPACE CISETS ;");
				insert_bw.newLine();
				insert_bw.write("COMMIT;");
				insert_bw.newLine();
				insert_bw.write("EXIT;");
				insert_bw.close();


			}catch(IOException e){
				e.printStackTrace();
			}

			Process p;
			try {
				p = Runtime.getRuntime().exec("sqlplus "+username+"@orcl/"+password+" @create.sql");
				p.waitFor();
				System.out.println("Ran create.sql");
				p = Runtime.getRuntime().exec("sqlldr "+username+"@orcl/"+password+" control = Queries.ctl log = Queries.log");
				p.waitFor();
				System.out.println("Queries.ctl");
				p = Runtime.getRuntime().exec("sqlldr "+username+"@orcl/"+password+" control = Advertisers.ctl log = Advertisers.log");
				p.waitFor();
				System.out.println("advertisers.ctl");
				p = Runtime.getRuntime().exec("sqlldr "+username+"@orcl/"+password+" control = Keywords.ctl log = Keywords.log");
				p.waitFor();
				System.out.println("keywords.ctl");
				p = Runtime.getRuntime().exec("sqlplus "+username+"@orcl/"+password+" @temp1.sql");
				p.waitFor();
				System.out.println("temp.sql");
				p = Runtime.getRuntime().exec("sqlplus "+username+"@orcl/"+password+" @insert.sql");
				p.waitFor();
				System.out.println("insert.sql");
				p = Runtime.getRuntime().exec("sqlplus "+username+"@orcl/"+password+" @adwords.sql > s.txt");
				p.waitFor();
				System.out.println("adwords.sql");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


			try {
				CallableStatement cs = con.prepareCall("{? = call read_query_table(?,?,?,?,?,?)}");
				cs.registerOutParameter(1, Types.VARCHAR);
				cs.setInt(2,t1num);
				cs.setInt(3,t2num);
				cs.setInt(4,t3num);
				cs.setInt(5,t4num);
				cs.setInt(6,t5num);
				cs.setInt(7,t6num);
				cs.executeQuery();
				resp = cs.getString(1);

			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(resp.equals("SUCCESS")){
					Statement stat = con.createStatement();		
					ResultSet rg1 = stat.executeQuery(greedy_1_query);
					File file1 = new File("system.out.1");
					file1.createNewFile();
					BufferedWriter bw1 = new BufferedWriter(new FileWriter(file1, false));
					while(rg1.next()){
						bw1.write(rg1.getInt(1) +", "+ rg1.getInt(2)+", "+ rg1.getInt(3)+", "+ rg1.getFloat(4)+", "+ rg1.getFloat(5));
						bw1.newLine();
					}
					bw1.close();

					File file2 = new File("system.out.3");
					file2.createNewFile();
					ResultSet rb1 = stat.executeQuery(balance_1_query);
					BufferedWriter bw2 = new BufferedWriter(new FileWriter(file2, false));
					while(rb1.next()){
						bw2.write(rb1.getInt(1) +", "+ rb1.getInt(2)+", "+ rb1.getInt(3)+", "+ rb1.getFloat(4)+", "+ rb1.getFloat(5));
						bw2.newLine();
					}
					bw2.close();

					File file3 = new File("system.out.5");
					file3.createNewFile();
					ResultSet rgen1 = stat.executeQuery(generalized_1_query);
					BufferedWriter bw3 = new BufferedWriter(new FileWriter(file3, false));
					while(rgen1.next()){
						bw3.write(rgen1.getInt(1) +", "+ rgen1.getInt(2)+", "+ rgen1.getInt(3)+", "+ rgen1.getFloat(4)+", "+ rgen1.getFloat(5));
						bw3.newLine();
					}
					bw3.close();

					File file4 = new File("system.out.2");
					file4.createNewFile();
					ResultSet rg2 = stat.executeQuery(greedy_2_query);
					BufferedWriter bw4 = new BufferedWriter(new FileWriter(file4, false));
					while(rg2.next()){
						bw4.write(rg2.getInt(1) +", "+ rg2.getInt(2)+", "+ rg2.getInt(3)+", "+ rg2.getFloat(4)+", "+ rg2.getFloat(5));
						bw4.newLine();
					}
					bw4.close();

					File file5 = new File("system.out.4");
					file5.createNewFile();
					ResultSet rb2 = stat.executeQuery(balance_2_query);
					BufferedWriter bw5 = new BufferedWriter(new FileWriter(file5, false));
					while(rb2.next()){
						bw5.write(rb2.getInt(1) +", "+ rb2.getInt(2)+", "+ rb2.getInt(3)+", "+ rb2.getFloat(4)+", "+ rb2.getFloat(5));
						bw5.newLine();
					}
					bw5.close();

					File file6 = new File("system.out.6");
					if (!file6.exists()) {
						file6.createNewFile();
					}
					ResultSet rgen2 = stat.executeQuery(generalized_2_query);
					BufferedWriter bw6 = new BufferedWriter(new FileWriter(file6, false));
					while(rgen2.next()){
						bw6.write(rgen2.getInt(1) +", "+ rgen2.getInt(2)+", "+ rgen2.getInt(3)+", "+ rgen2.getFloat(4)+", "+ rgen2.getFloat(5));
						bw6.newLine();
					}
					bw6.close();	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}else {
			System.out.println("Failed to connect to Oracle DB");
		}
	}


	public static void createFile(){
		try{
			File create_file = new File("create.sql");
			BufferedWriter bw = new BufferedWriter(new FileWriter(create_file, true));
			create_file.createNewFile();
			bw.write("CREATE TABLE QUERIES(QID NUMBER PRIMARY KEY,"
					+ " QUERY VARCHAR2(400));");
			bw.newLine();
			bw.write("CREATE TABLE ADVERTISERS(ADVERTISERID INTEGER PRIMARY KEY,"
					+ " BUDGET NUMBER,"
					+ " CTC NUMBER, "
					+ " G_1_BALANCE NUMBER,"
					+ " G_2_BALANCE NUMBER,"
					+ " B_1_BALANCE NUMBER,"
					+ " B_2_BALANCE NUMBER,"
					+ " GEN_1_BALANCE NUMBER,"
					+ " GEN_2_BALANCE NUMBER,"
					+ " G_1_COUNT NUMBER,"
					+ " G_2_COUNT NUMBER,"
					+ " B_1_COUNT NUMBER,"
					+ " B_2_COUNT NUMBER,"
					+ " GEN_1_COUNT NUMBER,"
					+ " GEN_2_COUNT NUMBER);");
			bw.newLine();
			bw.write("CREATE TABLE KEYWORDS (ADVERTISERID NUMBER,"
					+ "	KEYWORD VARCHAR2(100),"
					+ " BID NUMBER, "
					+ " PRIMARY KEY (ADVERTISERID, KEYWORD),"
					+ " CONSTRAINT FK_ADVERTISERID_KEYWORDS FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID));");
			bw.newLine();
			bw.write("CREATE TABLE WORDS_DICTIONARY(WORDS VARCHAR2(200),"
					+ " INDEX_NUMBER NUMBER);");
			bw.newLine();
			bw.write("CREATE TABLE TEMP(WORDS VARCHAR2(200));");
			bw.newLine();
			bw.write("CREATE TABLE TEMP2(WORDS VARCHAR2(200));");
			bw.newLine();
			bw.write("CREATE TABLE GREEDY_FIRST "
					+ " (QID NUMBER,"
					+ " RANK NUMBER,"
					+ " ADVERTISERID NUMBER,"
					+ "	BALANCE NUMBER, "
					+ "	BUDGET NUMBER, "
					+ " CONSTRAINT FK_ADVERTISERID_G1 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID),"
					+ " CONSTRAINT FK_QID_G1 FOREIGN KEY (QID) REFERENCES QUERIES(QID));");
			bw.newLine();
			bw.write("CREATE TABLE GREEDY_SECOND "
					+ " (QID NUMBER,"
					+ " RANK NUMBER,"
					+ " ADVERTISERID NUMBER,"
					+ "	BALANCE NUMBER, "
					+ "	BUDGET NUMBER, "
					+ " CONSTRAINT FK_ADVERTISERID_G2 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID),"
					+ " CONSTRAINT FK_QID_G2 FOREIGN KEY (QID) REFERENCES QUERIES(QID));");
			bw.newLine();
			bw.write("CREATE TABLE BALANCE_FIRST "
					+ " (QID NUMBER,"
					+ " RANK NUMBER,"
					+ " ADVERTISERID NUMBER,"
					+ "	BALANCE NUMBER, "
					+ "	BUDGET NUMBER, "
					+ " CONSTRAINT FK_ADVERTISERID_B1 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID),"
					+ " CONSTRAINT FK_QID_B1 FOREIGN KEY (QID) REFERENCES QUERIES(QID));");
			bw.newLine();
			bw.write("CREATE TABLE BALANCE_SECOND "
					+ " (QID NUMBER,"
					+ " RANK NUMBER,"
					+ " ADVERTISERID NUMBER,"
					+ "	BALANCE NUMBER, "
					+ "	BUDGET NUMBER, "
					+ " CONSTRAINT FK_ADVERTISERID_B2 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID),"
					+ " CONSTRAINT FK_QID_B2 FOREIGN KEY (QID) REFERENCES QUERIES(QID));");
			bw.newLine();
			bw.write("CREATE TABLE GENERALIZED_FIRST "
					+ " (QID NUMBER,"
					+ " RANK NUMBER,"
					+ " ADVERTISERID NUMBER,"
					+ "	BALANCE NUMBER, "
					+ "	BUDGET NUMBER, "
					+ " CONSTRAINT FK_ADVERTISERID_GEN1 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID),"
					+ " CONSTRAINT FK_QID_GEN1 FOREIGN KEY (QID) REFERENCES QUERIES(QID));");
			bw.newLine();
			bw.write("CREATE TABLE GENERALIZED_SECOND "
					+ " (QID NUMBER,"
					+ " RANK NUMBER,"
					+ " ADVERTISERID NUMBER,"
					+ "	BALANCE NUMBER, "
					+ "	BUDGET NUMBER, "
					+ " CONSTRAINT FK_ADVERTISERID_GEN2 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID),"
					+ " CONSTRAINT FK_QID_GEN2 FOREIGN KEY (QID) REFERENCES QUERIES(QID));");
			bw.newLine();
			bw.write("CREATE TABLE ADVERTISERS_INDEX "
					+ " (ADVERTISERID NUMBER, "
					+ " INDEX_NUMBER NUMBER,  "
					+ " CNT NUMBER, "
					+ " PRIMARY KEY (ADVERTISERID, INDEX_NUMBER),"
					+ " CONSTRAINT FK_ADVERTISERID_AI FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID));");
			bw.newLine();
			bw.write("CREATE SEQUENCE  INDEX_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;");
			bw.newLine();
			bw.write("EXIT;");
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void insertFile(Scanner ptr, String fileName, BufferedWriter bw_insert){
		int counter = 0;
		String value0 = null;
		String value1 = null;
		String value2 = null;
		try{
			switch (fileName.toUpperCase()){
			case "TEMP" : 
				while (ptr.hasNextLine() && ptr.hasNext() != false ){
					value0 = ptr.next();
					value1 = ptr.next();
					value2 = ptr.next();
					bw_insert.write("INSERT INTO TEMP (WORDS) VALUES (Q'["+value1+"]');");		
					bw_insert.newLine();		
				}
				bw_insert.write("EXIT;");
				break;
			case "ADVERTISERS" :
				while (ptr.hasNextLine() && ptr.hasNext() != false ){
					value0 = ptr.next();
					value1 = ptr.next();
					value2 = ptr.next();
					bw_insert.write(value0+"\t"+value1+"\t"+value2+"\t"+value1+"\t"+value1+"\t"+value1+"\t"+value1+"\t"+value1+"\t"+value1+"	0	0	0	0	0	0	");
					bw_insert.newLine();
				}
				break;
			default: System.out.println("Please check the file.");			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}



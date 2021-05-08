package application;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class FetchData {


	private static void downloadUsingStream(String urlStr, String file) throws IOException{
		URL url = new URL(urlStr);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);
		byte[] buffer = new byte[1024];	
		int count=0;
		while((count = bis.read(buffer,0,1024)) != -1)
		{
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
	}

	public void UpdateTable(String s)
	{
		Alert a = new Alert(AlertType.NONE); 
		//Fetching data
		Calendar c1 = Calendar.getInstance(); 
		c1.set(c1.get(Calendar.YEAR),c1.get(Calendar.MONTH), c1.get(Calendar.DATE));
		Date dateOne = c1.getTime();      
		Instant inst = dateOne.toInstant(); 
		String p2  = Long.toString(inst.getEpochSecond());
		System.out.println("Period 2 :"+inst.getEpochSecond());
		c1.set(c1.get(Calendar.YEAR)-5,c1.get(Calendar.MONTH), c1.get(Calendar.DATE));
		dateOne = c1.getTime(); 
		inst = dateOne.toInstant(); 
		System.out.println("Period 1 :"+inst.getEpochSecond());
		String p1  = Long.toString(inst.getEpochSecond());
		String url1 = "https://query1.finance.yahoo.com/v7/finance/download/"+s+"?period1="+p1+"&period2="+p2+"&interval=1mo&events=history";
		//downloadUsingNIO(url, "C:\\Users\\admin\\Desktop\\notes12.csv");
		try {
			downloadUsingStream(url1,"F:\\LAKSHMI\\6th SEM\\Mini Project\\Training Data Set\\"+s+".csv");

			try
			{
				//Connection
				String url = "jdbc:mysql://localhost:3306/sample_database";
				String uname = "root";
				String passwd = "2000";
				String file = s.split("\\.")[0];
				file=file.replace('-','_');		
				System.out.println("FILE NAME : "+file);
				Connection con = DriverManager.getConnection( url,uname,passwd);  
				Statement st = con.createStatement();
				int flag=0;
				ResultSet rs = st.executeQuery("show tables");
				while(rs.next())
				{
					String name = rs.getString(1);
					if(name.equalsIgnoreCase(file))
						flag=1;
				}
				if(flag==0)
				{
					System.out.println("Creating tables");
					int batchSize = 20;
					String query = "Create table  "+ file + " (date date,Close_price Double)";
					st.executeUpdate(query);
					try {
						con.setAutoCommit(false);
						String sql = "INSERT INTO " + file + " (date ,Close_price) VALUES (?, ?)";
						PreparedStatement statement = con.prepareStatement(sql);

						BufferedReader lineReader = new BufferedReader(new FileReader("F:\\LAKSHMI\\6th SEM\\Mini Project\\Training Data Set\\"+ s +".csv"));
						System.out.println("executing");
						String lineText = null;

						int count = 0;

						lineReader.readLine(); // skip header line

						while ((lineText = lineReader.readLine()) != null)
						{
							String[] data = lineText.split(",");
							String date = data[0];

							String[] t = date.split("-"); 							

							if(!data[4].equalsIgnoreCase("null"))
							{
								if(Integer.valueOf(t[2]) <= 31 && Integer.valueOf(t[1]) <= 12)
								{

									LocalDate date1 = LocalDate.parse(date); 
									java.sql.Date sqlDate = java.sql.Date.valueOf( date1 );
									statement.setDate(1, sqlDate);
								}
								else
								{
									System.out.println("Not matching");
									date = t[2] + "-"+t[1]+"-"+t[0];  
									LocalDate date1 = LocalDate.parse(date);
									java.sql.Date sqlDate = java.sql.Date.valueOf( date1 );
									statement.setDate(1, sqlDate);
								}
								double close_price = Double.parseDouble(data[4]);
								statement.setDouble(2, close_price);
								statement.addBatch();
							}
							if (count % batchSize == 0){
								statement.executeBatch();}
						}
						lineReader.close();
						statement.executeBatch();
						con.commit();
					} 
					catch(IOException s1){
						s1.printStackTrace();}
					catch (SQLException ex){
						ex.printStackTrace();
						try{
							con.rollback();} 
						catch (SQLException e1){
							e1.printStackTrace();
						}
						con.close();}
					rs = st.executeQuery("select * from " + file);
					System.out.println("Alter table");
					int columnCount = rs.getMetaData().getColumnCount();
					if(columnCount==2)
					{
						query = "alter table " +file+" add column Ret_price double";
						st.executeUpdate(query);

						rs = st.executeQuery("select * from "+file+"  order by date desc");
						while(rs.next())
						{
							double num1 = rs.getDouble(2);
							java.sql.Date sqlDate = rs.getDate(1);
							if(rs.next())
							{
								double num2 = rs.getDouble(2);
								double num3 = (num1-num2)/num2;
								//out.println("Date : "+sqlDate+"Ret_price : "+num3);
								String query1 = "update "+ file +" set  Ret_price =  ?  where date = ? ";
								PreparedStatement preparedStmt = con.prepareStatement(query1);
								preparedStmt.setDouble(1, num3);  
								preparedStmt.setDate(2, sqlDate);

								preparedStmt.executeUpdate();

							}
							rs.previous();
						}
						con.commit();
					}
					Thread.sleep(200);
					rs = st.executeQuery("select * from tables");
					if(rs.next()==false)
					{
						String sql = "INSERT INTO tables (id,table_name) VALUES (?, ?)";
						PreparedStatement statement = con.prepareStatement(sql);
						statement.setInt(1, 1);
						statement.setString(2,file);						
						statement.executeUpdate();
						con.commit();						
					}
					else
					{
						rs.last();
						int max=rs.getInt(1);
						String sql = "INSERT INTO tables (id,table_name) VALUES (?, ?)";
						PreparedStatement statement = con.prepareStatement(sql);
						statement.setInt(1, max+1);
						statement.setString(2,file);						
						statement.executeUpdate();
					}					con.commit();						
					rs= st.executeQuery("select * from tables");
					while(rs.next())
					{
						System.out.println(rs.getInt(1)+" "+rs.getString(2));
					}
					con.close();
					a.setAlertType(AlertType.INFORMATION);
					a.setContentText("Stock added to database successfully");
					a.show();					
				}		
			}catch (SQLException e) {
				System.out.println(e);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}catch (IOException e2) {
			// TODO: handle exception
			//e2.printStackTrace();
			System.out.println("Invalid Characters");
			a.setAlertType(AlertType.ERROR);
			a.setContentText("Invalid Stock Code \t"+s);
			a.show();
		}
	}
}

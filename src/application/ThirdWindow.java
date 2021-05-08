package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ThirdWindow implements Initializable{
	Alert a = new Alert(AlertType.NONE); 
	@FXML
	public ListView<String> list;
	public static ListView<String> list1;
	@FXML
	public Button optimize;
	@FXML
	public Button goback;
	@FXML
	public Button update;
	String[] table;
	ObservableList<String> items ;
	//Initializing observable list with the tables in database
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String url = "jdbc:mysql://localhost:3306/sample_database";
		String uname = "root";
		String passwd = "2000";
		try
		{
			Connection con = DriverManager.getConnection( url,uname,passwd);  
			Statement st = con.createStatement();
			ResultSet rs = null;
			rs = st.executeQuery("select * from tables");
			rs.last();
			table = new String[rs.getRow()];
			int i=0;
			rs.beforeFirst();
			while(rs.next())
			{
				table[i++]=rs.getString(2).toUpperCase();
			}
			rs.close();
			st.close();
			con.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		items = FXCollections.observableArrayList(table);
		list.setItems(items);
		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		list1=list;
	}
		//invoking function to calculate/optimize the sharpe ratio and minimize the standard deviation  
	@FXML
	public void optimizer(ActionEvent event) throws IOException {
		ObservableList<String> table ;
		table = list.getSelectionModel().getSelectedItems();
		if(table.isEmpty())
			System.out.println("None of the stock is selected");
		else
		{
			Parent troot = FXMLLoader.load(getClass().getResource("/application/TableView.fxml"));
			Stage stage = (Stage) optimize.getScene().getWindow();
			Scene thirdScene = new Scene(troot);
			//stage.setResizable(false);
			stage.setScene(thirdScene);
		}
	}
	public static ObservableList<String> getSelectedTable()
	{ 
		ObservableList<String> table ;
		table = list1.getSelectionModel().getSelectedItems();
		return table;
	}
	//invoking function to goback to ratiobutton window to choose between internet and database ->> to fetch data for optimizing the stock of investment
	public void goBack(ActionEvent event) throws IOException {
		Parent sroot = FXMLLoader.load(getClass().getResource("/application/SecondWindow.fxml"));
		Stage stage = (Stage) goback.getScene().getWindow();
		Scene secondScene = new Scene(sroot);
		//stage.setResizable(false);
		stage.setScene(secondScene);
	}
	void listFilesForFolder(final File folder)throws Exception
	 {
		String url = "jdbc:mysql://localhost:3306/sample_database";
		String uname = "root";
		String passwd = "2000";
		Connection con = DriverManager.getConnection( url,uname,passwd);  
		Statement st = con.createStatement();
		//con.setAutoCommit(false);
		st.executeUpdate("truncate tables");
		//con.commit();
        
	    for (final File fileEntry : folder.listFiles()) 
	    {
	    	if (fileEntry.isDirectory()) 
	        {
	            listFilesForFolder(fileEntry);
	        } 
	        else 
	        {
	        	String table = fileEntry.getName().split("\\.")[0];
	    		table = table.replace('-','_');	    		
	    		st.executeUpdate("drop table "+table);
	    		//con.commit();*/
	            String file = fileEntry.getName().split("\\.csv")[0];
	    		System.out.println(file);
	    		FetchData f = new FetchData();
	    		f.UpdateTable(file);
	    		System.out.println("Fetch completed");
	        }
	    	a.setAlertType(AlertType.INFORMATION);
			a.setContentText("Updated Successfully");
			a.show();
			
	    }
	}
	public void ChangeTable(ActionEvent e) throws Exception
	{
		System.out.println("Update performed");
		String str = "F:\\LAKSHMI\\6th SEM\\Mini Project\\Training Data Set\\";
	    final File folder = new File(str);
		listFilesForFolder(folder);
		
		System.out.println("Fetching completed");
		
		Parent troot= FXMLLoader.load(getClass().getResource("/application/ThirdWindow.fxml"));
		Stage stage = (Stage) this.optimize.getScene().getWindow();
		Scene thirdScene = new Scene(troot);
		//stage.setResizable(true);
		stage.setScene(thirdScene);
	}
}

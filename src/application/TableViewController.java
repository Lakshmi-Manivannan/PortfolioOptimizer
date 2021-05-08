package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.ResourceBundle;

import org.ejml.simple.SimpleMatrix;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

@SuppressWarnings("serial")
class Incompatibe extends Exception {
	Incompatibe(String m)
	{
		super(m);
	}
	public String toString()
	{
		return "Can't Optimize The Selected Stocks\n Please Change the Stocks";
	}
}

public class TableViewController implements Initializable{
	Alert a = new Alert(AlertType.NONE); 
	@FXML
	private TableView<TableViewData> tableview = new TableView<TableViewData>();
	@FXML
	private TableColumn<TableViewData,String> stock_name;
	@FXML
	private TableColumn<TableViewData,String> max_profit_weight;
	@FXML
	private TableColumn<TableViewData,String> min_risk_weight;
	@FXML
	private TableColumn<TableViewData,String> e_returns;
	@FXML
	private Label l1;
	@FXML
	private Label l2;
	@FXML
	private Label l3;
	@FXML
	private Label l4;
	@FXML
	private Label l5;
	@FXML
	private Label l6;
	@FXML 
	public Button show;
	@FXML
	public Button goback;
	
	Stage tstage =new Stage();

	SimpleMatrix avg_Returns;
	SimpleMatrix E_weight;
	SimpleMatrix matrix ;
	SimpleMatrix FinalMatrix;
	SimpleMatrix MaxMatrix;
	SimpleMatrix MinMatrix;

	private final ObservableList<TableViewData> data = FXCollections.observableArrayList();
	/*new TableViewData("Jacob",2.5,3.2 ),
					new TableViewData("Isabella",5.2,6.3),
					new TableViewData("Ethan",80.2,65.3 ),
					new TableViewData("Emma",82.3,95.3 ),
					new TableViewData("Michael",45.5,56.2));*/
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

		ObservableList<String> table = ThirdWindow.getSelectedTable();
		for(String k:table)
		{
			System.out.println(k);
		}
		stock_name.setCellValueFactory(new PropertyValueFactory<TableViewData, String>("stock_name"));
		max_profit_weight.setCellValueFactory(new PropertyValueFactory<TableViewData, String>("max_stock"));
		min_risk_weight.setCellValueFactory(new PropertyValueFactory<TableViewData, String>("min_stock"));
		e_returns.setCellValueFactory(new PropertyValueFactory<TableViewData, String>("e_returns"));
		//tableview.setItems(data);
		System.out.println("its working");
		String url = "jdbc:mysql://localhost:3306/sample_database";
		String uname = "root";
		String passwd = "2000";
		try
		{
			Connection con = DriverManager.getConnection( url,uname,passwd);  
			Statement st = con.createStatement();
			ResultSet rs = null;

			double[] array1,array2;
			int j;
			SimpleMatrix avg_Returns = new SimpleMatrix(new double[table.size()][1]);
			SimpleMatrix E_weight = new SimpleMatrix(new double[table.size()][1]);
			SimpleMatrix matrix = new SimpleMatrix (new double[table.size()][table.size()]);
			SimpleMatrix FinalMatrix = new SimpleMatrix (new double[10000][table.size()+3]);
			SimpleMatrix MaxMatrix = new SimpleMatrix (new double[10000][table.size()+3]);
			Random r = new Random();
			double start = 0.0,end =  Double.valueOf(table.size());
			int posi=0,posj=0;
			TableViewController t1 = new TableViewController();		
			
			for(String k : table)
			{
				j=0;
				rs = st.executeQuery("select Ret_price from "+ k +" order by date desc");
				rs.last();
				array1 = new double[rs.getRow()];
				rs.beforeFirst();
				while(rs.next())
				{
					array1[j]=rs.getDouble(1);
					j++;
				}
				double val = t1.mean(array1);
				avg_Returns.setRow(posi, 0, val*12.0);
				posj=0;
				for(String l : table)
				{
					if(posj>=posi)
					{

						j=0;
						rs = st.executeQuery("select Ret_price from "+ l +" order by date desc");
						rs.last();
						array2 = new double[rs.getRow()];
						rs.beforeFirst();
						while(rs.next())
						{
							array2[j]=rs.getDouble(1);
							j++;
						}
						if(array1.length ==array2.length)
						{
							matrix.setRow(posi, posj,(t1.variance(array1, array2))*12.0);
							matrix.setRow(posj, posi,(t1.variance(array1, array2))*12.0);	
						}			
						else
						{
							throw new Incompatibe("Size are different");
						}
					}
					posj=posj+1;
				}
				posi=posi+1;
			}
			int i=0;
			double max_val = 0.0;
			for(int rotate=0;rotate<1000;rotate++)
			{
				for(int limit = 0;limit<10000;limit++)
				{
					for( i=0;i<table.size();i++)
					{
						double v = start + (r.nextDouble() * (end - start));
						E_weight.setRow(i, 0,v);
					}
					double sum1 = t1.sum(E_weight);
					for(i=0;i<table.size();i++)
					{
						double E_val = E_weight.get(i, 0);
						E_weight.setRow(i, 0,(E_val/sum1));
					}
					SimpleMatrix actual = (E_weight.transpose()).mult(avg_Returns);//firstMatrix.mult(secondMatrix);
					SimpleMatrix standard_deviation = ((E_weight.transpose()).mult(matrix)).mult(E_weight);
					rs.close();
					for(int column = 0;column<table.size();column++)
					{
						FinalMatrix.setRow(limit, column, E_weight.get(column));
					}
					FinalMatrix.setRow(limit, table.size(), actual.get(0));
					FinalMatrix.setRow(limit,(table.size()+ 1), Math.sqrt(standard_deviation.get(0)));
					FinalMatrix.setRow(limit, (table.size()+2), (actual.get(0)/Math.sqrt(standard_deviation.get(0))));
				}
				int pos = t1.findMax1(FinalMatrix);
				if(FinalMatrix.get(pos,(FinalMatrix.numCols()-1))>max_val)
				{
					max_val=FinalMatrix.get(pos,(FinalMatrix.numCols()-1));
					MaxMatrix.set(FinalMatrix);
				}				
			}
			FinalMatrix.set(MaxMatrix);
			DecimalFormat df = new DecimalFormat("#%");
			int pos = t1.findMax1(FinalMatrix);
			int min = t1.findMin1(FinalMatrix);
			System.out.println("Max. Position : "+pos);
			System.out.println("Min. Position : "+min);
			System.out.println("Stocks \t\t\t\t\tMax. Sharpe Ratio\t\t\t\t\tMin. Stadard Deviation\n");
			st.executeUpdate("truncate Max_profit");
			con.setAutoCommit(false);
			String sql = "INSERT INTO Max_profit (returns_value,s_seviation,value) VALUES (?, ?,?)";
			PreparedStatement statement = con.prepareStatement(sql);
			for(i=0;i<FinalMatrix.numRows();i++)
			{
				statement.setDouble(1, FinalMatrix.get(i,FinalMatrix.numCols()-3 ));
				statement.setDouble(2, FinalMatrix.get(i,FinalMatrix.numCols()-2 ));
				if(i==pos)
					statement.setInt(3, 1);
				else if(i==min)
					statement.setInt(3, -1);
				else
					statement.setInt(3, 0);					
				statement.execute();
			}
			for(i=0;i<table.size();i++)
			{
				System.out.println(table.get(i)+"\t\t\t\t\t"+df.format(FinalMatrix.get(pos, i))+"\t\t\t\t\t"+df.format(FinalMatrix.get(min, i)));
				data.add(new TableViewData(table.get(i),df.format(FinalMatrix.get(pos, i)),df.format(FinalMatrix.get(min, i)),df.format(avg_Returns.get(i, 0))));
			}
			System.out.println("\nMax. Sharpe Ratio ");
			for(j= table.size();j<FinalMatrix.numCols();j++)
			{
				l1.setText("Expected Returns : "+df.format(FinalMatrix.get(pos, j++)));
				l2.setText("Stadard Deviation : "+df.format(FinalMatrix.get(pos, j++)));
				l5.setText("Sharpe Ratio : "+df.format(FinalMatrix.get(pos, j++)));
				System.out.println("\nExpected Returns : \t\t\t\t"+l1.getText()+"\nStadard Deviation:\t\t\t\t"+l2.getText()+"\nSharpe Ratio : \t\t\t\t"+l5.getText());
			}
			System.out.println("\nMin. Standard Deviation ");
			for(j= table.size();j<FinalMatrix.numCols();j++)
			{
				l3.setText("Expected Returns : "+df.format(FinalMatrix.get(min, j++)));
				l4.setText("Stadard Deviation : "+df.format(FinalMatrix.get(min, j++)));
				l6.setText("Sharpe Ratio : "+df.format(FinalMatrix.get(min, j++)));
				System.out.println("\nExpected Returns : \t\t\t\t"+l3.getText()+"\nStadard Deviation:\t\t\t\t"+l4.getText()+"\nSharpe Ratio : \t\t\t\t"+l6.getText());
			}
			con.commit();
		}
		catch (Incompatibe e)
		{
			a.setAlertType(AlertType.WARNING);
			a.setContentText("Choose different stocks"+e.getMessage());
			a.show();
			Parent troot;
			try {
				troot = FXMLLoader.load(getClass().getResource("/application/ThirdWindow.fxml"));
				Stage stage = (Stage) this.tableview.getScene().getWindow();
				Scene thirdScene = new Scene(troot);
				//stage.setResizable(true);
				stage.setScene(thirdScene);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		catch (SQLException e2) {
			// TODO: handle exception
			e2.printStackTrace();
		}
		
		
		tableview.setItems(data);
	}

	public void goBack(ActionEvent event) throws IOException {
		Parent sroot = FXMLLoader.load(getClass().getResource("/application/ThirdWindow.fxml"));
		Stage stage = (Stage) goback.getScene().getWindow();
		Scene secondScene = new Scene(sroot);
		//stage.setResizable(false);
		stage.setScene(secondScene);
	}
	public void show(ActionEvent e) throws Exception
	{
		Parent troot = FXMLLoader.load(getClass().getResource("/application/TableView.fxml"));
		Stage stage = (Stage) show.getScene().getWindow();
		Scene thirdScene = new Scene(troot);
		//stage.setResizable(false);
		stage.setScene(thirdScene);
	}

	//plotting graph

	public void btnAction(ActionEvent event) throws Exception 
	{
		Parent ttroot = FXMLLoader.load(getClass().getResource("/application/Graph.fxml"));
		Scene tthirdScene = new Scene(ttroot);
		//stage.setResizable(false);
		tstage.setScene(tthirdScene);
		System.out.println("Button Clicked");
		tstage.show();
	}

	//finding max. Sharpe Ratio for 10000 iterations

	//finding max. Sharpe Ratio for 1000 iterations in 10000 max. Sharpe ratio (10000 iterations)
	int findMax1(SimpleMatrix mat) 
	{ 
		double maxElement = 0; 
		int pos =0;
		for (int i = 0; i < mat.numRows(); i++) { 
			if (mat.get(i, (mat.numCols()-1)) > maxElement) { 
				maxElement = mat.get(i, (mat.numCols()-1)); 
				pos = i;
			} 
		} 
		return pos; 
	}
	//finding min. Standard deviation for 1000 iterations in 10000 min. Standard deviation (10000 iterations)
	int findMin1(SimpleMatrix mat) 
	{ 
		double minElement = 0; 
		int pos =0;
		minElement = mat.get(pos, (mat.numCols()-2));
		for (int i = 0; i < mat.numRows(); i++)
		{ 
			if ((mat.get(i, (mat.numCols()-2)) < minElement))
			{ 
				minElement = mat.get(i, (mat.numCols()-2)); 
				pos = i;
			} 
		} 
		return pos; 
	} 
	//finding min.Standard deviation for 10000 iterations

	//finding sum of the weights
	double sum(SimpleMatrix arr) 
	{ 
		double sum = 0.0;     
		int i; 
		for (i = 0; i < arr.numRows(); i++) 
			sum +=  arr.get(i, 0); 
		return sum; 
	} 
	//finding mean value of the return price of the stock
	double mean(double arr[]) 
	{ 
		double sum = 0; 

		for(int i = 0; i < arr.length; i++) 
			sum = sum + arr[i]; 

		return sum /(arr.length-1); 
	} 
	//finding covariance between two stocks
	double variance(double arr1[], 
			double arr2[]) 
	{ 
		double sum = 0; 

		for(int i = 0; i < arr1.length; i++) 
			sum = sum + (arr1[i] - mean(arr1)) * 
			(arr2[i] - mean(arr2)); 
		return sum / (arr1.length-1); 
	} 
}

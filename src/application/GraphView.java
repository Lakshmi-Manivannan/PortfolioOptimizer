package application;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class GraphView implements Initializable{
	@FXML
	public ScatterChart<Double,Double> chart;
	@FXML
	public Label label;
	
	@FXML
	NumberAxis xAxis= new NumberAxis();//("Values for X-Axis", 0, 10, 10);
	@FXML
	NumberAxis yAxis = new NumberAxis();//("Values for Y-Axis", 0, 100, 100);
	
	void plot()
	{
		String url = "jdbc:mysql://localhost:3306/sample_database";
		String uname = "root";
		String passwd = "2000";
		
		try
		{
			Connection con = DriverManager.getConnection( url,uname,passwd);  
			Statement st = con.createStatement();
			ResultSet rs = null;
			XYChart.Series<Double,Double> series1 = new XYChart.Series<Double,Double>();
			XYChart.Series<Double,Double> series2 = new XYChart.Series<Double,Double>();
			XYChart.Series<Double,Double> series3 = new XYChart.Series<Double,Double>();

			rs = st.executeQuery("select * from Max_profit");
			while(rs.next())
			{
				if(rs.getInt(3)==1)
					series2.getData().add(new XYChart.Data<Double,Double>(rs.getDouble(2),rs.getDouble(1)));
				else if(rs.getInt(3)==-1)
					series3.getData().add(new XYChart.Data<Double,Double>(rs.getDouble(2),rs.getDouble(1)));
				series1.getData().add(new XYChart.Data<Double,Double>(rs.getDouble(2),rs.getDouble(1)));
			}
			
			
			series1.setName("Optimizer");
			series2.setName("Max. profit");
			series3.setName("Mini. Risk");
			xAxis.setLabel("Standard Deviation(Volatility)");
			yAxis.setLabel("Expected Returns");
			chart.setTitle("Portfolio Optimizer");
			chart.setAnimated(true);
			chart.animatedProperty();
			//chart.getData().addAll(series1,series2);
			chart.getData().addAll(series1,series2,series3);
			System.out.println("kaw");	
			chart.setLegendVisible(true);
			
			
			chart.setStyle(("-fx-background-color: rgba(0,168,355,0.05);-fx-background-radius: 5;"));
			rs.close();
			st.close();
			con.close();
		}
		
		catch(Exception e)
		{
			System.out.println("exception"+e.getMessage());
		}
		
	}
		@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
			plot();
			}   
}

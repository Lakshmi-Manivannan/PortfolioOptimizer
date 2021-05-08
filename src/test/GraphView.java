package test;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class GraphView implements Initializable {
	@FXML
	public LineChart<Double,Double> chart;

	/*@FXML
	NumberAxis xAxis = new NumberAxis("Number saved", 1, 10.1, 1);

	@FXML
	NumberAxis yAxis = new NumberAxis("Calculated Value", 0, 100, 1);
	*/
	@FXML
	NumberAxis xAxis = new NumberAxis("Values for X-Axis", 0, 3, 1);
	@FXML
    NumberAxis yAxis = new NumberAxis("Values for Y-Axis", 0, 3, 1);



	@FXML
	private void handleButtonAction(ActionEvent event) {
		/*ObservableList<XYChart.Series<Double,Double>> lineChartData = FXCollections.observableArrayList(
				new LineChart.Series<Double,Double>("Series 1", FXCollections.observableArrayList(
						new XYChart.Data<Double,Double>(0.0, 1.0),
						new XYChart.Data<Double,Double>(1.2, 1.4),
						new XYChart.Data<Double,Double>(2.2, 1.9),
						new XYChart.Data<Double,Double>(2.7, 2.3),
						new XYChart.Data<Double,Double>(2.9, 0.5)
						)),
				new LineChart.Series<Double,Double>("Series 2", FXCollections.observableArrayList(
						new XYChart.Data<Double,Double>(0.0, 1.6),
						new XYChart.Data<Double,Double>(0.8, 0.4),
						new XYChart.Data<Double,Double>(1.4, 2.9),
						new XYChart.Data<Double,Double>(2.1, 1.3),
						new XYChart.Data<Double,Double>(2.6, 0.9)
						))
				);*/
		XYChart.Series<Double,Double> series1 = new XYChart.Series<Double,Double>();
		series1.getData().add(new XYChart.Data<Double,Double>(0.0, 1.0));
		series1.getData().add( new XYChart.Data<Double,Double>(1.2, 1.4));
		series1.getData(). add(new XYChart.Data<Double,Double>(2.2, 1.9));
		series1.getData().add(new XYChart.Data<Double,Double>(2.7, 2.3));
		series1.getData().add(new XYChart.Data<Double,Double>(2.9, 100.00));
		xAxis.setLabel("X - Axis");
		yAxis.setLabel("Y - Axis");
		//chart = new LineChart(xAxis, yAxis, series1);

		chart.setAnimated(true);
		chart.animatedProperty();
		chart.getData().add(series1);
		System.out.println("kaw");

	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}    }

package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class SecondWindow {

	@FXML
	private RadioButton internet;
	@FXML
	private RadioButton database;
	public void radioButtonChange(ActionEvent e) throws IOException 
	{
		Parent troot;    	
		//System.out.println("DATA FETCHED FROM DATABASE");
		if(database.isSelected())
			troot= FXMLLoader.load(getClass().getResource("/application/ThirdWindow.fxml"));
		else 
			troot = FXMLLoader.load(getClass().getResource("/application/InternetD.fxml"));
		Stage stage = (Stage) this.database.getScene().getWindow();
		Scene thirdScene = new Scene(troot);
		//stage.setResizable(true);
		stage.setScene(thirdScene);
	}
}
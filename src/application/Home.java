package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Home {
	@FXML
	public Button home;
	//private Label MyLabel;
	@FXML
	public void changeWindow(ActionEvent event) throws IOException {
		Parent sroot = FXMLLoader.load(getClass().getResource("/application/SecondWindow.fxml"));
	    Stage stage = (Stage) home.getScene().getWindow();
	    Scene secondScene = new Scene(sroot);
	    //stage.setResizable(false);
	    stage.setScene(secondScene);
	}
}

package application;
import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InternetD {
	@FXML
	public ListView<String> data_list;
	@FXML
	public Button goback;
	@FXML
	public Button add_list;
	@FXML
	public TextField t;
	public void goBack(ActionEvent event) throws IOException {
		Parent sroot = FXMLLoader.load(getClass().getResource("/application/SecondWindow.fxml"));
		Stage stage = (Stage) goback.getScene().getWindow();
		Scene secondScene = new Scene(sroot);
		//stage.setResizable(false);
		stage.setScene(secondScene);
	}
	public void AddData() {

		String M = t.getText();
		System.out.println(M);
		data_list.getItems().add(M);
		//setStyle(data_list.getIndex() % 2 == 0 ? "-fx-background-color: blue;" : "-fx-background-color: red;");
	}

	@FXML
	public void SetData(ActionEvent e) throws Exception
	{
		ObservableList<String> tables = data_list.getItems();
		System.out.println(tables);
		FetchData d = new FetchData();
		for(String s:tables)
		{
			System.out.println(s.toUpperCase());
			d.UpdateTable(s.toUpperCase());
		}
		Parent troot= FXMLLoader.load(getClass().getResource("/application/ThirdWindow.fxml"));
		Stage stage = (Stage) this.add_list.getScene().getWindow();
		Scene thirdScene = new Scene(troot);
		//stage.setResizable(true);
		stage.setScene(thirdScene);
	}
}

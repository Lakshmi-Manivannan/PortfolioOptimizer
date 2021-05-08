package application;

import javafx.beans.property.SimpleStringProperty;

public class TableViewData {
	private SimpleStringProperty stock_name;
	private SimpleStringProperty max_stock;
	private SimpleStringProperty min_stock;
	private SimpleStringProperty e_returns;

	public TableViewData(String stock_name, String max_stock, String min_stock,String e_returns) {
		super();
		this.stock_name = new SimpleStringProperty(stock_name);
		this.max_stock = new SimpleStringProperty(max_stock);
		this.min_stock = new SimpleStringProperty(min_stock);
		this.e_returns = new SimpleStringProperty(min_stock);
	}
	
	public void setStock_name(String stock_name) {
		this.stock_name = new SimpleStringProperty(stock_name);
	}

	public void setMax_stock(String max_stock) {
		this.max_stock = new SimpleStringProperty(max_stock);
	}

	public void setMin_stock(String min_stock) {
		this.min_stock =new SimpleStringProperty(min_stock);
	}

	public void setE_returns(String e_returns) {
		this.e_returns = new SimpleStringProperty(e_returns);
	}

	public String getStock_name() {
		return stock_name.get();
	}

	public String getMax_stock() {
		return max_stock.get();
	}

	public String getMin_stock() {
		return min_stock.get();
	}
	public String getE_returns() {
		return e_returns.get();
	}
}

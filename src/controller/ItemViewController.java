package controller;

import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import db.Db;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entity.Customer;
import entity.Item;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.ItemTM;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;


public class ItemViewController implements Initializable {

    public Button btnAdd;
    public AnchorPane root;
    public FontAwesomeIconView iconHome;
    @FXML
    private JFXTextField txtItemCode;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private JFXTextField txtUprice;

    @FXML
    private Button btnSave;

    @FXML
    private TableView<ItemTM> tblItem;

    @FXML
    private JFXTextField txtSearch;

    PreparedStatement preparedStatement;
    Connection connection= DBConnection.getInstance().getConnection();


    @FXML
    void btnSaveOnAtion(ActionEvent event) throws SQLException {

        if(btnSave.getText().equalsIgnoreCase("Save")){
            String code=txtItemCode.getText();
            String des=txtDescription.getText();
            String up=txtUprice.getText();
            String qty=txtQty.getText();

            preparedStatement=connection.prepareStatement("insert into item values (?,?,?,?)");
            preparedStatement.setString(1,code);
            preparedStatement.setString(2,des);
            preparedStatement.setString(3,up);
            preparedStatement.setString(4,qty);
            int i=preparedStatement.executeUpdate();
            if(i>0){
                new Alert(Alert.AlertType.CONFIRMATION,"Item Added successfully").show();
                btnAddOnAction(event);
                btnAdd.setDisable(false);
                loadItems();
            }
            else {
                new Alert((Alert.AlertType.ERROR),"Failed To Insert!").show();
            }
        }
        else {
            String code=txtItemCode.getText();
            String des=txtDescription.getText();
            String up=txtUprice.getText();
            String qty=txtQty.getText();

            preparedStatement=connection.prepareStatement("update item set unitPrice=?, description=?, qtyOnHand=? where code=?");
            preparedStatement.setString(1,up);
            preparedStatement.setString(2,des);
            preparedStatement.setString(3,qty);
            preparedStatement.setString(4,code);

            int i =preparedStatement.executeUpdate();
            if(i>0){
                new Alert(Alert.AlertType.INFORMATION,"Updated").show();
            }
            else {
                new Alert(Alert.AlertType.ERROR,"Failed To Update").show();
            }
            loadItems();
            btnAdd.setDisable(false);
            tblItem.refresh();
            btnAddOnAction(event);
           }

    }

    @FXML
    void txtSaveKeyRelease(KeyEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        tblItem.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblItem.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItem.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblItem.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("uprice"));
        tblItem.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("button"));

        loadItems();

        tblItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            btnSave.setText("UPDATE");

            txtItemCode.setText(newValue.getItemCode());
            txtDescription.setText(newValue.getDescription());
            txtQty.setText(newValue.getQty());
            txtUprice.setText(newValue.getUprice());


        });



    }

    public void loadItems() {

        ObservableList<ItemTM> itemTMS=tblItem.getItems();
        itemTMS.clear();
        tblItem.refresh();

        try {

            preparedStatement=connection.prepareStatement("SELECT * FROM item");
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                String code=resultSet.getString(1);
                String des=resultSet.getString(2);
                String up=resultSet.getString(3);
                String qty=resultSet.getString(4);
                Button btn=new Button("DELETE");
                btn.setStyle("-fx-background-color: red");
                itemTMS.add(new ItemTM(code,des,up,qty,btn));
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        deleteItem(code);
                        try {
                            btnAddOnAction(event);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteItem(String code){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Are You Sure You Wantto Delete This Item?", ButtonType.YES,ButtonType.CANCEL);
        Optional<ButtonType> buttonType=alert.showAndWait();
        if (buttonType.get()==ButtonType.YES){
            try {
                preparedStatement=connection.prepareStatement("DELETE from item where code=?");
                preparedStatement.setString(1,code);
                int i=preparedStatement.executeUpdate();
                if(i>0){
                    new Alert(Alert.AlertType.INFORMATION,"Customer Has Been Deleted Successfully").show();
                    loadItems();
                    tblItem.refresh();

                }
                else {
                    new Alert(Alert.AlertType.INFORMATION,"Failed To Delete the Customer!").show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public void btnAddOnAction(ActionEvent event) throws SQLException {

        reset();


    }

    private void reset() throws SQLException {
        txtDescription.setText("");
        txtQty.setText("");
        txtUprice.setText("");
        btnSave.setText("SAVE");
        //btnSave.setDisable(true);

        int i=1;
        preparedStatement=connection.prepareStatement("SELECT * FROM item");
        ResultSet rst=preparedStatement.executeQuery();
        rst.last();
        String x=rst.getString(1);



        int id= Integer.valueOf(x.substring(1));
        int maxid=maxid=id+1;
        String newid;
        System.out.println(maxid);

        if(x.startsWith("I00")){

            newid="I00"+maxid;
            System.out.println(newid);
            txtItemCode.setText(newid);


        }
        else if(x.startsWith("I0")){

            newid="I0"+maxid;
            System.out.println(newid);
            txtItemCode.setText(newid);
        }
        else {

            newid="C"+maxid;
            System.out.println(newid);
            txtItemCode.setText(newid);
        }
    }

    public void navigate(MouseEvent mouseEvent) throws IOException {

        if (mouseEvent.getSource() instanceof FontAwesomeIconView){
            FontAwesomeIconView icon = (FontAwesomeIconView) mouseEvent.getSource();

            Parent root = null;

            switch(icon.getId()){
                case "iconHome":
                    root = FXMLLoader.load(this.getClass().getResource("/view/main.fxml"));
                    break;
                case "iconItem":
                    root = FXMLLoader.load(this.getClass().getResource("/view/itemView.fxml"));
                    break;
                case "iconPlaceOrder":
                    root = FXMLLoader.load(this.getClass().getResource("/view/PlaceOrderView.fxml"));
                    break;
                case "iconSearch":
                    root = FXMLLoader.load(this.getClass().getResource("/view/SearchOrderView.fxml"));
                    break;
            }

            if (root != null){
                Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(subScene);
                primaryStage.centerOnScreen();

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
                tt.setFromX(-subScene.getWidth());
                tt.setToX(0);
                tt.play();

            }
        }
    }
        

}

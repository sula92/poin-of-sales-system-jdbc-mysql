package controller;

import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import db.Db;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entity.Customer;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.CustomerTM;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerViewController implements Initializable {


    public FontAwesomeIconView iconHome;
    public AnchorPane root;
    @FXML
    private JFXTextField txId;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtAddress;

   @FXML
    private Button btnSave;

    @FXML
    private Button btnCus;

    @FXML
    private TableView<CustomerTM> tblCustomer;

    @FXML
    private TableColumn colCusId;

    @FXML
    private TableColumn colCusName;

    @FXML
    private TableColumn colCusAdd;

    @FXML
    private TableColumn colDel;

    @FXML
    private JFXTextField txtSearch;

    private PreparedStatement preparedStatement;
    private Connection connection=DBConnection.getInstance().getConnection();

    ArrayList<Customer> customers=new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colCusId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCusName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCusAdd.setCellValueFactory(new PropertyValueFactory<>("address"));
        colDel.setCellValueFactory(new PropertyValueFactory<>("button"));

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            btnSave.setText("UPDATE");
            txId.setText(newValue.getId());
            txtName.setText(newValue.getName());
            txtAddress.setText(newValue.getAddress());
            btnCus.setDisable(true);



        });

        try {
            loadCustomer();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtSearch.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ObservableList<CustomerTM> customerTMS=tblCustomer.getItems();
                customerTMS.clear();

                for (Customer customer:customers) {
                    if(customer.getId().contains(newValue)||customer.getName().contains(newValue)||customer.getAddress().contains(newValue)) {
                        Button button=new Button("DELETE");
                        button.setStyle("-fx-background-color: red");
                        customerTMS.add(new CustomerTM(customer.getId(), customer.getName(), customer.getAddress(), button));
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                customerTMS.remove(customer);
                                deleteCustomer(customer.getId());
                                txtSearch.clear();
                                tblCustomer.refresh();
                            }

                        });
                    }

                }
            }
        });

    }


    @FXML
    void btnCusOnAction(ActionEvent event) throws SQLException {

        txtAddress.setText("");
        txtName.setText("");
        btnSave.setText("SAVE");

        int i=1;
        preparedStatement=connection.prepareStatement("select * FROM customer");
        ResultSet resultSet=preparedStatement.executeQuery();
        boolean b=resultSet.last();

        String x=resultSet.getString(1);



        int id= Integer.valueOf(x.substring(1));
        int maxid=maxid=id+1;
        String newid;
        System.out.println(maxid);

        if(x.startsWith("C00")){

            newid="C00"+maxid;
            System.out.println(newid);
            txId.setText(newid);


        }
        else if(x.startsWith("C0")){

            newid="C0"+maxid;
            System.out.println(newid);
            txId.setText(newid);
        }
        else {

            newid="C"+maxid;
            System.out.println(newid);
            txId.setText(newid);
        }


    }

    @FXML
    void btnSaveOnAction(ActionEvent event) throws SQLException {
        String id = txId.getText();
        String nm = txtName.getText();
        String add = txtAddress.getText();

        if(btnSave.getText().equalsIgnoreCase("save")) {

            preparedStatement=connection.prepareStatement("insert into customer values(?,?,?)");
            preparedStatement.setString(1,id);
            preparedStatement.setString(2,nm);
            preparedStatement.setString(3,add);
            int i=preparedStatement.executeUpdate();
            if(i<=0){
                new Alert(Alert.AlertType.ERROR,"ERROR!").show();
            }
            else {
                new Alert(Alert.AlertType.INFORMATION,"Customer Added Successfully ").show();
            }
            loadCustomer();
            tblCustomer.refresh();
            btnCusOnAction(event);
        }
        else {

            preparedStatement=connection.prepareStatement("update customer set name=?, address=? where customerId=?");
            preparedStatement.setString(1,nm);
            preparedStatement.setString(2,add);
            preparedStatement.setString(3,id);
            int i=preparedStatement.executeUpdate();
            if ((i < 0)) {
                new Alert(Alert.AlertType.ERROR, "An Error!").show();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Updated").show();
            }
            btnCusOnAction(event);
        }

       // loadCustomer();
        btnCus.setDisable(false);



    }





    public void loadCustomer() throws SQLException {

        ObservableList<CustomerTM> customerTMS=tblCustomer.getItems();
        customerTMS.clear();

        Statement stm = DBConnection.getInstance().getConnection().createStatement();
        ResultSet rst = stm.executeQuery("SELECT * FROM customer");
        while (rst.next()){
            String id = rst.getString(1);
            String nm = rst.getString(2);
            String add = rst.getString(3);

            Button del=new Button("DELETE");
            del.setStyle("-fx-background-color: red");

            CustomerTM customerTM=new CustomerTM(id,nm,add,del);
            customers.add(new Customer(id,nm,add));
            customerTMS.add(customerTM);

            del.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Customer customer=new Customer(id,nm,add);
                    customerTMS.remove(customer);
                    deleteCustomer(id);
                    tblCustomer.refresh();
                }

            });
        }

    }

    private void deleteCustomer(String id){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Are You Sure You Wantto Delete This Customer?", ButtonType.YES,ButtonType.CANCEL);
        Optional<ButtonType> buttonType=alert.showAndWait();
        if (buttonType.get()==ButtonType.YES){
            try {
                preparedStatement=connection.prepareStatement("DELETE from customer where customerId=?");
                preparedStatement.setString(1,id);
                int i=preparedStatement.executeUpdate();
                if(i>0){
                    new Alert(Alert.AlertType.INFORMATION,"Customer Has Been Deleted Successfully").show();
                    loadCustomer();
                    tblCustomer.refresh();
                }
                else {
                    new Alert(Alert.AlertType.INFORMATION,"Failed To Delete the Customer!").show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

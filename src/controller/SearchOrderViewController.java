package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import db.Db;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entity.Customer;
import entity.OrderDetail;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.PlaceOrderTM;
import util.SerchOrderTM;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class SearchOrderViewController implements Initializable {
    public JFXComboBox<Customer> cmbcus;
    public AnchorPane root;
    public TableView tblOrders;
    public TableColumn colOrderId;
    public TableColumn colOrderDate;
    public TableColumn colCusId;
    public TableColumn colCusNm;
    public TableColumn colTot;
    public FontAwesomeIconView iconHome;
    public JFXTextField txtSearch;
    private ArrayList<SerchOrderTM> orders = new ArrayList<>();
    PreparedStatement pst;
    Connection connection= DBConnection.getInstance().getConnection();


    @Override
    public void initialize(URL location, ResourceBundle resources) {




        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderid"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCusId.setCellValueFactory(new PropertyValueFactory<>("cusId"));
        colCusNm.setCellValueFactory(new PropertyValueFactory<>("cusName"));
        colTot.setCellValueFactory(new PropertyValueFactory<>("total"));

        try {
            pst=connection.prepareStatement("select O.id, O.date, O.customerId, C.name, SUM(OD.qty*OD.unitPrice) from (`order` O INNER JOIN customer C on O.customerId = C.customerId INNER JOIN orderdetail OD on O.id = OD.orderId) group by o.id");
            ResultSet rst=pst.executeQuery();
            while (rst.next()) {
                SerchOrderTM serchOrderTM=new SerchOrderTM(rst.getString(1), rst.getDate(2).toLocalDate(),rst.getString(3),rst.getString(4),rst.getDouble(5));
                orders.add(serchOrderTM);
                tblOrders.getItems().add(serchOrderTM);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ObservableList<SerchOrderTM> serchOrderTMS=FXCollections.observableList(tblOrders.getItems());
                serchOrderTMS.clear();
                orders.stream().forEach(serchOrderTM -> {
                    if ((serchOrderTM.getOrderid().contains(newValue)||
                            serchOrderTM.getCusId().contains(newValue) ||
                            serchOrderTM.getCusName().contains(newValue) ||
                            serchOrderTM.getDate().toString().contains(newValue))){

                        serchOrderTMS.add(serchOrderTM);
                    }
                });
            }
        });

    }

    private Double getTotal(ArrayList<OrderDetail> orderDetails) {
        double tot= orderDetails.stream().mapToDouble(orderDetail -> (orderDetail.getUnitPrice() * orderDetail.getQty())).sum();
        return tot;
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

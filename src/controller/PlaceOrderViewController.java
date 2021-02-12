package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import db.Db;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entity.*;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import util.ItemTM;
import util.PlaceOrderTM;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static db.Db.customers;

public class PlaceOrderViewController implements Initializable {

    public TableView<PlaceOrderTM> tblOrder;
    public TableColumn colCode;
    public TableColumn colDes;
    public TableColumn colQty;
    public TableColumn colUprice;
    public TableColumn colTot;
    public TableColumn colDel;
    public Button btnReset;
    @FXML
    private AnchorPane root;

    @FXML
    private JFXComboBox<String> cmbCusId;

    @FXML
    private JFXComboBox<String> cmbItemCode;

    @FXML
    private JFXTextField txtQtyOnHand;

    @FXML
    private JFXTextField txtCusNm;

    @FXML
    private JFXTextField txtItemDes;

    @FXML
    private JFXTextField txtUprice;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private Button btnAdd;

    @FXML
    private Label labOrderId;

    @FXML
    private Label labDate;

    @FXML
    private Label labTot;

    @FXML
    private Label labTotal;

    @FXML
    private Button btnOrder;

    @FXML
    private FontAwesomeIconView iconHome;

    private ArrayList<Item> tempItems = new ArrayList<>();

    private ArrayList<Customer> tempCustomers=new ArrayList<>();

    Button button;

    PreparedStatement preparedStatement;

    Connection connection=DBConnection.getInstance().getConnection();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        LocalDate today = LocalDate.now();
        labDate.setText(today.toString());
        txtCusNm.setEditable(false);
        txtItemDes.setEditable(false);

        labOrderId.setText(setOrderId());

        tblOrder.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblOrder.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("des"));
        tblOrder.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrder.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("uprice"));
        tblOrder.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("tot"));
        tblOrder.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("button"));

        //colDes.setVisible(false);

        loadAllitems();
        loadAllCustomers();
        loadCustomerIDs();
        loadItemCodes();

        cmbCusId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            txtCusNm.setText(getCustomerName(newValue));
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Item item = getItems(newValue);
            txtItemDes.setText(item.getDescription());
            txtQtyOnHand.setText(item.getQty());
            txtUprice.setText(item.getUprice());
            btnAdd.setText("+ADD");
        });

        tblOrder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnAdd.setText("UPDATE");
            txtItemDes.setText(newValue.getDes());
            txtQty.setText(newValue.getQty());
            cmbItemCode.setValue(newValue.getCode());
            for (Item item : tempItems) {
                if (newValue.getCode().equalsIgnoreCase(item.getItemCode())) {
                    int qty = Integer.parseInt(item.getQty());
                    System.out.println(qty);
                    int orderqty = Integer.parseInt(newValue.getQty());
                    btnAdd.setText("UPDATE");
                    txtQtyOnHand.setText(String.valueOf(qty + orderqty));
                }
            }
        });


    }

    @FXML
    void btnAddOnAction(ActionEvent event) {

        cmbCusId.setDisable(true);

        if (cmbCusId.getSelectionModel().getSelectedIndex() == -1) {
            new Alert(Alert.AlertType.ERROR, "You need to select a customer", ButtonType.OK).show();
            cmbCusId.setDisable(false);
            cmbCusId.requestFocus();
            return;
        }

        if (cmbItemCode.getSelectionModel().getSelectedIndex() == -1) {
            new Alert(Alert.AlertType.ERROR, "Please Select an Item", ButtonType.OK).show();
            cmbItemCode.requestFocus();
            return;
        }

        if(txtQty.getText().equalsIgnoreCase("")){
            new Alert(Alert.AlertType.ERROR,"Please Put Some Quantity!").show();
            return;
        }


        int qty = Integer.parseInt(txtQty.getText());

        if(qty<=0) {
            new Alert(Alert.AlertType.ERROR, "Please Put a Valid Quantity!").show();
            return;
        }

        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
        double unitPrice = Double.parseDouble(txtUprice.getText());
        String selectedItemCode = cmbItemCode.getSelectionModel().getSelectedItem();
        ObservableList<PlaceOrderTM> placeOrderTMS=tblOrder.getItems();
        PlaceOrderTM placeOrderTM=new PlaceOrderTM();
        button=new Button("DELETE");
        button.setStyle("-fx-background-color: red");

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (PlaceOrderTM placeOrderTM:tblOrder.getItems()) {
                    setBtnDel(placeOrderTM);
                }
                txtItemDes.setText("");
                txtQty.setText("");
                txtQtyOnHand.setText("");
                txtUprice.setText("");
                btnAdd.setText("+ADD");
            }
        });

        PlaceOrderTM selectedOrder=tblOrder.getSelectionModel().getSelectedItem();
        boolean present=false;

        if (qty <= 0 || qty > qtyOnHand) {
            new Alert(Alert.AlertType.ERROR, "Invalid Qty", ButtonType.OK).show();
            txtQty.requestFocus();
            txtQty.selectAll();

            setTotal(placeOrderTMS);

            return;
        }

        if(btnAdd.getText().equalsIgnoreCase("+add")){
            if(!placeOrderTMS.isEmpty()) {
                for (PlaceOrderTM tm : tblOrder.getItems()) {
                    System.out.println("fuck3"+tm);
                    System.out.println(cmbItemCode.getValue());
                    if (tm.getCode().equalsIgnoreCase(cmbItemCode.getValue())) {
                        System.out.println(tm);
                        int oldqty=Integer.valueOf(tm.getQty());
                        int newQty = qty + oldqty;
                        tm.setQty(String.valueOf(newQty));
                        tm.setTot(String.valueOf(newQty * unitPrice));
                        tblOrder.refresh();
                        //cmbItemCode.getSelectionModel().clearSelection();

                        updateQtyOnHand(tm.getCode(),qty);

                        cmbItemCode.getSelectionModel().clearSelection();
                        txtQty.setText("");
                        txtQtyOnHand.setText("");
                        txtItemDes.setText("");

                        setTotal(placeOrderTMS);

                        return;
                    }


                }
                PlaceOrderTM orderTM=new PlaceOrderTM(cmbItemCode.getValue(), txtItemDes.getText(), txtQty.getText(), txtUprice.getText(), String.valueOf(qty * unitPrice), button);

                updateQtyOnHand(cmbItemCode.getValue(),qty);

                placeOrderTMS.add(orderTM);
                tblOrder.refresh();

                cmbItemCode.getSelectionModel().clearSelection();
                txtQty.setText("");
                txtQtyOnHand.setText("");
                txtItemDes.setText("");
                txtUprice.setText("");

                setTotal(placeOrderTMS);

                return;


            }
            else {
                System.out.println("ffffff");
                placeOrderTMS.add(new PlaceOrderTM(cmbItemCode.getValue(), txtItemDes.getText(), txtQty.getText(), txtUprice.getText(), String.valueOf(qty * unitPrice), button));
                tblOrder.refresh();
                updateQtyOnHand(cmbItemCode.getValue(),qty);

                cmbItemCode.getSelectionModel().clearSelection();
                txtQty.setText("");
                txtQtyOnHand.setText("");
                txtItemDes.setText("");
                txtUprice.setText("");

                setTotal(placeOrderTMS);
            }

        }
        //update
        else{
            for (PlaceOrderTM tm:placeOrderTMS) {
                if(tm.getCode().equalsIgnoreCase(selectedOrder.getCode())){
                    placeOrderTM.setCode(selectedItemCode);
                    placeOrderTM.setDes(txtItemDes.getText());
                    placeOrderTM.setQty(txtQty.getText());
                    placeOrderTM.setUprice(String.valueOf(unitPrice));
                    placeOrderTM.setTot(String.valueOf(qty*unitPrice));
                    placeOrderTM.setButton(button);
                    int i=placeOrderTMS.indexOf(selectedOrder);
                    placeOrderTMS.set(i,placeOrderTM);

                    updateQtyOnHand(selectedItemCode,qty);

                    cmbItemCode.getSelectionModel().clearSelection();
                    txtQty.setText("");
                    txtQtyOnHand.setText("");
                    txtItemDes.setText("");
                    txtUprice.setText("");

                    setTotal(placeOrderTMS);

                }
            }
        }
        cmbItemCode.getSelectionModel().clearSelection();
        txtQty.setText("");
        txtQtyOnHand.setText("");
        txtItemDes.setText("");
        txtUprice.setText("");

    }

    private void setTotal(ObservableList<PlaceOrderTM> placeOrderTMS) {
        double Tot=0.0;
        for (PlaceOrderTM placeOrderTM:placeOrderTMS) {
            double tot=Double.parseDouble(placeOrderTM.getTot());
            Tot=Tot+tot;
        }
        labTotal.setText(String.valueOf(Tot));
    }



    private void updateQtyOnHand(String itemCode,int newQty) {
        for (Item item:tempItems) {
            if(item.getItemCode().equalsIgnoreCase(itemCode)){
                int newQtyOnHand=Integer.parseInt(item.getQty())-newQty;
                item.setQty(String.valueOf(newQtyOnHand));
                //txtQtyOnHand.setText(String.valueOf(newQtyOnHand));
            }
        }
    }

    @FXML
    void btnOrder(ActionEvent event) throws JRException {
        if(tblOrder.getItems().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Please put Some Items", ButtonType.OK).show();
            return;
        }

        try {

            preparedStatement=connection.prepareStatement("INSERT INTO `Order` VALUES (?,?,?)");
            preparedStatement.setObject(1,labOrderId.getText());
            preparedStatement.setObject(2, LocalDate.now());
            preparedStatement.setObject(3,cmbCusId.getValue());
            int i=preparedStatement.executeUpdate();

            if (i>0){
                preparedStatement =connection.prepareStatement("INSERT INTO OrderDetail VALUES (?,?,?,?)");
                for (PlaceOrderTM placeOrderTM:tblOrder.getItems()) {
                    preparedStatement.setObject(1,labOrderId.getText());
                    preparedStatement.setObject(2,placeOrderTM.getCode());
                    preparedStatement.setObject(3,placeOrderTM.getQty());
                    preparedStatement.setObject(4,placeOrderTM.getUprice());
                    //updateQtyOnDB(placeOrderTM.getCode(),placeOrderTM.getQty());
                    int j=preparedStatement.executeUpdate();
                    if(j<0){
                        new Alert(Alert.AlertType.ERROR,"Can not Save The Order!").show();
                        return;
                    }
                    else {
                        updateQtyOnDB(placeOrderTM.getCode(),placeOrderTM.getQty());
                    }
                }
            }
            else {
                new Alert(Alert.AlertType.ERROR,"Can not Save The Order!").show();
                return;
            }
            new Alert(Alert.AlertType.INFORMATION, "Order Added Successfully", ButtonType.OK).showAndWait();
            labOrderId.setText(setOrderId());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        JasperDesign jasperDesign = JRXmlLoader.load(this.getClass().getResourceAsStream("/reports/posbill.jrxml"));

        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        Map<String ,Object> parametrers= new HashMap<>();
        parametrers.put("orderid",labOrderId.getText());
        //System.out.println(s);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parametrers,DBConnection.getInstance().getConnection());
        JasperViewer.viewReport(jasperPrint);

        reset();


    }

    private void updateQtyOnDB(String code, String qty) throws SQLException {
        int currentQty=Integer.parseInt(getQty(code));
        int orderQty=Integer.parseInt(qty);
        String newQtyonHand=String.valueOf(currentQty-orderQty);
        try {
            preparedStatement=connection.prepareStatement("update item set qtyOnHand=? where code=?");
            preparedStatement.setObject(1,newQtyonHand);
            preparedStatement.setObject(2,code);
            int i=preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getQty(String code) throws SQLException {
        preparedStatement=connection.prepareStatement("select qtyOnHand from item where code=?");
        preparedStatement.setObject(1,code);
        ResultSet r=preparedStatement.executeQuery();
        r.next();
        return  r.getString(1);
    }

    @FXML
    void iconHomeOnAction(MouseEvent event) {

    }

    private void loadItemCodes() {

        ObservableList<String> itemCodes=cmbItemCode.getItems();
        itemCodes.clear();
       tempItems.stream().forEach(item -> {
           itemCodes.add(item.getItemCode());
       });
    }

    private void loadCustomerIDs() {
        ObservableList<String> cusCodes=cmbCusId.getItems();
        cusCodes.clear();
        tempCustomers.stream().forEach(customer -> {
            cusCodes.add(customer.getId());
        });
    }

    private String getCustomerName(String id){
        String cid=tempCustomers.stream().filter(customer -> customer.getId().equalsIgnoreCase(id)).findAny().get().getName();
        return cid;
    }

    private Item getItems(String id) {
       Item i=tempItems.stream().filter(item -> item.getItemCode().equalsIgnoreCase(id)).findAny().get();
        return i;
    }

    public void setBtnDel(PlaceOrderTM placeOrderTM){
        for (Item item:tempItems) {
            if(item.getItemCode().equalsIgnoreCase(placeOrderTM.getCode())){
                item.setQty(Integer.valueOf(item.getQty())+Integer.valueOf(placeOrderTM.getQty())+"");
            }
        }
        tblOrder.getItems().remove(placeOrderTM);
        tblOrder.refresh();
    }

    public String setOrderId(){

            try {
                ResultSet r=connection.prepareStatement("select id from `order` ").executeQuery();
                r.last();
                String maxid= r.getString(1);
                int idMax=Integer.parseInt(maxid.substring(1))+1;
                if(idMax<10){
                    String max="O00"+idMax;
                    return max;
                }
                else if(idMax<100 && idMax>=10){
                    String max="O0"+idMax;
                    return max;
                }
                else {
                    String max="O"+idMax;
                    return max;
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }

            return "O001";
        }



    public void reset(){
        cmbItemCode.getSelectionModel().clearSelection();
        cmbCusId.getSelectionModel().clearSelection();
        txtUprice.setText("");
        txtItemDes.setText("");
        txtQty.setText("");
        txtQtyOnHand.setText("");
        btnAdd.setText("+ADD");
        tblOrder.getItems().clear();
        setOrderId();
        labTotal.setText("0.00");
        txtCusNm.setText("");
        cmbCusId.setDisable(false);
    }


    public void btnResetOnAction(ActionEvent event) {
        tblOrder.getItems().stream().forEach(placeOrderTM -> {
            tempItems.stream().forEach(item -> {
                if (placeOrderTM.getCode().equalsIgnoreCase(item.getItemCode())){
                    item.setQty(String.valueOf(Integer.parseInt(item.getQty())+Integer.parseInt(placeOrderTM.getQty())));
                }
            });
        });
        reset();
    }

    private void loadAllitems(){
        try {
            ResultSet r=connection.prepareStatement("SELECT * from item").executeQuery();
            while (r.next()) {
                tempItems.add(new Item(r.getString(1),r.getString(2),r.getString(4),r.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAllCustomers() {
        try {
            ResultSet r=connection.prepareStatement("SELECT * from customer").executeQuery();
            while (r.next()) {
                tempCustomers.add(new Customer(r.getString(1),r.getString(2),r.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

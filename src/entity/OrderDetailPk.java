package entity;

public class OrderDetailPk {
    private String orderID;
    private String itemCode;

    public OrderDetailPk() {
    }

    public OrderDetailPk(String orderID, String itemCode) {
        this.orderID = orderID;
        this.itemCode = itemCode;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    @Override
    public String toString() {
        return "OrderDetailPk{" +
                "orderID='" + orderID + '\'' +
                ", itemCode='" + itemCode + '\'' +
                '}';
    }
}

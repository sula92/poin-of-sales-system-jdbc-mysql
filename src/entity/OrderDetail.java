package entity;

public class OrderDetail {


    private int qty;
    private double unitPrice;
    private OrderDetailPk orderDetailPk;

    public OrderDetail() {
    }

    public OrderDetail(int qty, double unitPrice, OrderDetailPk orderDetailPk) {
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.orderDetailPk = orderDetailPk;
    }

    public OrderDetail(int qty, double unitPrice, String orderId, String itemCode) {
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.orderDetailPk=new OrderDetailPk(orderId,itemCode);
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public OrderDetailPk getOrderDetailPk() {
        return orderDetailPk;
    }

    public void setOrderDetailPk(OrderDetailPk orderDetailPk) {
        this.orderDetailPk = orderDetailPk;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "qty=" + qty +
                ", unitPrice=" + unitPrice +
                ", orderDetailPk=" + orderDetailPk +
                '}';
    }
}


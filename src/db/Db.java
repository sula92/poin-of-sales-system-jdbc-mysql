package db;
import com.sun.org.apache.xpath.internal.operations.Or;
import entity.Customer;
import entity.Item;
import entity.Order;
import entity.OrderDetail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Db {

    public static List<Customer> customers=new ArrayList<>();
    public static List<Item> items=new ArrayList<>();
    public static List<Order> orders=new ArrayList<>();
    public static List<OrderDetail> orderDetails =new ArrayList<>();


    static {

        customers.add(new Customer("C001","sula","add1"));
        customers.add(new Customer("C002","amal","add2"));
        customers.add(new Customer("C003","kamal","add3"));
        customers.add(new Customer("C004","nimal","add4"));

        items.add(new Item("I001","Pen","100","20.0"));
        items.add(new Item("I002","Pencil","50","10.0"));
        items.add(new Item("I003","Eraser","150","30.0"));
        items.add(new Item("I004","Cutter","30","60.0"));

        OrderDetail orderDetail1=new OrderDetail(8,20,"O001","I001");
        orderDetails.add(orderDetail1);
        OrderDetail orderDetail2=new OrderDetail(10,50,"O001","I001");
        orderDetails.add(orderDetail2);

        ArrayList<OrderDetail> orderDetails1=new ArrayList<OrderDetail>();
        orderDetails1.add(orderDetail1);
        orderDetails1.add(orderDetail2);


        orders.add(new Order("O001", LocalDate.of(2019,1,20),"C001",orderDetails1));


    }


}

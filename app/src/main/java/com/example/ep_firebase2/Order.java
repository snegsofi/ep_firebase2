package com.example.ep_firebase2;

public class Order {

    private String id;
    private String product;
    private String number;
    private String address;
    private String phone;
    private String timeDelivery;
    private Integer price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTimeDelivery() {
        return timeDelivery;
    }

    public void setTimeDelivery(String timeDelivery) {
        this.timeDelivery = timeDelivery;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Order(String id, String product, String number, String address, String phone, String timeDelivery, Integer price) {
        this.id = id;
        this.product = product;
        this.number = number;
        this.address = address;
        this.phone = phone;
        this.timeDelivery = timeDelivery;
        this.price=price;
    }

}

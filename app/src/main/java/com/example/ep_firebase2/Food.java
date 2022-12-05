package com.example.ep_firebase2;

public class Food {
    private String name;
    private Integer price;
    private Integer count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Food(String name, Integer price) {
        this.name = name;
        this.price = price;
    }
    public Food(Integer price, Integer count) {
        this.count = count;
        this.price = price;
    }
}

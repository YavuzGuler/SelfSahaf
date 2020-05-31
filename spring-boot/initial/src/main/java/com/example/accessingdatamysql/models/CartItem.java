package com.example.accessingdatamysql.models;

import com.example.accessingdatamysql.models.embeddedKey.CartItemKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.ManyToAny;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "cart")
public class CartItem {



    @EmbeddedId
    private CartItemKey cartItemID;

    @Column
    private Integer amount;

    @Transient
    private float price;

    @Transient
    private Integer productID;

    @Transient
    private Integer sellerID;



    @JsonIgnoreProperties("cart")
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartUserID")
    @JoinColumn(name = "cartUserID", referencedColumnName = "userID")
    private User user;



    @JsonIgnoreProperties("cart")
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sellID")
    @JoinColumn(name = "sellID",referencedColumnName = "sellingID")
    private Sells sells;

    public CartItem() {

    }

    public CartItem(Integer amount,User user, Sells sells) {
        this.user = user;
        this.sells = sells;
    }

    public CartItemKey getCartItemID() {
        return cartItemID;
    }

    public void setCartItemID(CartItemKey cartItemID) {
        this.cartItemID = cartItemID;
    }
    @JsonIgnore
    public User getUser() {
        return user;
    }
    @JsonIgnore
    public void setUser(User user) {
        this.user = user;
    }

    public Integer getProductID() {
        return this.sells.getProductID();
    }

    public Integer getSellerID() {
        return this.sells.getSellerID();
    }

    public Integer getAmount() {
        return amount;
    }
    public Integer getPrice()
    {
        return this.sells.getPrice();
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    @JsonIgnore
    public Sells getSells() {
        return sells;
    }
    @JsonIgnore
    public void setSells(Sells sells) {
        this.sells = sells;
    }

}

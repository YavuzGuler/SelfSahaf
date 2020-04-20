package com.example.accessingdatamysql.models;

import com.example.accessingdatamysql.models.embeddedKey.PriceKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "price")
public class Price {

    @EmbeddedId
    private PriceKey priceID;




    @JsonIgnoreProperties("prices")
    @ManyToOne

    @JoinColumns(
            {
                    @JoinColumn(updatable=false,insertable=false, name="sellerID", referencedColumnName="sellerID"),
                    @JoinColumn(updatable=false,insertable=false, name="productID", referencedColumnName="productID"),
            })
    private Sells sells;



    private Integer price;


    @JsonIgnore
    public PriceKey getPriceID() {
        return priceID;
    }

    public void setPriceID(PriceKey priceID) {
        this.priceID = priceID;
    }
    public String getDate(){ return priceID.getDatetime();}


    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Sells getSells() {
        return sells;
    }

    public void setSells(Sells sells) {
        this.sells = sells;
    }


}
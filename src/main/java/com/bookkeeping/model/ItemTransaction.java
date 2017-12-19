package com.bookkeeping.model;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.utilities.ControllerUtilities;
import com.google.gson.Gson;

import java.math.BigDecimal;

/**
 * Created by chandanm on 8/12/16.
 */
public class ItemTransaction extends ModelObj{
    private float quantity;
    private String item;
    private float price;
    private BigDecimal amount;
    private String eventDate;
    private ItemTransactionType itemTransactionType;


    public ItemTransaction(String item, float quantity, float price,String eventDate) {

        this.quantity = ControllerUtilities.formatDecimalValue(quantity);
        this.item = item;
        this.price = ControllerUtilities.formatDecimalValue(price);
        this.amount = this.calculate().setScale(Constants.DECIMAL_SCALE, BigDecimal.ROUND_HALF_EVEN);
        //System.out.println(this.amount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
        this.eventDate=eventDate;
        this.setUniqueKey(item+ Constants.UNIQUE_KEY_SEPARATOR+eventDate);
    }

    public ItemTransactionType getItemTransactionType() {
        return itemTransactionType;
    }

    public void setItemTransactionType(ItemTransactionType itemTransactionType) {
        this.itemTransactionType = itemTransactionType;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static void main(String[] args) {
        //"quantity":4.56,"item":"toor","price":45006.32,
        ItemTransaction itemTransaction = new ItemTransaction("123",4.501f,45006.32f,"wqe");
        System.out.println(itemTransaction.toString());
    }

    private BigDecimal calculate() {

        return ControllerUtilities.formatDecimalValue(BigDecimal.valueOf(this.getPrice()).multiply(BigDecimal.valueOf(this.getQuantity())));

    }
}

package com.bookkeeping.model;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.utilities.ControllerUtilities;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by chandanm on 7/9/16.
 */
public class ItemSell extends ItemTransaction{

    private String transactionId; //InvoiceId for the KisaanTransactions, this is added
    private float bharti;
    private double netQuantity;

    public ItemSell(String item, float bharti, float quantity, float price, String eventDate) {
        super(item, quantity, price,eventDate);
        //Do amount calculation again in case we do negative quantity
        this.setAmount(BigDecimal.ZERO);
        if(quantity>0) {
            this.setAmount(BigDecimal.valueOf(quantity*price));
        }
        this.bharti = bharti;
        this.setItemTransactionType(ItemTransactionType.SELL);
        this.netQuantity = ControllerUtilities.formatDecimalValue(this.bharti * this.getQuantity());
        this.setNetQuantity(ControllerUtilities.formatDecimalValue(this.netQuantity));
        this.setAmount(ControllerUtilities.formatDecimalValue(this.getAmount()));
        this.setUniqueKey(item + Constants.UNIQUE_KEY_SEPARATOR+ this.getEventDate() + Constants.UNIQUE_KEY_SEPARATOR + this.get_id());

    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public float getBharti() {
        return bharti;
    }

    public void setBharti(float bharti) {
        this.bharti = bharti;
    }

    public double getNetQuantity() {
        return netQuantity;
    }

    public void setNetQuantity(double netQuantity) {
        this.netQuantity = netQuantity;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static void main(String[] args) {
        ItemSell itemSell = new ItemSell("d",0.9f,10,300,"2312");
        System.out.println(itemSell.toString());

        DecimalFormat df=new DecimalFormat("0.00");
        System.out.println("yo::"+ControllerUtilities.formatDecimalValue(251525.15));
    }
}

package com.bookkeeping.model;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.utilities.ControllerUtilities;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by chandan on 6/4/16.
 */
public class KisaanTransaction extends ModelObj {

    private String kisaan;
    private String khareeddar;
    private List<ItemSell> itemSold;
    private float hamaaliRate=Constants.HAMALI_RATE;
    private float mapariRate=Constants.MAPARI_RATE;
    private float cashSpecialRate=0f;

    private float brokerCommission=Constants.BROKERAGE_RATE;
    private double amount;
    private double amountKhareeddar;
    private double amountBrokerage;
    private double amountMapari;
    private double amountHamaali;
    private double amountCashSpecial;
    private String eventDate;
    private double totalAmount;
    private String paymentType;
    private double amountPaid;

    public KisaanTransaction(String kisaan,
                              String khareeddar,
                              String dt,
                              List<ItemSell> itemSells
    ) {
        super();
        this.kisaan = kisaan;
        this.khareeddar = khareeddar;
        this.itemSold = itemSells;
        this.setEventDate(dt);
        this.setUniqueKey(kisaan + Constants.UNIQUE_KEY_SEPARATOR + khareeddar + Constants.UNIQUE_KEY_SEPARATOR+ this.getEventDate() + Constants.UNIQUE_KEY_SEPARATOR +this.get_id());
        calculate();

    }



    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getAmountBrokerage() {
        return amountBrokerage;
    }

    public void setAmountBrokerage(double amountBrokerage) {
        this.amountBrokerage = amountBrokerage;
    }

    public double getAmountMapari() {
        return amountMapari;
    }

    public void setAmountMapari(double amountMapari) {
        this.amountMapari = amountMapari;
    }

    public double getAmountHamaali() {
        return amountHamaali;
    }

    public void setAmountHamaali(double amountHamaali) {
        this.amountHamaali = amountHamaali;
    }

    public double getAmountCashSpecial() {
        return amountCashSpecial;
    }

    public void setAmountCashSpecial(double amountCashSpecial) {
        this.amountCashSpecial = amountCashSpecial;
    }


    public double getAmountKhareeddar() {
        return amountKhareeddar;
    }

    public void setAmountKhareeddar(double amountKhareeddar) {
        this.amountKhareeddar = amountKhareeddar;
    }

    public float getBrokerCommission() {
        return brokerCommission;
    }

    public void setBrokerCommission(float brokerCommission) {
        this.brokerCommission = brokerCommission;
    }


    public float getHamaaliRate() {
        return hamaaliRate;
    }

    public void setHamaaliRate(float hamaaliRate) {
        this.hamaaliRate = hamaaliRate;
    }

    public float getMapariRate() {
        return mapariRate;
    }

    public void setMapariRate(float mapariRate) {
        this.mapariRate = mapariRate;
    }

    public float getCashSpecialRate() {
        return cashSpecialRate;
    }

    public void setCashSpecialRate(float cashSpecialRate) {
        this.cashSpecialRate = cashSpecialRate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getKisaan() {
        return kisaan;
    }

    public void setKisaan(String kisaan) {
        this.kisaan = kisaan;
    }

    public String getKhareeddar() {
        return khareeddar;
    }

    public void setKhareeddar(String khareeddar) {
        this.khareeddar = khareeddar;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void calculate() {
        this.totalAmount = 0;

        for(ItemSell itemSell : this.getItemSold()) {
            this.totalAmount += itemSell.getAmount().doubleValue();
        }
        this.amountMapari = ControllerUtilities.formatDecimalValue(getMapariAmount());
        this.amountHamaali =  ControllerUtilities.formatDecimalValue(getHamaaliAmount());
        this.amountKhareeddar = ControllerUtilities.formatDecimalValue(this.totalAmount-this.amountMapari);
        this.amountBrokerage = ControllerUtilities.formatDecimalValue(getBrokerageAmount());
        this.amountCashSpecial = ControllerUtilities.formatDecimalValue(getCashSpecialAmount());
        this.amount =  ControllerUtilities.formatDecimalValue(this.totalAmount - this.amountBrokerage - this.amountHamaali - this.amountMapari - this.amountCashSpecial);
    }


    private double getBrokerageAmount() {

        //return this.brokerCommission/100*this.totalAmount;

        BigDecimal bigDecimal = new BigDecimal(this.brokerCommission);
        bigDecimal = bigDecimal.divide(new BigDecimal(100)).multiply(BigDecimal.valueOf(this.totalAmount));
        return ControllerUtilities.formatDecimalValue(bigDecimal).doubleValue();
    }

    private double getCashSpecialAmount() {
        if(this.cashSpecialRate>0) {
            BigDecimal bigDecimal = new BigDecimal(this.cashSpecialRate);
            bigDecimal = bigDecimal.divide(new BigDecimal(100)).multiply(BigDecimal.valueOf(this.totalAmount));
            return ControllerUtilities.formatDecimalValue(bigDecimal).doubleValue();
            //return this.cashSpecialRate/100*this.totalAmount;
        }
        return 0d;
    }

    private double getMapariAmount() {

        double amt = 0;
        for(ItemSell itemSell: this.getItemSold()) {
            amt += this.mapariRate * (int)itemSell.getQuantity();
            float remaining = itemSell.getQuantity() - (int) itemSell.getQuantity();
            if (remaining > 0.5) {
                amt = amt + this.mapariRate;
            } else if(remaining>0){
                amt = amt + this.mapariRate / 2;
            }
        }
        return amt;
    }

    private double getHamaaliAmount() {
        double amt = 0;
        for(ItemSell itemSell: this.getItemSold()) {
            amt += this.hamaaliRate * itemSell.getQuantity();
        }
        return amt;
    }

    public List<ItemSell> getItemSold() {
        return itemSold;
    }

    public void setItemSold(List<ItemSell> itemSold) {
        this.itemSold = itemSold;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

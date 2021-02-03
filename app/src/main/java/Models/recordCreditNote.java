package Models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class recordCreditNote {
    private String id;
    private String date;
    private String lastcreditnotenumber;
    private String creditnotenumber;
    private String customerid;
    private String customername;
    private String creditnotenet;
    private String vat;
    private String creditnotetotal;
    private String note;
    private String user;
    private int status;

    private String vattext;
    private double vatval;
    private int vatbutton;

    public recordCreditNote() {
    }

    public recordCreditNote(String id, String date, String lastcreditnotenumber, String creditnotenumber, String customerid, String customername, String creditnotenet, String vat, String creditnotetotal, String note, String user, int status, String vattext, double vatval, int vatbutton) {
        this.id = id;
        this.date = date;
        this.lastcreditnotenumber = lastcreditnotenumber;
        this.creditnotenumber = creditnotenumber;
        this.customerid = customerid;
        this.customername = customername;
        this.creditnotenet = creditnotenet;
        this.vat = vat;
        this.creditnotetotal = creditnotetotal;
        this.note = note;
        this.user = user;
        this.status = status;
        this.vattext = vattext;
        this.vatval = vatval;
        this.vatbutton = vatbutton;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getLastcreditnotenumber() {
        return lastcreditnotenumber;
    }

    public String getCreditnotenumber() {
        return creditnotenumber;
    }

    public String getCustomerid() {
        return customerid;
    }

    public String getCustomername() {
        return customername;
    }

    public String getCreditnotenet() {
        return creditnotenet;
    }

    public String getVat() {
        return vat;
    }

    public String getCreditnotetotal() {
        return creditnotetotal;
    }

    public String getNote() {
        return note;
    }

    public String getUser() {
        return user;
    }

    public int getStatus() {
        return status;
    }

    public String getVattext() {
        return vattext;
    }

    public double getVatval() {
        return vatval;
    }

    public int getVatbutton() {
        return vatbutton;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLastcreditnotenumber(String lastcreditnotenumber) {
        this.lastcreditnotenumber = lastcreditnotenumber;
    }

    public void setCreditnotenumber(String creditnotenumber) {
        this.creditnotenumber = creditnotenumber;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public void setCreditnotenet(String creditnotenet) {
        this.creditnotenet = creditnotenet;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public void setCreditnotetotal(String creditnotetotal) {
        this.creditnotetotal = creditnotetotal;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setVattext(String vattext) {
        this.vattext = vattext;
    }

    public void setVatval(double vatval) {
        this.vatval = vatval;
    }

    public void setVatbutton(int vatbutton) {
        this.vatbutton = vatbutton;
    }
}

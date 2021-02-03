package Models;

public class addInvoice {
    private String id;
    private String date;
    private String lastinvoiceno;
    private String invoicenuber;
    private String customerid;
    private String customername;
    private String invoicenet;
    private String vat;
    private String invoicetotal;
    private String categoryid;
    private String categoryname;
    private String subcategoryid;
    private String subcategoryname;
    private String note;
    private String user;
    private int status;

    private String vattext;
    private double vatval;
    private int vatbutton;

    public addInvoice() {
    }

    public addInvoice(String id, String date, String lastinvoiceno, String invoicenuber, String customerid, String customername, String invoicenet, String vat, String invoicetotal, String categoryid, String categoryname, String subcategoryid, String subcategoryname, String note, String user, int status, String vattext, double vatval, int vatbutton) {
        this.id = id;
        this.date = date;
        this.lastinvoiceno = lastinvoiceno;
        this.invoicenuber = invoicenuber;
        this.customerid = customerid;
        this.customername = customername;
        this.invoicenet = invoicenet;
        this.vat = vat;
        this.invoicetotal = invoicetotal;
        this.categoryid = categoryid;
        this.categoryname = categoryname;
        this.subcategoryid = subcategoryid;
        this.subcategoryname = subcategoryname;
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

    public String getLastinvoiceno() {
        return lastinvoiceno;
    }

    public String getInvoicenuber() {
        return invoicenuber;
    }

    public String getCustomerid() {
        return customerid;
    }

    public String getCustomername() {
        return customername;
    }

    public String getInvoicenet() {
        return invoicenet;
    }

    public String getVat() {
        return vat;
    }

    public String getInvoicetotal() {
        return invoicetotal;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public String getSubcategoryid() {
        return subcategoryid;
    }

    public String getSubcategoryname() {
        return subcategoryname;
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

    public void setLastinvoiceno(String lastinvoiceno) {
        this.lastinvoiceno = lastinvoiceno;
    }

    public void setInvoicenuber(String invoicenuber) {
        this.invoicenuber = invoicenuber;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public void setInvoicenet(String invoicenet) {
        this.invoicenet = invoicenet;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public void setInvoicetotal(String invoicetotal) {
        this.invoicetotal = invoicetotal;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public void setSubcategoryid(String subcategoryid) {
        this.subcategoryid = subcategoryid;
    }

    public void setSubcategoryname(String subcategoryname) {
        this.subcategoryname = subcategoryname;
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

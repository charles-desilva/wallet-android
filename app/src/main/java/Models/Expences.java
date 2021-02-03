package Models;

public class Expences {
    private String id;
    private String date1;
    private String accountid;
    private String accountname;
    private String categoryid;
    private String categoryname;
    private String subcategoryid;
    private String subcategoryname;
    private String invoicetotal;
    private String vat;
    private String invoicenet;
    private String internalref;
    private String supplierName;
    private String date2;
    private String supplierinvno;
    private String note;
    private String path;
    private int status;
    private String user;

    private String vattext;
    private double vatval;
    private int vatbutton;

    public Expences() {
    }

    public Expences(String id, String date1, String accountid, String accountname, String categoryid, String categoryname, String subcategoryid, String subcategoryname, String invoicetotal, String vat, String invoicenet, String internalref, String supplierName, String date2, String supplierinvno, String note, String path, int status, String user, String vattext, double vatval, int vatbutton) {
        this.id = id;
        this.date1 = date1;
        this.accountid = accountid;
        this.accountname = accountname;
        this.categoryid = categoryid;
        this.categoryname = categoryname;
        this.subcategoryid = subcategoryid;
        this.subcategoryname = subcategoryname;
        this.invoicetotal = invoicetotal;
        this.vat = vat;
        this.invoicenet = invoicenet;
        this.internalref = internalref;
        this.supplierName = supplierName;
        this.date2 = date2;
        this.supplierinvno = supplierinvno;
        this.note = note;
        this.path = path;
        this.status = status;
        this.user = user;
        this.vattext = vattext;
        this.vatval = vatval;
        this.vatbutton = vatbutton;
    }

    public String getId() {
        return id;
    }

    public String getDate1() {
        return date1;
    }

    public String getAccountid() {
        return accountid;
    }

    public String getAccountname() {
        return accountname;
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

    public String getInvoicetotal() {
        return invoicetotal;
    }

    public String getVat() {
        return vat;
    }

    public String getInvoicenet() {
        return invoicenet;
    }

    public String getInternalref() {
        return internalref;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getDate2() {
        return date2;
    }

    public String getSupplierinvno() {
        return supplierinvno;
    }

    public String getNote() {
        return note;
    }

    public String getPath() {
        return path;
    }

    public int getStatus() {
        return status;
    }

    public String getUser() {
        return user;
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

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
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

    public void setInvoicetotal(String invoicetotal) {
        this.invoicetotal = invoicetotal;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public void setInvoicenet(String invoicenet) {
        this.invoicenet = invoicenet;
    }

    public void setInternalref(String internalref) {
        this.internalref = internalref;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public void setSupplierinvno(String supplierinvno) {
        this.supplierinvno = supplierinvno;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUser(String user) {
        this.user = user;
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

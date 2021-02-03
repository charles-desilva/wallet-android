package Stables;

import java.util.ArrayList;

public class Reports {
    ArrayList<Integer> idlist=new ArrayList();
    ArrayList <String> namelist=new ArrayList();

    public Reports() {
        idlist.add(0);
        idlist.add(1);
        idlist.add(2);
        idlist.add(3);
        idlist.add(4);
        idlist.add(5);

        namelist.add("Outflow");
        namelist.add("Inflow");
        namelist.add("Invoice Rendered");
        namelist.add("Credit Note Issued");
        namelist.add("Bank Reconcilation");
        namelist.add("Vat Returns");
    }

    public ArrayList<Integer> getVatSettingsId(){
        return idlist;
    }

    public ArrayList<String> getVatSettingsNames(){
        return namelist;
    }
}

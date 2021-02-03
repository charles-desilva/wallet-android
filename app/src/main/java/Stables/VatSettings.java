package Stables;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VatSettings {
    ArrayList <Integer> idlist=new ArrayList();
    ArrayList <String> namelist=new ArrayList();

    public VatSettings() {
        idlist.add(0);
        idlist.add(1);

        namelist.add("Flat Rate Scheme VAT Returns");
        namelist.add("Standard VAT Returns");
    }

    public ArrayList<Integer> getVatSettingsId(){
        return idlist;
    }

    public ArrayList<String> getVatSettingsNames(){
        return namelist;
    }
}

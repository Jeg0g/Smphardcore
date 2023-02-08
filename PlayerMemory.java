package me.jeg0g.smphardcore;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerMemory {
    private ArrayList<String> storageCords= new ArrayList<String>();
    public String[] getStorageCords(){
        String[] temp= new String[storageCords.size()];
        temp=storageCords.toArray(temp);
        return temp;
    }
    public ArrayList<String> getALStorageCords(){
        return storageCords;
    }
    public String getStringCords(){
        String outstr = "";
        for (String item:storageCords){
            outstr+=(item+",");
        }
        return outstr;
    }
    public void setStringCords(String str){
        String[] strlst = str.split(",");
        storageCords = new ArrayList<String>(Arrays.asList(strlst));
    }
    public void addStorageCord(String cords){
        storageCords.add(cords);
    }
    public void removeStorageCord(String cord){
        storageCords.remove(cord);
    }
    public void setStorageCords(ArrayList<String> cords){
        storageCords=cords;
    }
}

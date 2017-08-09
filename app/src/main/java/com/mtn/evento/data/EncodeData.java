package com.mtn.evento.data;

import java.util.HashMap;

/**
 * Created by user on 8/4/2017.
 */

public class EncodeData {
    private static EncodeData encodeData;
    private static HashMap<String,String>  hasher = new HashMap<String, String>() ;
    public static EncodeData getInstance(){

        if(encodeData != null){
            return encodeData;
        }
        else
        {
            hasher.put("A","K");hasher.put("a","k");
            hasher.put("B","V");hasher.put("b","v");
            hasher.put("C","W");hasher.put("c","w");
            hasher.put("D","X");hasher.put("d","x");
            hasher.put("E","Y");hasher.put("e","y");
            hasher.put("F","Z");hasher.put("f","z");
            hasher.put("G","Q");hasher.put("g","q");
            hasher.put("H","R");hasher.put("h","r");
            hasher.put("I","S");hasher.put("i","s");
            hasher.put("J","T");hasher.put("j","t");
            hasher.put("K","U");hasher.put("k","u");
            hasher.put("L","A");hasher.put("l","a");
            hasher.put("M","B");hasher.put("m","b");
            hasher.put("N","C");hasher.put("n","c");
            hasher.put("O","D");hasher.put("o","d");
            hasher.put("P","E");hasher.put("p","e");
            hasher.put("Q","F");hasher.put("q","f");
            hasher.put("R","G");hasher.put("r","g");
            hasher.put("S","H");hasher.put("s","h");
            hasher.put("T","I");hasher.put("t","i");
            hasher.put("U","J");hasher.put("u","j");
            hasher.put("V","L");hasher.put("v","l");
            hasher.put("W","M");hasher.put("w","m");
            hasher.put("X","N");hasher.put("x","n");
            hasher.put("Y","O");hasher.put("y","o");
            hasher.put("Z","P");hasher.put("z","p");
            hasher.put("0","9");hasher.put("1","4");
            hasher.put("2","7");hasher.put("3","8");
            hasher.put("4","2");hasher.put("5","1");
            hasher.put("6","0");hasher.put("7","3");
            hasher.put("8","5");hasher.put("9","6");
            hasher.put(" ","__");
            return encodeData;
        }
    }
    public static String encode(String dataToEncode){
       String encodingData= "";
        for (int i = 0; i <  dataToEncode.length(); i++) {

            char c = dataToEncode.charAt(i);
           if(hasher.containsKey(""+c)){
               encodingData +=  hasher.get(""+c);
           }
           else
           {
               encodingData +=  c;
           }

        }
        return encodingData;
    }
}

package collections;

import java.util.HashMap;
import java.util.Iterator;

/*
put()	Adds or updates a key-value pair
get()	Returns the value for a given key
containsKey()	Checks if the map contains the key
 */
public class map {
    public static HashMap<String , Integer> filetypes = new HashMap<>();
    public static void mapinto(String s){
        s = s.toUpperCase();
        if ((s.equals("PNG"))||(s.equals("JPG"))||s.equals("JPEG")) {s = "IMAGE";}
        switch(s){
            case "PDF" ->filetypes.put("PDF",filetypes.getOrDefault("PDF",0)+1);
            case "TXT" ->filetypes.put("Text",filetypes.getOrDefault("Text",0)+1);
            case "DOCX" ->filetypes.put("Docx",filetypes.getOrDefault("Docx",0)+1);
            case "GIF" ->filetypes.put("Gif",filetypes.getOrDefault("Gif",0)+1);
            case "IMAGE"->filetypes.put("Image",filetypes.getOrDefault("Image",0)+1);
            default->filetypes.put("Others",filetypes.getOrDefault("Others",0)+1); //if new type , put in others 
        }
    }
    public static void mapinto(String s,String type){// decreases count if atleast one file of type is present
        if (type.equals("del")){
            s = s.toUpperCase();
            if ((filetypes.containsKey(s))&&(filetypes.get(s)>1)){
                filetypes.put(s,filetypes.get(s)-1);
            }
            else{
                filetypes.remove(s);
                System.out.println("Removed this file ");
            }
        }
    }
    public static String getfiltype(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) { // Ensure dot exists and is not at beginning or end
            return filename.substring(dotIndex + 1);
        }
        return ""; 
    }
    public static void dispkeys(){
        Iterator<String> it = filetypes.keySet().iterator();
        String key;
        while(it.hasNext()){
            key = it.next();
            if(!key.equals("Others")){
                System.out.print(key+", ");
            }
        }
        if(filetypes.getOrDefault("Others",0)>0){
            System.out.println("\n-found "+filetypes.get("Others")+" File(s) of unrecognized type");}
    }
}

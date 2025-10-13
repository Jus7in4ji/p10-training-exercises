
import java.util.HashMap;

/*
put()	Adds or updates a key-value pair
get()	Returns the value for a given key
containsKey()	Checks if the map contains the key
 */
public class map {
    public static HashMap<String , Integer> filetypes = new HashMap<>();
    public static void insert(String s){
        s = s.toUpperCase();
        if (filetypes.containsKey(s)){ // increases count of types present
            filetypes.put(s,filetypes.get(s)+1);
        }
        else{
            filetypes.put("Others",filetypes.get("Others")+1); //if new type , put in others 
        }
    }
    public static void rem(String s){// decreases count if atleast one file of type is present
        s = s.toUpperCase();
        if ((filetypes.containsKey(s))&&(filetypes.get(s)>0)){
            filetypes.put(s,filetypes.get(s)-1);
        }
        else{
            System.out.println("No files of this type present");
        }
    }
}

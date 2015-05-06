import java.util.ArrayList;

public class Parser {
    public static ArrayList<String> explode(String line) {
        ArrayList<String> array = new ArrayList<String>();
        int indexOf, fromIndex = 0;
        while ((indexOf = line.indexOf(',', fromIndex)) != -1) {
            array.add(line.substring(fromIndex, indexOf));
            fromIndex = indexOf+1;
        }
        array.add(line.substring(fromIndex));
        return array;
    }
    
    public static void print(ArrayList<String> array) {
        int arraySize = array.size();
        for(int i=0; i<arraySize; i++) {
            System.out.println(i+". "+array.get(i));
        }
    }
}

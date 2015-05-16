import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
 
public class CSVReader { 
    public static ArrayList<Slice> extract(String csvFile) {
        BufferedReader br = null;
        String line = "", csvSplitBy = "\\t";
        ArrayList<Slice> slices = new ArrayList<Slice> ();
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(",",".");
                String[] slicesStr = line.split(csvSplitBy);
                Slice slice = new Slice(slicesStr[0], slicesStr[1], slicesStr[2], slicesStr[3]);
                //slice.print();
                slices.add(slice);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return slices;
    }
}
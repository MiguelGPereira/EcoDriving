import java.util.ArrayList;

public class ServiceOptimizer {
    
    protected static String slicesFilePath = "./slices.csv";

    protected static String stationsFilePath = "C:\\Users\\Sergio Esteves\\IdeaProjects\\EcoDrivingEdu\\src\\times.csv";
 
    public static void main(String[] args)
    {
        //Slices slices = new Slices(CSVReader.extract(slicesFilePath));
        ArrayList<Station> stations = CSVReader.extractStations(stationsFilePath);
        
        /*slices.enforceTopSpeeds();
        slices.addAccelerationSlices();
        //slices.print();
        
        slices.calculate();
        //slices.print();
        
        slices.enforceTime(240);
        //slices.print();*/
    }
}

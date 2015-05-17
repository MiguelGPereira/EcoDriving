
import java.util.ArrayList;

public class ServiceOptimizer {
    
    protected static String slicesFilePath = "C:\\Users\\eduardextreme\\NetBeansWorkspace\\LGPAlpha\\build\\classes\\slicesComplete.csv";
    protected static String stationsFilePath = "C:\\Users\\eduardextreme\\NetBeansWorkspace\\LGPAlpha\\build\\classes\\stations.csv";
 
    public static void main(String[] args)
    {
        Slices slices = new Slices(CSVReader.extractSlices(slicesFilePath));
        ArrayList<Station> stations = CSVReader.extractStations(stationsFilePath);
        
        /*ArrayList<ArrayList<Slice> > tripsArr = slicer(slices, stations);
        ArrayList<Slices> trips = new ArrayList<Slices>();
        int numTrips = tripsArr.size();
        for (int i=0; i<numTrips; i++) {
            int targetTime = stations.get(i+1).getTimeArrival() - stations.get(i).getTimeDeparture();
            System.out.println("Target time: "+targetTime);
            trips.add(new Slices(tripsArr.get(i)));
            trips.get(i).enforceTime(targetTime);
        }*/
        
        //System.out.println("\n\nWITH SLICE FUSION:\n\n");
        
        ArrayList<ArrayList<Slice> > tripsArr = slicer(slices, stations);
        ArrayList<Slices> trips = new ArrayList<Slices>();
        int numTrips = tripsArr.size();
        for (int i=0; i<numTrips; i++) {
            int targetTime = stations.get(i+1).getTimeArrival() - stations.get(i).getTimeDeparture();
            //System.out.println("Target time: "+targetTime);
            trips.add(new Slices(tripsArr.get(i)));
            trips.get(i).fuse();
            trips.get(i).enforceTime(targetTime);
        }
        
        Print.exportResult(trips);
    }
    
    public static ArrayList<ArrayList<Slice> > slicer(Slices slicesObj, ArrayList<Station> stations) {
        ArrayList<Slice> slices = slicesObj.getKeepSlices();
        int numSlices = slices.size();
        
        int numStations = stations.size();
        
        ArrayList<ArrayList<Slice> > trips = new ArrayList<ArrayList<Slice> >();
        trips.add(new ArrayList<Slice>());
        int stationNum=1;
        for (int i=0; i<numSlices; i++) {
            trips.get(stationNum-1).add(slices.get(i));
            if(slices.get(i).getEndPointKm() == stations.get(stationNum).getPK()) {
                trips.add(new ArrayList<Slice>());
                stationNum++;
            }
        }
        
        trips.remove(stationNum-1);
        
        // Print trips
        /*for (int i=0; i<trips.size(); i++) {
            System.out.println("Trip "+i);
            for (int j=0; j<trips.get(i).size(); j++) {
                trips.get(i).get(j).printSimple();
            }
        }*/
        
        return trips;
    }
}

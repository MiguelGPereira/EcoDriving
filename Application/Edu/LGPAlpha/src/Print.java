import java.util.ArrayList;

public class Print {
    public static void printInt(int num, String label) {
        System.out.printf(label + ":%d", num);
    }
    
    public static void printDouble(double num, String label) {
        System.out.printf(label + ":");
        if (num>=0)
            System.out.print(" ");
        System.out.printf("%.4f", num);
    }
    
    public static void exportResult(ArrayList<Slices> trips, ArrayList<Station> stations) {
        int numTrips = trips.size();
        for (int i=0; i<numTrips; i++) {
            trips.get(i).exportResult(stations.get(i).getStationName(), stations.get(i+1).getStationName());
        }
    }
}

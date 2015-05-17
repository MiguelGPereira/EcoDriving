public class Station {
    protected String stationName; // m
    protected int timeArrival; // s
    protected int timeDeparture; // s
    protected double pk;

    public Station(String stationName, String timeArrival, String timeDeparture, String pk)
    {
        this.stationName = stationName;
        this.timeArrival = Integer.parseInt(timeArrival);
        this.timeDeparture = Integer.parseInt(timeDeparture);
        this.pk = Double.parseDouble(pk);
    }

    public int getTimeArrival() {
        return timeArrival;
    }

    public void setTimeArrival(int timeArrival) {
        this.timeArrival = timeArrival;
    }

    public int getTimeDeparture() {
        return timeDeparture;
    }

    public void setTimeDeparture(int timeDeparture) {
        this.timeDeparture = timeDeparture;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    double getPK() {
        return pk;
    }

    public void print() {
        System.out.println("Station: " + stationName+ " Arrival: " + timeArrival + " Departure: " + timeDeparture + " PK: " + pk);
    }
}

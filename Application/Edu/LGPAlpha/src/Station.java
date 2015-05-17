/**
 * Created by Sergio Esteves on 5/17/2015.
 */
public class Station {
    protected String stationName; // m
    protected int timeArrival; //chegada s
    protected int timeDeparture; //partida s
    protected double PK;

    public Station(String stationName, String timeArrival, String timeDeparture, String PK)
    {
        this.stationName = stationName;
        this.timeArrival = Integer.parseInt(timeArrival);
        this.timeDeparture = Integer.parseInt(timeDeparture);
        this.PK = Double.parseDouble(PK);
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

    public void print()
    {
        System.out.println(stationName+ " " + timeArrival + " " + timeDeparture + " " + PK);
    }
}

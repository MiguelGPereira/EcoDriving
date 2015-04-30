import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LogLine {
    static final String DATE_FORMAT = "yyyyMMdd-HHmmss";
    
    Date date;
    int train, driver, absorbed, recovered, kms, speed, weight, 
        twentyMeterCounter, totalForce, tractionEffort, brakeEffortEP, 
        brakeEffortP, forceTC31, forceTC32, Offset, LineVoltage;
    boolean doorsRel;
    String status;
    float InputCurrent;

    public LogLine(String logLine) throws ParseException {
        ArrayList<String> array = Parser.explode(logLine);
        date = new SimpleDateFormat(DATE_FORMAT).parse(array.get(0));
        train = Integer.parseInt(array.get(1));
        driver = Integer.parseInt(array.get(2));
        absorbed = Integer.parseInt(array.get(3));
        recovered = Integer.parseInt(array.get(4));
        kms = Integer.parseInt(array.get(5));
        doorsRel = ("1".equals(array.get(6)));
        speed = Integer.parseInt(array.get(7));
        weight = Integer.parseInt(array.get(8));
        twentyMeterCounter = Integer.parseInt(array.get(9));
        totalForce = Integer.parseInt(array.get(10));
        tractionEffort = Integer.parseInt(array.get(11));
        brakeEffortEP = Integer.parseInt(array.get(12));
        brakeEffortP = Integer.parseInt(array.get(13));
        forceTC31 = Integer.parseInt(array.get(14));
        forceTC32 = Integer.parseInt(array.get(15));
        status = array.get(16);
        Offset = Integer.parseInt(array.get(17));
        LineVoltage = Integer.parseInt(array.get(18));
        InputCurrent = Float.parseFloat(array.get(19));
    }
    
    public void print() {
        System.out.println("Date: "+date.toString());
        System.out.println("Train Number: "+train);
        System.out.println("Driver Number: "+driver);
        System.out.println("Energy Absorbed: "+absorbed+" kWh");
        System.out.println("Energy Recovered: "+recovered+" kWh");
        System.out.println("Kilometers: "+kms+" kms");
        System.out.println("Doors Released: "+(doorsRel ? "True" : "False"));
        System.out.println("Speed: "+speed+" km/h");
        System.out.println("Weight: "+weight+" t");
        System.out.println("20m Counter: "+twentyMeterCounter+" ("+(twentyMeterCounter*20)+" m)");
        System.out.println("Total Force: "+totalForce+" %");
        System.out.println("Traction Effort: "+tractionEffort+" %");
        System.out.println("Electrical and Pneumatic Brake Effort: "+brakeEffortEP+" %");
        System.out.println("Pneumatic Brake Effort: "+brakeEffortP+" %");
        System.out.println("Force From Traction Converter 1: "+forceTC31+" kN");
        System.out.println("Force From Traction Converter 2: "+forceTC32+" kN");
        System.out.println("Train Status: "+status);
        System.out.println("Station Offset: "+Offset+"' station");
        System.out.println("Line Voltage: "+LineVoltage+" V");
        System.out.println("Input Current: "+InputCurrent+" A");
    }
}

public final class Slice implements Comparable<Slice> {
    protected double originalStartPointKm, originalEndPointKm; // km, km
    
    protected double startPointKm, endPointKm, gradient; // km, km, ‰
    protected int speedLimit; // km/h
    protected boolean type; // true - acceleration slice, false - keep slice
    
    protected double startPoint, endPoint, distance; // m
    protected double startSpeed, endSpeed, averageSpeed; // m/s
    protected double startTime, endTime; // s
    protected double startSpeedKmh, endSpeedKmh; // km/h
    protected double timeNeeded; // s (Keep: Distance to time; Acceleration: Distance to accelerate)
    
    final protected static double acceleration = 1; // m/s²
    final protected static double accelerationKmh2 = Conversion.ms2ToKmh2(acceleration);
    final protected int minimumSpeedKmh = 0; // km/h
    final protected int maximumSpeedKmh = 140; // km/h
    
    protected int invades;
    
    public double maxPossibleSpeedKmh(double distanceKm) {
        if (invadesNext()) {
            return Math.sqrt((startSpeedKmh*startSpeedKmh)+(2*accelerationKmh2*distanceKm)); // vf = sqrt(vi^2+2ad)
        } else if (invadesPrevious()) {
            return Math.sqrt((endSpeedKmh*endSpeedKmh)-(2*accelerationKmh2*distanceKm)); // vi = sqrt(vf^2-2ad)
        }
        return Conversion.kmhToMs(averageSpeed);
    }
    
    public boolean isAccelerating() {
        return (endSpeedKmh>startSpeedKmh);
    }
    
    public boolean isBraking() {
        return (endSpeedKmh<startSpeedKmh);
    }
    
    public boolean invadesNext() {
        return (invades==1);
    }
    
    public boolean invadesPrevious() {
        return (invades==-1);
    }
    
    public int getInvades() {
        return invades;
    }
    
    public void setInvadesNext() {
        invades = 1;
    }
    
    public void setInvadesPrevious() {
        invades = -1;
    }
    
    public static double getAcceleration() {
        return acceleration;
    }

    public void resetExceptSpeeds() {
        startPointKm = originalStartPointKm;
        endPointKm = originalEndPointKm;
        
        startPoint = startPointKm*1000;
        endPoint = endPointKm*1000;
        distance = endPoint - startPoint;
        
        startTime = 0;
        endTime = 0;
        timeNeeded = 0;
    }

    public void reset() {
        startPointKm = originalStartPointKm;
        endPointKm = originalEndPointKm;
        startAuxiliaryVariables();
    }

    public Slice clone() {
        Slice slice = new Slice(this.startPointKm, this.endPointKm, this.gradient, this.speedLimit, this.type);
        slice.originalStartPointKm = this.originalStartPointKm;
        slice.originalEndPointKm = this.originalEndPointKm;
        slice.startPointKm = this.startPointKm;
        slice.endPointKm = this.endPointKm;
        slice.gradient = this.gradient;
        slice.speedLimit = this.speedLimit;
        slice.type = this.type;
        slice.startPoint = this.startPoint;
        slice.endPoint = this.endPoint;
        slice.distance = this.distance;
        slice.startSpeed = this.startSpeed;
        slice.endSpeed = this.endSpeed;
        slice.averageSpeed = this.averageSpeed;
        slice.startTime = this.startTime;
        slice.endTime = this.endTime;
        slice.startSpeedKmh = this.startSpeedKmh;
        slice.endSpeedKmh = this.endSpeedKmh;
        slice.timeNeeded = this.timeNeeded;
        return slice;
        //return new Slice(this.startPointKm, this.endPointKm, this.gradient, this.speedLimit, this.type);
    }

    @Override
    public int compareTo(Slice otherSlice) {
        double result = this.gradient - otherSlice.getGradient();
        if (result>0)
            return -1;
        else if (result<0)
            return 1;
        
        result = this.distance - otherSlice.getDistance();
        if (result>0)
            return 1;
        else if (result<0)
            return -1;
        else
            return 0;
    }
    
    public Slice(String startPointKm, String endPointKm, String gradient, String speedLimit) {
        this.startPointKm = Double.parseDouble(startPointKm);
        this.originalStartPointKm = this.startPointKm;
        this.endPointKm = Double.parseDouble(endPointKm);
        this.originalEndPointKm = this.endPointKm;
        this.gradient = Double.parseDouble(gradient);
        this.speedLimit = Integer.parseInt(speedLimit);
        this.type = false;
        startAuxiliaryVariables();
    }

    public Slice(double startPointKm, double endPointKm, double gradient, int speedLimit, boolean type) {
        this.startPointKm = startPointKm;
        this.originalStartPointKm = this.startPointKm;
        this.endPointKm = endPointKm;
        this.originalEndPointKm = this.endPointKm;
        this.gradient = gradient;
        this.speedLimit = speedLimit;
        this.type = type;
        startAuxiliaryVariables();
    }
    
    protected void startAuxiliaryVariables() {
        invades = 0;
        startPoint = startPointKm*1000;
        endPoint = endPointKm*1000;
        distance = endPoint - startPoint;
        startSpeedKmh = 0;
        endSpeedKmh = 0;
        startSpeed = 0;
        endSpeed = 0;
        averageSpeed = 0;
        startTime = 0;
        endTime = 0;
        timeNeeded = 0;
    }

    public void editStartTime(double startTime) {
        this.startTime = startTime;
        endTime = startTime + timeNeeded;
    }
    
    public void addSliceInfo(double gradient, int speedLimit) {
        this.gradient = gradient;
        this.speedLimit = speedLimit;
    }
    
    public void calculateTimeNeeded() {
        if (type) { // Acceleration: Distance to accelerate
            timeNeeded = Math.abs(endSpeed-startSpeed) / acceleration;
            endTime = startTime + timeNeeded;
            distance = timeNeeded * averageSpeed;
            /*endPoint = startPoint + distance;
            endPointKm = Conversion.msToKmh(endPoint);*/
        } else { // Keep: Distance to time
            timeNeeded = distance / averageSpeed;
            endTime = startTime + timeNeeded;
        }
    }
    
    public void changeStartPointKmKeepingDistance(double startPointKm) {
        if (!type)
            return;
        this.startPointKm = startPointKm;
        startPoint = startPointKm*1000;
        endPoint = startPoint + distance;
        endPointKm = endPoint/1000;
    }
    
    public void changeEndPointKmKeepingDistance(double endPointKm) {
        if (!type)
            return;
        this.endPointKm = endPointKm;
        endPoint = endPointKm*1000;
        startPoint = endPoint - distance;
        startPointKm = startPoint/1000;
    }
    
    public void changeStartPointKm(double startPointKm) {
        this.startPointKm = startPointKm;
        startPoint = startPointKm*1000;
        calculateDistance();
    }
    
    public void changeEndPointKm(double endPointKm) {
        this.endPointKm = endPointKm;
        endPoint = endPointKm*1000;
        calculateDistance();
    }
    
    public void calculateDistance() {
        distance = endPoint - startPoint;
        if (!type && averageSpeed>0) { // Recalculate time
            timeNeeded = distance / averageSpeed;
            endTime = startTime + timeNeeded;
        }
    }
    
    public void changeStartSpeedKmh(double startSpeedKmh) {
        this.startSpeedKmh = startSpeedKmh;
        startSpeed = Conversion.kmhToMs(startSpeedKmh);
        calculateAverageSpeed();
    }
    
    public void changeEndSpeedKmh(double endSpeedKmh) {
        this.endSpeedKmh = endSpeedKmh;
        endSpeed = Conversion.kmhToMs(endSpeedKmh);
        calculateAverageSpeed();
    }
    
    public void calculateAverageSpeed() {
        averageSpeed = (startSpeed+endSpeed)/2;
    }
    
    public void setSpeed(double speed) {
        changeStartSpeedKmh(speed);
        changeEndSpeedKmh(speed);
    }
    
    public void enforceTopSpeed() {
        setSpeed(speedLimit);
    }
    
    public boolean decrementSpeed() {
        double speed = startSpeedKmh-1;
        if (speed < minimumSpeedKmh)
            return false;
        setSpeed(speed);
        return true;
    }
    
    public void print() {
        System.out.print("Type: ");
        if (type)
            System.out.print("Acceleration");
        else
            System.out.print("Keep        ");
        
        if (invades==1)
            System.out.print("Invades Next");
        else if (invades==-1)
            System.out.print("Invades Previous");
        
        Print.printDouble(originalStartPointKm, "  Original Start Point (km)");
        Print.printDouble(originalEndPointKm, "  Original End Point (km)");
        
            
        
        Print.printDouble(startPointKm, "  Start Point (km)");
        Print.printDouble(endPointKm, "  End Point (km)");
        Print.printDouble(gradient, "  Gradient (‰)");
        Print.printInt(speedLimit, "  Speed Limit (km/h)");
        System.out.print("\n");
        
        Print.printDouble(startPoint, "   Start Point (m)");
        Print.printDouble(endPoint, "  End Point (m)");
        Print.printDouble(distance, "  Distance (m)");
        
        Print.printDouble(startSpeedKmh, "  Start Speed (km/h)");
        Print.printDouble(endSpeedKmh, "  End Speed (km/h)");
        
        Print.printDouble(startSpeed, "  Start Speed (m/s)");
        Print.printDouble(endSpeed, "  End Speed (m/s)");
        Print.printDouble(averageSpeed, "  Average Speed (m/s)");
        
        Print.printDouble(startTime, "  Start Time (s)");
        Print.printDouble(endTime, "  End Time (s)");
        Print.printDouble(timeNeeded, "  Time Needed (s)");
        System.out.print("\n\n");
    }
    
    public void printSimple() {
        System.out.print("Type: ");
        if (type)
            System.out.print("Acceleration");
        else
            System.out.print("Keep        ");
        
        Print.printDouble(startPointKm, "  Start Point (km)");
        Print.printDouble(endPointKm, "  End Point (km)");
        Print.printDouble(gradient, "  Gradient (‰)");
        Print.printInt(speedLimit, "  Speed Limit (km/h)");
        System.out.print("\n");
    }
    
    public double getHighestSpeed() {
        return Math.max(startSpeedKmh, endSpeedKmh);
    }
    
    public double getStartPointKm() {
        return startPointKm;
    }

    public double getEndPointKm() {
        return endPointKm;
    }

    public double getGradient() {
        return gradient;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public double getDistance() {
        return distance;
    }

    public double getDistanceKm() {
        return (endPointKm-startPointKm);
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public double getStartSpeedKmh() {
        return startSpeedKmh;
    }

    public double getEndSpeedKmh() {
        return endSpeedKmh;
    }

    public double getOriginalStartPointKm() {
        return originalStartPointKm;
    }

    public double getOriginalEndPointKm() {
        return originalEndPointKm;
    }
    
    public boolean isAcceleration() {
        return type;
    }
    
    public boolean isKeep() {
        return !type;
    }

    public void exportResult() {
        if (type) {
            double startPointAux = startPoint;
            if (startSpeedKmh<endSpeedKmh) { // ACCELERATING
                for(double i=startSpeedKmh; i<endSpeedKmh; i=i+5) {
                    double speedDifference = Math.min(endSpeedKmh-i,5);
                    double timeNeededAux = Conversion.kmhToMs(speedDifference) / acceleration;
                    double averageSpeedAux = (Conversion.kmhToMs(i)+Conversion.kmhToMs(i+speedDifference))/2;
                    double distanceAux = timeNeededAux * averageSpeedAux;
                    System.out.printf("%.4f\t%.4f\t%.0f\t%d\t\n", (startPointAux/1000), ((startPointAux+distanceAux)/1000), i+speedDifference, speedLimit);
                    startPointAux = startPointAux + distanceAux;
                }
            } else { // BRAKING
                for(double i=startSpeedKmh; i>endSpeedKmh; i=i-5) {
                    double speedDifference = Math.min(i-endSpeedKmh,5);
                    double timeNeededAux = Conversion.kmhToMs(speedDifference) / acceleration;
                    double averageSpeedAux = (Conversion.kmhToMs(i)+Conversion.kmhToMs(i-speedDifference))/2;
                    double distanceAux = timeNeededAux * averageSpeedAux;
                    System.out.printf("%.4f\t%.4f\t%.0f\t%d\t\n", (startPointAux/1000), ((startPointAux+distanceAux)/1000), i-speedDifference, speedLimit);
                    startPointAux = startPointAux + distanceAux;
                }
            }
            /* 1 em 1 km/h
            double startPointAux = startPoint;
            if (startSpeedKmh<endSpeedKmh) { // ACCELERATING
                for(double i=startSpeedKmh; i<endSpeedKmh; i++) {
                    double timeNeededAux = Conversion.kmhToMs(1) / acceleration;
                    double averageSpeedAux = (Conversion.kmhToMs(i)+Conversion.kmhToMs(i+1))/2;
                    double distanceAux = timeNeededAux * averageSpeedAux;
                    System.out.printf("%.4f\t%.4f\t%.0f\t\n", (startPointAux/1000), ((startPointAux+distanceAux)/1000), i+1);
                    startPointAux = startPointAux + distanceAux;
                }
            } else { // BRAKING
                for(double i=startSpeedKmh; i>endSpeedKmh; i--) {
                    double timeNeededAux = Conversion.kmhToMs(1) / acceleration;
                    double averageSpeedAux = (Conversion.kmhToMs(i)+Conversion.kmhToMs(i-1))/2;
                    double distanceAux = timeNeededAux * averageSpeedAux;
                    System.out.printf("%.4f\t%.4f\t%.0f\t\n", (startPointAux/1000), ((startPointAux+distanceAux)/1000), i-1);
                    startPointAux = startPointAux + distanceAux;
                }
            }*/
        } else {
            System.out.printf("%.4f\t%.4f\t%.0f\t%d\t\n", startPointKm, endPointKm, endSpeedKmh, speedLimit);
        }
    }
}

import java.util.ArrayList;
import java.util.Collections;

public class Slices {
    protected ArrayList<Slice> backupSlices;
    protected ArrayList<Slice> keepSlices;
    protected ArrayList<Slice> slices;
    protected int numKeepSlices;
    protected int numSlices;
    
    
    public Slices(ArrayList<Slice> slices) {
        this.slices = new ArrayList<Slice>();
        this.backupSlices = new ArrayList<Slice>();
        this.keepSlices = slices;
        numKeepSlices = slices.size();
        numSlices = 0;

        for (int i=0; i<numKeepSlices; i++)
            backupSlices.add(slices.get(i).clone());
    }
    
    protected void addAccelerationSlices() {
        slices.add(new Slice(0,0,0,0,true));
        for (int i=0; i<numKeepSlices; i++) {
            slices.add(keepSlices.get(i));
            slices.add(new Slice(0,0,0,0,true));
        }
        numSlices = slices.size();
    }
    
    public void enforceTopSpeeds() {
        for (int i=0; i<numKeepSlices; i++) {
            keepSlices.get(i).enforceTopSpeed();
        }
    }

    public void calculate() {
        // Calculate Speeds
        for (int i=0; i<numSlices; i++) {
            if (slices.get(i).isAcceleration()) {
                if (i > 0)
                    slices.get(i).changeStartSpeedKmh(slices.get(i-1).getEndSpeedKmh());
                if (i+1 < numSlices)
                    slices.get(i).changeEndSpeedKmh(slices.get(i+1).getStartSpeedKmh());
            }
        }
        
        // Calculate time needed (and distance of acceleration)
        for (int i=0; i<numSlices; i++) {
            slices.get(i).calculateTimeNeeded();
        }
        
        // Places the acceleration points and cut the keep slices respectively
        for (int i=0; i<numSlices; i++) {
            if (slices.get(i).isAcceleration()) {
                
                if (slices.get(i).getDistance() <= 0) {
                    // Useless slice since there is no speed difference, therefore, no acceleration
                    slices.get(i).changeStartPointKm(slices.get(i+1).getStartPointKm());
                    slices.get(i).changeEndPointKm(slices.get(i-1).getEndPointKm());
                    continue;
                }
                
                if (i == 0) {
                    // First slice, therefore, it will be on the start of the next (keep) slice
                    putSliceInStartOfNextSlice(slices.get(i), slices.get(i+1));
                    continue;
                } else if (i == (numSlices-1)) {
                    // Last slice, therefore, it will be on the end of the previous (keep) slice
                    putSliceInEndOfPreviousSlice(slices.get(i), slices.get(i-1));
                    continue;
                }
                
                if (slices.get(i-1).getSpeedLimit() < slices.get(i).getHighestSpeed()) {
                    // The previous slice has a speed limit lower than the highest speed on this acceleration, put this slice in the start of the next (keep) slice
                    putSliceInStartOfNextSlice(slices.get(i), slices.get(i+1));
                    continue;
                } else if (slices.get(i+1).getSpeedLimit() < slices.get(i).getHighestSpeed()) {
                    // The next slice has a speed limit lower than the highest speed on this acceleration, put this slice in the end of the previous (keep) slice
                    putSliceInEndOfPreviousSlice(slices.get(i), slices.get(i-1));
                    continue;
                }
                
                if (slices.get(i-1).getGradient() < slices.get(i+1).getGradient()) {
                    // The previous slice has a inferior gradient, making it more efficient to accelerate or break (?), put this slice in the end of the previous (keep) slice
                    putSliceInEndOfPreviousSlice(slices.get(i), slices.get(i-1));
                    continue;
                } else if (slices.get(i-1).getGradient() > slices.get(i+1).getGradient()) {
                    // The next slice has a inferior gradient, making it more efficient to accelerate or break (?), put this slice in the start of the next (keep) slice
                    putSliceInStartOfNextSlice(slices.get(i), slices.get(i+1));
                    continue;
                }
                
                // Where it has more space without giving a not enough distance error, therefore, tries to put it on the previous first
                if (slices.get(i-1).getDistance() >= slices.get(i).getDistance()) {
                    putSliceInEndOfPreviousSlice(slices.get(i), slices.get(i-1));
                    continue;
                } else {
                    putSliceInStartOfNextSlice(slices.get(i), slices.get(i+1));
                    continue;
                }
            }
        }
        
        // Calculate times
        for (int i=1; i<numSlices; i++) {
            slices.get(i).editStartTime(slices.get(i - 1).getEndTime());
        }
        
    }
    
    protected void putSliceInStartOfNextSlice(Slice accSlice, Slice keepSlice) {
        accSlice.changeStartPointKmKeepingDistance(keepSlice.getStartPointKm());
        keepSlice.changeStartPointKm(accSlice.getEndPointKm());
        accSlice.addSliceInfo(keepSlice.getGradient(), keepSlice.getSpeedLimit());
    }
    
    protected void putSliceInEndOfPreviousSlice(Slice accSlice, Slice keepSlice) {
        accSlice.changeEndPointKmKeepingDistance(keepSlice.getEndPointKm());
        keepSlice.changeEndPointKm(accSlice.getStartPointKm());
        accSlice.addSliceInfo(keepSlice.getGradient(), keepSlice.getSpeedLimit());
    }

    public void enforceTime(double time) {
        resetSlicesExceptSpeeds();
        addAccelerationSlices();
        enforceTopSpeeds();
        calculate();
        double currentTime = getTime();
        ArrayList<Slice> orderedSlices = orderSlicesByGradient();
         
        
        
        /*System.out.println("XSlices:");
        for (int i=0; i<numKeepSlices; i++)
            orderedSlices.get(i).print();*/
        ArrayList<Slice> lastSlices = cloneSliceArray(slices, numSlices);
        while(currentTime < time) {
            lastSlices = cloneSliceArray(slices, numSlices);
            resetSlicesExceptSpeeds();
            addAccelerationSlices();
            boolean decremented = false;
            for (int i=0; !decremented; i++) {
                decremented = orderedSlices.get(i).decrementSpeed();
            }
            //orderedSlices.get(0).decrementSpeed();
            calculate();
            currentTime = getTime();
            System.out.println("Current time: " + currentTime);
            print();
        }
        
        slices = lastSlices;
        currentTime = getTime();
        System.out.println("Best time: " + currentTime);
        print();
    }

    protected void resetSlicesExceptSpeeds() {
        slices.clear();
        resetKeepSlicesExceptSpeeds();
        numSlices = 0;
        //keepSlices = cloneSliceArray(backupSlices, numKeepSlices);
    }
    
    public void resetKeepSlicesExceptSpeeds() {
        for (int i=0; i<numKeepSlices; i++) {
            keepSlices.get(i).resetExceptSpeeds();
        }
    }

    protected ArrayList<Slice> orderSlicesByGradient() {
        //ArrayList<Slice> arr = cloneSliceArray(keepSlices, numKeepSlices);
        ArrayList<Slice> arr = new ArrayList<Slice>();
        for (int i=0; i<numKeepSlices; i++)
            arr.add(keepSlices.get(i));
        Collections.sort(arr);
        return arr;
    }
    
    public void print() {
        System.out.println("Slices:");
        for (int i=0; i<numSlices; i++)
            slices.get(i).print();
    }
    
    public ArrayList<Slice> cloneSliceArray(ArrayList<Slice> originalArray) {
        return cloneSliceArray(originalArray, originalArray.size());
    }
        
    public ArrayList<Slice> cloneSliceArray(ArrayList<Slice> originalArray, int arraySize) {
        ArrayList<Slice> newArray = new ArrayList<Slice>();
        for (int i=0; i<arraySize; i++)
            newArray.add(originalArray.get(i).clone());
        return newArray;
    }

    public double getTime() {
        return slices.get(numSlices-1).getEndTime();
    }
}

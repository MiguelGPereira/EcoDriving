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
        calculateAux();
        
        /*boolean wasCorrected = true;
        while (wasCorrected) {
            wasCorrected = false;
            
            for (int i=0; i<numSlices; i++) {
                if (slices.get(i).isKeep()) {
                    if (slices.get(i).getDistance()<0) {
                        slices.get(i).print();
                        if (slices.get(i-1).invadesNext()) {
                            if (slices.get(i+1).invadesPrevious()) {
                                System.out.println("Is invaded by both acceleration slices (Prev:"+slices.get(i-1).getDistanceKm()+") (Next:"+slices.get(i+1).getDistanceKm()+")");
                            } else {
                                System.out.println("Is invaded by previous acceleration slice ("+slices.get(i-1).getDistanceKm()+")");
                            }
                        } else if (slices.get(i+1).invadesPrevious()) {
                            System.out.println("Is invaded by next acceleration slice ("+slices.get(i+1).getDistanceKm()+")");
                        }
                        
                        double originalDistanceKm = slices.get(i).getOriginalEndPointKm() - slices.get(i).getOriginalStartPointKm();
                        double accelerationKmh2 = Conversion.ms2ToKmh2(Slice.getAcceleration());
                        double startSpeedKmh = slices.get(i).getStartSpeedKmh();
                        //double correctSpeedKmh = Math.floor(Math.sqrt((2*accelerationKmh2*originalDistanceKm)+(startSpeedKmh*startSpeedKmh)));
                        System.out.println("Distancia:"+originalDistanceKm);
                        System.out.println("Start:"+slices.get(i).getOriginalStartPointKm());
                        System.out.println("End:"+slices.get(i).getOriginalEndPointKm());
                        //System.out.println("Correct Speed:"+correctSpeedKmh);
                        
                        double previousSliceStartSpeedKmh = slices.get(i-1).getStartSpeedKmh();
                        double nextSliceEndSpeedKmh = slices.get(i+1).getEndSpeedKmh();
                        double intersectionSpeedKmh = Math.sqrt(((originalDistanceKm*2*accelerationKmh2)+(previousSliceStartSpeedKmh*previousSliceStartSpeedKmh)+(nextSliceEndSpeedKmh*nextSliceEndSpeedKmh))/2);
                        
                        System.out.println("Previous Slice Start Speed Kmh:"+previousSliceStartSpeedKmh);
                        System.out.println("This Slice Speed Kmh:"+slices.get(i).getEndSpeedKmh()+"="+slices.get(i).getStartSpeedKmh());
                        System.out.println("Next Slice End Speed Kmh:"+nextSliceEndSpeedKmh);
                        System.out.println("Intersection Speed Kmh:"+intersectionSpeedKmh);
                        
                        double maxPossibleSpeedPrevKmh = slices.get(i-1).maxPossibleSpeedKmh(originalDistanceKm);
                        double maxPossibleSpeedNextKmh = slices.get(i+1).maxPossibleSpeedKmh(originalDistanceKm);
                        System.out.println("Max Possible Speed Prev Kmh:"+maxPossibleSpeedPrevKmh);
                        System.out.println("Max Possible Speed Next Kmh:"+maxPossibleSpeedNextKmh);
                        
                        //correctSpeedKmh = 30;
                        slices.get(i).setSpeed(Math.min(maxPossibleSpeedPrevKmh, maxPossibleSpeedNextKmh));
                        wasCorrected = true;
                    }
                }
            }

            if (wasCorrected) {
                System.out.println("RECALCULANDO");
                resetSlicesExceptSpeeds();
                addAccelerationSlices();
                calculateAux();
            } 
        }*/
        
        // Calculate times
        for (int i=1; i<numSlices; i++) {
            slices.get(i).editStartTime(slices.get(i - 1).getEndTime());
        }
        
    }
    
    protected void calculateAux() {
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
                    if ((i+1)<numSlices)
                        slices.get(i).changeStartPointKm(slices.get(i+1).getStartPointKm());
                    else
                        slices.get(i).changeStartPointKm(slices.get(i-1).getEndPointKm());
                    slices.get(i).changeEndPointKm(slices.get(i-1).getEndPointKm());
                    continue;
                }
                
                if (i == 0) {
                    // First slice, therefore, it will be on the start of the next (keep) slice
                    putSliceInStartOfNextSlice(slices.get(i), slices.get(i+1));
                    slices.get(i).setInvadesNext();
                    continue;
                } else if (i == (numSlices-1)) {
                    // Last slice, therefore, it will be on the end of the previous (keep) slice
                    putSliceInEndOfPreviousSlice(slices.get(i), slices.get(i-1));
                    slices.get(i).setInvadesPrevious();
                    continue;
                }
                
                if (slices.get(i-1).getSpeedLimit() < slices.get(i).getHighestSpeed()) {
                    // The previous slice has a speed limit lower than the highest speed on this acceleration, put this slice in the start of the next (keep) slice
                    putSliceInStartOfNextSlice(slices.get(i), slices.get(i+1));
                    slices.get(i).setInvadesNext();
                    continue;
                } else if (slices.get(i+1).getSpeedLimit() < slices.get(i).getHighestSpeed()) {
                    // The next slice has a speed limit lower than the highest speed on this acceleration, put this slice in the end of the previous (keep) slice
                    putSliceInEndOfPreviousSlice(slices.get(i), slices.get(i-1));
                    slices.get(i).setInvadesPrevious();
                    continue;
                }
                
                if (slices.get(i-1).getGradient() < slices.get(i+1).getGradient()) {
                    // The previous slice has a inferior gradient, making it more efficient to accelerate or break (?), put this slice in the end of the previous (keep) slice
                    putSliceInEndOfPreviousSlice(slices.get(i), slices.get(i-1));
                    slices.get(i).setInvadesPrevious();
                    continue;
                } else if (slices.get(i-1).getGradient() > slices.get(i+1).getGradient()) {
                    // The next slice has a inferior gradient, making it more efficient to accelerate or break (?), put this slice in the start of the next (keep) slice
                    putSliceInStartOfNextSlice(slices.get(i), slices.get(i+1));
                    slices.get(i).setInvadesNext();
                    continue;
                }
                
                // Where it has more space without giving a not enough distance error, therefore, tries to put it on the previous first
                if (slices.get(i-1).getDistance() >= slices.get(i).getDistance()) {
                    putSliceInEndOfPreviousSlice(slices.get(i), slices.get(i-1));
                    slices.get(i).setInvadesPrevious();
                    continue;
                } else {
                    putSliceInStartOfNextSlice(slices.get(i), slices.get(i+1));
                    slices.get(i).setInvadesNext();
                    continue;
                }
            }
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
            /*System.out.println("Current time: " + currentTime);
            print();*/
        }
        
        slices = lastSlices;
        currentTime = getTime();
        System.out.println("Time available: " + time);
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

    public ArrayList<Slice> getKeepSlices() {
        return keepSlices;
    }
    
    public void fuse() {
        if (numKeepSlices<2)
            return;
        
        // Separate the slices by the different speed limits along the service
        ArrayList<ArrayList<Slice> > arrSpeedLimits = new ArrayList<ArrayList<Slice> >();
        arrSpeedLimits.add(new ArrayList<Slice>());        
        double speedLimit = keepSlices.get(0).getSpeedLimit();
        int speedArr = 0;
        arrSpeedLimits.get(speedArr).add(keepSlices.get(0));
        for (int i=1; i<numKeepSlices; i++) {
            if (keepSlices.get(i).getSpeedLimit()==speedLimit) {
                arrSpeedLimits.get(speedArr).add(keepSlices.get(i));
            } else {
                speedArr++;
                speedLimit = keepSlices.get(i).getSpeedLimit();
                arrSpeedLimits.add(new ArrayList<Slice>());       
                arrSpeedLimits.get(speedArr).add(keepSlices.get(i));
            }
        }
        
        // Print
        /*for (int i=0; i<arrSpeedLimits.size(); i++) {
            System.out.println("i:"+i);
            for (int j=0; j<arrSpeedLimits.get(i).size(); j++) {
                System.out.print("i:"+i+" j:"+j+" ");
                arrSpeedLimits.get(i).get(j).printSimple();
            }
        }*/
        
        ArrayList<ArrayList<ArrayList<Slice> > > arrGradients = new ArrayList<ArrayList<ArrayList<Slice> > >();
        int numSpeedLimits = arrSpeedLimits.size();
        for (int i=0; i<numSpeedLimits; i++) {
            arrGradients.add(new ArrayList<ArrayList<Slice> >());
            arrGradients.get(i).add(new ArrayList<Slice>());
            arrGradients.get(i).get(0).add(arrSpeedLimits.get(i).get(0));
            
            int arrSize = arrSpeedLimits.get(i).size();
            if (arrSize<2) // Nothing to fuse in this speed limit array since there is only 1 slice
                continue;
            
            double highestGradient = arrSpeedLimits.get(i).get(0).getGradient();
            double lowestGradient = highestGradient;
            
            int gradientNumber = 0;
            for (int j=1; j<arrSize; j++) {
                double sliceGradient = arrSpeedLimits.get(i).get(j).getGradient();
                if (Math.abs(sliceGradient-highestGradient)>2 || Math.abs(sliceGradient-lowestGradient)>2) {
                    gradientNumber++;
                    arrGradients.get(i).add(new ArrayList<Slice>());
                    arrGradients.get(i).get(gradientNumber).add(arrSpeedLimits.get(i).get(j));
                    highestGradient = arrSpeedLimits.get(i).get(j).getGradient();
                    lowestGradient = highestGradient;
                    continue;
                }
                
                if (sliceGradient > highestGradient)
                    highestGradient = sliceGradient;
                if (sliceGradient < lowestGradient)
                    lowestGradient = sliceGradient;
                
                arrGradients.get(i).get(gradientNumber).add(arrSpeedLimits.get(i).get(j));
            }
        }
        
        // Print
        /*System.out.println("\n\n///////////////////////////////////////////////////////////\n\n");
        for (int i=0; i<arrGradients.size(); i++) {
            System.out.println("i:"+i);
            for (int j=0; j<arrGradients.get(i).size(); j++) {
                System.out.println("i:"+i+" j:"+j);
                for (int k=0; k<arrGradients.get(i).get(j).size(); k++) {
                    System.out.print("i:"+i+" j:"+j+" k:"+k+" ");
                    arrGradients.get(i).get(j).get(k).printSimple();
                }
            }
        }*/
        
        ArrayList<Slice> arrNew = new ArrayList<Slice>();
        int numGradients = arrGradients.size();
        for (int i=0; i<numGradients; i++) {
            
            int numGradientsi = arrGradients.get(i).size();
            for (int j=0; j<numGradientsi; j++) {
                int arrSize = arrGradients.get(i).get(j).size();
                if (arrSize<2) {
                    arrNew.add(arrGradients.get(i).get(j).get(0));
                    continue;
                }
                
                double startPointKm = arrGradients.get(i).get(j).get(0).getStartPointKm();
                double endPointKm = arrGradients.get(i).get(j).get(arrSize-1).getEndPointKm();
                double distanceKm = endPointKm-startPointKm;
                double averageGradient = 0;
                
                for (int k=0; k<arrSize; k++) {
                    double sliceDistanceKm = arrGradients.get(i).get(j).get(k).getDistanceKm();
                    double sliceGradient = arrGradients.get(i).get(j).get(k).getGradient();
                    averageGradient = averageGradient + ((sliceDistanceKm/distanceKm)*sliceGradient);
                }
                
                arrNew.add(new Slice(startPointKm, endPointKm, averageGradient, arrGradients.get(i).get(j).get(0).getSpeedLimit(), false));
            }
        }
        
        // Print
        /*System.out.println("\n\n///////////////////////////////////////////////////////////\n\n");
        for (int i=0; i<arrNew.size(); i++) {
            System.out.print("i:"+i+" ");
            arrNew.get(i).printSimple();
        }*/
        
        this.slices = new ArrayList<Slice>();
        this.backupSlices = new ArrayList<Slice>();
        this.keepSlices = arrNew;
        numKeepSlices = keepSlices.size();
        numSlices = 0;

        for (int i=0; i<numKeepSlices; i++)
            backupSlices.add(arrNew.get(i).clone());
    }

    public void exportResult(String startStation, String endStation, int time) {
        System.out.println("STATION\t" + startStation + "\t" + endStation + "\t" + time);
        for (int i=0; i<numSlices; i++) {
            slices.get(i).exportResult();
        }
        
        /*double startPointKm = slices.get(0).getStartPointKm();
        double endPointKm = slices.get(0).getEndPointKm();
        Double endSpeedKmh = slices.get(0).getEndSpeedKmh();
        for (int i=1; i<numSlices; i++) {
            //slices.get(i).exportResult();
            double sliceStartPointKm = slices.get(i).getStartPointKm();
            double sliceEndPointKm = slices.get(i).getEndPointKm();
            double sliceEndSpeedKmh = slices.get(i).getEndSpeedKmh();
            if (endSpeedKmh!=sliceEndSpeedKmh) {
                System.out.printf("%.4f\t%.4f\t%.1f\n", startPointKm, endPointKm, endSpeedKmh);
                startPointKm = sliceStartPointKm;
                endPointKm = sliceEndPointKm;
                endSpeedKmh = sliceEndSpeedKmh;
            } else {
                endPointKm = sliceEndPointKm;
            }
        }
        System.out.printf("%.4f\t%.4f\t%d\n\n", startPointKm, endPointKm, endSpeedKmh.intValue());*/
    }
}

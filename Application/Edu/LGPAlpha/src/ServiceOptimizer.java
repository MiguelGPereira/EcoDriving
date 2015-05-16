public class ServiceOptimizer {
    
    protected static String slicesFilePath = "C:\\Users\\eduardextreme\\NetBeansWorkspace\\LGPAlpha\\build\\classes\\slices.csv";
 
    public static void main(String[] args)
    {
        Slices slices = new Slices(CSVReader.extract(slicesFilePath));
        
        slices.enforceTopSpeeds();
        slices.addAccelerationSlices();
        //slices.print();
        
        slices.calculate();
        //slices.print();
        
        slices.enforceTime(240);
        //slices.print();
    }
}

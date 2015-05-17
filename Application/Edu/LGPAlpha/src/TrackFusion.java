public class TrackFusion {
    protected static String slicesFilePath = "C:\\Users\\eduardextreme\\NetBeansWorkspace\\LGPAlpha\\build\\classes\\slicesComplete.csv";
 
    public static void main(String[] args)
    {
        Slices slices = new Slices(CSVReader.extract(slicesFilePath));
        slices.fuse();
        slices.enforceTopSpeeds();
        slices.addAccelerationSlices();
        slices.calculate();
        slices.enforceTime(1);
    }
}

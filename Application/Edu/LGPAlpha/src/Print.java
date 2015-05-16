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
}

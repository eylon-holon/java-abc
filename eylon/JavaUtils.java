import java.util.*;

class JavaUtils {
    public static String str(Object arg) {
        if (arg == null)
            return "null";
        if (arg instanceof int[])
            return Arrays.toString((int[]) arg);
        if (arg instanceof boolean[])
            return Arrays.toString((boolean[]) arg);
        if (arg instanceof String[])
            return Arrays.toString((String[]) arg);
        return arg.toString();
    }
    
    public static void print(Object... args) {
        for (var arg : args) {
            System.out.print(str(arg));
            System.out.print(' ');
        }
        System.out.println();
    }
    
    public static String input(String prompt) {
        var scanner = new Scanner(System.in);
    
        System.out.print(prompt);
        var answer = scanner.nextLine();
        
        System.out.println();
        scanner.close();
    
        return answer;
    }    
}

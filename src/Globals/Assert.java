package Globals;

public class Assert {
    public static <T> void equals(T a, T b){
        if(a!=b){
            throw new RuntimeException("Assert fail:'"+a+"' not equal to '"+b+"'");
        }
    }

    public static void equals(String a, String b){
        if(!a.equals(b)){
            throw new RuntimeException("Assert fail:'"+a+"' not equal to '"+b+"'");
        }
    }

    public static void bool(boolean a){
        if(!a){
            throw new RuntimeException("Assert fail:'"+a+"is false.");
        }
    }
}

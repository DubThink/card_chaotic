package core;

import processing.core.PImage;



import java.util.HashMap;

public class SymbolInjector {
    protected static final char OFFSET=0x80;
    protected static final Symbol dummySymbol;
    static Symbol[] symbols;
    static HashMap<String, Symbol> keywords;
    static char nextSymbol=0;

    private static boolean initd =false;

    static {
        dummySymbol=new Symbol();
        init();
    }

    private static void init(){
        if(initd)return;
        symbols=new Symbol[0xff-OFFSET];
        keywords = new HashMap<>();
        initd=true;
    }
    public static void blockSymbols(String s) {
        init();
        blockSymbols(s.toCharArray());
    }

    public static void blockSymbols(char[] s){
        for(char c:s){
            assert symbols[c-OFFSET]==null;
            symbols[c-OFFSET]=dummySymbol; // fill with dummy values
        }
    }

    public static Symbol createSymbol(PImage image){
        Symbol ret = new Symbol();
        ret.image=image;
        // find next char
        while(nextSymbol<symbols.length && symbols[nextSymbol] != null)
            nextSymbol++;
        assert nextSymbol < symbols.length;

        ret.c=(char)(OFFSET+nextSymbol);
        symbols[nextSymbol]=ret;
        return ret;
    }

    public static void addKey(String key, Symbol symbol) {
        assert symbols[symbol.c-OFFSET] == symbol;
        keywords.put(key, symbol);
    }

    public static boolean isCharSymbol(char c){
        if(c<OFFSET)return false;
        return symbols[c-OFFSET]!=null && symbols[c-OFFSET].image!=null;
    }

    public static boolean isStringSymbol(String s){
        return keywords.containsKey(s);
    }

    public static Symbol lookupSymbol(char c){
        return symbols[c-OFFSET];
    }

    public static Symbol getSymbolByString(String s){
//        System.out.println("looking up '"+s+"'");
        if(keywords.containsKey(s))
            return keywords.get(s);
        return null;
    }

}

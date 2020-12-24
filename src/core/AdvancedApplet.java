package core;

import processing.core.PApplet;

public class AdvancedApplet extends PApplet {

    public static final char CC_BOLD = 0x80;

    static{
        SymbolInjector.blockSymbols(new char[]{AdvancedApplet.CC_BOLD});
    }
    static boolean scheck(char[] sc, int i, char c, boolean oobFail){
        if(i<0||i>=sc.length)return oobFail;
        return sc[i]==c;
    }

    /**
     * Parses text and injects symbols based on the SymbolInjector and StringInjector configs
     * /E/A/F/W: one of each element
     * <taco> tokens are always lowercase
     */
    public static String hyperText(String s){
        char[] sc = s.toCharArray();
        StringBuilder output = new StringBuilder(s.length()); // preallocate what should be enough space
        int start = 0;
        for(int i=0;i<s.length();i++){
            // possible symbol token
            if(sc[i] == '/'){
                // escaped slash
                boolean handled = false;
                if(scheck(sc,i-1,'\\',false)){
                    output.append(s,start,i-1);
                    start=i;
                } else if(i+1<sc.length) {
                    // symbol to eat
                    if (SymbolInjector.isStringSymbol("" + sc[i + 1])) {
                        // actual symbol
                        output.append(s, start, i);
                        output.append(SymbolInjector.getSymbolByString("" + sc[i + 1]).c);
                        i++;
                        start = i + 1;
                    }
                }
            } else if (sc[i] == '^'){
                if(scheck(sc,i-1,'\\',false)){
                    output.append(s,start,i-1);
                    start=i;
                } else {
                    output.append(s, start, i);
                    output.append(CC_BOLD);
                    start = i + 1;
                }
            }
        }
        output.append(s,start,s.length());
        return output.toString();
    }
}

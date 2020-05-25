import java.io.IOException;
import java.util.*;
import java.io.File;

public class TestStuff {
    public static void main(String[]args) throws IOException {
        System.out.println(hexConverter("A5"));
    }
    private static int hexConverter(String hex){
        char[] hexChars = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        int decimal = 0;
        for(int i = hex.length()-1; i >= 0; i--){
            int p = i;
            for(int j = 0; j < hexChars.length; j++){
                if(hex.charAt(i) == hexChars[j]){
                        decimal += j*Math.pow(16,hex.length()-i-1);
                        break;
                }
            }
        }
        return decimal;
    }
    private static String[] opcodeParser(String line){
        String[] out = new String[3];
        out[0] = line; out[1] = line; out[2] = line;
        //if the line starts with a space(not a label) skip spaces until you hit something
        if(out[1].indexOf(' ') == 0){
            out[1] = spaceIterator(line);
            out[0] = null;
        } else {
            int i = 0;
            //see how long the label is, then use that to create the proper substrings
            while(!(out[1].charAt(i) == (' '))){
                i++;
            }
            out[0] = out[0].substring(0,i);
            out[1] = spaceIterator(out[1].substring(i));
        }
        //check if there is more content after the opcode, if there is, assign it to out[2]
        int space = out[1].indexOf(' ');
        if(space != -1) {
            out[2] = spaceIterator(out[1].substring(space));
            out[1] = out[1].substring(0, space);
        } else {
            out[2] = null;
        }
        //check for comments
        for(int i = 0; i < 3; i++) {
            if (out[i] != null) {
                if (out[i].contains(".")) {
                    if(out[i].indexOf(' ') < out[i].indexOf('.')){
                        out[i] = out[i].substring(0, out[i].indexOf(' '));
                    } else {
                        out[i] = out[i].substring(0, out[i].indexOf('.'));
                    }
                }
            }
        }
        return out;
    }

    private static String spaceIterator(String string){
        int i = 0;
        while(string.substring(i,i+1).equals(" ")){
            i++;
        }
        return string.substring(i);
    }

    private static boolean readLines(String filename) throws IOException{
        File infile = new File(filename);
        Scanner scan;
        if(infile.exists()) {
            scan = new Scanner(infile).useDelimiter("\n");
        }
        else{
            return false;
        }
        String out = scan.next();
        if(out == null){
            return false;
        }
        System.out.println(scan.next());
        return true;
        /*
        lines = Files.readAllLines(infile.toPath(), Charset.defaultCharset());*/
    }
}

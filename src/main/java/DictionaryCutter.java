import java.io.*;
import java.util.HashSet;

/**
 * Created by dewartan on 11/14/16.
 */
public class DictionaryCutter {
    public static void main(String[] args) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("dictionaryMod.txt"));
        int counter = 0;
        try {
            BufferedReader file = new BufferedReader(new FileReader("/Users/dewartan/Desktop/dictionary.txt"));
            String line;
            while(file.ready()) {
                line = file.readLine();
                if(!line.matches(".*\\d+.*") && !line.matches(".*\\W+.*") && line.length() > 3) {
                    writer.write(line + "\n");
                    counter++;
                }
                if(counter > 150000) {
                    break;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(counter);
    }
}

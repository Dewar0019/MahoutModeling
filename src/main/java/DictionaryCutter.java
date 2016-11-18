import java.io.*;

/**
 * Created by dewartan on 11/14/16.
 */
public class DictionaryCutter {
    public static void main(String[] args) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("dictionaryMod.txt"));
        int counter = 0;
        try {
            BufferedReader file = new BufferedReader(new FileReader(args[0]));
            String line;
            while(file.ready()) {
                line = file.readLine();
                if (!line.matches(".*\\d+.*") && !line.matches(".*\\W+.*") && line.length() > 3) {
                    writer.write(line + "\n");
                    counter++;
                }
                if (counter > 400000) {
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

import java.io.*;

/**
 * Hadoop Group 15
 *
 * Cuts down the number of entries retrieved from the dictionary
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
                //Removes entries containing numbers, symbols, and less than three characters
                if (!line.matches(".*\\d+.*") && !line.matches(".*\\W+.*") && line.length() > 3) {
                    writer.write(line + "\n");
                    counter++;
                }
                if (counter > 400000) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(counter);
    }
}

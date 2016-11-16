//package todelete;
//
//import com.google.common.collect.Lists;
//import org.apache.mahout.math.RandomAccessSparseVector;
//import org.apache.mahout.math.Vector;
//
//import java.io.*;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//
//public class ToVectors
//{
//    private static HashSet<String> dictionary = new HashSet<String>();
//    private static HashMap<String, List<String>> professions = new HashMap<>();
//    private static String lemmaFile;
//
//    public ToVectors(String professionsFile, String lemmaFile) throws IOException {
//        this.lemmaFile = lemmaFile;
//        processNames(professionsFile);
//    }
//
//
//
//
//    public List<MahoutVector> vectorize() throws IOException {
//        List<MahoutVector> vectors = Lists.newArrayList();
//
//        // Iterate the CSV records
//        BufferedReader reader = new BufferedReader(new FileReader(this.lemmaFile));
//        BufferedWriter writer = new BufferedWriter(new FileWriter("nonclassified.txt"));
//        String line;
//
//        try {
//            while (reader.ready()) {
//
//                line = reader.readLine();
//
//                String[] parsedLine = line.split("\t");
//
//                String[] wordFrequency = parsedLine[1].replaceAll("[<>,]", " ").split("   ");
//
//                for (int i = 0; i < wordFrequency.length; i++) {
//                    String[] parsedWord = wordFrequency[i].trim().split(" ");
//                    dictionary.add(parsedWord[0].trim().toLowerCase());
//                }
//            }
//
//            //reset reader
//            reader.mark(0);
//            while (reader.ready()) {
//                line = reader.readLine();
//
//                String[] parsedLine = line.split("\t");
//                String personName = parsedLine[0].trim().toLowerCase();
//                System.out.println(personName);
//                String[] wordFrequency = parsedLine[1].replaceAll("[<>,]", " ").split("   ");
//                HashMap<String, Integer> wordFrequencyMap = getWordHashMap(wordFrequency);
//
//                //Classifiable
//                if(professions.containsKey(personName)) {
//                    List<String> professionVectors = professions.get(personName);
//                    for(String job: professionVectors) {
//                        MahoutVector mahoutVector = new MahoutVector();
//                        mahoutVector.classifier = job;
//                        Vector vector = new RandomAccessSparseVector(dictionary.size()-1, dictionary.size()-1);
//                        int index = 0;
//                        for(String word: dictionary) {
//                            if(wordFrequencyMap.containsKey(word)) {
//                                vector.set(index,wordFrequencyMap.get(word));
//                            } else {
//                                vector.set(index, 0);
//                            }
//                            index++;
//                        }
//                        mahoutVector.vector = vector;
//                        vectors.add(mahoutVector);
//                    }
//
//                } else {
//                    //Unclassifiable so write to file
//                    writer.write(line);
//                }
//            }
//            return vectors;
//        }
//        finally {
//            reader.close();
//            writer.close();
//        }
//    }
//
//    /**
//     *
//     * @param wordFrequency
//     * @return
//     */
//    private HashMap<String, Integer> getWordHashMap(String[] wordFrequency) {
//        HashMap<String, Integer> map = new HashMap<>();
//        for (int i = 0; i < wordFrequency.length; i++) {
//            String[] parsedWord = wordFrequency[i].trim().split(" ");
//            map.put(parsedWord[0].toLowerCase().trim(), Integer.parseInt(parsedWord[1]));
//        }
//        return map;
//    }
//
//    private static void processNames(String professionsFile) throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader(professionsFile));
//        String line;
//        while(reader.ready()) {
//            line = reader.readLine();
//            String[] splitProfessions = line.split(":");
//            professions.put(splitProfessions[0].trim().toLowerCase(), Arrays.asList(splitProfessions[1].trim().toLowerCase().split(",")));
//        }
//        reader.close();
//    }
//
//
//}

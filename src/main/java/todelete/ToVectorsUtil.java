//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import org.apache.mahout.math.RandomAccessSparseVector;
//import org.apache.mahout.math.Vector;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.text.NumberFormat;
//import java.text.ParsePosition;
//import java.util.*;
//
//public class ToVectorsUtil
//{
//	private static long wordCount = 1;
//	private static final Map<String, Long> words = Maps.newHashMap();
//	private static HashSet<String> names = new HashSet<>();
//
//	public ToVectorsUtil(String professionsFile) throws IOException {
//		processNames(professionsFile);
//	}
//
//	private static void processNames(String professionsFile) throws IOException {
//		BufferedReader reader = new BufferedReader(new FileReader(professionsFile));
//		String line;
//		while(reader.ready()) {
//			line = reader.readLine();
//			String[] splitProfessions = line.split(":");
//			names.add(splitProfessions[1].trim().toLowerCase());
//		}
//		reader.close();
//	}
//
//	public List<MahoutVector> vectorize(String line) throws IOException {
//
//		List<MahoutVector> vectors = Lists.newArrayList();
//		List<WordFrequency> professionFrequencies = Lists.newArrayList();
//
//		String[] parsedLine = line.split("\t");
//		String personName = parsedLine[0];
//		System.out.println(personName);
//		String[] wordFrequency = parsedLine[1].replaceAll("[<>,]", " ").split("   ");
//
//		Vector vector = new RandomAccessSparseVector(wordFrequency.length, wordFrequency.length);
////				vector.set(i, processNumeric(parsedWord[1]));
//
//		for(int i =0; i<wordFrequency.length; i++) {
//			String[] parsedWord =  wordFrequency[i].trim().split(" ");
//			if(listOfProfessions.contains(parsedWord[0].toLowerCase())) {
//				professionFrequencies.add(new WordFrequency(parsedWord[0], Integer.parseInt(parsedWord[1])));
//			}
//		}
//		Collections.sort(professionFrequencies);
//
//		List<WordFrequency> finalList = new ArrayList<>();
//		for(int i = 0; i<3; i++) {
//			finalList.add(professionFrequencies.get(i));
//		}
//
////		MahoutVector mahoutVector = new MahoutVector();
////		mahoutVector.classifier = personName;
////		mahoutVector.vector = vector;
////		vectors.add(mahoutVector);
//
//		return vectors;
//
//	}
//
//	// Not sure how scalable this is going to be
//	protected double processString(String data)
//	{
//
//		Long theLong = words.get(data);
//		if (theLong == null)
//		{
//			theLong = wordCount++;
//			words.put(data, theLong);
//		}
//
//		return theLong;
//	}
//
//	protected double processNumeric(String data)
//	{
//		double d = 0;
//		if (isNumeric(data))
//		{
//			d = Double.parseDouble(data);
//		}
//		return d;
//	}
//
//	public static boolean isNumeric(String str)
//	{
//		NumberFormat formatter = NumberFormat.getInstance();
//		ParsePosition parsePosition = new ParsePosition(0);
//		formatter.parse(str, parsePosition);
//		return str.length() == parsePosition.getIndex();
//	}
//
//}

//package todelete;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.SequenceFile;
//import org.apache.hadoop.io.Text;
//import org.apache.mahout.math.VectorWritable;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.util.List;
//
//public class MahoutTest {
//	private static final Logger logger = LoggerFactory.getLogger(MahoutTest.class);
//
//	public static void main(String[] args) throws Throwable {
//		String professionsFile = "/Users/dewartan/Downloads/mahout/src/main/resources/professions.txt";
//        String lemmaFile = "/Users/dewartan/Downloads/mahout/src/main/resources/wiki-big-lemma-index.tbz2";
//		String seqFile = args[1];
//
//
//		csv2vectors(professionsFile, lemmaFile, seqFile);
////		train(testFilePath, , tempPath, seqFile);
//	}
//
//	public static void csv2vectors(String professionsFile, String lemmaFile,  String seqFile) throws IOException
//	{
//		Configuration conf = new Configuration();
//		FileSystem fs = FileSystem.getLocal(conf);
//
//		Path seqFilePath = new Path(seqFile);
//
//		fs.delete(seqFilePath,false);
//
//		SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, seqFilePath, Text.class, VectorWritable.class);
//
//		try
//		{
//			ToVectors toVectors = new ToVectors(professionsFile,lemmaFile);
//			List<MahoutVector> vectors = toVectors.vectorize();
//
//			// Init the labels
//
//			for (MahoutVector vector : vectors)
//			{
//				VectorWritable vectorWritable = new VectorWritable();
//				vectorWritable.set(vector.vector);
//				writer.append(new Text("/" + vector.classifier + "/"), vectorWritable);
//			}
//		}
//
//		finally
//		{
//			writer.close();
//		}
//	}
//
////	public static void train(String testFilePath, String outputPath, String tmpDirectory, String seqFilePath) throws Throwable
////	{
////
////
////		Configuration conf = new Configuration();
////		FileSystem fs = FileSystem.getLocal(conf);
////
////		TrainNaiveBayesJob trainNaiveBayes = new TrainNaiveBayesJob();
////		trainNaiveBayes.setConf(conf);
////
////
////		fs.delete(new Path(outputPath),true);
////		fs.delete(new Path(tmpDirectory),true);
////
////		trainNaiveBayes.run(new String[] { "--input", seqFilePath, "--output", outputPath, "-el", "--overwrite", "--tempDir", tmpDirectory });
////
////		// Train the classifier
////		NaiveBayesModel naiveBayesModel = NaiveBayesModel.materialize(new Path(outputPath), conf);
////
////		System.out.println("features: " + naiveBayesModel.numFeatures());
////		System.out.println("labels: " + naiveBayesModel.numLabels());
////
////	    AbstractVectorClassifier classifier = new ComplementaryNaiveBayesClassifier(naiveBayesModel);
////
////	    ToVectors toVectors = new ToVectors(testFilePath);
////
////	    List<MahoutVector> vectors = toVectors.vectorize();
////
////
////	    int total = 0;
////	    int success = 0;
////
////	    for (MahoutVector mahoutVector : vectors)
////	    {
////	    	Vector prediction = classifier.classifyFull(mahoutVector.vector);
////
////	    	// They sorted alphabetically
////	    	// 0 = anomaly, 1 = normal (because 'anomaly' > 'normal')
////	    	double anomaly = prediction.get(0);
////	    	double normal = prediction.get(1);
////
////	    	String predictedClass = "anomaly";
////	    	if (normal > anomaly)
////	    	{
////	    		predictedClass="normal";
////	    	}
////
////	    	if (predictedClass.equals(mahoutVector.classifier))
////	    	{
////	    		success++;
////	    	}
////
////	    	total ++;
////	    }
////
////	    System.out.println(total + " : " + success + " : " + (total - success) + " " + ((double)success/total));
////
////		//StandardNaiveBayesClassifier classifier = new StandardNaiveBayesClassifier();
////
////	}
//
//
//
//}

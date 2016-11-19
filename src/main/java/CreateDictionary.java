import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.HashSet;

/**
 * Hadoop Group 15
 *
 * Creates a dictionary of all the word frequencies found
 */
public class CreateDictionary {


    public static class DictionaryCreatorMapper extends Mapper<LongWritable, Text, Text, Text> {

        private static HashSet<String> dictionary = new HashSet();
        private Text output = new Text();

        @Override
        public void map(LongWritable offset, Text input, Context context) throws IOException, InterruptedException {
            String[] parsedLine = input.toString().split("\t");
            String[] wordFrequency = parsedLine[1].replaceAll("[<>,]", " ").split("   ");
            StringBuilder writeDictionary = new StringBuilder();
            output.set("");
            for (int i = 0; i < wordFrequency.length; i++) {
                String toParse = wordFrequency[i].trim();
                String word = toParse.substring(0, toParse.lastIndexOf(" ")).trim();
                if(!dictionary.contains(word) && !word.matches(".*\\d+.*") && !word.matches(".*\\W+.*") && word.length() > 3) {
                    writeDictionary.append(word).append("\n");
                    dictionary.add(word);
                }
            }
            output.set(writeDictionary.toString().trim());
            if(output.toString().length() > 3) {
                context.write(output, new Text(""));
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("Creating dictionary file");

        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        Path inputPath = new Path(otherArgs[0]);
        Path outputPath = new Path(otherArgs[1]);

        Job job = Job.getInstance(conf, "CreateDictionary");

        job.setJarByClass(CreateDictionary.class);

        job.setMapperClass(DictionaryCreatorMapper.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);

        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


}

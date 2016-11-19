import model.ProfessionFrequency;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Hadoop Group 15
 *
 * Classify the Unknown with their professions for mahout
 */
public class ClassifyUnknown {

    public static class ClassifyUnknownMapper extends Mapper<LongWritable, Text, Text, Text> {

        private static HashSet<String> professions = new HashSet<String>();


        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            Configuration conf = context.getConfiguration();

            URI[] cacheFiles = DistributedCache.getCacheFiles(conf);
            URI professionsFile = null;

            if (cacheFiles != null && cacheFiles.length > 0) {
                for (int i = 0; i < cacheFiles.length; i++) {
                    if (cacheFiles[i].toString().contains("profession.txt")) {
                        System.out.println("Found Professions File");
                        professionsFile = cacheFiles[i];
                        break;
                    }
                }
                processNames(professionsFile.toString());
            }
        }


        /**
         * Process names from professions.txt
         * @param professionsFile
         * @throws IOException
         */
        private static void processNames(String professionsFile) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(professionsFile));
            String line;
            while (reader.ready()) {
                line = reader.readLine();
                String[] splitProfessions = line.split(":");
                for (String job : splitProfessions[1].split(",")) {
                    professions.add(job.trim());
                }
            }
            reader.close();
        }

        /**
         * Create a hashmap of word frequencies
         * @param wordFrequency
         * @return
         */
        private HashMap<String, Integer> getWordHashMap(String[] wordFrequency) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            for (int i = 0; i < wordFrequency.length; i++) {
                String toParse = wordFrequency[i].trim();
                String word = toParse.substring(0, toParse.lastIndexOf(" ")).trim();
                map.put(word, Integer.parseInt(toParse.substring(toParse.lastIndexOf(" ")).trim()));
            }
            return map;
        }


        @Override
        public void map(LongWritable offset, Text input, Context context) throws IOException, InterruptedException {
            String[] parsedLine = input.toString().split("\t");
            String personName = parsedLine[0].trim().toLowerCase();
            String[] wordFrequency = parsedLine[1].replaceAll("[<>,]", " ").split("   ");
            HashMap<String, Integer> wordFrequencyMap = getWordHashMap(wordFrequency);
            List<ProfessionFrequency> possibleProfessions = new ArrayList<>();
            for (String word : wordFrequencyMap.keySet()) {
                if (professions.contains(word)) {
                    possibleProfessions.add(new ProfessionFrequency(word, wordFrequencyMap.get(word)));
                }
            }
            Collections.sort(possibleProfessions);

            StringBuilder builder = new StringBuilder();
            int counter = 0;
            while (counter < 3 && counter < possibleProfessions.size()) {
                builder.append(possibleProfessions.get(counter).job).append(", ");
                counter++;
            }
            String finalProfessions = "";
            if (builder.toString().length() > 0) {
                finalProfessions = builder.toString().substring(0, builder.toString().lastIndexOf(","));
            }
            context.write(new Text(personName + " : " + finalProfessions), new Text(""));
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if(args.length < 3) {
            throw new IllegalArgumentException("wrong inputs, example: professions.txt unknownusersPath outputPath");
        }

        Path professions = new Path(otherArgs[0]);
        Path inputPath = new Path(otherArgs[1]);
        Path outputPath = new Path(otherArgs[2]);


        //Add the file into the cache for the mapper to read
        DistributedCache.addCacheFile(professions.toUri(), conf);

        Job job = Job.getInstance(conf, "ClassifyUnknown");

        job.setJarByClass(ClassifyUnknown.class);

        job.setMapperClass(ClassifyUnknownMapper.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


}

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
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 */
public class CreateVector {

    public static class VectorCreatorMapper extends Mapper<LongWritable, Text, Text, VectorWritable> {

        private static HashMap<String, List<String>> professions = new HashMap<String, List<String>>();
        private static HashSet<String> dictionary = new HashSet<String>();
        private VectorWritable vectorWritable = new VectorWritable();


        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            Configuration conf = context.getConfiguration();


            URI[] cacheFiles = DistributedCache.getCacheFiles(conf);
            URI professionsFile = null;
            URI dictionaryFile = null;

            if (cacheFiles != null && cacheFiles.length > 0) {
                for (int i = 0; i < cacheFiles.length; i++) {
                    if (cacheFiles[i].toString().contains("profession.txt")) {
                        System.out.println("Found Professions File");
                        professionsFile = cacheFiles[i];
                    } else if (cacheFiles[i].toString().contains("dictionary")) {
                        System.out.println("Found Dictionary File");
                        dictionaryFile = cacheFiles[i];
                    }

                    if(professionsFile != null & dictionaryFile != null) {
                        break;
                    }
                }

                processNames(professionsFile.toString());
                createDictionary(dictionaryFile.toString());
            }
        }


        /**
         * Create hashmap of dictionary
         * @param dictionaryFile
         * @throws IOException
         */
        private static void createDictionary(String dictionaryFile) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(dictionaryFile));
            String line;
            while(reader.ready()) {
                line = reader.readLine().trim();
                if(!line.matches(".*\\d+.*") && !line.matches(".*\\W+.*") && line.length() > 3) {
                    dictionary.add(line);
                }
            }
            reader.close();
        }

        /**
         * Process names from professions.txt
         * @param professionsFile
         * @throws IOException
         */
        private static void processNames(String professionsFile) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(professionsFile));
            String line;
            while(reader.ready()) {
                line = reader.readLine();
                String[] splitProfessions = line.split(":");
                List<String> jobs = new ArrayList<String>();
                for(String job : splitProfessions[1].split(",")) {
                    jobs.add(job);
                }
                professions.put(splitProfessions[0].trim().toLowerCase(), jobs);
            }
            reader.close();
        }

        /**
         * Create a hashmap of word frequencies
         * @param wordFrequency
         * @return
         */
        private HashMap<String, Double> getWordHashMap(String[] wordFrequency) {
            HashMap<String, Double> map = new HashMap<String, Double>();
            for (int i = 0; i < wordFrequency.length; i++) {
                String toParse = wordFrequency[i].trim();
                String word = toParse.substring(0, toParse.lastIndexOf(" ")).trim();
                map.put(word, processNumeric(toParse.substring(toParse.lastIndexOf(" ")).trim()));
            }
            return map;
        }

        /**
         * Format word frequencies in numbers
         * @param data
         * @return
         */
        private double processNumeric(String data) {
            Double d = Double.NaN;
            if (isNumeric(data)) {
                try {
                    d = Double.parseDouble(data);
                }catch(NumberFormatException e) {

                }
            }
            //Ensure values are positive
            return Math.abs(d);
        }

        /**
         * Check if given string is a number and if not, grab the number in the string
         * @param str
         * @return
         */
        private static boolean isNumeric(String str)
        {
            NumberFormat formatter = NumberFormat.getInstance();
            ParsePosition parsePosition = new ParsePosition(0);
            formatter.parse(str, parsePosition);
            return str.length() == parsePosition.getIndex();
        }

        @Override
        public void map(LongWritable offset, Text input, Context context) throws IOException, InterruptedException {
            String[] parsedLine = input.toString().split("\t");
            String personName = parsedLine[0].trim().toLowerCase();
            String[] wordFrequency = parsedLine[1].replaceAll("[<>,]", " ").split("   ");
            HashMap<String, Double> wordFrequencyMap = getWordHashMap(wordFrequency);
            List<MahoutVector> vectors = new ArrayList<MahoutVector>();
            if(professions.containsKey(personName)) {
                List<String> professionVectors = professions.get(personName);
                for (String job : professionVectors) {
                    MahoutVector mahoutVector = new MahoutVector();
                    mahoutVector.classifier = job;
                    Vector vector = new RandomAccessSparseVector(dictionary.size(), dictionary.size());
                    int index = 0;
                    for (String word : dictionary) {
                        if (wordFrequencyMap.containsKey(word)) {
                            vector.set(index, wordFrequencyMap.get(word));
                        } else {
                            vector.set(index, 0.0);
                        }
                        index++;
                    }
                    mahoutVector.vector = vector;
                    vectors.add(mahoutVector);
                }
            }

            for (MahoutVector vector : vectors)
            {
                vectorWritable.set(vector.vector);
                context.write(new Text("/" + vector.classifier + "/"), vectorWritable);
            }

        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if(args.length < 4) {
            throw new IllegalArgumentException("wrong inputs, example: professions.txt dictionary.txt inputpath outputpath");
        }

        Path professions = new Path(otherArgs[0]);
        Path dictionaryPath = new Path(otherArgs[1]);
        Path inputPath = new Path(otherArgs[2]);
        Path outputPath = new Path(otherArgs[3]);


        //Add the file into the cache for the mapper to read
        DistributedCache.addCacheFile(professions.toUri(), conf);
        DistributedCache.addCacheFile(dictionaryPath.toUri(), conf);

        Job job = Job.getInstance(conf, "CreateVector");

        job.setJarByClass(CreateVector.class);

        job.setMapperClass(VectorCreatorMapper.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(VectorWritable.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


}

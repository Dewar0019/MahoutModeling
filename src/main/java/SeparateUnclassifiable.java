/**
 * Created by dewartan on 11/15/16.
 */
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
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;


public class SeparateUnclassifiable {

    public static class SeparateUnknownsMapper extends Mapper<LongWritable, Text, Text, Text> {

        private static HashSet<String> professions = new HashSet<>();

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


        private static void processNames(String professionsFile) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(professionsFile));
            String line;
            while(reader.ready()) {
                line = reader.readLine();
                String[] splitProfessions = line.split(":");
                professions.add(splitProfessions[0].trim().toLowerCase());
            }
            reader.close();
        }



        @Override
        public void map(LongWritable offset, Text input, Context context) throws IOException, InterruptedException {
            String[] parsedLine = input.toString().split("\t");
            String personName = parsedLine[0].trim().toLowerCase();
            //UnClassifiable
            if(!professions.contains(personName)) {
              context.write(new Text(personName), new Text(parsedLine[1]));
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        Path professions = new Path(otherArgs[0]);
        Path inputPath = new Path(otherArgs[1]);
        Path outputPath = new Path(otherArgs[2]);

        //Add the file into the cache for the mapper to read
        DistributedCache.addCacheFile(professions.toUri(), conf);

        Job job = Job.getInstance(conf, "SeparateUnclassifiable");

        job.setJarByClass(SeparateUnclassifiable.class);

        job.setMapperClass(SeparateUnknownsMapper.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);

        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


}

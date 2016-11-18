/**
 * Created by dewartan on 11/18/16.
 */
public class ProfessionFrequency implements Comparable {
    public String job;
    public int frequency;

    public ProfessionFrequency(String job, int frequency) {
        this.job = job;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Object o) {
        return frequency - ((ProfessionFrequency) o).frequency;
    }
}

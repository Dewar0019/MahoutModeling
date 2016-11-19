package model;

/**
 * Hadoop Group 15
 *
 * Allows sorting for retrieving the three most likely professions of the unclassifiables
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

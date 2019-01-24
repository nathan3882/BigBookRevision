package me.nathan3882;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class NotesFile implements Comparable<NotesFile> {

    private static Pattern pNumRegex = Pattern.compile("[pP](\\d+)");
    private File file;
    private String fileName;
    private LinkedList<NotesFile> otherSortedNotesFiles = new LinkedList<>();
    private int lowerBound = -1;
    private int upperBound = -1;
    private boolean sortedByLower;

    public NotesFile(File file, boolean trackOtherNotesFiles) {

        this.file = file;
        this.fileName = file.getName();
        this.lowerBound = getFileLowerBound();
        this.upperBound = getFileUpperBound();
        if (trackOtherNotesFiles) {
            for (File otherFile : new File(".").listFiles()) {
                if (BusinessRevision.get().isValidPagesFile(otherFile)) {
                    otherSortedNotesFiles.add(new NotesFile(otherFile, false));
                }
            }
        }
        sort(false);
    }

    public static NotesFile from(File file, boolean trackOtherNotesFiles) {
        return new NotesFile(file, trackOtherNotesFiles);
    }

    private void sort(boolean sortByLower) {
        this.sortedByLower = sortByLower;
        Collections.sort(otherSortedNotesFiles);
    }

    public void setSortedByLower(boolean sortedByLower) {
        this.sortedByLower = sortedByLower;
    }

    private int getPBound(boolean upperBound) {
        char[] chars = fileName.toCharArray();
        String pXNumbers = "  "; //Support for 1-9999 pages
        boolean foundLower = false;
        int bound = -1;
        for (int i = 0; i < chars.length; i++) {
            String charAt = String.valueOf(chars[i]);
            pXNumbers = pXNumbers.substring(1) + charAt;
            if (pNumRegex.matcher(pXNumbers).find()) { //found p with a number next to it
                if (upperBound) {
                    if (!foundLower) {
                        foundLower = true;
                    } else {
                        bound = i;
                        break;
                    }
                } else {
                    bound = i;
                    break;
                }
            }
        }
        return bound;
    }

    private int getBounds(boolean upperBound) {
        String restAfterP = fileName.split("\\.")[0].substring(getPBound(upperBound));
        String digits = restAfterP.split("\\s")[0];
        return Integer.parseInt(digits);
    }

    public int getFileLowerBound() {
        if (this.lowerBound == -1) {
            this.lowerBound = getBounds(false);
        }
        return lowerBound;
    }

    public int getFileUpperBound() {
        if (this.upperBound == -1) {
            this.upperBound = getBounds(true);
        }
        return this.upperBound;
    }

    /*
    Actually takes action, does upper bounds etcetera
     */
    public void act() {

    }

    public String getFileName() {
        return fileName;
    }

    public List<NotesFile> getOtherSortedNotesFiles() {
        return otherSortedNotesFiles;
    }

    public void lowerBoundTo(int newLowerBound, boolean checkOtherFiles) {
        int lower = getFileLowerBound();
        if (lower == newLowerBound) {
            System.err.println("Conflict occured");
            return;
        }
        File renamed = new File(getFileName().replace("p" + getFileLowerBound(), "p" + newLowerBound));
        getFile().renameTo(renamed);
        if (checkOtherFiles) {

        }
        //Update this.lowerBound
    }

    public void upperBoundTo(int newUpperBound, boolean checkOtherFiles) {
        if (getFileUpperBound() == newUpperBound) {
            System.err.println("Conflict occured");
            return;
        }
        File renamed = new File(getFileName().replace("p" + getFileUpperBound(), "p" + newUpperBound));
        this.file.renameTo(renamed);
        this.fileName = file.getName();
        this.upperBound = newUpperBound;

        if (checkOtherFiles) {
            sort(false); //Assure that the other notes files are sorted by the upper bound
            boolean onOrPastThisOne = false;
            int size = otherSortedNotesFiles.size();
            for (int i = 0; i < size; i++) {
                NotesFile current = otherSortedNotesFiles.get(i);
                int nextIndex = i + 1;
                NotesFile next = nextIndex >= size ? null : otherSortedNotesFiles.get(i + 1);
                int currentUpper;
                if (current.equals(this)) {
                    onOrPastThisOne = true;
                    currentUpper = newUpperBound;  //Same file as the one being changed, make upper the new one
                } else {
                    currentUpper = current.getFileUpperBound(); //Not same file, make upper the phyiscal file's upper
                }
                if (onOrPastThisOne && next != null) { //next != null prevents un required checks for being at the highest upperBound that has been set
                    int nextLower = next.getFileLowerBound();
                    if (currentUpper > nextLower) {
                        next.lowerBoundTo(currentUpper + 1, false); //+1 prevents multiple covering same page
                    }
                }
            }
        }
        //Update this.upperBound
    }
    public File getFile() {
        return file;
    }

    public boolean equals(NotesFile anotherNotesFile) {
        return this.getFileName().equals(anotherNotesFile.getFileName());
    }

    @Override
    public int compareTo(NotesFile o) {
        if (sortedByLower()) {
            return lowerBound - o.getFileLowerBound();
        } else { //comparing uppers
            return upperBound - o.getFileUpperBound();
        }
    }

    private boolean sortedByLower() {
        return this.sortedByLower;
    }
}

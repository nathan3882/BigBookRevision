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
        sort(true);
    }

    public static NotesFile from(File file, boolean trackOtherNotesFiles) {
        return new NotesFile(file, trackOtherNotesFiles);
    }

    private static int getPBound(String fileName, boolean upperBound) {
        char[] chars = fileName.toCharArray();
        String pXNumbers = "  ";
        boolean foundLower = false;
        int bound = -1;
        for (int i = 0; i < chars.length; i++) {
            String charAt = String.valueOf(chars[i]);
            pXNumbers = pXNumbers.substring(1) + charAt;
            if (pNumRegex.matcher(pXNumbers).find()) { //found p with a number next to it
                if (upperBound && !foundLower) {
                    foundLower = true;
                } else {
                    bound = i;
                    break;
                }
            }
        }
        return bound;
    }

    public static int getBounds(String fileName, boolean upperBound) {
        String restAfterP = fileName.split("\\.")[0].substring(getPBound(fileName, upperBound));
        String digits = restAfterP.split("\\s")[0];
        return Integer.parseInt(digits);
    }

    private static void changeFileInfo(NotesFile notesFile, File renamed, String name, int bound, boolean upper) {
        notesFile.file = renamed;
        notesFile.fileName = name;
        if (upper) {
            notesFile.upperBound = bound;
        } else {
            notesFile.lowerBound = bound;
        }
    }

    private void sort(boolean sortByLower) {
        this.sortedByLower = sortByLower;
        Collections.sort(otherSortedNotesFiles);
    }

    public void setSortedByLower(boolean sortedByLower) {
        this.sortedByLower = sortedByLower;
    }

    public int getPBound(boolean upperBound) {
        return getPBound(getFileName(), upperBound);
    }

    private int getBounds(boolean upperBound) {
        return getBounds(fileName, upperBound);
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

    public String getFileName() {
        return fileName;
    }

    public List<NotesFile> getOtherSortedNotesFiles() {
        return otherSortedNotesFiles;
    }

    public void lowerBoundTo(int newLowerBound, boolean checkOtherFiles) {
        int lower = getFileLowerBound();
        if (lower == newLowerBound) {
            System.err.println("Conflict occured when setting lower bounds (" + lower + " to " + newLowerBound + ")");
            return;
        }
        File renamed = new File(getFileName().replace("p" + getFileLowerBound(), "p" + newLowerBound));
        updateSorted(renamed, newLowerBound, false);
        this.file.renameTo(renamed);

        changeFileInfo(this, renamed, renamed.getName(), newLowerBound, false);

        if (checkOtherFiles) {
            sort(false);
            boolean onOrPastThisOne = false;
            int size = otherSortedNotesFiles.size();
            int completed = 1;
            for (int i = size - 1; i >= 0; i--) {
                NotesFile current = otherSortedNotesFiles.get(i);
                int oneBelowCurrentIndex = i - 1;
                NotesFile next = oneBelowCurrentIndex < 0 ? null : otherSortedNotesFiles.get(oneBelowCurrentIndex);
                int currentLower = newLowerBound;
                if (current.equals(this)) {
                    onOrPastThisOne = true;
                }
                if (onOrPastThisOne && next != null) { //next != null prevents un required checks for being at the highest upperBound that has been set
                    int nextUpper = next.getFileUpperBound();
                    if (currentLower < nextUpper) {
                        int nextNewUpper = currentLower - (completed); /*negate the deduction + add it too*/
                        System.out.println("change next to " + nextNewUpper + " because " + currentLower + " < " + nextUpper);
                        next.upperBoundTo(nextNewUpper, true); //+x prevents multiple covering same page
                        completed++;
                    }else{
                        System.out.println(currentLower + " > " + nextUpper);
                    }
                }
            }

        }
        doReverseCheck();
        //Update this.lowerBound
    }

    public void upperBoundTo(int newUpperBound, boolean checkOtherFiles) {
        if (getFileUpperBound() == newUpperBound) {
            System.err.println("Conflict occured");
            return;
        }
        File renamed = new File(getFileName().replace("p" + getFileUpperBound(), "p" + newUpperBound));
        //update other sorted to this updated obj

        updateSorted(renamed, newUpperBound, true);
        this.file.renameTo(renamed);

        changeFileInfo(this, renamed, renamed.getName(), newUpperBound, true);

        if (checkOtherFiles) {
            int completed = 1;
            sort(true); //Assure that the other notes files are sorted by the lower bound so can do lower bound checks
            boolean onOrPastThisOne = false;
            int size = otherSortedNotesFiles.size();
            for (int i = 0; i < size; i++) {
                NotesFile current = otherSortedNotesFiles.get(i);
                int nextIndex = i + 1;
                NotesFile next = nextIndex >= size ? null : otherSortedNotesFiles.get(nextIndex);
                int currentUpper = newUpperBound;
                if (current.equals(this)) {
                    onOrPastThisOne = true;
                }
                if (onOrPastThisOne && next != null) { //next != null prevents un required checks for being at the highest upperBound that has been set
                    int nextLower = next.getFileLowerBound();
                    if (currentUpper > nextLower) {
                        next.lowerBoundTo(currentUpper + completed, true); //+x prevents multiple covering same page
                        completed++;
                    }
                }
            }
        }
        doReverseCheck();
        //Update this.upperBound
    }

    private void doReverseCheck() {
        int lower = getFileLowerBound();
        if (lower > getFileUpperBound()) {
            upperBoundTo(lower, false);
        }
    }

    private void updateSorted(File renamed, int bound, boolean upper) {
        for (int i = 0; i < new LinkedList<>(otherSortedNotesFiles).size(); i++) {
            NotesFile file = otherSortedNotesFiles.get(i);
            if (file.equals(this)) { //equals the old version of this
                changeFileInfo(file, renamed, renamed.getName(), bound, upper); //update to new version
                otherSortedNotesFiles.set(i, file); //set to new version
                break; //break with updated version
            }
        }
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

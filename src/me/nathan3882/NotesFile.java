package me.nathan3882;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NotesFile {

    private static Pattern pNumRegex = Pattern.compile("[pP](\\d+)");
    private File file;
    private String fileName;
    private List<NotesFile> otherNotesFiles = new ArrayList<>();
    private int lowerBound = -1;
    private int upperBound = -1;

    public NotesFile(File file, boolean trackOtherNotesFiles) {
        this.file = file;
        this.fileName = file.getName();
        this.lowerBound = getFileLowerBound();
        this.upperBound = getFileUpperBound();
        if (trackOtherNotesFiles) {
            for (File otherFile : new File(".").listFiles()) {
                if (BusinessRevision.get().isValidPagesFile(otherFile)) {
                    otherNotesFiles.add(new NotesFile(otherFile, false));
                }
            }
        }
    }

    public static NotesFile from(File file, boolean trackOtherNotesFiles) {
        return new NotesFile(file, trackOtherNotesFiles);
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

    public List<NotesFile> getOtherNotesFiles() {
        return otherNotesFiles;
    }

    public void lowerBoundTo(int newLowerBound) {
        int lower = getFileLowerBound();
        if (lower == newLowerBound || lower == upperBound) {
            System.err.println("Conflict occured");
            return;
        }

        //Update this.lowerBound
    }

    public void upperBoundTo(int newUpperBound) {
        int upper = getFileUpperBound();
        if (upper == newUpperBound || upper == upperBound) {
            System.err.println("Conflict occured");
            return;
        }
        File renamed = new File(getFileName().replace("p" + getFileUpperBound(), "p" + newUpperBound));
        getFile().renameTo(renamed);

        //Update this.upperBound
    }

    public File getFile() {
        return file;
    }
}

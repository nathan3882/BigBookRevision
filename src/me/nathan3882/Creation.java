package me.nathan3882;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class Creation {

    private int pageCount;
    private int doneFileCount = 0;
    private long completionTimeMillis;
    private String fileLoc;
    private boolean abs;
    private String extension;
    private int chunk = 50;

    public Creation(String fileLoc, int pageCount, String extension, int chunk) {
        this.fileLoc = fileLoc;
        this.pageCount = pageCount;
        this.abs = fileLoc.equals("{abs}");
        this.extension = extension;
        this.chunk = chunk;
    }

    public Creation(String fileLoc, int pageCount) {
        this(fileLoc, pageCount, ".docx", 50); //Default to word doc format
    }

    public static Pattern getFileNameRegex() {
        return Pattern.compile("(p|P)\\d+\\s(to)\\s(p|P)\\d");
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getExtension() {
        return extension;
    }

    public boolean isAbs() {
        return abs;
    }

    public int getDoneFileCount() {
        return this.doneFileCount;
    }

    public void setDoneFileCount(int doneFileCount) {
        this.doneFileCount = doneFileCount;
    }

    public CompletionTime create() {
        long start = System.currentTimeMillis();
        CompletionTime completionTime = new CompletionTime();
        File fileLoc = new File(getFileLoc());
        if (fileLoc.isDirectory() || isAbs()) {
            //interval files to create = pageCount / 50;
            int chunk = getChunk();
            int upperBound = -1;

            int filesToCreate = getPageCount() / chunk; //5

            int leftOver = getPageCount() % chunk;

//            filesToCreate + "with" + leftOver + "left over"
            for (int done = 0; done < filesToCreate; done++) {
                int lowerBound = done * chunk;
                upperBound = lowerBound + chunk;
                createNotesFile(lowerBound, upperBound);
                setDoneFileCount(done);
            }
            createNotesFile(upperBound, upperBound + leftOver);
            setDoneFileCount(getDoneFileCount() + 2); //+ 1 negates that iteration started at 0, + another 1 accounts for the creation above
        } else {
            completionTime.setWasActuallyCompleted(false);
            System.out.println("Not actually a valid file location");
        }
        completionTime.setCompletionTimeMillis(System.currentTimeMillis() - start);
        return completionTime;
    }

    private void createNotesFile(int lowerBound, int upperBound) {
        String fName = generateFileName(lowerBound, upperBound) + getExtension();
        File file = isAbs() ? new File(fName) : new File(getFileLoc(), fName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateFileName(int lowerPageBound, int upperPageBound) {
        return "p" + lowerPageBound + " to p" + upperPageBound;
    }

    private int getChunk() {
        return chunk; //Lots of fifty ie a file "0-50" and "100-150"
    }

    public String getFileLoc() {
        return fileLoc;
    }

    public long getCompletionTimeMillis() {
        return completionTimeMillis;
    }
}

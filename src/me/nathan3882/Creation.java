package me.nathan3882;

import java.io.File;
import java.io.IOException;

public class Creation {

    private int pageCount;
    private int doneFileCount = 0;
    private long completionTimeMillis;
    private String fileLoc;
    private boolean abs;
    private String extension;

    public Creation(String fileLoc, int pageCount, String extension) {
        this.fileLoc = fileLoc;
        this.pageCount = pageCount;
        this.abs = fileLoc.equals("{abs}");
        this.extension = extension;
    }

    public Creation(String fileLoc, int pageCount) {
        this(fileLoc, pageCount, ".docx"); //Default to word doc format
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
            int regFilesToCreate = getPageCount() / chunk;
            int upperBound = -1;
            int leftOver = getPageCount() % chunk;
//            regFilesToCreate + "with" + leftOver + "left over"
            for (int done = 0; done < regFilesToCreate; done++) {
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
        return 50; //Lots of fifty ie a file "0-50" and "100-150"
    }

    public String getFileLoc() {
        return fileLoc;
    }

    public long getCompletionTimeMillis() {
        return completionTimeMillis;
    }
}

package me.nathan3882;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusinessRevision {

    private static Stage stage = getFirstStage();
    private static boolean done = false;
    private static BusinessRevision businessRevision = new BusinessRevision();
    private static Pattern changeBoundPattern = Pattern.compile("change (p|P)\\d+\\s(to)\\s(p|P)\\d");
    private String fileLoc;
    private String extension;
    private int chunk = 50; //Default to fifty
    private List<NotesFile> allFiles = new ArrayList<>();

    public static void main(String[] args) {
        for (File file : new File(".").listFiles()) {
            if (get().isValidPagesFile(file)) {
                get().getAllFiles().add(new NotesFile(file, true)); //true for second arg will allow page modifications
            }
        }

        while (!done) {
            System.out.println("Please enter info regarding stage : " + get().getStage() + " or alternatively type 'change px to px' to safely change an upper/lower bound!");
            if (get().getStage() == Stage.FILE) {
                System.out.println("Enter {abs} for directory jar is in");
            }
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNext()) {
                String entered = scanner.nextLine();
                System.out.println("ne = " + entered);
                if (isChangeFormat(entered)) {
                    int aBound = NotesFile.getBounds(entered, false);
                    int toThisBound = NotesFile.getBounds(entered, true);
                    boolean modLower = false;
                    NotesFile toMod = null;
                    for (NotesFile aFile : get().getAllFiles()) {
                        int lower = aFile.getFileLowerBound();
                        int upper = aFile.getFileUpperBound();
                        if (lower == aBound) { //change lower to "toThisBound"
                            modLower = true;
                        } else if (upper == aBound) { //change upper to "toThisBound"
                            modLower = false;
                        } else {
                            continue; //Wont excecute the code below if not valid NotesFile
                        }
                        toMod = aFile;
                        break;
                    }
                    if (toMod == null) break;
                    if (modLower) {
                        toMod.lowerBoundTo(toThisBound, true);
                    }else{
                        toMod.upperBoundTo(toThisBound, true);
                    }
                    continue;
                }
                Stage stage = get().getStage();
                if (stage == Stage.FILE) {
                    get().setFileLoc(entered.trim());
                    get().setStage(Stage.FILE_EXTENSION);
                } else if (stage == Stage.FILE_EXTENSION) {
                    get().setExtension("." + entered);
                    get().setStage(Stage.CHUNK);

                } else if (stage == Stage.CHUNK) {
                    try {
                        get().setChunk(Integer.parseInt(entered));
                    } catch (NumberFormatException exception) {
                        System.out.println("That's not a valid number");
                        continue;
                    }
                    get().setStage(Stage.NUMBER);
                } else if (stage == Stage.NUMBER) {
                    int pageCount;
                    try {
                        pageCount = Integer.parseInt(entered);
                        done = true;
                    } catch (NumberFormatException exception) {
                        exception.printStackTrace();
                        System.err.println("That is not a valid page count");
                        continue;
                    }

                    Creation creation = new Creation(get().getFileLoc(), pageCount, get().getExtension(), get().getChunk());

                    CompletionTime completionTime = creation.create();
                    System.out.println("Created " + creation.getDoneFileCount() + " files in... just under " + (completionTime.inSeconds() + 1) + " second/s (" + completionTime.inMillis() + " ms)!");
                }
            }
        }
    }

    private static boolean isChangeFormat(String entered) {
        return changeBoundPattern.matcher(entered).find();
    }

    public static BusinessRevision get() {
        return businessRevision;
    }

    private static Stage getFirstStage() {
        return Stage.FILE;
    }

    private List<NotesFile> getAllFiles() {
        return allFiles;
    }

    public boolean isValidPagesFile(File file) {
        String name = file.getName();
        String nameWithoutExt = name.split("\\.")[0];
        Pattern regex = Creation.getFileNameRegex();
        Matcher matcher = regex.matcher(nameWithoutExt);
        return matcher.find();
    }

    public String getExtension() {
        return extension;
    }

    private void setExtension(String extension) {
        this.extension = extension;
    }

    private String getFileLoc() {
        return fileLoc;
    }

    private void setFileLoc(String string) {
        fileLoc = string;
    }

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    private Stage getStage() {
        return stage;
    }

    private void setStage(Stage stage) {
        BusinessRevision.stage = stage;
    }
}
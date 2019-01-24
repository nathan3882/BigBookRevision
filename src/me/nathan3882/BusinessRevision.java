package me.nathan3882;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusinessRevision {

    private static Stage stage = getFirstStage();
    private static boolean done = false;
    private static String fileLoc;
    private static BusinessRevision businessRevision = new BusinessRevision();
    private String extension;
    private int chunk = 50; //Default to fifty

    private List<Map.Entry<Integer, Integer>> currentFilePages = new LinkedList<>();

    public static void main(String[] args) {
        Map<Integer, Integer> currentFiles = new HashMap<>();

        List<NotesFile> toAct = new ArrayList<>();
        for (File file : new File(".").listFiles()) {
            if (get().isValidPagesFile(file)) {
                NotesFile notesFile = new NotesFile(file, true);
                notesFile.upperBoundTo(145, true);
                toAct.add(notesFile);
                break;
            }
        }

        while (!done) {
            System.out.println("Please enter info regarding stage : " + get().getStage());
            if (get().getStage() == Stage.FILE) {
                System.out.println("Enter {abs} for directory jar is in");
            }
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNext()) {
                String entered = scanner.next();
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

    public static BusinessRevision get() {
        return businessRevision;
    }

    private static Stage getFirstStage() {
        return Stage.FILE;
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
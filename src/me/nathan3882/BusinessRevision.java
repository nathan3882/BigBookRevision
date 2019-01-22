package me.nathan3882;

import java.util.Scanner;

public class BusinessRevision {

    private static Stage stage = getFirstStage();
    private static boolean done = false;
    private static String fileLoc;
    private static BusinessRevision businessRevision = new BusinessRevision();
    private String extension;
    private int chunk;

    public static void main(String[] args) {

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
                    }catch(NumberFormatException exception) {
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

    public String getExtension() {
        return extension;
    }

    private void setExtension(String extension) {
        this.extension = extension;
    }

    private String getFileLoc() {
        return fileLoc;
    }

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    private void setFileLoc(String string) {
        fileLoc = string;
    }

    private Stage getStage() {
        return stage;
    }

    private void setStage(Stage stage) {
        BusinessRevision.stage = stage;
    }
}
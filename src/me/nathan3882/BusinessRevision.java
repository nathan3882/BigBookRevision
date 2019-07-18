package me.nathan3882;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusinessRevision {

    private static BusinessRevision businessRevision = new BusinessRevision();
    private Stage stage = getFirstStage();
    private boolean done = false;
    private Pattern changeBoundPattern = Pattern.compile("change (p|P)\\d+\\s(to)\\s(p|P)\\d");
    private String fileLoc;
    private String extension;
    private int chunk = 50; //Default to fifty
    private List<NotesFile> allFiles = new ArrayList<>();

    private JPanel cards;
    private SPanel activePanel;

    public static void main(String[] args) {
        doLookAndFeel();

        BusinessRevision bRevision = BusinessRevision.get();

        File sameDirectory = new File(".");
        File[] files = sameDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (bRevision.isValidPagesFile(file)) {
                    bRevision.getAllFiles().add(new NotesFile(bRevision, file, true)); //true for second arg will allow page modifications
                }
            }
        }

        while (!bRevision.done) {
            System.out.println("Please enter info regarding stage : " + bRevision.getStage() + " or alternatively type 'change px to px' to safely change an upper/lower bound!");
            if (bRevision.getStage() == Stage.FILE) {
                System.out.println("Enter {abs} for directory jar is in");
            }
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNext()) {
                String entered = scanner.nextLine();
                if (bRevision.isChangeFormat(entered)) {
                    int fromThis = NotesFile.getBounds(entered, false);
                    int toThis = NotesFile.getBounds(entered, true);
                    bRevision.performChange(fromThis, toThis);
                    continue;
                }
                Stage stage = bRevision.getStage();
                if (stage == Stage.FILE) {
                    bRevision.setFileLoc(entered.trim());
                    bRevision.setStage(Stage.FILE_EXTENSION);
                } else if (stage == Stage.FILE_EXTENSION) {
                    bRevision.setExtension("." + entered);
                    bRevision.setStage(Stage.CHUNK);

                } else if (stage == Stage.CHUNK) {
                    try {
                        bRevision.setChunk(Integer.parseInt(entered));
                    } catch (NumberFormatException exception) {
                        System.out.println("That's not a valid number");
                        continue;
                    }
                    bRevision.setStage(Stage.NUMBER);
                } else if (stage == Stage.NUMBER) {
                    int pageCount;
                    try {
                        pageCount = Integer.parseInt(entered);
                        bRevision.done = true;
                    } catch (NumberFormatException exception) {
                        exception.printStackTrace();
                        System.err.println("That is not a valid page count");
                        continue;
                    }

                    Creation creation = new Creation(bRevision.getFileLoc(), pageCount, bRevision.getExtension(), bRevision.getChunk());

                    CompletionTime completionTime = creation.create();
                    System.out.println("Created " + creation.getDoneFileCount() + " files in... just under " + (completionTime.inSeconds() + 1) + " second/s (" + completionTime.inMillis() + " ms)!");
                }
            }
        }
    }

    private static void doLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static BusinessRevision get() {
        return businessRevision;
    }

    public boolean isValidPagesFile(File file) {
        String name = file.getName();
        String nameWithoutExt = name.split("\\.")[0];
        Pattern regex = Creation.getFileNameRegex();
        Matcher matcher = regex.matcher(nameWithoutExt);
        return matcher.find();
    }

    public void openPanel(SPanel panel) {
        if (getActivePanelClass() == null) {
            setActivePanelClass(panel);
        }
        CardLayout cardLayout = (CardLayout) (cards.getLayout());
        cardLayout.show(cards, panel.getPanelReferenceName());
        this.activePanel = panel;
        cards.revalidate();
    }

    private void initFrame() {
        JFrame frame = new JFrame("Revision File Manager");
        frame.setContentPane(get().getCards());

        DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        int frameHeight = 500;
        int frameWidth = 750;
        frame.setLocation(new Point(mode.getWidth() / 2 - (frameWidth / 2), mode.getHeight() / 2 - (frameHeight / 2)));
        frame.setPreferredSize(new Dimension(frameWidth, frameHeight));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void performChange(int fromThis, int toThis) {
        boolean modLower = false;
        NotesFile toMod = null;
        for (NotesFile aFile : get().getAllFiles()) {
            int lower = aFile.getFileLowerBound();
            int upper = aFile.getFileUpperBound();
            if (lower == fromThis) { //change lower to "toThisBound"
                modLower = true;
            } else if (upper == fromThis) { //change upper to "toThisBound"
                modLower = false;
            } else {
                continue; //Wont excecute the code below if not valid NotesFile
            }
            toMod = aFile;
            break;
        }
        if (toMod != null) {
            if (modLower) {
                toMod.lowerBoundTo(toThis, true);
            } else {
                toMod.upperBoundTo(toThis, true);
            }
        }
    }

    private boolean isChangeFormat(String entered) {
        return changeBoundPattern.matcher(entered).find();
    }

    private void addPanelToCard(SPanel panel) {
        if (get().getCards() == null) {
            get().setCards(new JPanel(new CardLayout()));
        }
        get().getCards().add(panel.getPanel(), panel.getPanelReferenceName());
    }

    private Stage getFirstStage() {
        return Stage.FILE;
    }

    private List<NotesFile> getAllFiles() {
        return allFiles;
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
        this.stage = stage;
    }

    public JPanel getCards() {
        return cards;
    }

    public void setCards(JPanel cards) {
        this.cards = cards;
    }

    public SPanel getActivePanelClass() {
        return this.activePanel;
    }

    public void setActivePanelClass(SPanel sPanel) {
        this.activePanel = sPanel;
    }
}
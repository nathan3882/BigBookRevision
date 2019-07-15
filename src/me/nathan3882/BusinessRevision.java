package me.nathan3882;

import me.nathan3882.forms.CoreForm;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusinessRevision {

    private Stage stage = getFirstStage();
    private boolean done = false;
    private static BusinessRevision businessRevision = new BusinessRevision();
    private Pattern changeBoundPattern = Pattern.compile("change (p|P)\\d+\\s(to)\\s(p|P)\\d");
    private String fileLoc;
    private String extension;
    private int chunk = 50; //Default to fifty
    private List<NotesFile> allFiles = new ArrayList<>();

    private JPanel cards;
    private SPanel activePanel;
    private CoreForm coreForm;

    public static void main(String[] args) {
        doLookAndFeel();
        BusinessRevision inst = BusinessRevision.get();
        for (File file : new File(".").listFiles()) {
            if (get().isValidPagesFile(file)) {
                get().getAllFiles().add(new NotesFile(inst, file, true)); //true for second arg will allow page modifications
            }
        }



        CoreForm coreForm = new CoreForm(inst);
        inst.coreForm = coreForm;
        inst.addPanelToCard(coreForm);

        inst.openPanel(coreForm);

        inst.initFrame();
        while (!inst.done) {
            System.out.println("Please enter info regarding stage : " + inst.getStage() + " or alternatively type 'change px to px' to safely change an upper/lower bound!");
            if (inst.getStage() == Stage.FILE) {
                System.out.println("Enter {abs} for directory jar is in");
            }
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNext()) {
                String entered = scanner.nextLine();
                if (inst.isChangeFormat(entered)) {
                    int fromThis = NotesFile.getBounds(entered, false);
                    int toThis = NotesFile.getBounds(entered, true);
                    inst.performChange(fromThis, toThis);
                    continue;
                }
                Stage stage = inst.getStage();
                if (stage == Stage.FILE) {
                    inst.setFileLoc(entered.trim());
                    inst.setStage(Stage.FILE_EXTENSION);
                } else if (stage == Stage.FILE_EXTENSION) {
                    inst.setExtension("." + entered);
                    inst.setStage(Stage.CHUNK);

                } else if (stage == Stage.CHUNK) {
                    try {
                        inst.setChunk(Integer.parseInt(entered));
                    } catch (NumberFormatException exception) {
                        System.out.println("That's not a valid number");
                        continue;
                    }
                    inst.setStage(Stage.NUMBER);
                } else if (stage == Stage.NUMBER) {
                    int pageCount;
                    try {
                        pageCount = Integer.parseInt(entered);
                        inst.done = true;
                    } catch (NumberFormatException exception) {
                        exception.printStackTrace();
                        System.err.println("That is not a valid page count");
                        continue;
                    }

                    Creation creation = new Creation(inst.getFileLoc(), pageCount, inst.getExtension(), inst.getChunk());

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

    public static BusinessRevision get() {
        return businessRevision;
    }

    private Stage getFirstStage() {
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
        this.stage = stage;
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

    public void setCards(JPanel cards) {
        this.cards = cards;
    }

    public JPanel getCards() {
        return cards;
    }

    private void addPanelToCard(SPanel panel) {
        if (get().getCards() == null) {
            get().setCards(new JPanel(new CardLayout()));
        }
        get().cards.add(panel.getPanel(), panel.getPanelReferenceName());
    }

    public void setActivePanelClass(SPanel sPanel) {
        this.activePanel = sPanel;
    }

    public SPanel getActivePanelClass() {
        return this.activePanel;
    }
}
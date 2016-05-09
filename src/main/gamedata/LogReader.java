package main.gamedata;

import main.deckdetection.Detect;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LogReader {
    public int opponentPlayerNo = 0;
    public String opponentClass = "";
    public List<String> opponentPlayedCards = new ArrayList<>();
    public int logLineNumber = 1;

    public LogReader() {

    }

    public void readHearthstoneLog() {
        List<String> logOutput = getHearthstoneLogContent("C:/Program Files (x86)/Hearthstone/Hearthstone_Data/output_log.txt");
        String line;

        assert logOutput != null;
        while (logOutput.size() > logLineNumber) {
            line = logOutput.get(logLineNumber);

            if(line.contains("JUST_PLAYED") && line.contains("player=" + opponentPlayerNo)) {

                if (isCardPartOfDeck(line)) {
                    // Add card to the playedCardList
                    String card = opponentPlayedCard(line);
                    System.out.println("Played: " + card);
                    opponentPlayedCards.add(card);
                    Detect detect = new Detect(opponentPlayedCards, opponentClass);
                    detect.run();

                } else {
                    // Don't add it.
                    System.out.println("Played (dscovered card): " + opponentPlayedCard(line));
                }
            }

            // Get player number and opponent hero class/name.
            else if (opponentPlayerNo == 0 && opponentClass.equals("") && line.contains("to OPPOSING PLAY") && line.contains("player=") && line.contains("HERO")) {
                heroOrPlayerNoDetected(line);
            }

            else if(line.contains("[Zone] ZoneChangeList.ProcessChanges() - processing index=0 change=powerTask=[power=[game=id=")) {
                newGameDetected();
            }

            logLineNumber++;
        }
    }

    private List<String> getHearthstoneLogContent(String pathToFile) {
        try {
            File file = new File(pathToFile);
            if (file.exists() && !file.isDirectory()) {
                return Files.readAllLines(Paths.get(pathToFile));
            } else {
                // TODO:(Kristian)
                // The out_log.txt file does not exist, we should notify the user with a warning pop up.
                System.out.println(pathToFile + " does not exist!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String heroNameToClassName(String heroName) {
        switch (heroName) {
            case "Anduin Wrynn":
                return "Priest";

            case "Malfurion Stormrage":
                return "Druid";

            case "Thrall":
                return "Shaman";

            case "Rexxar":
                return "Hunter";

            case "Jaina Proudmoore":
                return "Mage";

            case "Uther Lightbringer":
                return "Paladin";

            case "Valeera Sanguinar":
                return "Rogue";

            case "Gul'dan":
                return "Warlock";

            case "Garrosh Hellscream":
                return "Warrior";

            // Alternate Heroes

            case "Alleria Windrunner":
                return "Hunter";

            case "Medivh":
                return "Mage";

            case "Magni Bronzebeard":
                return "Warrior";

            case "Lady Liadrin":
                return "Paladin";

            case "Khadgar":
                return "Mage";

            default:
                return "Lord Jaraxxus or Ragnaros the Firelord played?";
        }
    }

    private void newGameDetected() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                System.out.println("Your OS is not windows, clearing the terminal by printing 50 empty new lines.");
                for (int i = 0; i < 50; i++) {
                    System.out.println();
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("NEW GAME\n");

        opponentPlayedCards.clear();
        opponentPlayerNo = 0;
        opponentClass = "";
    }

    private void heroOrPlayerNoDetected(String line) {
        // Hero name of the opponent is determined here.
        String heroName = line;
        heroName = heroName.substring((heroName.indexOf("name=") + 5), (heroName.indexOf("id=") - 1));
        opponentClass = heroNameToClassName(heroName);
        System.out.println("Against: " + opponentClass + ".");

        // Player number of the opponent is determined here.
        line = line.substring((line.indexOf("player=") + 7), line.indexOf("] to"));
        opponentPlayerNo = Integer.valueOf(line);
    }

    private String opponentPlayedCard(String line) {
        // Card the opponent just played.
        return line.substring((line.indexOf("name=") + 5), line.indexOf("] tag=JUST_PLAYED"));
    }

    private Boolean isCardPartOfDeck(String line) {
        // If the card is obtained by the discover mechanic, then it's id is over 67.
        line = line.substring((line.indexOf("entity=[id=") + 11), (line.indexOf(" cardId=")));
        int cardId = Integer.valueOf(line);

        return cardId <= 67;
    }

    private String getOpponentBattlenetName(String line) {
        return line.substring((line.indexOf("name=") + 5), line.indexOf("] tag=MU"));
    }
}
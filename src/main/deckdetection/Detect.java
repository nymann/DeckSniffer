package main.deckdetection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Detect {
    private String opponentClass;
    private List<String> opponentPlayedCards;

    public Detect(List<String> opponentPlayedCards, String opponentClass) {
        this.opponentClass = opponentClass;
        this.opponentPlayedCards = opponentPlayedCards;
    }

    public void run() {
        List<String> allClassDecks = listAllClassDecks();
        Map<Float, String> map = new TreeMap<>(Collections.reverseOrder());
        for (String deckName : allClassDecks) {
            List<String> deckToTest = fromFileToList(deckName);
            float accuracy = accuracyOfDeck(deckToTest);
            System.out.println(deckName + " has an accuracy of " + accuracy + "%. There shouls be " + allClassDecks.size() + "");
            map.put(accuracy, deckName);
        }

        for(Map.Entry<Float, String> entry : map.entrySet()) {
            Float key = entry.getKey();
            String value = entry.getValue();
            String formattedKey = String.format("%.02f", key);
            System.out.println("\t" + value + "\t(" + formattedKey + "%)");
        }

        System.out.println("\t\t\tMap size: " + map.size());
    }

    private List<String> listAllClassDecks() {
        File folder = new File("decks/" + opponentClass);
        File[] listOfFiles = folder.listFiles();

        String deckName;
        List<String> allClassDecks = new ArrayList<>();

        assert listOfFiles != null;
        for (File file: listOfFiles) {
            if (file.isFile()) {
                deckName = file.getName();
                deckName = deckName.substring(0, deckName.indexOf("."));
                allClassDecks.add(deckName);
            }
        }

        return allClassDecks;
    }

    private List<String> fromFileToList(String deckName) {
        try {
            return Files.readAllLines(Paths.get("decks/" + opponentClass + "/" + deckName + ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private float accuracyOfDeck(List<String> deckToTest) {
        int correct = 0;
        int total = opponentPlayedCards.size();
        //System.out.println("__");
        for (String playedCard: opponentPlayedCards) {
            if (doesDeckContainCard(deckToTest, playedCard)) {
                //System.out.println(playedCard + " is a part of the deck.");
                correct++;
            }
            //System.out.println(correct + "/" + total);
        }

        return ((((float) correct) / ((float) total)) * 100);
    }

    private Boolean doesDeckContainCard(List<String> deckToTest, String playedCard) {
        for (String line : deckToTest) {
            if (line.contains(playedCard)) {
                return true;
            }
        }
        return false;
    }
}

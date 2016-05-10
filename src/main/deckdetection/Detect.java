package main.deckdetection;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Ordering;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Detect {
    private String opponentClass;
    private List<String> opponentPlayedCards;
    private Boolean oneCopyOfCardAlreadyRegistered = false;

    public Detect(List<String> opponentPlayedCards, String opponentClass) {
        this.opponentClass = opponentClass;
        this.opponentPlayedCards = opponentPlayedCards;
    }

    public void run() {
        List<String> allClassDecks = listAllClassDecks();
        float temp = 0;
        String highestAccuracyDeckName = "";

        ListMultimap<Float, String> multiMap = MultimapBuilder.treeKeys().arrayListValues().build();
        for (String deckName : allClassDecks) {
            List<String> deckToTest = fromFileToList(deckName);
            float accuracy = accuracyOfDeck(deckToTest);
            multiMap.put(accuracy, deckName);
        }

        // sort the list.
        ImmutableListMultimap<Float, String> immutableListMultimap = new ImmutableListMultimap.Builder<Float, String>()
                .orderKeysBy(Ordering.natural().reversed())
                .putAll(multiMap)
                .build();

        System.out.println("-----------------------------");
        System.out.println("\tDeckname\tAccuracy");
        System.out.println("-----------------------------");
        for (Map.Entry<Float, String> entry : immutableListMultimap.entries()) {
            String value = entry.getValue();
            Float key = entry.getKey();
            String formattedKey = String.format("%.01f", key);

            if (key > temp) {
                temp = key;
                highestAccuracyDeckName = value;
            }

            if (value.length() < 3) {
                System.out.println("\t" + value + "\t\t\t(" + formattedKey + "%)");
            }
            else if (value.length() < 8) {
                System.out.println("\t" + value + "\t\t(" + formattedKey + "%)");
            } else {
                System.out.println("\t" + value + "\t(" + formattedKey + "%)");
            }
        }

        // Call function that prints out the remaining cards contained in the highest accuracy deck.
        if (temp == 0) {
            System.out.println("Deck is probably not in our local database.");
        }
        else {
            List<String> opponentsDeck = fromFileToList(highestAccuracyDeckName);
            assert opponentsDeck != null;
            System.out.println("-----------------------------");
            System.out.println("Predict. remaining (" + opponentPlayedCards.size() + "/30)");
            System.out.println("-----------------------------");
            printRemainingCard(opponentsDeck);
            System.out.println("-----------------------------");
        }
    }

    private List<String> listAllClassDecks() {
        File folder = new File("decks/" + opponentClass);
        File[] listOfFiles = folder.listFiles();

        String deckName;
        List<String> allClassDecks = new ArrayList<>();

        assert listOfFiles != null;
        for (File file : listOfFiles) {
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
        for (String playedCard : opponentPlayedCards) {
            if (doesDeckContainCard(deckToTest, playedCard)) {
                correct++;
            }
        }

        return ((((float) correct) / ((float) total)) * 100);
    }

    private Boolean doesDeckContainCard(List<String> deckToTest, String playedCard) {
        int howManyTimesDidWePlayThisCard = countOccurrencesPlayedCards(opponentPlayedCards, playedCard);

        for (String line : deckToTest) {
            if (line.contains(playedCard) && line.contains("2")) {
                return true;
            }
            else if(line.contains(playedCard) && line.contains("1") && howManyTimesDidWePlayThisCard != 2) {
                return true;
            }
            else if(line.contains(playedCard) && line.contains("1") && !oneCopyOfCardAlreadyRegistered) {
                oneCopyOfCardAlreadyRegistered = !oneCopyOfCardAlreadyRegistered;
                return true;
            }
        }
        return false;
    }

    private int countOccurrencesPlayedCards(List<String> deck, String card) {
        return Collections.frequency(deck, card);
    }

    private void printRemainingCard(List<String> predictedDeck) {
        for (String card : predictedDeck) {
            String cardName = card.substring(2);
            int count = countOccurrencesPlayedCards(opponentPlayedCards, cardName);

            if ((card.contains("1") && count == 1) || (card.contains("2") && count == 2)) {
                System.out.print("");
            }
            else if((card.contains("1") && count == 2)) {
                System.out.println("-" + card + ("!"));
            }
            else if((card.contains("1") || card.contains("2")) && count == 0) {
                System.out.println(card);
            }
            else if(card.contains("2") && count == 1) {
                String temp = card.replace("2", "1");
                System.out.println(temp);
            }
            else {
                System.out.println(card);
            }

        }
    }
}

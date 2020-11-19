import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Cryptogram {

    static final String filename = "dictionary.txt";            /* Name of dictionary file */
    static ArrayList<String> inputWords;                        /* ArrayList of encoded input words */
    static HashMap<Integer, ArrayList<String>> lookupByLength;  /* Map of dictionary word length -> words list */
    static String alphabet = "abcdefghijklmnopqrstuvwxyz";      /* Used for building decoders */
    static ArrayList<String> decodedStrings;                    /* ArrayList of all decoded strings */
    static String input;                                        /* Encoded user input */

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        // Setup
        lookupByLength = new HashMap<>();
        decodedStrings = new ArrayList<>();

        // Build dictionary
        buildLineLengthDictionary();

        // Get input, split into words, make arraylist
        input = scan.nextLine();
        //input = "boz wp o xop w zob"; // example input
        //input = "dww ikeaihaz skl"; // example input
        String[] inputArray = input.split(" ");
        inputWords = new ArrayList<>(Arrays.asList(inputArray));

        // Solve!
        solve(inputWords);

        // Print output to console
        System.out.println(decodedStrings.size());
        decodedStrings.forEach(System.out::println);
    }

    /**
     * This method recursively builds potential strings.
     *
     * @param inputWords        arraylist of all encoded input words
     * @param decodedWordString potential decoded string of words
     * @param decoder           decoder map
     * @param inputWordIndex    tracks which input word we are working on
     * @return true once the potential string and the input string
     * are the same length. Returns false if no match is possible.
     */
    static boolean solve(ArrayList<String> inputWords, String decodedWordString,
                         HashMap<Character, Character> decoder, int inputWordIndex) {
        boolean foundMatch = false;

        // If these are the same length, then we have found a valid mapping and "decoded"
        // an entire string.
        if (input.length() == decodedWordString.length()) {
            decodedStrings.add(decodedWordString);
            foundMatch = true;
        }
        else {
            // Get all words of same length (prune)
            ArrayList<String> wordsOfSameLength
                    = lookupByLength.get(inputWords.get(inputWordIndex).length());

            // Now, iterate through all possible words, in alphabetical order
            wordsOfSameLength.forEach(possibleWord -> {

                // !! Must make fresh decoder here, so there exists no overwrites
                HashMap<Character, Character> newDecoder = getDecoder();
                newDecoder.putAll(decoder);

                if (updateDecoder(newDecoder, inputWords.get(inputWordIndex), possibleWord)) {
                    // We know this is valid option now, add this word to decoded words
                    String updatedDecodedWords = decodedWordString.concat(" " + possibleWord);
                    solve(inputWords, updatedDecodedWords, newDecoder, inputWordIndex + 1);
                }
            });
        }
        return foundMatch;
    }

    /**
     * Helper to setup/begin solve method
     *
     * @param inputWords arraylist of encoded input words
     */
    static void solve(ArrayList<String> inputWords) {
        // Get all words of same length (prune)
        ArrayList<String> wordsOfSameLength
                = lookupByLength.get(inputWords.get(0).length());

        // Now, iterate through all possible words, in alphabetical order
        wordsOfSameLength.forEach(possibleWord -> {
            HashMap<Character, Character> decoder = getDecoder();
            if (updateDecoder(decoder, inputWords.get(0), possibleWord)) {
                solve(inputWords, possibleWord, decoder, 1);
            }
        });
    }

    /**
     * This method updates the decoder mapping.
     *
     * @param decoder      the mapping for this instance of solve
     * @param inputWord    the encoded word we are verifying
     * @param possibleWord the potential word that might match
     * @return true if all characters map correctly.
     */
    static boolean updateDecoder(HashMap<Character, Character> decoder,
                                 String inputWord, String possibleWord) {
        char[] inputWordArray = inputWord.toCharArray();            // Single word from input
        char[] possibleWordArray = possibleWord.toCharArray();      // Possible word from dictionary
        boolean isPossibleEncoding = true;                          // is this a valid encoding?

        // Iterate through each char in possible word
        for (int i = 0; i < inputWord.length(); i++) {

            // If decoder value hasn't been assigned yet ("!")
            // and value isn't used for any other key, assign it
            if ((decoder.get(inputWordArray[i]) == '!')
                    && !decoder.containsValue(possibleWordArray[i])) {
                decoder.replace(inputWordArray[i], possibleWordArray[i]);
            }
            // else if already assigned to another character, this substitution is not valid
            else if ((decoder.get(inputWordArray[i]) != possibleWordArray[i])) {
                isPossibleEncoding = false;
                break;
            }
        }
        return isPossibleEncoding;
    }

    /**
     * Builds creates an instance of a decoder Hashmap to track decoding.
     *
     * @return new HashMap (decoder)
     */
    static HashMap<Character, Character> getDecoder() {
        HashMap<Character, Character> decoder = new HashMap<>();
        for (char c : alphabet.toCharArray()) {
            // We will make "!" represent unassigned element
            decoder.put(c, '!');
        }
        return decoder;
    }

    /**
     * Builds a map so we can quickly look up words by length.
     */
    static void buildLineLengthDictionary() {
        try {
            BufferedReader bufferedReader
                    = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (lookupByLength.containsKey(line.length())) {
                    lookupByLength.get(line.length()).add(line);
                }
                else {
                    ArrayList<String> newList = new ArrayList<>();
                    newList.add(line);
                    lookupByLength.put(line.length(), newList);
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

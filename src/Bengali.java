/**
 * Created by Reyad on 10/14/2018.
 */


public class Bengali implements Language {
    private final int START_LETTER = 0x0980;
    private final int END_LETTER = 0x09FF;
    private final int MAX = END_LETTER - START_LETTER + 1;
    private final int MAX_POSSIBLE_LENGTH_OF_WORD = 1024;
    private final int MAX_POSSIBLE_WORD_IN_SUGGESTION = (1<<18);


    @Override
    public int maxLetter() {
        return MAX;
    }

    @Override
    public int startLetter() {
        return START_LETTER;
    }

    @Override
    public int endLetter() {
        return END_LETTER;
    }

    @Override
    public int mapToInt(int c) {
        return c;
    }

    @Override
    public int retrieveLetter(int v) {
        return v;
    }

    @Override
    public int maxPossibleWordDepth() {
        return MAX_POSSIBLE_LENGTH_OF_WORD;
    }

    @Override
    public int maxPossibleWordInSuggestion() {
        return MAX_POSSIBLE_WORD_IN_SUGGESTION;
    }

    @Override
    public boolean isLetter(int c) {
        return (START_LETTER <= c && c <= END_LETTER);
    }
}

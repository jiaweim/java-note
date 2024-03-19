package mjw.java.concurrency.forkjoin;

import java.util.Random;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 19 Mar 2024, 3:35 PM
 */
public class DocumentMock {

    private String[] words = {"the", "hello", "goodbye", "packt",
            "java", "thread", "pool", "random", "class", "main"};

    public String[][] generateDocument(int numLines, int numWords,
            String word) {
        int counter = 0;
        String[][] document = new String[numLines][numWords];
        Random random = new Random();
        // 随机填充
        for (int i = 0; i < numLines; i++) {
            for (int j = 0; j < numWords; j++) {
                int index = random.nextInt(words.length);
                document[i][j] = words[index];
                if (document[i][j].equals(word)) {
                    counter++;
                }
            }
        }
        System.out.println("DocumentMock: The word appears " + counter
                + " times in the document");
        return document;
    }
}

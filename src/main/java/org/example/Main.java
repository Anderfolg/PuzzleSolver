package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main( String[] args ) {
        List<String> fragments1 = readFragmentsFromFile("src/main/resources/source.txt");
        String longest = findLongestSequence(fragments1);
        System.out.println("Longest sequence1: " + longest);

    }

    public static List<String> readFragmentsFromFile( String fileName ) {
        List<String> fragments = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if ( !line.isEmpty() && line.matches("\\d+") ) { // Ensure the line contains only digits
                    fragments.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return fragments;
    }

    /**findLongestSequence(List<String> fragments): Основний метод,
     *  який приймає список фрагментів та повертає найдовшу можливу послідовність.

     Створює два Trie: prefixTrie для префіксів та suffixTrie для суфіксів.
     Заповнює Trie префіксами та суфіксами фрагментів.
     Ітерується по всіх фрагментах, запускаючи для кожного рекурсивний пошук findSequence.
     Зберігає найдовшу знайдену послідовність.
     */
    public static String findLongestSequence( List<String> fragments ) {
        int n = fragments.size();
        Trie prefixTrie = new Trie();
        Trie suffixTrie = new Trie();

        for (int i = 0; i < n; i++) {
            String fragment = fragments.get(i);
            if ( fragment.length() >= 2 ) {
                prefixTrie.insert(fragment.substring(0, 2), i);
                suffixTrie.insert(fragment.substring(fragment.length() - 2), i);
            }
        }

        String longestSequence = "";
        Set<Integer> used = new HashSet<>();
        for (int start = 0; start < n; start++) {
            String currentSequence = fragments.get(start);
            used.clear();
            used.add(start);
            String sequence = findSequence(fragments, prefixTrie, currentSequence, used);
            if ( sequence.length() > longestSequence.length() ) {
                longestSequence = sequence;
            }
        }
        return longestSequence;
    }

    /**findSequence(List<String> fragments, Trie prefixTrie, String currentSequence, Set<Integer> used):
     *Рекурсивний метод для пошуку послідовності.

     currentSequence: Поточна побудована послідовність.
     used: Множина індексів вже використаних фрагментів.
     Знаходить останні дві цифри currentSequence.
     Шукає в prefixTrie всі фрагменти, які починаються з цих двох цифр.
     Для кожного знайденого фрагмента, якщо він ще не використаний:
     Додає індекс фрагмента до used.
     Рекурсивно викликає findSequence з новою послідовністю.
     Видаляє індекс фрагмента з used (backtracking).
     Повертає найдовшу знайдену послідовність.
     */
    private static String findSequence( List<String> fragments, Trie prefixTrie, String currentSequence, Set<Integer> used ) {
        if ( currentSequence.length() < 2 ) return currentSequence;

        String lastTwo = currentSequence.substring(currentSequence.length() - 2);
        List<Integer> nextIndices = prefixTrie.find(lastTwo);

        String longest = currentSequence;

        for (int nextIndex : nextIndices) {
            if ( !used.contains(nextIndex) ) {
                used.add(nextIndex);
                String nextFragment = fragments.get(nextIndex);

                String newSequence = currentSequence.substring(0, currentSequence.length() - 2) + nextFragment;

                String seq = findSequence(fragments, prefixTrie, newSequence, used);
                if ( seq.length() > longest.length() ) {
                    longest = seq;
                }
                used.remove(nextIndex);
            }
        }

        return longest;
    }
}

/**
 * TrieNode містить два поля:
 * children: Це відображення (HashMap),
 * де ключем є префікс (або суфікс) рядка, а значенням — наступний вузол Trie.
 * Воно представляє зв'язки між вузлами в дереві.
 * indices: Це список індексів фрагментів, які закінчуються на даний префікс (або суфікс).
 * Він потрібен для відстеження, які фрагменти вже використані в послідовності.
 */
class TrieNode {
    Map<String, TrieNode> children = new HashMap<>();
    List<Integer> indices = new ArrayList<>(); // Зберігаємо індекси фрагментів
}

/**
 * Trie містить поле:
 * root: Кореневий вузол дерева Trie.
 * insert(String s, int index): Метод для вставки рядка s (префікса або суфікса) в Trie. index — це індекс фрагмента у вхідному списку. Він проходить по символах рядка s, створюючи нові вузли, якщо їх немає, і зберігає індекс фрагмента в останньому вузлі.
 * find(String prefix): Метод для пошуку всіх індексів фрагментів,
 * які починаються з заданого prefix. Він проходить по дереву Trie,
 * і якщо знаходить відповідний префікс, повертає список індексів.
 */
class Trie {
    TrieNode root = new TrieNode();

    public void insert( String s, int index ) {
        TrieNode current = root;
        for (int i = 0; i < s.length(); i++) {
            String prefix = s.substring(0, i + 1);
            current.children.putIfAbsent(prefix, new TrieNode());
            current = current.children.get(prefix);
        }
        current.indices.add(index);
    }

    public List<Integer> find( String prefix ) {
        TrieNode current = root;
        for (int i = 0; i < prefix.length(); i++) {
            String sub = prefix.substring(0, i + 1);
            if ( !current.children.containsKey(sub) ) {
                return new ArrayList<>();
            }
            current = current.children.get(sub);
        }
        return current.indices;
    }
}


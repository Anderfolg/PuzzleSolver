package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        /*List<String> fragments = Arrays.asList("942517", "605676", "498291", "668826", "357057", "478151", "315629", "007148", "252887", "421662",
                "284505", "467650", "115330", "648206", "207562", "612298", "576885", "294200", "847595", "021597",
                "074878", "801997", "585401", "168510", "385293", "151863", "022142", "340350", "976151", "337989",
                "863284", "488310", "303887", "939173", "331413", "905657", "833617", "170794", "094486", "551394",
                "943693", "147970", "400196", "537505", "367493", "117178", "675840", "868721", "519081", "735564",
                "401733", "915348", "169233", "324651", "958675", "368753", "861460", "401341", "343222", "794373",
                "816374", "535119", "188234", "577779", "097792", "729303", "782637", "148159", "830641", "716890",
                "397853", "871196", "277603", "749226", "839595", "131852", "409432", "810698", "456030", "529185",
                "758823", "265024", "051041", "699031", "737269", "139340", "730977", "249786", "039931", "055669",
                "100107", "653178", "279773", "336550", "332847", "685485", "423269", "193536", "890062", "377637",
                "595777", "412134", "322736", "546929", "616370", "767332", "781184", "920944", "851005", "258850",
                "064083", "051202", "427711", "359855", "540928", "314284", "085261", "880969", "649699", "064881",
                "705423", "646927", "252556", "272007", "217511", "620286", "229724", "108865", "124636", "231417",
                "961201", "658432", "775416", "246027", "854036", "687762", "389097", "013153", "417085", "919198",
                "988711", "488665");
        String longest = findLongestSequence(fragments);
        System.out.println("Longest sequence: " + longest);*/
        List<String> fragments1 = readFragmentsFromFile("src/main/resources/source.txt");
        String longest = findLongestSequence(fragments1);
        System.out.println("Longest sequence1: " + longest);

    }

    public static List<String> readFragmentsFromFile(String fileName) {
        List<String> fragments = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && line.matches("\\d+")) { // Ensure the line contains only digits
                    fragments.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return fragments;
    }
    public static String findLongestSequence(List<String> fragments) {
        int n = fragments.size();
        Trie prefixTrie = new Trie();
        Trie suffixTrie = new Trie();

        for (int i = 0; i < n; i++) {
            String fragment = fragments.get(i);
            if (fragment.length() >= 2) {
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
            String sequence = findSequence(fragments, prefixTrie, suffixTrie, currentSequence, used);
            if (sequence.length() > longestSequence.length()) {
                longestSequence = sequence;
            }
        }
        return longestSequence;
    }

    private static String findSequence(List<String> fragments, Trie prefixTrie, Trie suffixTrie, String currentSequence, Set<Integer> used) {
        if (currentSequence.length() < 2) return currentSequence;

        String lastTwo = currentSequence.substring(currentSequence.length() - 2);
        List<Integer> nextIndices = prefixTrie.find(lastTwo);

        String longest = currentSequence;

        for (int nextIndex : nextIndices) {
            if (!used.contains(nextIndex)) {
                used.add(nextIndex);
                String nextFragment = fragments.get(nextIndex);

                String newSequence = currentSequence.substring(0, currentSequence.length() - 2) + nextFragment;

                String seq = findSequence(fragments, prefixTrie, suffixTrie, newSequence, used);
                if (seq.length() > longest.length()) {
                    longest = seq;
                }
                used.remove(nextIndex);
            }
        }

        return longest;
    }
}
class TrieNode {
    Map<String, TrieNode> children = new HashMap<>();
    List<Integer> indices = new ArrayList<>(); // Зберігаємо індекси фрагментів
}

class Trie {
    TrieNode root = new TrieNode();

    public void insert(String s, int index) {
        TrieNode current = root;
        for (int i = 0; i < s.length(); i++) {
            String prefix = s.substring(0, i + 1);
            current.children.putIfAbsent(prefix, new TrieNode());
            current = current.children.get(prefix);
        }
        current.indices.add(index);
    }

    public List<Integer> find(String prefix) {
        TrieNode current = root;
        for (int i = 0; i < prefix.length(); i++) {
            String sub = prefix.substring(0, i + 1);
            if (!current.children.containsKey(sub)) {
                return new ArrayList<>();
            }
            current = current.children.get(sub);
        }
        return current.indices;
    }
}


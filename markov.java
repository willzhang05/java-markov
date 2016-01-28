import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class markov {
    public static void main(String[] args) throws Exception {
        try {
            if (args.length != 2) {
                System.out.println("Error: Wrong number of arguments");
                System.out.println("Usage: java markov.java <filename> <length>");
                System.exit(1);
            }
            String filename = args[0];
            Scanner scanner = new Scanner(new File(filename));
            ArrayList<String> arr = new ArrayList<String>();
            String s;
            while (scanner.hasNext()) {
                s = scanner.next().replaceAll("[^\\x00-\\x7F]", "");
                arr.add(s);
                if ((s.charAt(s.length() - 1) + "").compareTo(".") == 0) {
                    arr.add("");
                    arr.add("");
                }
            }
            ArrayList<HashMap<String[], ArrayList<String>>> temp = genMarkovOrdTwoMaps(filterOut(arr.toArray(new String[]{})));
            System.out.println(genMarkovSentence(temp, Integer.parseInt(args[1])));
        } catch (FileNotFoundException e) {
            System.out.println("Error occured! " + e);
            System.exit(1);
        }
    }

    public static ArrayList<String> filterOut(String[] input) {
        ArrayList<String> r = new ArrayList<String>();
        for (String word : input) {
            if (!word.contains("http") && !word.contains("#") && !word.contains("@")
                    && word.compareTo("") != 0 && word.compareTo(" ") != 0 && word.compareTo("RT") != 0) {
                r.add(word.trim());
            }
        }
        return r;
    }

    public static String genMarkovSentence(ArrayList<HashMap<String[], ArrayList<String>>> maps, int limit) {
        String r = " ";
        int index = (int) (Math.random() * maps.size());
        HashMap<String[], ArrayList<String>> randMap = maps.get(index);
        String[] key = new ArrayList<String[]>(randMap.keySet()).get(0);
        while (!(key[0].compareTo("") == 0) && !Character.isUpperCase(key[0].charAt(0))) { //first 2 words
            index = (int) (Math.random() * maps.size());
            randMap = maps.get(index);
            key = new ArrayList<String[]>(randMap.keySet()).get(0); //String[] {a, b}
        }
        r += key[0] + " " + key[1];
        ArrayList<String> values = randMap.get(key);
        int numVal, //index of random value in value ArrayList
            sentences = 0;
        while (sentences < limit) {
            numVal = (int) (Math.random() * values.size()); //pick random index from value ArrayList
            r += " " + values.get(numVal);
            index = findKeyInMaps(maps, new String[]{key[1], values.get(numVal)});
            if (index < 0) {
                break;
            }
            if (values.get(numVal).contains(".")) {
                sentences++;
            }
            randMap = maps.get(index); //get next map using key[1] and last random value
            key = new String[]{key[1], values.get(numVal)};
            values = randMap.get(new ArrayList<String[]>(randMap.keySet()).get(0));
        }
        return r;
    }

    public static ArrayList<HashMap<String[], ArrayList<String>>> genMarkovOrdTwoMaps(ArrayList<String> all) {
        ArrayList<HashMap<String[], ArrayList<String>>> maps = new ArrayList<HashMap<String[], ArrayList<String>>>();
        for (int i = 0; i < all.size() - 2; i++) {
            HashMap<String[], ArrayList<String>> temp = new HashMap<String[], ArrayList<String>>();
            if (containsWordPair(maps, new String[]{all.get(i), all.get(i + 1)})) {
                appendValue(maps, new String[]{all.get(i), all.get(i + 1)}, all.get(i + 2));
            } else {
                int cp = i;
                temp.put(new String[]{all.get(i), all.get(i + 1)}, new ArrayList<String>() {{
                    add(all.get(cp + 2));
                }});
                maps.add(temp);
            }
        }
        for (HashMap<String[], ArrayList<String>> map : maps) {
            for (String[] inSet : map.keySet()) {
                for (int i = 0; i < inSet.length - 1; i++) {
                    System.out.println("[" + inSet[i] + ", " + inSet[i + 1] + "] = " + map.get(inSet).toString());
                }
            }
        }
        return maps;
    }

    public static boolean containsWordPair(ArrayList<HashMap<String[], ArrayList<String>>> maps, String[] key) {
        for (HashMap<String[], ArrayList<String>> map : maps) {
            String[] temp = new ArrayList<String[]>(map.keySet()).get(0);
            if (temp[0].compareTo(key[0]) == 0 && temp[1].compareTo(key[1]) == 0) {
                return true;
            }
        }
        return false;
    }

    public static void appendValue(ArrayList<HashMap<String[], ArrayList<String>>> maps, String[] key, String val) {
        for (HashMap<String[], ArrayList<String>> map : maps) {
            for (String[] inSet : map.keySet()) {
                if (inSet[0].compareTo(key[0]) == 0 && inSet[1].compareTo(key[1]) == 0) {
                    map.get(inSet).add(val);
                    return;
                }
            }
        }
    }

    public static int findKeyInMaps(ArrayList<HashMap<String[], ArrayList<String>>> maps, String[] key) {
        int index = 0;
        for (HashMap<String[], ArrayList<String>> map : maps) {
            String[] temp = new ArrayList<String[]>(map.keySet()).get(0);
            if (temp[0].compareTo(key[0]) == 0 && temp[1].compareTo(key[1]) == 0) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
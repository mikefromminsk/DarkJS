package org.fdns;


public class StringComparator {

    public static int compare(String firstString, String secondString) {
        char[] first = firstString.toCharArray();
        char[] second = secondString.toCharArray();
        java.util.Arrays.sort(first);
        java.util.Arrays.sort(second);
        // TODO change place first and second arrays when it will be fast
        int differenceLevel = 0;
        int secondIndex = 0;
        for (int firstIndex = 0; firstIndex < first.length; firstIndex++) {
            if (first[firstIndex] == second[secondIndex]) {
                secondIndex++;
            } else if (first[firstIndex] < second[secondIndex]) {
                differenceLevel += 1;
            } else if (first[firstIndex] > second[secondIndex]) {
                differenceLevel += 1;
                firstIndex--;
                secondIndex++;
            }
            if (secondIndex >= second.length && firstIndex != first.length - 1){
                differenceLevel += first.length - firstIndex;
                break;
            }
        }
        return differenceLevel;
    }
}

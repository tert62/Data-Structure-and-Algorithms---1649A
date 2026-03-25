import java.util.LinkedList;

public class SortBooks {

    public static void quickSort(LinkedList<Book> list, int low, int high) {

        if (low < high) {

            int pi = partition(list, low, high);

            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }

    private static int partition(LinkedList<Book> list, int low, int high) {

        Book pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {

            if (list.get(j).title.compareToIgnoreCase(pivot.title) <= 0) {
                i++;
                swap(list, i, j);
            }
        }

        swap(list, i + 1, high);

        return i + 1;
    }

    private static void swap(LinkedList<Book> list, int i, int j) {
        Book temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
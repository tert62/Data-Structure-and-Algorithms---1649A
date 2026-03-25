import java.util.ArrayList;

public class SearchOrder {

    public static Order binarySearch(ArrayList<Order> list, int id) {

        int left = 0, right = list.size() - 1;

        while (left <= right) {

            int mid = (left + right) / 2;

            if (list.get(mid).id == id)
                return list.get(mid);

            if (list.get(mid).id < id)
                left = mid + 1;
            else
                right = mid - 1;
        }

        return null;
    }
}
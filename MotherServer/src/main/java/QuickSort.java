import java.util.ArrayList;
import java.util.List;


public class QuickSort<T extends Comparable<T>> {

        public List<T> quickSort(List<T> list) {
            if (list.size() <= 1) {
                return list;
            }

            T pivot = list.get(list.size() / 2);
            List<T> less = new ArrayList<>();
            List<T> equal = new ArrayList<>();
            List<T> greater = new ArrayList<>();

            for (T element : list) {
                int cmp = element.compareTo(pivot);
                if (cmp < 0) {
                    less.add(element);
                } else if (cmp > 0) {
                    greater.add(element);
                } else {
                    equal.add(element);
                }
            }

            List<T> sorted = new ArrayList<>();
            sorted.addAll(quickSort(greater));
            sorted.addAll(equal);
            sorted.addAll(quickSort(less));

            return sorted;
        }
}

public class ComparableInteger implements Comparable<ComparableInteger> {

    private Integer value;

    public ComparableInteger(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public int compareTo(ComparableInteger other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }


}
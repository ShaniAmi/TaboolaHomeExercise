import java.util.Date;
import java.util.List;

public class MyClass {
    private final Date time;
    private final String name;
    private final List<Long> numbers;
    private final List<String> strings;

    public MyClass(Date time, String name, List<Long> numbers, List<String> strings) {
        this.time = time;
        this.name = name;
        this.numbers = numbers;
        this.strings = strings;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyClass)) return false;
        MyClass myClass = (MyClass) o;
        return time.equals(myClass.time) &&
                name.equals(myClass.name) &&
                numbers.equals(myClass.numbers) &&
                strings.equals(myClass.strings);
    }


    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(name);
        for (long item : numbers) {
            out.append(" ").append(item);
        }
        return out.toString();
    }

    public void removeString(String str) {
        strings.remove(str);
    }

    public boolean containsNumber(long number) {
        return numbers.contains(number);
    }

    public boolean isHistoric() {
        return time.before(new Date());
    }
}

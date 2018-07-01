import java.util.*;

public class SavingComparatorByValue implements Comparator<Saving> {
    @Override
    public int compare(Saving o1, Saving o2) {
        return (int)((int)o2.getValoreSaving() - (int)o1.getValoreSaving());
    }
}
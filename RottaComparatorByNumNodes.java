import java.util.*;

public class RottaComparatorByNumNodes implements Comparator<Rotta> {
    @Override
    public int compare(Rotta o1, Rotta o2) {
        return (int)(o1.getClienti().size() - o2.getClienti().size());
    }
}

import java.util.*;

public class RottaComparatorByAvgCapacityNodes implements Comparator<Rotta> {
    @Override
    public int compare(Rotta o1, Rotta o2) {
    	return (int) ((o1.getQuantitaCarico() + o1.getQuantitaScarico()) / o1.getClienti().size() - (o2.getQuantitaCarico() + o2.getQuantitaScarico())/o2.getClienti().size()); 
    }
}
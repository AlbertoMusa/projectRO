import java.util.*;

public class SavingComparatorByType implements Comparator<Saving> 
{   
	@Override
    public int compare(Saving o1, Saving o2) 
    {
		//se il tipo del primo elemento dei due saving è identico ordino in base al costo
		//altrimenti 
		
		int p1 = o1.getPrimoNodo().getTipo().toCharArray()[0];
		int p2 = o2.getPrimoNodo().getTipo().toCharArray()[0];
		
    	return p2 - p1;
    }
}
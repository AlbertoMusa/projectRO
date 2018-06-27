import java.util.ArrayList;
import java.util.Collections;


public class Risolutore
{
	
	public static Istanza risolvi(Istanza istanza, String mode)
	{	
		ClarkWright.esegui(mode, istanza);
		istanza.stampaRotte();
		
		allineamentoNumeroRotte(istanza);
		istanza.stampaRotte();

 		LocalSearch.esegui(istanza);
 		istanza.stampaRotte();
		
		return istanza;
	}
	
	public static void allineamentoNumeroRotte(Istanza istanza)
	{
		Collections.sort(istanza.getRotte(), new RottaComparatorByAvgCapacityNodes());  //RottaComparatorByAvgCapacityNodes RottaComparatorByNumNodes
		
		int i = 0; 
		while(istanza.getNumVeicoli() < istanza.getRotte().size())	
		{
			ArrayList<Rotta> listaRotte = istanza.getRotte();
			boolean removed = false;
			
			for(int j = 0; j < istanza.getRotte().size(); j++)
			{
				Nodo nodo = listaRotte.get(i).getClienti().get(1);
				if(nodo.getTipo().equals("L"))
				{		
					if(i!=j && (nodo.getQuantita() + listaRotte.get(j).getQuantitaScarico() <= istanza.getCapacitaVeicoli()))
					{
						listaRotte.get(j).aggiungoNodoASinistra(nodo);
						listaRotte.get(i).removeFirst();
						j=0;
						
						//verifico la possibilit� di eliminare la rotta gi� prima di uscire da questo "do" 
						if(listaRotte.get(i).getClienti().size() == 2) 
						{
							istanza.getRotte().remove(i);
							removed = true;
							break;
						}
					}
					else
						j++;				
				}
				else
				{
					if(i != j && (nodo.getQuantita() + listaRotte.get(j).getQuantitaCarico() <= istanza.getCapacitaVeicoli()))
					{
						listaRotte.get(j).aggiungoNodoADestra(nodo);
						listaRotte.get(i).removeFirst();
						j = 0;
						
						//verifico la possibilit� di eliminare la rotta gi� prima di uscire da questo "do" 
						if(listaRotte.get(i).getClienti().size() == 2) 
						{
							istanza.getRotte().remove(i);
							removed = true;
							break;
						}
					}
					else
						j++;
				}
			}

			if(!removed) 
				istanza.setRotte(listaRotte);
		}
	}
}
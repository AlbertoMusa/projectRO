import java.util.Collections;


public class AllineamentoRotte 
{
	public static void esegui(Istanza istanza)
	{
		int n = istanza.getRotte().size();
		if(istanza.getNumVeicoli() > n)
			aumentaRotte(istanza);
		else if(istanza.getNumVeicoli() < n)
			riduciRotte(istanza);
	}
	//scorro le rotte, se la rotta ha piu di un nodo L rimuovo un nodo L per aggiungerlo ad una nuova
	// se invece ne ha solo uno passo alla rotta successiva e ripeto
	// ripeto la procedura finche non ho ottenuto il numero di rotte desiderato
	private static void aumentaRotte(Istanza istanza)
	{
		int j=0;
		while(istanza.getRotte().size() < istanza.getNumVeicoli())
		{
			if(istanza.getRotte().get(j).getLineHauls().size()>2)
			{
				Rotta nuova = new Rotta(istanza.getDeposito(),istanza.getRotte().get(j).removeFirst());
				istanza.getRotte().add(nuova);
			}
			else
				j++;
		}
 		istanza.updateSolution();
	}
	
	//ordino le rotte per valore medio dei nodi più basso, successivamente partendo dalla prima rotta in elenco
	//cerco di spostare un suo nodo in una delle restanti rotte (ove possibile) rispettando i vincoli di capacità
	//procedo finche la rotta non sarà vuota e a questo punto la elimino
	//ripeto l'operazione con le rotte successive finche non raggiungo il numero di rotte desiderato
	private static void riduciRotte(Istanza istanza)
	{
		int p=1;
		int r=0;

		Collections.sort(istanza.getRotte(), new RottaComparatorByAvgCapacityNodes());
		while(istanza.getNumVeicoli() < istanza.getRotte().size())
		{
			int i=0;
			while(i<istanza.getRotte().size())
			{
				if(istanza.getRotte().get(r).getLineHauls().size() == 1 &&
						istanza.getRotte().get(r).getBackHauls().size() > 0 && p==1)
				{
					p=2;
				}
				else if(i!=r && istanza.getRotte().get(r).getClienti().get(p).getTipo().equals("L") &&
						istanza.getRotte().get(r).getClienti().get(p).getQuantita()+
						istanza.getRotte().get(i).getQuantitaScarico()<=istanza.getCapacitaVeicoli())
				{
					istanza.getRotte().get(i).aggiungoNodoASinistra(istanza.getRotte().get(r).remove(p));
					i=0;
				}
				else if (i!=r && istanza.getRotte().get(r).getClienti().get(p).getTipo().equals("B") &&
						istanza.getRotte().get(r).getClienti().get(p).getQuantita()+
						istanza.getRotte().get(i).getQuantitaCarico()<=istanza.getCapacitaVeicoli())
				{
					istanza.getRotte().get(i).aggiungoNodoADestra(istanza.getRotte().get(r).remove(p));
					i=0;
					p=1;
				}
				else
				{
					i++;
				}
				
				if(istanza.getRotte().get(r).getClienti().size()==2)
				{
					istanza.getRotte().remove(r);
					i=istanza.getRotte().size();
				}
			}
			p=1;
			r++;
			if(r > istanza.getRotte().size()-1)
			{
				r=0;
				Collections.sort(istanza.getRotte(), new RottaComparatorByAvgCapacityNodes());
			}
			
		}
	
		assert istanza.getNumVeicoli() >= istanza.getRotte().size() ;
		istanza.updateSolution();
	}
}

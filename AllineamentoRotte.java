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

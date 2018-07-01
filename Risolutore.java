import java.util.ArrayList;
import java.util.Collections;


public class Risolutore
{
	
	public static Istanza risolvi(Istanza istanza, String mode)
	{	
		ClarkWright.esegui(mode, istanza);
		istanza.stampaRotte();
		System.out.println("--------------------FINE C&W " + mode +  "--------------------------");
		
		aumentaRotte(istanza);
		istanza.stampaRotte();
		System.out.println("--------------------FINE AUMENTO--------------------------");
		
		allineamentoNumeroRotte2(istanza);
		istanza.stampaRotte();
		System.out.println("--------------------FINE ALLINEAMENTO--------------------------");

 		LocalSearch.esegui(istanza);
 		istanza.stampaRotte();
		
		return istanza;
	}
	
	public static void allineamentoNumeroRotte(Istanza istanza)
	{
		Collections.sort(istanza.getRotte(), new RottaComparatorByAvgCapacityNodes());
		//Collections.sort(istanza.getRotte(), new RottaComparatorByNumNodes());

		ArrayList<Rotta> listaRotte = new ArrayList<Rotta>();
		for(Rotta r : istanza.getRotte())
			listaRotte.add(r.copiaDi());

		int i = 0; 
		while(istanza.getNumVeicoli() < istanza.getRotte().size())	
		{
			System.out.println(i);

            //ArrayList<Rotta> listaRotte = istanza.getRotte();
			boolean removed = false;
			
			for(int j = 0; j < listaRotte.size(); j++)
			{
				Nodo nodo = listaRotte.get(i).getClienti().get(1);
				if(nodo.getTipo().equals("L"))	//se il nodo in esame è di tipo L
				{		
					if(i!=j && (nodo.getQuantita() + listaRotte.get(j).getQuantitaScarico() <= istanza.getCapacitaVeicoli()))
					{
						listaRotte.get(j).aggiungoNodoASinistra(nodo);
						listaRotte.get(i).removeFirst();
						j=0;
						
						//verifico la possibilità di eliminare la rotta già prima di uscire da questo "do" 
						if(listaRotte.get(i).getClienti().size() == 2) 
						{
							listaRotte.remove(i);
							removed = true;
							break;
						}
					}
					else
						j++;				
				}
				else
				{
					if(i != j && (nodo.getQuantita() + listaRotte.get(j).getQuantitaCarico() <= istanza.getCapacitaVeicoli())) //se il nodo in esame è di tipo B
					{
						listaRotte.get(j).aggiungoNodoADestra(nodo);
						listaRotte.get(i).removeFirst();
						j = 0;
						
						//verifico la possibilità di eliminare la rotta già prima di uscire da questo "do" 
						if(listaRotte.get(i).getClienti().size() == 2) 
						{
							listaRotte.remove(i);
							removed = true;
							break;
						}
					}
					else
						j++;
				}
			}
			
			if(!removed)
			{
				//se la rotta non è stata rimossa allora incremento i per passare alla rotta successiva
				//e ripristino le rotta
				i++;

				listaRotte.clear();
				for(Rotta r : istanza.getRotte())
					listaRotte.add(r.copiaDi());
			}
			else
			{
				i=0;
				istanza.setRotte(listaRotte); //se invece ho rimosso la rotta salvo le modifiche nella lista rotte dell'istanza
			}
			//cambiare
			istanza.stampaRotte();
			if(i>istanza.getRotte().size()-1)
			{
		 		//LocalSearch.eseguiA(istanza);
				//i=0;
			}
			//
		}
	}

	public static void allineamentoNumeroRotte2(Istanza istanza)
	{
		Collections.sort(istanza.getRotte(), new RottaComparatorByAvgCapacityNodes());
		//Collections.sort(istanza.getRotte(), new RottaComparatorByNumNodes());
	
		int i = 0;
		int p = 1;
		while(istanza.getNumVeicoli() < istanza.getRotte().size())	
		{	
			for(int j = 0; j < istanza.getRotte().size(); j++)
			{
				Nodo nodo = istanza.getRotte().get(i).getClienti().get(p);
				if(nodo.getTipo().equals("L"))
				{		
					if((i!=j && (nodo.getQuantita() + istanza.getRotte().get(j).getQuantitaScarico() <= istanza.getCapacitaVeicoli())))
					{
						if(!((istanza.getRotte().get(i).getLineHauls().size()==1) && (istanza.getRotte().get(i).getBackHauls().size()>0)))
						{
							p=2;
						}
						else
						{
							istanza.getRotte().get(j).aggiungoNodoASinistra(nodo);
							istanza.getRotte().get(i).removeFirst();
							j=0;
							p=1;
							if(istanza.getRotte().get(i).getClienti().size() == 2) 
							{
								istanza.getRotte().remove(i);
								i=0;
							}
						}
					}
					else
						j++;				
				}
				else
				{
					if(i != j && (nodo.getQuantita() + istanza.getRotte().get(j).getQuantitaCarico() <= istanza.getCapacitaVeicoli())) //se il nodo in esame è di tipo B
					{
						istanza.getRotte().get(j).aggiungoNodoADestra(nodo);
						istanza.getRotte().get(i).getClienti().remove(p);
						j = 0;
						p=1;
						if(istanza.getRotte().get(i).getClienti().size() == 2) 
						{
							istanza.getRotte().remove(i);
							i=0;
						}
					}
					else
						j++;
				}
			}	
			i++;
			istanza.stampaRotte(); // se tolgo si blocca non so perchè
			//istanza.setCosto();
			if(i>istanza.getRotte().size()-1)
			{
		 		//LocalSearch.eseguiA(istanza);
				Collections.sort(istanza.getRotte(), new RottaComparatorByNumNodes());
				i=0;
			}
		}
	}
	
	public static void aumentaRotte(Istanza istanza)
	{
		int j=0;
		while(istanza.getRotte().size()<istanza.getNumVeicoli())
		{
			if(istanza.getRotte().get(j).getLineHauls().size()>2)
			{
				Rotta nuova = new Rotta(istanza.getDeposito(),istanza.getRotte().get(j).removeFirst());
				istanza.getRotte().add(nuova);
			}
			else
				j++;
		}
	}

}
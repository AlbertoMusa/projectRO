import java.util.*;

public class ClarkWright
{
	public static void esegui(String mode, Istanza istanza)
	{
		//calcolo i savings
		ArrayList<Saving> savings = new ArrayList<Saving>();
		Nodo[] clienti = istanza.getClienti();
		Nodo dep = istanza.getDeposito();
		for(int i = 0; i < clienti.length; i++)
			for(int j = i+1; j < clienti.length; j++)
			{
				double Da = DistanceCalculator.calcolaDistanza(clienti[i], dep)*2 + DistanceCalculator.calcolaDistanza(clienti[j], dep)*2;
				double Db = DistanceCalculator.calcolaDistanza(clienti[i], dep) + DistanceCalculator.calcolaDistanza(clienti[j], dep) + DistanceCalculator.calcolaDistanza(clienti[i], clienti[j]);
				
				if(clienti[i].getTipo().equals("B") && clienti[j].getTipo().equals("L"))
					savings.add(new Saving(clienti[j], clienti[i], Math.abs(Da - Db)));
				else
					savings.add(new Saving(clienti[i], clienti[j], Math.abs(Da - Db)));
			}
			
		//riordino i saving
		Collections.sort(savings, new SavingComparatorByValue());
		Collections.sort(savings, new SavingComparatorByType());
						
		//se mode == "SEQ"
		if(mode.equals("SEQ")) 
			Sequenziale(istanza, savings); //eseguo la versione sequenziale dell'algoritmo		
		else //altrimenti eseguo il parallelo
			Parallelo(istanza, savings);
		
		//aggiorno costo totale
		istanza.updateSolution();
	}
	
	//C&W sequenziale
	public static void Sequenziale(Istanza istanza, ArrayList<Saving> savings)
	{		
		ArrayList<Nodo> nodiOccupati = new ArrayList<Nodo>();
		int nodiIniziali = 0;
		
		//per ogni rotta DEPOSITO - NODO - DEPOSITO
		for(int j = 0; j < istanza.getRotte().size(); j++)
		{	
			if(j > 0)
				if(nodiIniziali < istanza.getRotte().get(j - 1).getClienti().size())
					j--;
			
			nodiIniziali = istanza.getRotte().get(j).getClienti().size();
			
			//scorro tutti i saving non "sistemati" precedentemente
			for(int i = 0; i < savings.size(); i++)
			{
				Nodo a = savings.get(i).getPrimoNodo();
				Nodo b = savings.get(i).getSecondoNodo();

				//se i nodi del saving sono gia' stati piazzati allora, setto il saving come "sistemato", quindi esco 
				if((istanza.getRotte().get(j).cercaNodo(a) > -1) && (istanza.getRotte().get(j).cercaNodo(b) > -1))
					break;

				boolean merged = false;

				//se solo uno dei due nodi e' presente in rotta, allora verifico se posso aggiungere l'altro a tale rotta
				if((istanza.getRotte().get(j).cercaNodo(a) > -1) ^ (istanza.getRotte().get(j).cercaNodo(b) > -1))
					merged = verificaFusione(i, istanza.getRotte().get(j), istanza, savings, nodiOccupati);


				if(merged) break;
			}
		}
	}
	
	//C&W parallelo
	public static void Parallelo(Istanza istanza, ArrayList<Saving> savings)
	{
		ArrayList<Nodo> nodiOccupati = new ArrayList<Nodo>();
		
		//per ogni saving
		for(int i = 0; i < savings.size(); i++)
		{
			Nodo a = savings.get(i).getPrimoNodo();
			Nodo b = savings.get(i).getSecondoNodo();

			if(nodiOccupati.contains(a) && nodiOccupati.contains(b))
				savings.get(i).setMerged();
			
			//scorro le rotte
			if(!savings.get(i).isMerged()) //se il saving e' gia' "sistemato" passo al successivo
				for(int j = 0; j < istanza.getRotte().size(); j++)
				{
					boolean merged = false;
					//se almeno uno dei due nodi del saving in esame e' presente all'interno della rotta i-esima
					if((istanza.getRotte().get(j).cercaNodo(a) > -1) ^ (istanza.getRotte().get(j).cercaNodo(b) > -1))
						merged = verificaFusione(i, istanza.getRotte().get(j), istanza, savings, nodiOccupati);
	
					if(merged) break;
				}
		}
	}
	
	//verifico la possibilità di fondere le rotte
	private static boolean verificaFusione(int savingIndex, Rotta rotta, Istanza istanza, ArrayList<Saving> savings, ArrayList<Nodo> nodiOccupati)
	{
		Nodo a = savings.get(savingIndex).getPrimoNodo();
		Nodo b = savings.get(savingIndex).getSecondoNodo();
				
		if(nodiOccupati.size() == 0)
			return verificaAggiuntaNodo(rotta, a, b, savings, savingIndex, istanza, nodiOccupati);
		else if(!(nodiOccupati.contains(a) && nodiOccupati.contains(b)))
			{
				if((nodiOccupati.contains(a)) && (!nodiOccupati.contains(b))) 		//ho il nodo a, aggiungo b
					return verificaAggiuntaNodo(rotta, a, b, savings, savingIndex, istanza, nodiOccupati);				
				else 
					if((nodiOccupati.contains(b)) && (!nodiOccupati.contains(a))) 		//ho il nodo b, aggiungo a
						return verificaAggiuntaNodo(rotta, b, a, savings, savingIndex, istanza, nodiOccupati);
					else //non ho nessuno, fondo le due rotte
						return verificaAggiuntaNodo(rotta, a, b, savings, savingIndex, istanza, nodiOccupati);
			}
		
		
		return false;
	}

	//provo ad aggiungere un nodo nella rotta 
	private static boolean verificaAggiuntaNodo(Rotta rotta, Nodo occupato, Nodo daAggiungere, ArrayList<Saving> savings, int savingIndex, Istanza istanza, ArrayList<Nodo> nodiOccupati)
	{
		Rotta rottaDaRimuovere = getRottaDatoNodo(daAggiungere, istanza.getRotte());

		//se il nodo occupato e' a sinistra nella rotta dovro' (eventualmente) aggiungere il nodo daAggiungere in a sinistra della rotta 
		if((rotta.cercaNodo(occupato) == 1))
		{ 
			//SOLO LINEHAUL
			if((daAggiungere.getTipo().equals("L")) && (controlloCarico(rotta, daAggiungere, "SINISTRA") <= istanza.getCapacitaVeicoli()))
			{
				istanza.getRotte().remove(rottaDaRimuovere);
				rotta.aggiungoNodoASinistra(daAggiungere);
				nodiOccupati.add(daAggiungere);
				nodiOccupati.add(occupato);
				savings.get(savingIndex).setMerged();
								
				return true;
			}
			else if((daAggiungere.getTipo().equals("B")) && (controlloCarico(rotta, daAggiungere, "DESTRA") <= istanza.getCapacitaVeicoli()))
			{
				istanza.getRotte().remove(rottaDaRimuovere);
				rotta.aggiungoNodoADestra(daAggiungere);
				nodiOccupati.add(daAggiungere);
				nodiOccupati.add(occupato);
				savings.get(savingIndex).setMerged();
								
				return true;
			}
		}
		//se il nodo occupato e' a destra dovro' nella rotta (eventualmente) aggiungere il nodo daAggiungere a destra della rotta 
		else if((rotta.cercaNodo(occupato) == (rotta.getClienti().size()-2))) 
		{
			//se il nodo in coda alla rotta e' un Backhaul porto' aggiungere SOLO BACKHAUL
			if(occupato.getTipo().equals("B"))
			{
				if((daAggiungere.getTipo().equals("B")) && (controlloCarico(rotta, daAggiungere, "DESTRA") <= istanza.getCapacitaVeicoli()))
				{
					istanza.getRotte().remove(rottaDaRimuovere);
					rotta.aggiungoNodoADestra(daAggiungere);
					nodiOccupati.add(daAggiungere);
					nodiOccupati.add(occupato);
					savings.get(savingIndex).setMerged();

					return true;
				}
			}
			else
				if(controlloCarico(rotta, daAggiungere, "DESTRA") <= istanza.getCapacitaVeicoli())
				{
					istanza.getRotte().remove(rottaDaRimuovere);
					rotta.aggiungoNodoADestra(daAggiungere);
					nodiOccupati.add(daAggiungere);
					nodiOccupati.add(occupato);
					savings.get(savingIndex).setMerged();		
					
					return true;					
				}
		}
		return false;
	}
		
	//controllo se il l'aggiunta del nodo in rotta rispetta i vincoli di carico/scarico
	private static int controlloCarico(Rotta rotta, Nodo nodo, String lato)
	{
		int carico = 0;
		int scarico = 0;
		
		//se aggiungo un nodo a sinistra, devo solo verificare la quantita nei linehauls
		if(lato.equals("SINISTRA"))
		{
			scarico = nodo.getQuantita();
			for(Nodo n: rotta.getLineHauls())
				scarico += n.getQuantita();
			
			return scarico;
		}
		else //altrimenti anche nei backhauls
		{
			if(nodo.getTipo().equals("L"))
			{
				scarico = nodo.getQuantita();
				carico = nodo.getQuantita();
				for(Nodo n: rotta.getLineHauls())
					scarico += n.getQuantita();
				
				return scarico;
			}
			else
			{
				carico = nodo.getQuantita();
				for(Nodo n: rotta.getBackHauls())
					carico += n.getQuantita();
				
				return carico;
			}
		}
	}

	//verifico in quale rotta è situato il nodo in esame
	private static Rotta getRottaDatoNodo(Nodo nodo, ArrayList<Rotta> rotte)
	{
		for(Rotta rotta : rotte)
			if(rotta.getClienti().contains(nodo))
				return rotta;
		return null;
	}
}
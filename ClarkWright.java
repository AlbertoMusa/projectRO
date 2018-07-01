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
				
		//stampo i saving
		//for(Saving s : savings)
			//System.out.println(s.getPrimoNodo().getID() + " --- " + s.getSecondoNodo().getID() + "  --> " + s.getValoreSaving());
		
		
		//istanza.stampaRotte();
		
		//se mode == "SEQ"
		if(mode.equals("SEQ")) 
			Sequenziale(istanza, savings); //eseguo la versione sequenziale dell'algoritmo
		
		else
			Parallelo(istanza, savings);
	}
	
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
				//System.out.println("\n------------------\nSAVING " + a.getID() + "-" + b.getID());

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
	
	public static void Parallelo(Istanza istanza, ArrayList<Saving> savings)
	{
		ArrayList<Nodo> nodiOccupati = new ArrayList<Nodo>();
		
		//per ogni saving
		for(int i = 0; i < savings.size(); i++)
		{
			Nodo a = savings.get(i).getPrimoNodo();
			Nodo b = savings.get(i).getSecondoNodo();

			//System.out.println("\n------------------\nSAVING " + a.getID() + "-" + b.getID());
			//istanza.stampaRotte();

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

	private static boolean verificaAggiuntaNodo(Rotta rotta, Nodo occupato, Nodo daAggiungere, ArrayList<Saving> savings, int savingIndex, Istanza istanza, ArrayList<Nodo> nodiOccupati)
	{
		Rotta rottaDaRimuovere = getRottaDatoNodo(daAggiungere, istanza.getRotte());

		//se il nodo occupato e' a sinistra nella rotta dovro' (eventualmente) aggiungere il nodo daAggiungere in a sinistra della rotta 
		if((rotta.cercaNodo(occupato) == 1))
		{ 
			//SOLO LINEHAUL
			if((daAggiungere.getTipo().equals("L")) && (controlloCarico(rotta, daAggiungere, "SINISTRA") <= istanza.getCapacitaVeicoli()))
			{
				//System.out.println("Fondo con  rotta " + rotta.getNodiToString());

				istanza.rimuovoRotta(rottaDaRimuovere);
				rotta.aggiungoNodoASinistra(daAggiungere);
				nodiOccupati.add(daAggiungere);
				nodiOccupati.add(occupato);
				savings.get(savingIndex).setMerged();
				
				//System.out.println("Rimuovo rotta " + rottaDaRimuovere.getNodiToString());			
				
				return true;
			}
			else if((daAggiungere.getTipo().equals("B")) && (controlloCarico(rotta, daAggiungere, "DESTRA") <= istanza.getCapacitaVeicoli()))
			{
				//System.out.println("Fondo con  rotta " + rotta.getNodiToString());

				istanza.rimuovoRotta(rottaDaRimuovere);
				rotta.aggiungoNodoADestra(daAggiungere);
				nodiOccupati.add(daAggiungere);
				nodiOccupati.add(occupato);
				savings.get(savingIndex).setMerged();
				
				//System.out.println("Rimuovo rotta " + rottaDaRimuovere.getNodiToString());			
				
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
					//System.out.println("Fondo con  rotta " + rotta.getNodiToString());

					istanza.rimuovoRotta(rottaDaRimuovere);
					rotta.aggiungoNodoADestra(daAggiungere);
					nodiOccupati.add(daAggiungere);
					nodiOccupati.add(occupato);
					savings.get(savingIndex).setMerged();
					
					//System.out.println("Rimuovo rotta " + rottaDaRimuovere.getNodiToString());
					
					return true;
				}
			}
			else
				if(controlloCarico(rotta, daAggiungere, "DESTRA") <= istanza.getCapacitaVeicoli())
				{
					//System.out.println("Fondo con  rotta " + rotta.getNodiToString());

					istanza.rimuovoRotta(rottaDaRimuovere);
					rotta.aggiungoNodoADestra(daAggiungere);
					nodiOccupati.add(daAggiungere);
					nodiOccupati.add(occupato);
					savings.get(savingIndex).setMerged();
					
					//System.out.println("Rimuovo rotta " + rottaDaRimuovere.getNodiToString());			
					
					return true;					
				}
		}
		return false;
	}
	
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

	
	private static Rotta getRottaDatoNodo(Nodo nodo, ArrayList<Rotta> rotte)
	{
		for(Rotta rotta : rotte)
			if(rotta.getClienti().contains(nodo))
				return rotta;
		return null;
	}
}
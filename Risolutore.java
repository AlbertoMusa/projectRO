import java.util.ArrayList;
import java.util.Collections;


public class Risolutore
{
	public static long[] risolvi(Istanza istanza, String mode)
	{	
		//clarke-wright
		long startTime = System.currentTimeMillis();
		ClarkWright.esegui(mode, istanza);
		long CW_Time = System.currentTimeMillis() - startTime;
		
		istanza.stampaRotte();
		System.out.println("--------------------FINE C&W " + mode +  "--------------------------");
		
		//allinaamento rotte al numero di veicoli
		startTime = System.currentTimeMillis();
		aumentaRotte(istanza);
		//System.out.println("--------------------FINE AUMENTO--------------------------");
		allineamentoNumeroRotte2(istanza);
		long AL_Time = System.currentTimeMillis() - startTime;
		istanza.stampaRotte();
		System.out.println("--------------------FINE ALLINEAMENTO--------------------------");

		//local search
		startTime = System.currentTimeMillis();
 		LocalSearch.esegui(istanza);
		long LS_Time = System.currentTimeMillis() - startTime;
 		istanza.stampaRotte();
		
 		long times[] = new long[3];
 		times[0] = CW_Time;
 		times[1] = AL_Time;
 		times[2] = LS_Time;
 		
 		return times;
	}
	
//	public static void allineamentoNumeroRotte(Istanza istanza)
//	{
//		Collections.sort(istanza.getRotte(), new RottaComparatorByAvgCapacityNodes());
//		//Collections.sort(istanza.getRotte(), new RottaComparatorByNumNodes());
//
//		ArrayList<Rotta> listaRotte = new ArrayList<Rotta>();
//		for(Rotta r : istanza.getRotte())
//			listaRotte.add(r.copiaDi());
//
//		int i = 0; 
//		while(istanza.getNumVeicoli() < istanza.getRotte().size())	
//		{
//			System.out.println(i);
//
//            //ArrayList<Rotta> listaRotte = istanza.getRotte();
//			boolean removed = false;
//			
//			for(int j = 0; j < listaRotte.size(); j++)
//			{
//				Nodo nodo = listaRotte.get(i).getClienti().get(1);
//				if(nodo.getTipo().equals("L"))	//se il nodo in esame Ã¨ di tipo L
//				{		
//					if(i!=j && (nodo.getQuantita() + listaRotte.get(j).getQuantitaScarico() <= istanza.getCapacitaVeicoli()))
//					{
//						listaRotte.get(j).aggiungoNodoASinistra(nodo);
//						listaRotte.get(i).removeFirst();
//						j=0;
//						
//						//verifico la possibilitÃ  di eliminare la rotta giÃ  prima di uscire da questo "do" 
//						if(listaRotte.get(i).getClienti().size() == 2) 
//						{
//							listaRotte.remove(i);
//							removed = true;
//							break;
//						}
//					}
//					else
//						j++;				
//				}
//				else
//				{
//					if(i != j && (nodo.getQuantita() + listaRotte.get(j).getQuantitaCarico() <= istanza.getCapacitaVeicoli())) //se il nodo in esame Ã¨ di tipo B
//					{
//						listaRotte.get(j).aggiungoNodoADestra(nodo);
//						listaRotte.get(i).removeFirst();
//						j = 0;
//						
//						//verifico la possibilitÃ  di eliminare la rotta giÃ  prima di uscire da questo "do" 
//						if(listaRotte.get(i).getClienti().size() == 2) 
//						{
//							listaRotte.remove(i);
//							removed = true;
//							break;
//						}
//					}
//					else
//						j++;
//				}
//			}
//			
//			if(!removed)
//			{
//				//se la rotta non Ã¨ stata rimossa allora incremento i per passare alla rotta successiva
//				//e ripristino le rotta
//				i++;
//
//				listaRotte.clear();
//				for(Rotta r : istanza.getRotte())
//					listaRotte.add(r.copiaDi());
//			}
//			else
//			{
//				i=0;
//				istanza.setRotte(listaRotte); //se invece ho rimosso la rotta salvo le modifiche nella lista rotte dell'istanza
//			}
//			//cambiare
//			istanza.stampaRotte();
//			if(i>istanza.getRotte().size()-1)
//			{
//		 		//LocalSearch.eseguiA(istanza);
//				//i=0;
//			}
//			//
//		}
//	}

	public static void allineamentoNumeroRotte2(Istanza istanza)
	{
		int p=1;
		int r=0;

		Collections.sort(istanza.getRotte(), new RottaComparatorByAvgCapacityNodes());
		while(istanza.getNumVeicoli()<istanza.getRotte().size())
		{
			istanza.stampaRotte();
			int i=0;
			while(i<istanza.getRotte().size())
			{
				//System.out.println(r + " " + p + " " + i);
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
			if(r>istanza.getRotte().size()-1)
			{
				r=0;
				Collections.sort(istanza.getRotte(), new RottaComparatorByAvgCapacityNodes());
			}
			
		}
	
		assert istanza.getNumVeicoli() >= istanza.getRotte().size() ;
	}
	
	public static void allineamentoNumeroRotteGio(Istanza istanza)
	{
		Collections.sort(istanza.getRotte(), new RottaComparatorByAvgCapacityNodes());

		ArrayList<Rotta> listaRotte = new ArrayList<Rotta>();
		listaRotte.clear();
		for(Rotta r : istanza.getRotte())
			listaRotte.add(r.copiaDi());

		int i = 0; 
		while((istanza.getNumVeicoli() < listaRotte.size())) 	
		{
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
				}
			}

			if(!removed)
			{
				listaRotte.clear();
				for(Rotta r: istanza.getRotte())
					listaRotte.add(r);
				
				i = (i+1)%listaRotte.size();
			}
			else{
				istanza.setRotte(listaRotte); //se invece ho rimosso la rotta salvo le modifiche nella lista rotte dell'istanza
			}
		}
			
		istanza.setRotte(listaRotte);
		assert istanza.getNumVeicoli() >= istanza.getRotte().size() ;

	}
	
	public static void aumentaRotte(Istanza istanza)
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
	}

}
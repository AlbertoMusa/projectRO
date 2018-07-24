public class LocalSearch
{
	public static void esegui(Istanza istanza)
	{	
		double guadagno = 1;

		//finche questa routine migliorara la soluzione procedo
		while(guadagno > 0){
			double oldCost= istanza.getCostoTotale();
			eseguiScambiTraRotte(istanza);
			eseguiScambi(istanza);
			eseguiRilocazione(istanza);
			guadagno = oldCost - istanza.getCostoTotale();
		}
	}
	
	//eseguo la mossa exchange 
	public static void eseguiScambi(Istanza istanza)
	{
		//scorro tutte i nodi j della rotta i e li confronto con tutti i nodi w della rotta k.
		//con il metodo compara verifico che le condizioni di scambio siano valide, in una serie di if a catena
		// se sono valide scambio i due nodi e rinizializzo tutte le variabili del metodo
		for(int i=0; i<istanza.getRotte().size(); i++)
		{
			for(int j=1; j<istanza.getRotte().get(i).getClienti().size()-1;j++)
			{
				for(int k=0; k<istanza.getRotte().size(); k++)
				{
					for(int w=1;w<istanza.getRotte().get(k).getClienti().size()-1;w++) 
					{
						if(compara(istanza.getRotte().get(i),
								istanza.getRotte().get(k),
								istanza.getRotte().get(i).getClienti().get(j),
								istanza.getRotte().get(k).getClienti().get(w),
								istanza.getCapacitaVeicoli()))
						{
							scambia(istanza.getRotte().get(i),  
							istanza.getRotte().get(k),
							istanza.getRotte().get(i).getClienti().get(j),
							istanza.getRotte().get(k).getClienti().get(w),
							j,w);
							//System.out.println(i + " " + j + " " + k + " " + w);
							i=0;
							j=1;
							k=0;
							w=1;
							istanza.updateSolution();
							////istanza.stampaRotte();
						}	
					}
				}
			}
		}
		System.out.println("----------------------FINE EXCHANGE----------------------");
	}
	
	//eseguo la mossa relocate
	
	public static void eseguiRilocazione(Istanza istanza)
	{
	//scorro tutte i nodi j della rotta i e li confronto con tutti i nodi w della rotta k.
	//con il metodo compara verifico che le condizioni di riallocamento siano valide, in una serie di if a catena
	// se sono valide rialloco il nodo j alla destra di w e rinizializzo tutte le variabili del metodo
		for(int i=0; i<istanza.getRotte().size(); i++)
		{
			for(int j=1; j<istanza.getRotte().get(i).getClienti().size()-1;j++)
			{
				for(int k=0; k<istanza.getRotte().size(); k++) 
				{
					for(int w=1;w<istanza.getRotte().get(k).getClienti().size()-1;w++) 
					{
						if(compara(istanza.getRotte().get(i),
								istanza.getRotte().get(k),
								istanza.getRotte().get(i).getClienti().get(j),
								w,istanza.getCapacitaVeicoli()))
							{
								scambia(istanza.getRotte().get(i),  
								istanza.getRotte().get(k),
								istanza.getRotte().get(i).getClienti().get(j),
								j,w);
								//System.out.println(i + " " + j + " " + k + " " + w);
								i=0;
								j=1;
								k=0;
								w=1;
						 		istanza.updateSolution();
								////istanza.stampaRotte();
							}	
					}
				}
			}
		}	
		System.out.println("----------------------FINE RELOCATE----------------------");
	}
	
	//eseguo la mossa exchange nei due sottoinsiemi di nodi (linehauls e backhauls)
	private static void eseguiScambiTraRotte(Istanza istanza)
	{
	//scorro tutte le rotte i e le confronto con tutte le rotte k. Verifico che non si tratti della stessa rotta
	//se non è cosi metto in due liste distinte i nodi di diverso tipo sia per i che per k
	//a questo punto combino le sottoliste in maniera tale che la prima appartenga ad una lista e la seconda ad un altra
	//e viceversa, se lo scambio risulta vantaggioso confermo lo scambio e resetto le variabili del metodo.
		for(int i=0; i<istanza.getRotte().size(); i++)
		{
			for(int k=0; k<istanza.getRotte().size(); k++) 
			{
				if(!(istanza.getRotte().get(i).equals(istanza.getRotte().get(k))))
				{
					Rotta app1 = new Rotta(istanza.getDeposito(),istanza.getRotte().get(i).getLineHauls(),istanza.getRotte().get(k).getBackHauls());
					Rotta app2 = new Rotta(istanza.getDeposito(),istanza.getRotte().get(k).getLineHauls(),istanza.getRotte().get(i).getBackHauls());
					if(calcolaDistanza(app1)+calcolaDistanza(app2)<calcolaDistanza(istanza.getRotte().get(i))+calcolaDistanza(istanza.getRotte().get(k)))
					{
						istanza.getRotte().set(i, app1);
						istanza.getRotte().set(k, app2);
				 		istanza.updateSolution();
						i=0;
						k=0;
					}
				}	
			}
		}
		System.out.println("----------------------FINE EXCHANGE PARTS----------------------");
	}
	
	private static boolean compara(Rotta a, Rotta b, Nodo aa, Nodo bb, int c)
	{
		//controllo se due nodi sono uguali, non appartengono allo stesso tipo, non superano la capacità massima,
		//uno dei due non sia l'ultimo nodo di linehaul, che la nuova configurazione non sia vantaggiosa
		//se tutte sono false restituisco vero
		if(aa.getID()==bb.getID())
		{
			//System.out.println("esco0");
			return false;
		}
		if(!(aa.getTipo().equals(bb.getTipo())))
		{
			//System.out.println("esco1");
			return false;
		}
		if(aa.getTipo().equals("L") &&
				(a.getQuantitaScarico()-aa.getQuantita()+bb.getQuantita()>c || b.getQuantitaScarico()-bb.getQuantita()+aa.getQuantita()>c))
		{
			//System.out.println("esco21");
			return false;
		}
		if(aa.getTipo().equals("B") &&
				(a.getQuantitaCarico()-aa.getQuantita()+bb.getQuantita()>c || b.getQuantitaCarico()-bb.getQuantita()+aa.getQuantita()>c))
		{
			//System.out.println("esco22");
			return false;
		}
		if(a.equals(b))
		{
			if(calcolaDistanza(bb,aa,a) >= calcolaDistanza(a))
				return false;
		}
		else
		{
			if(calcolaDistanza(a,aa,bb)+calcolaDistanza(b,bb,aa)>=calcolaDistanza(a)+calcolaDistanza(b))
				{
					//System.out.println("esco3");
					return false;
				}
		}
		return true;
	}
	
	private static boolean compara(Rotta a, Rotta b, Nodo aa, int bbb, int c)
	{
		//controllo se due nodi sono uguali, non appartengono allo stesso tipo, non superano la capacità massima,
		//uno dei due non sia l'ultimo nodo di linehaul, che la nuova configurazione non sia vantaggiosa
		//se tutte sono false restituisco vero
		if(a.equals(b))
		{
			return false;
		}
		else if(!(aa.getTipo().equals(b.getClienti().get(bbb).getTipo())))
		{
			return false;
		}
		else if(aa.getTipo().equals("L") &&
		(b.getQuantitaScarico()+aa.getQuantita()>c))
		{
			return false;
		}
		else if(aa.getTipo().equals("B") &&
				(b.getQuantitaCarico()+aa.getQuantita()>c))
		{
			return false;
		}
		else if(calcolaDistanza(a,aa,0)+calcolaDistanza(b,aa,bbb)>=calcolaDistanza(a)+calcolaDistanza(b))
		{
			return false;
		}
		else if(a.getLineHauls().size() <= 1)
			return false;
		return true;
	}
	
	//con tutti i metodi calcola distanza calcolo i pesi della rotta data utilizzando la formula della distanza tra due punti
	//ripetuta per ogni nodo della rotta e il suo successivo e sommando tra loro tutti i risultati
	//ho più casi in maniera tale da calcolarla anche con l'aggiunta di un nuovo nodo o la rimozione/spostamento di un vecchio o entrambe
	private static double calcolaDistanza(Nodo aa, Nodo bb,Rotta a)
	{
		double sum=0;
		for(int i=0; i<a.getClienti().size()-1;i++)
		{
			if(a.getClienti().get(i).equals(aa))
			{
				if(a.getClienti().get(i+1).equals(bb))
				{
					sum=sum+distanza(bb.getX(),aa.getX(),bb.getY(),aa.getY());

				}
				else
				{
					sum=sum+distanza(bb.getX(),a.getClienti().get(i+1).getX(),bb.getY(),a.getClienti().get(i+1).getY());

				}
			}
			else if(a.getClienti().get(i).equals(bb))
			{
				if(a.getClienti().get(i+1).equals(aa))
				{
					sum=sum+distanza(aa.getX(),bb.getX(),aa.getY(),bb.getY());
				}
				else
				{
					sum=sum+distanza(aa.getX(),a.getClienti().get(i+1).getX(),aa.getY(),a.getClienti().get(i+1).getY());
				}
			}
			else if(a.getClienti().get(i+1).equals(aa))
			{
				sum=sum+distanza(a.getClienti().get(i).getX(),bb.getX(),a.getClienti().get(i).getY(),bb.getY());
			}
			else if(a.getClienti().get(i+1).equals(bb))
			{
				sum=sum+distanza(a.getClienti().get(i).getX(),aa.getX(),a.getClienti().get(i).getY(),aa.getY());
			}
			else
			{
				sum=sum+distanza(a.getClienti().get(i).getX(),a.getClienti().get(i+1).getX(),a.getClienti().get(i).getY(),a.getClienti().get(i+1).getY());
			}
		}
		return sum;	
	}
	
	private static double calcolaDistanza(Rotta a, Nodo aa, Nodo bb)
	{
		double sum=0;
		for(int i=0; i<a.getClienti().size()-1;i++)
		{
			if(a.getClienti().get(i).equals(aa))
				sum=sum+distanza(bb.getX(),a.getClienti().get(i+1).getX(),bb.getY(),a.getClienti().get(i+1).getY());
			else if(a.getClienti().get(i+1).equals(aa))
				sum=sum+distanza(a.getClienti().get(i).getX(),bb.getX(),a.getClienti().get(i).getY(),bb.getY());
			else
				sum=sum+distanza(a.getClienti().get(i).getX(),a.getClienti().get(i+1).getX(),a.getClienti().get(i).getY(),a.getClienti().get(i+1).getY());	
		}
		return sum;
	}
	
	private static double calcolaDistanza(Rotta a)
	{
		double sum=0;
		for(int i=0; i<a.getClienti().size()-1;i++)
		{
			sum=sum+Math.sqrt((a.getClienti().get(i).getX()-a.getClienti().get(i+1).getX())*
					(a.getClienti().get(i).getX()-a.getClienti().get(i+1).getX())+
					(a.getClienti().get(i).getY()-a.getClienti().get(i+1).getY())*
					(a.getClienti().get(i).getY()-a.getClienti().get(i+1).getY()));			
		}
		return sum;
	}
	
	private static double calcolaDistanza(Rotta a, Nodo bb, int c)
	{
		double sum=0;
		for(int i=0; i<a.getClienti().size()-1;i++)
		{
			if(c==0)
			{
				if(a.getClienti().get(i).equals(bb)){}
				else if(a.getClienti().get(i+1).equals(bb))
				{
					sum=sum+distanza(a.getClienti().get(i).getX(),a.getClienti().get(i+2).getX(),a.getClienti().get(i).getY(),a.getClienti().get(i+2).getY());
				}
				else
				{
					sum=sum+distanza(a.getClienti().get(i).getX(),a.getClienti().get(i+1).getX(),a.getClienti().get(i).getY(),a.getClienti().get(i+1).getY());	
				}
			}
			else
			{
				if(i==c)
				{
					sum=sum+distanza(a.getClienti().get(i).getX(),bb.getX(),a.getClienti().get(i).getY(),bb.getY())+
							distanza(bb.getX(),a.getClienti().get(i+1).getX(),bb.getY(),a.getClienti().get(i+1).getY());
				}
				else
				{
					sum=sum+distanza(a.getClienti().get(i).getX(),a.getClienti().get(i+1).getX(),a.getClienti().get(i).getY(),a.getClienti().get(i+1).getY());	
				}
			}
		}
		return sum;
	}
	
	//scambio i nodi di due rotte distinte nel primo metodo
	//nel secondo aggiungo un nodo ad una rotta e rimuovo il medesimo da un'altra
	private static void scambia(Rotta a, Rotta b, Nodo aa, Nodo bb, int aaa, int bbb)
	{
		Nodo app=aa;
		a.getClienti().set(aaa, bb);
		b.getClienti().set(bbb, app);
		a.updateLoad();
		b.updateLoad();
		//System.out.println(aa.getID());
	}
	
	private static void scambia(Rotta a, Rotta b, Nodo aa, int aaa, int bbb)
	{
		b.getClienti().add(bbb+1, aa);
		a.getClienti().remove(aaa);
		a.updateLoad();
		b.updateLoad();
		//System.out.println(aa.getID());
	}
	
	private static double distanza(double x1, double x2, double y1, double y2)
	{
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
}

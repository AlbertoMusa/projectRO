import java.util.*;

public class LocalSearch
{
	public static void esegui(Istanza istanza)
	{
		for(int i=0; i<istanza.getRotte().size(); i++) //ciclo che prende tutte le rotte
		{
			for(int j=1; j<istanza.getRotte().get(i).getClienti().size()-1;j++) //ciclo che prende tutti i clienti non 0 per ogni rotta
			{
				for(int k=0; k<istanza.getRotte().size(); k++) //ciclo che prende tutte le rotte
				{
					for(int w=1;w<istanza.getRotte().get(k).getClienti().size()-1;w++) //ciclo che prende tutti i clienti non 0 per ogni rotta
					{
						//in pratica per ogni nodo presente (primi due cicli) lo posiziono in ogni possibile
						//altro posto scambiandolo con un altro nodo (che prendo con i restanti cicli)
						//se lo scambio è possibile e conveniente lo faccio e rinizio con la nuova configurazione
						//quando mi farà passare tutto vuol dire che avrò fatto la miglior configurazione...
						// fino ad ora ho trattato L e B a parte
						// il controllo per mischiarli lo faro dopo...
						// System.out.println(i + " " + j + " " + k + " " + w);
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
							System.out.println(i + " " + j + " " + k + " " + w);
							i=0;
							j=1;
							k=0;
							w=1;
							//istanza.stampaRotte();
						}	
					}
				}
			}
		}
		
		istanza.stampaRotte();
		System.out.println("FINE LS FASE1");
		
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
								System.out.println(i + " " + j + " " + k + " " + w);
								i=0;
								j=1;
								k=0;
								w=1;
								//istanza.stampaRotte();
							}	
					}
				}
			}
		}	
		
		istanza.stampaRotte();
		System.out.println("FINE LS FASE2");
		
		//qua controllo gli scambi tra L e B (da fare)
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
						System.out.println(i + " " + k);
						i=0;
						k=0;
					}
				}	
			}
		}	
				
	}
	
	private static boolean compara(Rotta a, Rotta b, Nodo aa, Nodo bb, int c)
	{
		if(aa.getID()==bb.getID()) //se il nodo è lo stesso esco
		{
			//System.out.println("esco0");
			return false;
		}
		if(!(aa.getTipo().equals(bb.getTipo()))) //se il nodo non è dello stesso tipo esco
		{
			//System.out.println("esco1");
			return false;
		}
		if(aa.getTipo().equals("L") &&
				(a.getQuantitaScarico()-aa.getQuantita()+bb.getQuantita()>c || b.getQuantitaScarico()-bb.getQuantita()+aa.getQuantita()>c))
		//se i nodi L scambiati hanno scarico maggiore della capacità
		{
			//System.out.println("esco21");
			return false;
		}
		if(aa.getTipo().equals("B") &&
				(a.getQuantitaCarico()-aa.getQuantita()+bb.getQuantita()>c || b.getQuantitaCarico()-bb.getQuantita()+aa.getQuantita()>c))
		//se i nodi B scambiati hanno carico maggiore della capacità
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
				//se la distanza con lo scambio conviene più di quella attuale
				{
					//System.out.println("esco3");
					return false;
				}
		}
		return true;
	}
	
	private static boolean compara(Rotta a, Rotta b, Nodo aa, int bbb, int c)
	{
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
		return true;
	}
	
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
	
	//nei prossimi due metodi calcolo la distanza tra due punti fino a ricavarla per tutta la rotta
	private static double calcolaDistanza(Rotta a, Nodo aa, Nodo bb)
	{
		//inverto ma se sono nella stessa riga non torna perche 0-1-2-3-4-0 = 0-2-2-3-4
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
	
	private static void scambia(Rotta a, Rotta b, Nodo aa, Nodo bb, int aaa, int bbb)
	{
		//inverto i nodi aa e bb nelle posizioni aaa e bbb delle rispettive rotte
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
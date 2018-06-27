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
						 System.out.println(i + " " + j + " " + k + " " + w);
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
							i,k);
							i=0;
							j=1;
							k=0;
							w=1;
						}	
					}
				}
			}
		}
		//qua controllo gli scambi singoli (da fare)
		
		//qua controllo gli scambi tra L e B (da fare)
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
				a.getQuantitaScarico()-aa.getQuantita()+bb.getQuantita()>c || b.getQuantitaScarico()-bb.getQuantita()+aa.getQuantita()>c)
		//se i nodi L scambiati hanno scarico maggiore della capacità
		{
			//System.out.println("esco2");
			return false;
		}
		if(aa.getTipo().equals("B") &&
				a.getQuantitaCarico()-aa.getQuantita()+bb.getQuantita()>c || b.getQuantitaCarico()-bb.getQuantita()+aa.getQuantita()>c)
		//se i nodi B scambiati hanno carico maggiore della capacità
		{
			//System.out.println("esco2");
			return false;
		}
		if(calcolaDistanza(a,aa,bb)+calcolaDistanza(b,bb,aa)>=calcolaDistanza(a)+calcolaDistanza(b))
		//se la distanza con lo scambio conviene più di quella attuale
		{
			//System.out.println("esco3");
			return false;
		}
		return true;
	}
	
	//nei prossimi due metodi calcolo la distanza tra due punti fino a ricavarla per tutta la rotta
	private static double calcolaDistanza(Rotta a, Nodo aa, Nodo bb)
	{
		double sum=0;
		for(int i=0; i<a.getClienti().size()-1;i++)
		{
			if(a.getClienti().get(i).equals(aa))
				sum=sum+Math.sqrt((bb.getX()-a.getClienti().get(i+1).getX())*
						(bb.getX()-a.getClienti().get(i+1).getX())+
						(bb.getY()-a.getClienti().get(i+1).getY())*
						(bb.getY()-a.getClienti().get(i+1).getY()));
			else if(a.getClienti().get(i+1).equals(aa))
				sum=sum+Math.sqrt((a.getClienti().get(i).getX()-bb.getX())*
						(a.getClienti().get(i).getX()-bb.getX())+
						(a.getClienti().get(i).getY()-bb.getY())*
						(a.getClienti().get(i).getY()-bb.getY()));
			else
				sum=sum+Math.sqrt((a.getClienti().get(i).getX()-a.getClienti().get(i+1).getX())*
						(a.getClienti().get(i).getX()-a.getClienti().get(i+1).getX())+
						(a.getClienti().get(i).getY()-a.getClienti().get(i+1).getY())*
						(a.getClienti().get(i).getY()-a.getClienti().get(i+1).getY()));			
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
	
	private static void scambia(Rotta a, Rotta b, Nodo aa, Nodo bb, int aaa, int bbb)
	{
		//inverto i nodi aa e bb nelle posizioni aaa e bbb delle rispettive rotte
		Nodo app=aa;
		a.getClienti().set(aaa, aa);
		b.getClienti().set(bbb, bb);
	}
}
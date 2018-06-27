import java.util.*;

public class LocalSearch
{
	public static void esegui(Istanza istanza)
	{
		for(int i=0; i<istanza.getRotte().size(); i++)
		{
			for(int j=1; j<istanza.getRotte().get(i).getClienti().size()-1;j++)
			{
				for(int k=0; k<istanza.getRotte().size(); k++)
				{
					for(int w=1;w<istanza.getRotte().get(k).getClienti().size()-1;w++)
					{
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
	}
	
	private static boolean compara(Rotta a, Rotta b, Nodo aa, Nodo bb, int c)
	{
		if(aa.getID()==bb.getID())
		{
			System.out.println("esco0");
			return false;
		}
		if(!(aa.getTipo().equals(bb.getTipo()) && aa.getTipo().equals("L")))
		{
			//System.out.println("esco1");
			return false;
		}
		if(a.getQuantitaScarico()-aa.getQuantita()+bb.getQuantita()>c || b.getQuantitaScarico()-bb.getQuantita()+aa.getQuantita()>c)
		{
			//System.out.println("esco2");
			return false;
		}
		if(calcolaDistanza(a,aa,bb)+calcolaDistanza(b,bb,aa)>=calcolaDistanza(a)+calcolaDistanza(b))
		{
			System.out.println("esco3");
			return false;
		}
		return true;
	}
	
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
		Nodo app=aa;
		a.getClienti().set(aaa, aa);
		b.getClienti().set(bbb, bb);
	}
}
import java.util.*;

public class LocalSearch
{
	public static void esegui(Istanza istanza)
	{	
		//qua dobbiamo scegliere l'ordine giusto degli esegui per avere il risultato migliore, nei test che ho fatto io risulta questo
		eseguiA(istanza);
		eseguiB(istanza);
		eseguiC(istanza);
		eseguiA(istanza);
		eseguiB(istanza);
	}
	
	public static void eseguiA(Istanza istanza)
	{
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
							//istanza.stampaRotte();
						}	
					}
				}
			}
		}
		istanza.stampaRotte();
		System.out.println("----------------------FINE LS FASE1----------------------");
	}
	
	public static void eseguiB(Istanza istanza)
	{
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
								//istanza.stampaRotte();
							}	
					}
				}
			}
		}	
		istanza.stampaRotte();
		System.out.println("----------------------FINE LS FASE2----------------------");
	}
	
	private static void eseguiC(Istanza istanza)
	{
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
		istanza.stampaRotte();
		System.out.println("----------------------FINE LS FASE3----------------------");
	}
	
	private static boolean compara(Rotta a, Rotta b, Nodo aa, Nodo bb, int c)
	{
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
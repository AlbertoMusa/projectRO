import java.util.ArrayList;
import java.util.Collections;

public class Istanza
{
	private Nodo deposito;
	private Nodo[] clienti;
	private int numVeicoli;
	private int capacitaVeicoli;
	private ArrayList<Rotta> rotte;
	private double costoTotale; //costo totale, ovvero la somma delle distanze di tutte le rotte

	public Istanza(Nodo deposito, Nodo[] clienti, int numVeicoli, int capacitaVeicoli)
	{
		this.deposito = deposito;
		this.clienti = clienti;
		this.numVeicoli = numVeicoli;
		this.capacitaVeicoli = capacitaVeicoli;
		this.rotte = new ArrayList<Rotta>();
		for(Nodo n : this.clienti)
			rotte.add(new Rotta(this.deposito, n));
		this.costoTotale = 0;
	}
	
	public void setRotte(ArrayList<Rotta> rotte)
	{
		this.rotte = rotte;
	}
	
	
	public void rimuovoRotta(Rotta r)
	{
		this.rotte.remove(r);
	}
	
	public void stampaRotte()
	{
		System.out.println("\n\nELENCO ROTTE");
		int k = 1;
		for(Rotta r: this.rotte)
		{
			String str = "";
			if(r.isOk(this.capacitaVeicoli))
				str = "OK";
			else
				str = "Not OK";
			
			System.out.println("Rotta_" + k + ": " + str + "   " + r.getNodiToString() + "  " + "[ " + r.getQuantitaScarico() + " ; " + r.getQuantitaCarico() + " ]");
			k++;
		}
		System.out.println("\n\n");
	}
	
	public Nodo getDeposito()
	{
		return this.deposito;
	}
	
	public Nodo[] getClienti()
	{
		return this.clienti;
	}
	
	public ArrayList<Rotta> getRotte()
	{
		return this.rotte;
	}
	
	public int getNumVeicoli()
	{
		return this.numVeicoli;
	}
	
	public int getCapacitaVeicoli()
	{
		return this.capacitaVeicoli;
	}
	
	//metodo che restituisce l'indice della rotta con meno linehaul
	public int getIndexOfRottaConMenoLineHaul()
	{
		int min = 999999999;
		int index = 0;
		
		for(int i = 0; i < this.getRotte().size(); i++)
		{
			int amount = this.getRotte().get(i).getLineHauls().size();
			if(amount < min)
			{
				min = amount;
				index = i;
			}
		}
		
		return index;
	}
	
	public double getIndexOfRottaConScaricoMedioInferiore()
	{
		double min = 99999999999.9999999999999999999999;
		int index = 0;
		
		for(int i = 0; i < this.getRotte().size(); i++)
		{
			double amount = this.getRotte().get(i).getQuantitaScarico() / this.getRotte().get(i).getLineHauls().size() ;
			if(amount < min)
			{
				min = amount;
				index = i;
			}
		}
		
		return index;
	}
	
	public void riordinaRotteDallaPiuCortaAllaPiuLunga()
	{
		Collections.sort(this.rotte, new RottaComparatorByNumNodes());
	}
}
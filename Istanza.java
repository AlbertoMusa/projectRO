import java.util.ArrayList;

public class Istanza
{
	private Nodo deposito;
	private Nodo[] clienti;
	private int numVeicoli;
	private int capacitaVeicoli;
	private ArrayList<Rotta> rotte;
	private double costoTotale; //costo totale, ovvero la somma delle distanze di tutte le rotte
	private String status ="NON OK";

	public Istanza(int numVeicoli, int capacitaVeicoli)
	{
		this.numVeicoli = numVeicoli;
		this.capacitaVeicoli = capacitaVeicoli;
	}
	
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
	
	//assegno le rotte all'istanza
	public void setRotte(ArrayList<Rotta> _rotte)
	{
		this.rotte.clear();
		for(Rotta r : _rotte)
			this.rotte.add(r.copiaDi());
	}
	
	//ricalcolo i costi e i carichi/scarichi della istanza e verifico che tutti i vincoli siano rispettati
	public void updateSolution()
	{
		this.costoTotale = 0;
		int fixedNodes = 0;
		boolean singoleRotteOk = true;

		for(Rotta r: this.rotte)
		{
			fixedNodes += r.getLineHauls().size() + r.getBackHauls().size();
			if(!r.isOk(this.capacitaVeicoli))
				singoleRotteOk = false;
			
			r.updateLoad();
			this.costoTotale = this.costoTotale + r.getCosto();
		}
		
		if(singoleRotteOk && (fixedNodes == this.clienti.length))
			this.status="OK";
		else
			this.status="NON OK";
	
		assert this.status == "OK";

	}
	
	
	//mostro a schermo le informazioni sulle rotte
	public void stampaRotte()
	{
		System.out.println("\nELENCO ROTTE");
		int k = 1;
		for(Rotta r: this.rotte)
		{
			String str = "";
			if(r.isOk(this.capacitaVeicoli))
				str = "OK";
			else
				str = "NON OK";
			
			System.out.println("Rotta_" + k + ":\t" + str  + "\t[ " + r.getQuantitaScarico() + " ; " + r.getQuantitaCarico() + " ]\t" + r.getCosto() + "\t" + r.getNodiToString());
			k++;
		}
				
		System.out.println("\nCosto Tot:\t" +this.costoTotale);
	}
	
	//aggiorno il costo totale della soluzione
	public void setCosto()
	{
		this.costoTotale=0;
		for(Rotta r: this.rotte)
			this.costoTotale= this.costoTotale + r.getCosto();
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

	//metodo che restituisce l'indice della rotta con scarico medio inferiore
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
	
	
//	public void riordinaRotteDallaPiuCortaAllaPiuLunga()
//	{
//		Collections.sort(this.rotte, new RottaComparatorByNumNodes());
//	}
	
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
	
	public String getStatus()
	{
		return this.status;
	}

	public double getCostoTotale()
	{
		return this.costoTotale;
	}
}
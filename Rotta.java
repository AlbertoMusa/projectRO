import java.util.ArrayList;

public class Rotta
{
	private ArrayList<Nodo> clienti;
	private double costo;
	private int quantitaCarico;	//somma delle quantità dei backhaul
	private int quantitaScarico; //somma delle quantità  dei linehaul

	public Rotta(Nodo deposito, Nodo cliente)
	{
		this.clienti = new ArrayList<Nodo>();
		this.clienti.add(deposito);
		this.clienti.add(cliente);
		this.clienti.add(deposito);
		
		this.costo = DistanceCalculator.calcolaDistanza(deposito, cliente)*2;
		if(cliente.getTipo().equals("L"))
		{
			this.quantitaCarico = 0;
			this.quantitaScarico = cliente.getQuantita();
		}
		else
		{
			this.quantitaCarico = cliente.getQuantita();
			this.quantitaScarico = 0;
		}
	}
	
	//aggiunte alberto
	public Rotta(Nodo deposito, ArrayList<Nodo> line, ArrayList<Nodo> back)
	{
		this.clienti = new ArrayList<Nodo>();
		this.clienti.add(deposito);
		for(int i=0; i<line.size(); i++)
		{
			this.clienti.add(line.get(i));
			this.quantitaScarico = this.quantitaScarico + line.get(i).getQuantita();
		}
		for(int i=0; i<back.size(); i++)
		{
			this.clienti.add(back.get(i));
			this.quantitaCarico = this.quantitaCarico + back.get(i).getQuantita();
		}
		this.clienti.add(deposito);
	}
	
	//cerco il nodo nella rotta e restituisco la sua posizione
	public int cercaNodo(Nodo nodo)
	{
		for(int k = 1; k < this.clienti.size() - 1; k++)
			if(this.clienti.get(k).getID() == nodo.getID())
				return k;
		
		return -1;
	}
	
	/*restituisco:
	 * 0 se il nodo e' a sinistra
	 * 1 se il nodo e' interno
	 * 2 se il nodo e' a destra
	 */
	public int isNodoEstremale(Nodo nodo)
	{
		if(this.clienti.get(1).getID() == nodo.getID())
			return 0;
		else if(this.clienti.get(this.clienti.size() - 2).getID() == nodo.getID())
			return 2;
		else
			return 1;
	}

	//aggiungo un nodo a destra, può quindi essere sia backhaul che linehaul
	public void aggiungoNodoADestra(Nodo cliente)
	{
		this.clienti.add(this.clienti.size() - 1, cliente);

		if(cliente.getTipo().equals("L"))
			this.quantitaScarico += cliente.getQuantita();
		else 
			if(cliente.getTipo().equals("B"))
				this.quantitaCarico += cliente.getQuantita();
	}
	
	//aggiungo un nodo a destra, può quindi essere solo linehaul
	public void aggiungoNodoASinistra(Nodo cliente)
	{
		this.clienti.add(1, cliente);

		if(cliente.getTipo().equals("L"))
			this.quantitaScarico += cliente.getQuantita();
		else 
			if(cliente.getTipo().equals("B"))
				this.quantitaCarico += cliente.getQuantita();
	}
		
	//converto in una stringa l'elenco dei nodi della rettà
	public String getNodiToString()
	{
		String list = "";
		for(Nodo n : this.clienti)
			list = list + n.getID() + " - "; 
		
		list = list.substring(0, list.length() - 3);
		return list;
	}
	
	//verifico se la rotta rispetta i vincoli
	public boolean isOk(int capacita)
	{
		boolean order = isOrderOk();
		boolean load = isLoadOk(capacita);
		
		boolean ok = order && load;

		assert load==true;
		assert order==true;
			
		return ok;
	}
	
	//verifico se la rotta è di tipo LLL...BBB
	private boolean isOrderOk()
	{
		if(this.getLineHauls().size()==0)
			return false;
		
		boolean BFound = false;
		for(Nodo n: this.getClienti())
		{
			if(BFound)
				if(n.getTipo().equals("L"))
					return false;
			
			if(n.getTipo().equals("B"))
				BFound = true;
		}
		
		return true;
	}
	
	//verifico se la rotta rispetta i vincoli del carico/scarico
	private boolean isLoadOk(int capacita)
	{		
		this.updateLoad();
		
		if(this.getQuantitaScarico() > capacita)
			return false;

		if(this.getQuantitaCarico() > capacita)
			return false;
		
		return true;
	}
	
	//ricalcolo la quantità di scarico e carico e aggiorno
	public void updateLoad()
	{
		int carico = 0;
		int scarico = 0;
		
		for(Nodo n: this.getLineHauls())
			scarico += n.getQuantita();
				
		for(Nodo n: this.getBackHauls())
			carico += n.getQuantita();
		
		this.quantitaScarico = scarico;
		this.quantitaCarico = carico;
	}
	
	//rimuovo il primo nodo della lista e lo restituisco
	public Nodo removeFirst()
	{
		Nodo d = this.getClienti().get(1);
		this.getClienti().remove(d);
		
		return d;
	}
	
	//rimuovo il nodo alla posizione n
	public Nodo remove(int n)
	{
		Nodo d = this.getClienti().get(n);
		this.getClienti().remove(d);
		
		return d;
	}
	
//	//ricalcolo il costo della rotta e aggiorno
//	public void calcolaCosto()
//	{
//		this.costo=0;
//		for(int i=0; i<this.clienti.size()-1; i++)
//		{
//			this.costo = this.costo + Math.sqrt((clienti.get(i).getX()-clienti.get(i+1).getX())*
//					(clienti.get(i).getX()-clienti.get(i+1).getX())+
//					(clienti.get(i).getY()-clienti.get(i+1).getY())*
//					(clienti.get(i).getY()-clienti.get(i+1).getY()));
//		}
//	}
	
	//da usare per creare copie per valore
	private Rotta(ArrayList<Nodo> _clienti)
	{
		this.clienti = new ArrayList<Nodo>();
		for(Nodo c : _clienti)
			this.clienti.add(c);

		this.updateCost();
		this.updateLoad();
	}
	
	//ricalcolo il costo della rotta e aggiorno
	public void updateCost()
	{
		double distanza = 0;
		for(int i = 0; i < this.getClienti().size() - 1; i++)
		{
			Nodo a = this.getClienti().get(i);
			Nodo b = this.getClienti().get(i+1);
			double d = DistanceCalculator.calcolaDistanza(a, b);
			distanza += d;
		}
		this.costo = distanza;
	}
	
	public Rotta copiaDi()
	{
		Rotta a = new Rotta(this.clienti);
		return a;
	}
	
	public int getQuantitaCarico()
	{
		return this.quantitaCarico;
	}
	
	public int getQuantitaScarico()
	{
		return this.quantitaScarico;
	}
	
	public double getCosto()
	{
		this.updateCost();
		return this.costo;
	}
	
	public ArrayList<Nodo> getClienti()
	{
		return this.clienti;
	}
	
	public ArrayList<Nodo> getLineHauls()
	{
		ArrayList<Nodo> linehauls = new ArrayList<Nodo>();
		for(Nodo l : this.clienti)
			if(l.getTipo().equals("L"))
				linehauls.add(l);
			else if(l.getTipo().equals("B"))
				break;
		
		return linehauls;
	}
	
	public ArrayList<Nodo> getBackHauls()
	{
		ArrayList<Nodo> backhauls = new ArrayList<Nodo>();
		for(Nodo l : this.clienti)
			if(l.getTipo().equals("B"))
				backhauls.add(l);

		return backhauls;
	}
}
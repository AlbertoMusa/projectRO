public class Saving
{
	private Nodo primoNodo;
	private Nodo secondoNodo;
	private double valoreSaving;
	private boolean merged;
	
	public Saving(Nodo primo, Nodo secondo, double valoreSaving)
	{
		this.primoNodo = primo;
		this.secondoNodo = secondo;
		this.valoreSaving = valoreSaving;
		this.merged = false;
	}
	
	public Nodo getPrimoNodo()
	{
		return this.primoNodo;
	}
	
	public Nodo getSecondoNodo()
	{
		return this.secondoNodo;
	}
	
	public double getValoreSaving()
	{
		return this.valoreSaving;
	}
	
	public boolean isMerged()
	{
		return this.merged;
	}
	
	public void setMerged()
	{
		this.merged = true;
	}
}
public class Nodo
{
	private int x;
	private int y;
	private String tipo;
	private int quantita;
	private int ID;
	
	public Nodo(int x, int y, String tipo, int quantita, int ID)
	{
		this.x = x;
		this.y = y;
		this.tipo = tipo;
		this.quantita = quantita;
		this.ID = ID;
	}

	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public String getTipo()
	{
		return this.tipo;
	}
	
	public int getQuantita()
	{
		return this.quantita;
	}
	
	public int getID()
	{
		return this.ID;
	}
}
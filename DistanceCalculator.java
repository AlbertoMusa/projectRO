public class DistanceCalculator 
{
	public static double calcolaDistanza(Nodo a, Nodo b) 
	{
		int deltaX = Math.abs(a.getX() - b.getX());
		int deltaY = Math.abs(a.getY() - b.getY());
		return Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
}

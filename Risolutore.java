public class Risolutore
{
	public static long[] risolvi(Istanza istanza, String mode)
	{	
		//clarke-wright
		long startTime = System.currentTimeMillis();
		ClarkWright.esegui(mode, istanza);
		long CW_Time = System.currentTimeMillis() - startTime;
		System.out.println("--------------------FINE C&W " + mode +  "--------------------------");
		istanza.stampaRotte();
		
		//allinaamento rotte al numero di veicoli
		startTime = System.currentTimeMillis();
		AllineamentoRotte.esegui(istanza);
		long AL_Time = System.currentTimeMillis() - startTime;
		System.out.println("--------------------FINE ALLINEAMENTO--------------------------");
		istanza.stampaRotte();
		
		//local search
		startTime = System.currentTimeMillis();
 		LocalSearch.esegui(istanza);
		long LS_Time = System.currentTimeMillis() - startTime;
		istanza.stampaRotte();
		
 		long times[] = new long[3];
 		times[0] = CW_Time;
 		times[1] = AL_Time;
 		times[2] = LS_Time;
 		
 		return times;
	}
}
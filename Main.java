import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;

public class Main {
	
	public static void main(String [] args) throws IOException
	{
		String folderName = "/home/alberto/Workbench/Eclipse/RO/bin/Istance/";
		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();
		
		File sol = new File("soluzioni2.txt");
		sol.createNewFile();
	    FileWriter writer = new FileWriter(sol);
	      
		for(File f: listOfFiles)
		{
			// togli ! e metti nome file
			if(!f.getName().equals("info.txt")){
			//if(f.getName().equals("C4.txt")){
				//inizializzazione problema
				//String file = args[0];
				System.out.println("----------------------------------------------------\n" + f.getName() + "\n");
				String file = folderName + f.getName();
				Istanza istanzaS = letturaFile(file);
				System.out.println("n veicoli: " + istanzaS.getNumVeicoli() + "\tcapacita: " +istanzaS.getCapacitaVeicoli());
				
				//risoluzione problema sequenziale
				istanzaS = Risolutore.risolvi(istanzaS, "SEQ");				
		
				//reinizializzazione problema
				Istanza istanzaP = letturaFile(file);
				
				System.out.println("--------------------------------------------------------------------\n--------------------------------------------------------------------");
		
				//risoluzione problema parallelo
				istanzaP = Risolutore.risolvi(istanzaP, "PAR");		
				
				System.out.println("\n" +f.getName() + "\tSEQ: " + istanzaS.getCostoTotale() + "\tPAR: " + istanzaP.getCostoTotale());
				writer.write(f.getName() + "\tSEQ: " + istanzaS.getCostoTotale() + "\tPAR: " + istanzaP.getCostoTotale() + "\n");
				writer.write("\tSeq\tn veivoli:" + istanzaS.getNumVeicoli() + "\tn rotte:" + istanzaS.getRotte().size() + "\n");
				writer.write("\tPar\tn veivoli:" + istanzaP.getNumVeicoli() + "\tn rotte:" + istanzaP.getRotte().size() + "\n");
				writer.write("\tStatusPar: " + istanzaS.getStatus() + "\tStatusSeq: " + istanzaP.getStatus() + "\n\n");


			}
		}
		writer.close();
	}
	
	//stampo le info dell'istanza
	private static void riepilogoIstanza(Istanza istanza)
	{
		Nodo[] cli = istanza.getClienti();
		Nodo dep = istanza.getDeposito();
		int numClienti = cli.length;
		
		System.out.println("Numero clienti = " + numClienti);
		System.out.println("Numero veicoli = " + istanza.getNumVeicoli());
		System.out.println("CapacitÃ  veicoli = " + istanza.getCapacitaVeicoli());
		System.out.println("---------------------------------------");
		System.out.println("Deposito");
		System.out.println("Coord { " + dep.getX() +  " , " + dep.getY() + " }");
		System.out.println("---------------------------------------");
		System.out.println("Clienti");
		for(int i = 0; i < numClienti; i++)
		{
			System.out.println("Coord = { " + cli[i].getX() +  " , " + cli[i].getY() + " }");
			System.out.println("Quantita = " + cli[i].getQuantita()); 
			System.out.println("Tipo = " + cli[i].getTipo());
			System.out.println("-----------------------");
		}
	}
	
	private static Istanza letturaFile(String file)
	{
		int numClienti = 0;
		int clientiAggiunti = 0;
		int numVeicoli = 0;
		int capacitaVeicoli = 0;
		Nodo[] clienti = null;
		Nodo deposito = null;
		
		BufferedReader br = null;
		FileReader fr = null;

		int row = 0;
		
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				row++;
				switch(row)
				{
					case 1:
						numClienti = Integer.parseInt(sCurrentLine);
						clienti = new Nodo[numClienti];
						break;
					case 2:
						break;
					case 3:
						numVeicoli = Integer.parseInt(sCurrentLine);
						break;
					case 4:
						String[] splitted = sCurrentLine.split("\\s+");
						capacitaVeicoli = Integer.parseInt(splitted[3]);
						deposito = new Nodo(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]), "D", 0, 0);
						break;
					default:
						String[] splitted_ = sCurrentLine.split("\\s+");
						int primo = Integer.parseInt(splitted_[2]);
						int secondo = Integer.parseInt(splitted_[3]);
						int quantita = primo + secondo;
						String etichetta = "";
						if (primo == 0)
							etichetta = "B";
						else
							etichetta = "L";
						
						Nodo cliente = new Nodo(Integer.parseInt(splitted_[0]), Integer.parseInt(splitted_[1]), etichetta, quantita, clientiAggiunti+1);
						clienti[clientiAggiunti] = cliente;
						clientiAggiunti++;
				}
			}

		} 
		catch (IOException e) {
			e.printStackTrace();

		}
		finally
		{
			try
			{
				if (br != null)
					br.close();

				if (fr != null)
					fr.close();
			} 
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return new Istanza(deposito, clienti, numVeicoli, capacitaVeicoli);
	}
}
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;

import org.apache.poi.ss.excelant.*;
import org.apache.poi.ss.excelant.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {
	
	public static void main(String [] args) throws IOException
	{
		//String folderName = "/home/alberto/Workbench/Eclipse/RO/bin/Istance/";
		String folderName = "D:\\Università\\materiale didattico\\Facoltà di Scienze\\Magistrale\\SEM4\\DS\\projectRO\\Istance\\";
		
		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();
		
		File riepGLO = new File("riepologoTUTTI.txt");
		riepGLO.createNewFile();
	    FileWriter writerGLO = new FileWriter(riepGLO);

	    //dati iniziali per la tabella excel
	    ArrayList<String[]> righe = new ArrayList<String[]>();
		String[] header = { "Istanza", "Tipo C&W", "Costo Totale", "GAP", "Tempo (ms)"};
		righe.add(header);

		for(File f: listOfFiles)
		{
			// togli ! e metti nome file
			if(!f.getName().equals("info.txt")){
			//if(f.getName().equals("A1.txt")){
							
				System.out.println("----------------------------------------------------\n" + f.getName() + "\n");
				String file = folderName + f.getName();
				
				Istanza istanzaS = letturaFile(file);
				System.out.println("n veicoli: " + istanzaS.getNumVeicoli() + "\tcapacita: " +istanzaS.getCapacitaVeicoli());
				
				//risoluzione problema sequenziale
				long timesS[] = Risolutore.risolvi(istanzaS, "SEQ");							
				
				//reinizializzazione problema
				Istanza istanzaP = letturaFile(file);
				
				System.out.println("--------------------------------------------------------------------\n--------------------------------------------------------------------");
		
				//risoluzione problema parallelo
				long timesP[] = Risolutore.risolvi(istanzaP, "PAR");		
				
				System.out.println("\n" +f.getName() + "\tSEQ: " + istanzaS.getCostoTotale() + "\tPAR: " + istanzaP.getCostoTotale());
				
				//scrivo sul riepilogo globale
				writerGLO.write(f.getName() + "\tSEQ: " + istanzaS.getCostoTotale() + "\tPAR: " + istanzaP.getCostoTotale() + "\n");
				writerGLO.write("\tSeq\tn veivoli:" + istanzaS.getNumVeicoli() + "\tn rotte:" + istanzaS.getRotte().size() + "\n");
				writerGLO.write("\tPar\tn veivoli:" + istanzaP.getNumVeicoli() + "\tn rotte:" + istanzaP.getRotte().size() + "\n");
				writerGLO.write("\tStatusPar: " + istanzaS.getStatus() + "\tStatusSeq: " + istanzaP.getStatus() + "\n\n");
				
				//scrivo sui file singoli
				double gapS = riepilogoSingolo(f.getName(), istanzaS, "SEQ", timesS);
				double gapP = riepilogoSingolo(f.getName(), istanzaP, "PAR", timesP);
				
				//aggiungo riga per la tabella di riepilo excel
				String[] rigaS = { f.getName() , "SEQ" , String.valueOf(istanzaS.getCostoTotale()), String.valueOf(gapS), String.valueOf(timesS[0] + timesS[1] + timesS[2])};
				String[] rigaP = { f.getName() , "PAR" , String.valueOf(istanzaP.getCostoTotale()), String.valueOf(gapP), String.valueOf(timesP[0] + timesP[1] + timesP[2])};
				righe.add(rigaS);
				righe.add(rigaP);
			}
		}
		writerGLO.close();
		
		//creo tabella excel di riepilogo
		creaTabellaExcel(righe);
	}
	

	private static void creaTabellaExcel(ArrayList<String[]> righe) throws IOException
	{
        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
        Sheet sheet = workbook.createSheet("Riepilogo");
        
        /* CreationHelper helps us create instances of various things like DataFormat, 
        Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();
        
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        
        // Create a Row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < righe.get(0).length; i++) {
          Cell cell = headerRow.createCell(i);
          cell.setCellValue(righe.get(0)[i]);
          cell.setCellStyle(headerCellStyle);
        }
        
        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        
        // Create Other rows and cells with employees data
        int rowNum = 1;
        righe.remove(0);
        for(String[] riga: righe) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(riga[0]);
            row.createCell(1).setCellValue(riga[1]);
            row.createCell(2).setCellValue(riga[2]);
            row.createCell(3).setCellValue(riga[3]);
            row.createCell(4).setCellValue(riga[4]);
        }

		// Resize all columns to fit the content size
        for(int i = 0; i < righe.get(0).length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("riepilogo.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
	}
	
	private static double riepilogoSingolo(String name, Istanza istanza, String mode, long[] tempi) throws IOException
	{
		File riepSIN = new File("riepologo_" + mode + "_" + name);
		riepSIN.createNewFile();
	    FileWriter writerSIN = new FileWriter(riepSIN);	
	    
	    for(int i = 0; i < istanza.getRotte().size(); i++)
	    	writerSIN.write("Rotta " + (i+1) + " : " + istanza.getRotte().get(i).getNodiToString() + " Costo = " + istanza.getRotte().get(i).getCosto() + "\n");
	    
	    writerSIN.write("\nCosto totale = " + istanza.getCostoTotale());
	    File wd = new File(".");
	    double bestCosto = getBestCosto(wd.getCanonicalPath() + "\\DetailedSols\\RPA_Solutions\\Detailed_Solution_" + name);
	    double gap = (istanza.getCostoTotale() - bestCosto)/bestCosto;
	    writerSIN.write("\nCosto best = " + bestCosto);
	    writerSIN.write("\nGAP = " + gap);
	    writerSIN.write("\n\nTempo CW = " + tempi[0]);
	    writerSIN.write("\nTempo AL = " + tempi[1]);
	    writerSIN.write("\nTempo LS = " + tempi[2]);
	    writerSIN.close();
	    
	    return gap;
	}
	
	private static double getBestCosto(String file)
	{
		File f = new File(file);
				
		BufferedReader br = null;
		FileReader fr = null;

		int row = 0;
		
		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				row++;
				switch(row)
				{	
					case 9:
						String[] value = sCurrentLine.split("\\s+");
						if (br != null)
							br.close();

						if (fr != null)
							fr.close();
						return Double.parseDouble(value[3]);
				}
			}

		} 
		catch (IOException e) {
			e.printStackTrace();

		}
		return 0;
	}
	
	//stampo le info dell'istanza
	private static void riepilogoIstanza(Istanza istanza)
	{
		Nodo[] cli = istanza.getClienti();
		Nodo dep = istanza.getDeposito();
		int numClienti = cli.length;
		
		System.out.println("Numero clienti = " + numClienti);
		System.out.println("Numero veicoli = " + istanza.getNumVeicoli());
		System.out.println("Capacità  veicoli = " + istanza.getCapacitaVeicoli());
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
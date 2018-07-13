# projectRO
- Main: classe iniziale, che avvia il Risolutore
- Nodo: classe che descrive i nodi (ovvero i clienti)
- Rotta: classe che rappresenta le rotte che si andranno a creare
- Istanza: classe che conterrà i dati del problema
- Risolutore: classe che contiene i metodi per la risoluzione del problema (Clarke-Wright, Allineamento Rotte, Local Search) 
- ClarkWright: classe per l'esecuzione dell'algoritmo di Clark and Wright
- Saving: classe che rappresenta il saving di due nodi
- SavingComparatorByValue: classe di appoggio per l'ordinamento dei savings sulla base del risparmio
- SavingComparatorByValue: classe di appoggio per l'ordinamento dei savings sulla base del tipo
- AllineamentoRotte: classe che riporta il numero di rotte al numero di veicoli dell'istanza
- RottaComparatorByAvgCapacityNodes: classe per l'ordinamento delle rotte (necessario nell'allineamento rotte)
- Local Search: classe per il miglioramento della soluzione tramite manovre di Local Search


# ISTRUZIONI
compilazione senza IDE
javac -cp .\poi-3.17\poi-3.17.jar;.\poi-3.17\poi-examples-3.17.jar;.\poi-3.17\poi-excelant-3.17.jar;.\poi-3.17\poi-ooxml-3.17.jar;.\poi-3.17\poi-ooxml-schemas-3.17.jar;.\poi-3.17\poi-scratchpad-3.17.jar;.\poi-3.17\ooxml-lib\curvesapi-1.04.jar;.\poi-3.17\ooxml-lib\xmlbeans-2.6.0.jar;.\poi-3.17\lib\commons-codec-1.10.jar;.\poi-3.17\lib\commons-collections4-4.1.jar;.\poi-3.17\lib\commons-logging-1.2.jar;.\poi-3.17\lib\junit-4.12.jar:.\poi-3.17\lib\log4j-1.2.17.jar; Main.java

esecuzione senza IDE
java -cp .\poi-3.17\poi-3.17.jar;.\poi-3.17\poi-examples-3.17.jar;.\poi-3.17\poi-excelant-3.17.jar;.\poi-3.17\poi-ooxml-3.17.jar;.\poi-3.17\poi-ooxml-schemas-3.17.jar;.\poi-3.17\poi-scratchpad-3.17.jar;.\poi-3.17\ooxml-lib\curvesapi-1.04.jar;.\poi-3.17\ooxml-lib\xmlbeans-2.6.0.jar;.\poi-3.17\lib\commons-codec-1.10.jar;.\poi-3.17\lib\commons-collections4-4.1.jar;.\poi-3.17\lib\commons-logging-1.2.jar;.\poi-3.17\lib\junit-4.12.jar:.\poi-3.17\lib\log4j-1.2.17.jar; Main
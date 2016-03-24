package printer;

import nl.positor.module.definition.LifeCycle;

public class PrinterImpl implements Printer, LifeCycle {
	private String lastPrint = "";
	
	public void print(String line) {
		lastPrint = line;
	}
	
	public void start() {
	}
	
	public void stop() {
	}
	
	@Override
	public String toString() {
		return lastPrint;
	}
}
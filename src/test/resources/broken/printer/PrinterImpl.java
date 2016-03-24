package printer;

import nl.positor.module.definition.LifeCycle;

public class PrinterImpl implements Printer, LifeCycle {
	
	public void print(String line) {
	}
	
	public void start() {
	}
	
	public void stop() {
	}
	
	@Override
	public String toString() {
		return "BROKEN";
	}
}
package printer;

public class PrinterImpl implements Printer {
	private String lastPrint = "";
	
	public void print(String line) {
		lastPrint = line;
	}
	
	@Override
	public String toString() {
		return lastPrint;
	}
}
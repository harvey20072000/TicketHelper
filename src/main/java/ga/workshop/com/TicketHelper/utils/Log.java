package ga.workshop.com.TicketHelper.utils;
import javax.swing.JOptionPane;

public class Log {
	public static void out(Object input,String type) {
		if(input == null || input.toString().isEmpty())
			return;
		switch (type) {
		case "error":
			JOptionPane.showMessageDialog(null, input, "LOG", JOptionPane.ERROR_MESSAGE);
			break;
		case "debug":
			JOptionPane.showMessageDialog(null, input, "LOG", JOptionPane.WARNING_MESSAGE);
			break;
		case "info":
			JOptionPane.showMessageDialog(null, input, "LOG", JOptionPane.INFORMATION_MESSAGE);
			break;
		default:
			break;
		}
	}
	
	public static void error(Object input,Object... args) {
		out(String.format(input.toString().replace("{}", "%s"), args),"error");
	}
	
	public static void debug(Object input,Object... args) {
		out(String.format(input.toString().replace("{}", "%s"), args),"debug");
	}
	
	public static void info(Object input,Object... args) {
		out(String.format(input.toString().replace("{}", "%s"), args),"info");
	}
}

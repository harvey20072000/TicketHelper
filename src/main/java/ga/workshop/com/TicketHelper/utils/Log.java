package ga.workshop.com.TicketHelper.utils;
import javax.swing.JOptionPane;

public class Log {
	public static void out(Object input,String type) {
		if(input == null || input.toString().isEmpty())
			return;
		int dialogIcon = 1;
		String dialogTitle = "LOG";
		switch (type) {
		case "error":
			dialogIcon = JOptionPane.ERROR_MESSAGE;
			break;
		case "debug":
			dialogIcon = JOptionPane.WARNING_MESSAGE;
			break;
		case "info":
			dialogIcon = JOptionPane.INFORMATION_MESSAGE;
			break;
		default:
			return;
		}
		
		final int finalDialogIcon = dialogIcon;
		new Thread() {
			public void run() {
				JOptionPane.showMessageDialog(null, input, dialogTitle, finalDialogIcon);
			};
		}.start();
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

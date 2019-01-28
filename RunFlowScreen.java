import javax.swing.SwingUtilities;

public class RunFlowScreen implements Runnable {
	public void run() {
		FlowScreen f = new FlowScreen();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new RunFlowScreen());
	}

}

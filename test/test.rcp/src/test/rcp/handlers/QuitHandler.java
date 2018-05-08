package test.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.widgets.Shell;

public class QuitHandler {
	@Execute
	public void execute(IWorkbench workbench, Shell shell) {
		workbench.close();
	}
}

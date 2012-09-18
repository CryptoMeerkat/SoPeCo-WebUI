package org.sopeco.frontend.client;

import org.sopeco.frontend.client.helper.ServerPush;
import org.sopeco.frontend.client.helper.SystemDetails;
import org.sopeco.frontend.client.layout.LoginBox;
import org.sopeco.frontend.client.layout.MainLayoutPanel;
import org.sopeco.frontend.client.layout.popups.Message;
import org.sopeco.frontend.client.rpc.StartupService;
import org.sopeco.frontend.client.rpc.StartupServiceAsync;
import org.sopeco.persistence.metadata.entities.DatabaseInstance;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry class of this webapplication.
 * 
 * @author Marius Oehler
 * 
 */
public class FrontendEntryPoint implements EntryPoint {

	private MainLayoutPanel mainLayoutPanel;

	private DatabaseInstance connectedDatabase;

	/**
	 * will be executed at the start of the application.
	 */
	public void onModuleLoad() {
		startup();

		SystemDetails.load();

		ServerPush.start();

		changeDatabase();
	}

	/**
	 * Initialize/reset the main view of the application.
	 * 
	 * @param newConnectedDatabase
	 *            the database of the current connection.
	 */
	public void initializeMainView(DatabaseInstance newConnectedDatabase) {
		connectedDatabase = newConnectedDatabase;

		RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();

		rootLayoutPanel.add(getMainLayoutPanel(true));
	}

	/**
	 * clears the root layout panel.
	 */
	private void clearRootLayout() {
		RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();

		rootLayoutPanel.clear();
	}

	/**
	 * Causing a change of the database.
	 */
	public void changeDatabase() {
		clearRootLayout();

		LoginBox box = new LoginBox(this);
		box.center();
	}

	/**
	 * Returns the main-layout-panel. If it doesn't exists, it will be created.
	 * 
	 * @param createNew
	 *            should it be recreated
	 * @return the main layout panel
	 */
	public MainLayoutPanel getMainLayoutPanel(boolean createNew) {
		if (mainLayoutPanel == null || createNew) {
			mainLayoutPanel = new MainLayoutPanel(this);
		}
		return mainLayoutPanel;
	}

	/**
	 * returns the database instance of the current connection/session.
	 * 
	 * @return database instance
	 */
	public DatabaseInstance getConnectedDatabase() {
		return connectedDatabase;
	}

	/**
	 * calls the startup procedure on the server.
	 */
	private void startup() {
		StartupServiceAsync startup = GWT.create(StartupService.class);

		startup.start(new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				GWT.log("startup passed");
			}

			@Override
			public void onFailure(Throwable caught) {
				Message.error("Error at startup");
			}
		});
	}
}

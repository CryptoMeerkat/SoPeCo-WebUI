package org.sopeco.frontend.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Marius Oehler
 * 
 */
public interface ScenarioManagerRPCAsync {

	/**
	 * Return a String-Array with all existing scenario names.
	 * 
	 * @param callback
	 */
	void getScenarioNames(AsyncCallback<String[]> callback);

	void addScenario(String name, AsyncCallback<Boolean> callback);

	void removeScenario(String name, AsyncCallback<Boolean> callback);

	void switchScenario(String name, AsyncCallback<Boolean> callback);

}

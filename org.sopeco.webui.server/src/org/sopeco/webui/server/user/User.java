/**
 * Copyright (c) 2013 SAP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the SAP nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SAP BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sopeco.webui.server.user;

import java.util.logging.Logger;

import org.sopeco.config.Configuration;
import org.sopeco.config.IConfiguration;
import org.sopeco.persistence.IPersistenceProvider;
import org.sopeco.persistence.entities.definition.MeasurementSpecification;
import org.sopeco.persistence.entities.definition.ScenarioDefinition;
import org.sopeco.webui.server.UiConfiguration;
import org.sopeco.webui.server.persistence.UiPersistence;
import org.sopeco.webui.shared.builder.MeasurementSpecificationBuilder;
import org.sopeco.webui.shared.builder.ScenarioDefinitionBuilder;
import org.sopeco.webui.shared.entities.account.Account;
import org.sopeco.webui.shared.entities.account.AccountDetails;

/**
 * 
 * @author Marius Oehler
 * 
 */
public class User {

	private String sessionId;
	
	/**
	 * Stores the SessionID to the SoPeCo Service. The user get a session id,
	 * when first time requesting the SoPeCo Service.
	 */
	private String serviceSessionId;

	private ScenarioDefinitionBuilder currentScenarioDefinitionBuilder;

	private String workingSpecification;
	private Account currentAccount;
	private IPersistenceProvider currentPersistenceProvider;
	private static final Logger LOGGER = Logger.getLogger(User.class.getName());
	private long lastRequestTime;

	public User(String sId) {
		sessionId = sId;
		lastRequestTime = System.currentTimeMillis();

		currentScenarioDefinitionBuilder = new ScenarioDefinitionBuilder();
	}

	public IPersistenceProvider getCurrentPersistenceProvider() {
		return currentPersistenceProvider;
	}

	public void setCurrentPersistenceProvider(IPersistenceProvider persistenceProvider) {
		this.currentPersistenceProvider = persistenceProvider;
	}

	public long getLastRequestTime() {
		return lastRequestTime;
	}

	public void setLastRequestTime(long pLastRequestTime) {
		this.lastRequestTime = pLastRequestTime;
	}

	public void setCurrentAccount(Account currentAccount) {
		this.currentAccount = currentAccount;
	}

	public Account getCurrentAccount() {
		return currentAccount;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setServiceSessionID(String serviceSessionId) {
		this.serviceSessionId = serviceSessionId;
	}

	public String getServiceSessionID() {
		return serviceSessionId;
	}
	
	public ScenarioDefinitionBuilder getCurrentScenarioDefinitionBuilder() {
		return currentScenarioDefinitionBuilder;
	}

	public void setCurrentScenarioDefinitionBuilder(ScenarioDefinitionBuilder scenarioDefinitionBuilder) {
		this.currentScenarioDefinitionBuilder = scenarioDefinitionBuilder;
	}

	public AccountDetails getAccountDetails() {
		return UiPersistence.getUiProvider().loadAccountDetails(currentAccount.getId());
	}

	// *******************************************************************************************************

	public boolean isExpired() {
		IConfiguration config = Configuration.getSessionSingleton(Configuration.getGlobalSessionId());
		int userTimeout = config.getPropertyAsInteger(UiConfiguration.USER_TIMEOUT, 0);
		if (userTimeout == 0 || System.currentTimeMillis() < lastRequestTime + userTimeout) {
			return false;
		} else {
			return true;
		}
	}

	public String getWorkingSpecification() {
		return workingSpecification;
	}

	/**
	 * Set the current specification, which is in the builder as default/working
	 * spec.
	 * 
	 * @param pWorkingSpecification
	 */
	public void setWorkingSpecification(String pWorkingSpecification) {
		this.workingSpecification = pWorkingSpecification;

		MeasurementSpecification specification = getCurrentScenarioDefinitionBuilder().getMeasurementSpecification(
				pWorkingSpecification);
		MeasurementSpecificationBuilder specificationBuilder = new MeasurementSpecificationBuilder(specification);
		getCurrentScenarioDefinitionBuilder().setSpecificationBuilder(specificationBuilder);
	}

	/**
	 * 
	 */
	public void storeCurrentScenarioDefinition() {
		LOGGER.info("store current ScenarioDefinition");

		ScenarioDefinition scenarioDef = currentScenarioDefinitionBuilder.getBuiltScenario();

		currentPersistenceProvider.store(scenarioDef);
	}
}

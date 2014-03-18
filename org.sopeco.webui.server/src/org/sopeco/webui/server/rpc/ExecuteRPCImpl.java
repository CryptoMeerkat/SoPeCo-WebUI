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
package org.sopeco.webui.server.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.validation.constraints.Null;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.sopeco.service.configuration.ServiceConfiguration;
import org.sopeco.service.persistence.entities.ScheduledExperiment;
import org.sopeco.service.rest.exchange.ExperimentStatus;
import org.sopeco.webui.server.rest.ClientFactory;
import org.sopeco.webui.server.rpc.servlet.SPCRemoteServlet;
import org.sopeco.webui.shared.entities.ExecutedExperimentDetails;
import org.sopeco.webui.shared.entities.FrontendScheduledExperiment;
import org.sopeco.webui.shared.entities.MECLog;
import org.sopeco.webui.shared.entities.RunningControllerStatus;
import org.sopeco.webui.shared.helper.MECLogEntry;
import org.sopeco.webui.shared.rpc.ExecuteRPC;

/**
 * 
 * @author Marius Oehler
 * 
 */
public class ExecuteRPCImpl extends SPCRemoteServlet implements ExecuteRPC {

	/** */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(ExecuteRPCImpl.class.getName());

	/*
	 * service /execution/schedule (POST)
	 * In the RESTful serivce, the scheduled experiment is NOT set active!
	 */
	@Override
	public void scheduleExperiment(FrontendScheduledExperiment rawScheduledExperiment) {
		requiredLoggedIn();
				
		WebTarget wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_EXECUTE,
															 ServiceConfiguration.SVC_EXECUTE_SCHEDULE);
		
		wt = wt.queryParam(ServiceConfiguration.SVCP_ACCOUNT_TOKEN, getToken());
		
		ScheduledExperiment scheduledExperiment = ServiceConverter.convertFrontendScheduledExperiment(rawScheduledExperiment);
		scheduledExperiment.setActive(true);
		
		Response r = wt.request(MediaType.APPLICATION_JSON).post(Entity.entity(scheduledExperiment, MediaType.APPLICATION_JSON));
		
		if (r.getStatus() != Status.OK.getStatusCode()) {
			LOGGER.info("The experiment could not be set to execution state. Most likely the "
						+ "ScheduledExperiment cannot be has the status 'active'.");
		}
	}

	@Override
	public List<FrontendScheduledExperiment> getScheduledExperiments() {
		
		WebTarget wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_EXECUTE,
						 									 ServiceConfiguration.SVC_EXECUTE_SCHEDULE);
		
		wt = wt.queryParam(ServiceConfiguration.SVCP_ACCOUNT_TOKEN, getToken());
		
		Response r = wt.request(MediaType.APPLICATION_JSON).get();
		
		List<ScheduledExperiment> listSE = r.readEntity(new GenericType<List<ScheduledExperiment>>() { });
		List<FrontendScheduledExperiment> listFSE = new ArrayList<FrontendScheduledExperiment>();
		
		for (ScheduledExperiment se : listSE) {
			listFSE.add(ServiceConverter.convertScheduledExperiment(se));
		}
		
		return listFSE;
	}

	@Override
	public boolean removeScheduledExperiment(long id) {
		requiredLoggedIn();
		
		WebTarget wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_EXECUTE,
															 String.valueOf(id));
		
		wt = wt.queryParam(ServiceConfiguration.SVCP_ACCOUNT_TOKEN, getToken());
		
		Response r = wt.request(MediaType.APPLICATION_JSON).delete();

		return r.getStatus() == Status.OK.getStatusCode();
	}

	@Override
	public boolean setScheduledExperimentEnabled(long id, boolean enabled) {
		requiredLoggedIn();
		
		WebTarget wt = null;
		
		if (enabled) {
			wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_ACCOUNT,
													   String.valueOf(id),
													   ServiceConfiguration.SVC_EXECUTE_ENABLE);
		} else {
			wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_ACCOUNT,
					 								   String.valueOf(id),
					 								   ServiceConfiguration.SVC_EXECUTE_DISABLE);
		}
		
		wt = wt.queryParam(ServiceConfiguration.SVCP_ACCOUNT_TOKEN, getToken());

		Response r = wt.request(MediaType.APPLICATION_JSON).put(Entity.entity(Null.class, MediaType.APPLICATION_JSON));
		
		return r.getStatus() == Status.OK.getStatusCode();
	}

	@Override
	public List<ExecutedExperimentDetails> getExecutedExperimentDetails() {
		requiredLoggedIn();

		System.out.println("Fetching list of ExecutedExperimentDetails.");
		
		// first fetch the current selected scenario name
		WebTarget wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_ACCOUNT,
				 											 ServiceConfiguration.SVC_ACCOUNT_INFO);
		
		wt = wt.queryParam(ServiceConfiguration.SVCP_ACCOUNT_TOKEN, getToken());
		
		Response r = wt.request(MediaType.APPLICATION_JSON).get();
		
		org.sopeco.service.persistence.entities.AccountDetails ad = r.readEntity(org.sopeco.service.persistence.entities.AccountDetails.class);
		
		if (ad == null) {
			return new ArrayList<ExecutedExperimentDetails>();
		}
		
		// now request the executedExperimentDetails corresponding to the current selected scenario
		wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_EXECUTE,
			     								   ServiceConfiguration.SVC_EXECUTE_DETAILS);
		
		wt = wt.queryParam(ServiceConfiguration.SVCP_EXECUTE_TOKEN, getToken());
		wt = wt.queryParam(ServiceConfiguration.SVCP_EXECUTE_SCENARIONAME, ad.getSelectedScenario());
		
		r = wt.request(MediaType.APPLICATION_JSON).get();
		
		List<org.sopeco.service.persistence.entities.ExecutedExperimentDetails> list =
				r.readEntity(new GenericType<List<org.sopeco.service.persistence.entities.ExecutedExperimentDetails>>() { });
		
		if (list != null) {
			
			List<ExecutedExperimentDetails> eedlist = new ArrayList<ExecutedExperimentDetails>();
			for (org.sopeco.service.persistence.entities.ExecutedExperimentDetails eed : list) {
				eedlist.add(ServiceConverter.convertToExecutedExperimentDetails(eed));
			}
			
		}

		System.out.println("Returning null, when fetching ExecutedExperimentDetails.");
		
		return null;
	}

	@Override
	public MECLog getMECLog(long id) {
		requiredLoggedIn();
		
		WebTarget wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_EXECUTE,
						 									 ServiceConfiguration.SVC_EXECUTE_MECLOG);
		
		wt = wt.queryParam(ServiceConfiguration.SVCP_ACCOUNT_TOKEN, getToken());
		wt = wt.queryParam(ServiceConfiguration.SVCP_EXECUTE_ID, id);
		
		Response r = wt.request(MediaType.APPLICATION_JSON).get();
		
		return ServiceConverter.convertToMECLog(r.readEntity(org.sopeco.service.persistence.entities.MECLog.class));
	}

	@Override
	public RunningControllerStatus getControllerLog() {
		requiredLoggedIn();
		
		// first fetch the current experiment key
		WebTarget wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_ACCOUNT,
				 											 ServiceConfiguration.SVC_ACCOUNT_INFO);
		
		wt = wt.queryParam(ServiceConfiguration.SVCP_ACCOUNT_TOKEN, getToken());
		
		Response r = wt.request(MediaType.APPLICATION_JSON).get();
		
		org.sopeco.service.persistence.entities.AccountDetails ad = r.readEntity(org.sopeco.service.persistence.entities.AccountDetails.class);
		
		if (ad == null) {
			return null;
		}
		
		wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_EXECUTE,
												   ServiceConfiguration.SVC_EXECUTE_STATUS);
		
		wt = wt.queryParam(ServiceConfiguration.SVCP_ACCOUNT_TOKEN, getToken());
		wt = wt.queryParam(ServiceConfiguration.SVCP_EXECUTE_KEY, ad.getExperimentKeyOfSelectedScenario());
		
		r = wt.request(MediaType.APPLICATION_JSON).get();
		
		ExperimentStatus es = r.readEntity(ExperimentStatus.class);

		if (r.getStatus() == Status.OK.getStatusCode()) {
			// now convert the ExperimentStatus object into a RunningControllerStatus object
			RunningControllerStatus rcs = new RunningControllerStatus();
			rcs.setAccount(es.getAccountId());
			rcs.setHasFinished(es.isFinished());
			rcs.setLabel(es.getLabel());
			rcs.setProgress(es.getProgress());
			rcs.setScenario(es.getScenarioName());
			rcs.setTimeRemaining(es.getTimeRemaining());
			rcs.setTimeStart(es.getTimeStart());
			
			rcs.setEventLogList(new ArrayList<MECLogEntry>());
			for (org.sopeco.service.execute.MECLogEntry meclogentry : es.getEventLogList()) {
				rcs.getEventLogList().add(ServiceConverter.convertToMECLogEntry(meclogentry));
			}
			
			return rcs;
		}
		
		return null;
	}

	@Override
	public void abortCurrentExperiment() {
		requiredLoggedIn();
		
		// first fetch the current experiment key
		WebTarget wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_ACCOUNT,
				 											 ServiceConfiguration.SVC_ACCOUNT_INFO);
		
		wt = wt.queryParam(ServiceConfiguration.SVCP_ACCOUNT_TOKEN, getToken());
		
		Response r = wt.request(MediaType.APPLICATION_JSON).get();
		
		org.sopeco.service.persistence.entities.AccountDetails ad = r.readEntity(org.sopeco.service.persistence.entities.AccountDetails.class);
		
		if (ad == null) {
			return;
		}
		
		wt = ClientFactory.getInstance().getClient(ServiceConfiguration.SVC_EXECUTE,
												   ServiceConfiguration.SVC_EXECUTE_ABORT);
		
		wt = wt.queryParam(ServiceConfiguration.SVCP_ACCOUNT_TOKEN, getToken());
		wt = wt.queryParam(ServiceConfiguration.SVCP_EXECUTE_KEY, ad.getExperimentKeyOfSelectedScenario());
		
		r = wt.request(MediaType.APPLICATION_JSON).put(Entity.entity(Null.class, MediaType.APPLICATION_JSON));
	}
}

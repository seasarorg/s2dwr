package org.seasar.dwr.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.dwrp.Batch;
import org.directwebremoting.dwrp.HtmlCallMarshaller;
import org.directwebremoting.dwrp.ProtocolConstants;
import org.directwebremoting.extend.Call;
import org.directwebremoting.extend.Calls;
import org.directwebremoting.extend.Creator;
import org.directwebremoting.extend.InboundContext;
import org.directwebremoting.extend.InboundVariable;
import org.directwebremoting.extend.MarshallException;
import org.directwebremoting.extend.ServerException;
import org.directwebremoting.extend.TypeHintContext;
import org.directwebremoting.util.Messages;
import org.seasar.dwr.util.ReflectUtil;

public class S2HtmlCallMarshaller extends HtmlCallMarshaller {

	/**
	 * By default we disable GET, but this hinders old Safaris
	 */
	private boolean allowGetForSafariButMakeForgeryEasier = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.directwebremoting.extend.Marshaller#marshallInbound(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public Calls marshallInbound(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServerException {
		// We must parse the parameters before we setup the conduit because it's
		// only after doing this that we know the scriptSessionId

		WebContext webContext = WebContextFactory.get();
		Batch batch = (Batch) request.getAttribute(ATTRIBUTE_BATCH);
		if (batch == null) {
			batch = new Batch(request, crossDomainSessionSecurity,
					allowGetForSafariButMakeForgeryEasier, sessionCookieName);

			// Save calls for retry exception
			request.setAttribute(ATTRIBUTE_BATCH, batch);
		}

		// Various bits of the Batch need to be stashed away places
		storeParsedRequest(request, webContext, batch);

		Calls calls = batch.getCalls();

		// Debug the environment
		if (log.isDebugEnabled() && calls.getCallCount() > 0) {
			// We can just use 0 because they are all shared
			InboundContext inctx = (InboundContext) batch.getInboundContexts()
					.get(0);
			StringBuffer buffer = new StringBuffer();

			for (Iterator it = inctx.getInboundVariableNames(); it.hasNext();) {
				String key = (String) it.next();
				InboundVariable value = inctx.getInboundVariable(key);
				if (key.startsWith(ProtocolConstants.INBOUND_CALLNUM_PREFIX)
						&& key.indexOf(ProtocolConstants.INBOUND_CALLNUM_SUFFIX
								+ ProtocolConstants.INBOUND_KEY_ENV) != -1) {
					buffer.append(key);
					buffer.append('=');
					buffer.append(value.toString());
					buffer.append(", ");
				}
			}

			if (buffer.length() > 0) {
				log.debug("Environment:  " + buffer.toString());
			}
		}

		callLoop: for (int callNum = 0; callNum < calls.getCallCount(); callNum++) {
			Call call = calls.getCall(callNum);
			InboundContext inctx = (InboundContext) batch.getInboundContexts()
					.get(callNum);

			// Get a list of the available matching methods with the coerced
			// parameters that we will use to call it if we choose to use
			// that method.
			Creator creator = creatorManager.getCreator(call.getScriptName());

			// Which method are we using?
			Method method = findMethod(call, inctx);
			if (method == null) {
				String name = call.getScriptName() + '.' + call.getMethodName();
				String error = Messages.getString(
						"BaseCallMarshaller.UnknownMethod", name);
				log.warn("Marshalling exception: " + error);

				call.setMethod(null);
				call.setParameters(null);
				call.setException(new IllegalArgumentException(error));

				continue callLoop;
			}

			call.setMethod(method);

			// Check this method is accessible
			accessControl.assertExecutionIsPossible(creator, call
					.getScriptName(), method);

			method = ReflectUtil.getConcreteMethod(method);

			// Convert all the parameters to the correct types
			Object[] params = new Object[method.getParameterTypes().length];
			for (int j = 0; j < method.getParameterTypes().length; j++) {
				try {
					Class paramType = method.getParameterTypes()[j];
					InboundVariable param = inctx.getParameter(callNum, j);
					TypeHintContext incc = new TypeHintContext(
							converterManager, method, j);
					params[j] = converterManager.convertInbound(paramType,
							param, inctx, incc);
				} catch (MarshallException ex) {
					log.warn("Marshalling exception", ex);

					call.setMethod(null);
					call.setParameters(null);
					call.setException(ex);

					continue callLoop;
				}
			}

			call.setParameters(params);
		}

		return calls;
	}

	/**
	 * @param allowGetForSafariButMakeForgeryEasier
	 *            Do we reduce security to help Safari
	 */
	public void setAllowGetForSafariButMakeForgeryEasier(
			boolean allowGetForSafariButMakeForgeryEasier) {
		super
				.setAllowGetForSafariButMakeForgeryEasier(allowGetForSafariButMakeForgeryEasier);
		this.allowGetForSafariButMakeForgeryEasier = allowGetForSafariButMakeForgeryEasier;
	}

	/**
	 * Build a Batch and put it in the request
	 * 
	 * @param request
	 *            Where we store the parsed data
	 * @param webContext
	 *            We need to notify others of some of the data we find
	 * @param batch
	 *            The parsed data to store
	 */
	private void storeParsedRequest(HttpServletRequest request,
			WebContext webContext, Batch batch) {
		String normalizedPage = pageNormalizer.normalizePage(batch.getPage());
		webContext.setCurrentPageInformation(normalizedPage, batch
				.getScriptSessionId());

		// Remaining parameters get put into the request for later consumption
		Map paramMap = batch.getSpareParameters();
		if (paramMap.size() != 0) {
			for (Iterator it = paramMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();

				request.setAttribute(key, value);
				log.debug("Moved param to request: " + key + "=" + value);
			}
		}
	}

	/**
	 * Find the method the best matches the method name and parameters
	 * 
	 * @param call
	 *            The function call we are going to make
	 * @param inctx
	 *            The data conversion context
	 * @return A matching method, or null if one was not found.
	 */
	private Method findMethod(Call call, InboundContext inctx) {
		if (call.getScriptName() == null) {
			throw new IllegalArgumentException(Messages
					.getString("BaseCallMarshaller.MissingClassParam"));
		}

		if (call.getMethodName() == null) {
			throw new IllegalArgumentException(Messages
					.getString("BaseCallMarshaller.MissingMethodParam"));
		}

		Creator creator = creatorManager.getCreator(call.getScriptName());
		Method[] methods = creator.getType().getMethods();
		List available = new ArrayList();

		methods: for (int i = 0; i < methods.length; i++) {
			// Check method name and access
			if (methods[i].getName().equals(call.getMethodName())) {
				// Check number of parameters
				if (methods[i].getParameterTypes().length == inctx
						.getParameterCount()) {
					// Clear the previous conversion attempts (the param types
					// will probably be different)
					inctx.clearConverted();

					// Check parameter types
					for (int j = 0; j < methods[i].getParameterTypes().length; j++) {
						Class paramType = methods[i].getParameterTypes()[j];
						if (!converterManager.isConvertable(paramType)) {
							// Give up with this method and try the next
							continue methods;
						}
					}

					available.add(methods[i]);
				}
			}
		}

		// Pick a method to call
		if (available.size() > 1) {
			log.warn("Warning multiple matching methods. Using first match.");
		}

		if (available.isEmpty()) {
			return null;
		}

		// At the moment we are just going to take the first match, for a
		// later increment we might pick the best implementation
		return (Method) available.get(0);
	}

}

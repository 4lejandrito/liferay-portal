/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine;

import com.liferay.batch.engine.action.ImportTaskPostAction;
import com.liferay.batch.engine.action.ImportTaskPreAction;
import com.liferay.batch.engine.context.ImportTaskContext;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineImportTaskErrorLocalServiceUtil;
import com.liferay.batch.engine.strategy.BatchEngineErrorHandler;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.odata.entity.EntityModel;

import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public abstract class BaseBatchEngineTaskItemDelegate<T>
	implements BatchEngineTaskItemDelegate<T> {

	@Override
	public void create(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception {

		_importItems(
			this, items, _batchEngineErrorHandler,
			item -> createItem(item, parameters));
	}

	private void _importItems(
		BatchEngineTaskItemDelegate<T> batchEngineTaskItemDelegate,
		Collection<T> items,
		BatchEngineErrorHandler batchEngineErrorHandler, UnsafeFunction<T, T, Exception> unsafeFunction) throws Exception {

		for (T item : items) {
			// hook transactionality
			try {
				ImportTaskContext importTaskContext =
					new ImportTaskContext();

				for (ImportTaskPreAction importTaskPreAction :
					importTaskPreActions) {

					importTaskPreAction.run(
						batchEngineImportTask, batchEngineTaskItemDelegate,
						importTaskContext, item);
				}

				T persistedItem = unsafeFunction.apply(item);

				if (persistedItem == null) {
					continue;
				}

				for (ImportTaskPostAction importTaskPostAction :
					importTaskPostActions) {

					importTaskPostAction.run(
						batchEngineImportTask, batchEngineTaskItemDelegate,
						importTaskContext, item, persistedItem);
				}
			} catch (Exception exception) {
				_log.error(exception);

				addBatchEngineImportTaskError(
					batchEngineImportTask, batchEngineTaskItemDelegate, item,
					ItemIndexThreadLocal.get(), exception);

				// hook error handling

				batchEngineErrorHandler.handleError(exception);
			} finally {
				ItemIndexThreadLocal.remove();
			}
		}

	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	protected <T> void addBatchEngineImportTaskError(
		BatchEngineImportTask batchEngineImportTask,
		BatchEngineTaskItemDelegate<T> batchEngineTaskItemDelegate, T item,
		int itemIndex, Exception exception) {

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					BatchEngineImportTaskErrorLocalServiceUtil.
						addBatchEngineImportTaskError(
							batchEngineImportTask.getCompanyId(),
							batchEngineImportTask.getUserId(),
							batchEngineImportTask.getBatchEngineImportTaskId(),
							item.toString(), itemIndex,
							ErrorMessageUtil.getErrorMessage(
								exception, batchEngineImportTask.getUserId()));

					batchEngineImportTaskExceptionHandlers.forEach(
						batchEngineImportTaskExceptionHandler ->
							batchEngineImportTaskExceptionHandler.handle(
								batchEngineImportTask,
								batchEngineTaskItemDelegate, exception, item));

					return null;
				});
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}


	public T createItem(T item, Map<String, Serializable> parameters)
		throws Exception {

		return null;
	}

	@Override
	public void delete(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception {

		_importItems(
			this, items, _batchEngineErrorHandler,
			item -> {
				deleteItem(item, parameters);

				return item;
			});
	}

	public void deleteItem(T item, Map<String, Serializable> parameters)
		throws Exception {
	}

	@Override
	public Set<String> getAvailableCreateStrategies() {
		return _availableCreateStrategies;
	}

	@Override
	public Set<String> getAvailableUpdateStrategies() {
		return _availableUpdateStrategies;
	}

	@Override
	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception {

		return null;
	}

	@Override
	public boolean hasCreateStrategy(String createStrategy) {
		return _availableCreateStrategies.contains(createStrategy);
	}

	@Override
	public boolean hasUpdateStrategy(String updateStrategy) {
		return _availableUpdateStrategies.contains(updateStrategy);
	}

	@Override
	public void setBatchEngineImportStrategy(
		BatchEngineErrorHandler batchEngineErrorHandler) {

		this._batchEngineErrorHandler = batchEngineErrorHandler;
	}

	@Override
	public void setContextCompany(Company contextCompany) {
		this.contextCompany = contextCompany;
	}

	@Override
	public void setContextUriInfo(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}

	@Override
	public void setContextUser(User contextUser) {
		this.contextUser = contextUser;
	}

	@Override
	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	@Override
	public void update(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception {

		for (T item : items) {
			updateItem(item, parameters);
		}
	}

	public void updateItem(T item, Map<String, Serializable> parameters)
		throws Exception {
	}

	protected BatchEngineErrorHandler _batchEngineErrorHandler;
	protected Company contextCompany;
	protected User contextUser;
	protected String languageId;
	protected UriInfo uriInfo;

	private final Set<String> _availableCreateStrategies =
		Collections.unmodifiableSet(SetUtil.fromArray("INSERT"));
	private final Set<String> _availableUpdateStrategies =
		Collections.unmodifiableSet(SetUtil.fromArray("UPDATE"));

}
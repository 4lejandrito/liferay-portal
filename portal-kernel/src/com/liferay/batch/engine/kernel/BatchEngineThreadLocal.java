/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.kernel;

import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Stefano Motta
 */
// We should try to decouple this from batch engine and just use it from the batch engine modules. I suggest the names below.
// Suggested name: LazyReferencingThreadLocal
public class BatchEngineThreadLocal {

	// Suggested name: isLazyReferencingEnabled
	public static boolean isBatchEngine() {
		return _batchEngine.get();
	}

	public static boolean isModelIncomplete() {
		return _modelIncomplete.get();
	}

	// Suggested name: setLazyReferencingEnabled
	public static void setBatchEngine(boolean batchEngine) {
		_batchEngine.set(batchEngine);
	}

	// When updating nested entities we will probably need a stack of these
	public static void setModelIncomplete(boolean modelIncomplete) {
		_modelIncomplete.set(modelIncomplete);
	}

	private static final ThreadLocal<Boolean> _batchEngine =
		new CentralizedThreadLocal<>(
			BatchEngineThreadLocal.class + "._batchEngine",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _modelIncomplete =
		new CentralizedThreadLocal<>(
			BatchEngineThreadLocal.class + "._modelIncomplete",
			() -> Boolean.FALSE);

}
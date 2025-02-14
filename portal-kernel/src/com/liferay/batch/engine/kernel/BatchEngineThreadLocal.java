/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.kernel;

import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Stefano Motta
 */
public class BatchEngineThreadLocal {

	public static boolean isBatchEngine() {
		return _batchEngine.get();
	}

	public static boolean isModelIncomplete() {
		return _modelIncomplete.get();
	}

	public static void setBatchEngine(boolean batchEngine) {
		_batchEngine.set(batchEngine);
	}

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
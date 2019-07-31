package org.dea.fimgstoreclient;

import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;

public interface IFimgStoreClientBase {
	public FimgStoreUriBuilder getUriBuilder();
	
	public default boolean hasFileAccess() {
		return false;
	}
}

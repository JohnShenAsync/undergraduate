package net.basilwang.core;

import net.basilwang.enums.TAHelperDownloadPhrase;

public interface OnDownloadProgressListener {

	void onDownloadProgress(int percent,int downloadedData,int total,TAHelperDownloadPhrase phrase);

}
 
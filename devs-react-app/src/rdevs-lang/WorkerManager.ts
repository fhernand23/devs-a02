import * as monaco from "monaco-editor-core";

import Uri = monaco.Uri;
import { RdevsLangWorker } from './rdevsLangWorker';
import { languageID } from './config';

export class WorkerManager {

	private worker: monaco.editor.MonacoWebWorker<RdevsLangWorker>;
	private workerClientProxy: Promise<RdevsLangWorker>;

	constructor() {
		this.worker = null;
	}

	private getClientproxy(): Promise<RdevsLangWorker> {

		if (!this.workerClientProxy) {
			this.worker = monaco.editor.createWebWorker<RdevsLangWorker>({
				moduleId: 'RdevsLangWorker',
				label: languageID,
				createData: {
					languageId: languageID,
				}
			});

			this.workerClientProxy = <Promise<RdevsLangWorker>><any>this.worker.getProxy();
		}

		return this.workerClientProxy;
	}

	async getLanguageServiceWorker(...resources: Uri[]): Promise<RdevsLangWorker> {
		const _client: RdevsLangWorker = await this.getClientproxy();
		await this.worker.withSyncedResources(resources)
		return _client;
	}
}

import * as worker from 'monaco-editor-core/esm/vs/editor/editor.worker';
import { RdevsLangWorker } from './rdevsLangWorker';

self.onmessage = () => {
	worker.initialize((ctx) => {
		return new RdevsLangWorker(ctx)
	});
};

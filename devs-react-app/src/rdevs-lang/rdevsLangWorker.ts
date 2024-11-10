import * as monaco from "monaco-editor-core";

import IWorkerContext = monaco.worker.IWorkerContext;
import RdevsLangLanguageService from "../language-service/LanguageService";
import { IRdevsLangError } from "../language-service/RdevsErrorlistener";


export class RdevsLangWorker {

    private _ctx: IWorkerContext;
    private languageService: RdevsLangLanguageService;
    constructor(ctx: IWorkerContext) {
        this._ctx = ctx;
        this.languageService = new RdevsLangLanguageService();
    }

    doValidation(): Promise<IRdevsLangError[]> {
        const code = this.getTextDocument();
        return Promise.resolve(this.languageService.validate(code));
    }

    private getTextDocument(): string {
        const model = this._ctx.getMirrorModels()[0];
        return model.getValue();
    }

}

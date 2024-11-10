import { ANTLRErrorListener, RecognitionException, Recognizer } from "antlr4ts";

export interface IRdevsLangError {
    startLineNumber: number;
    startColumn: number;
    endLineNumber: number;
    endColumn: number;
    message: string;
    code: string;
}

export default class RdevsErrorListener implements ANTLRErrorListener<any> {
    private errors: IRdevsLangError[] = [];

    syntaxError(
        recognizer: Recognizer<any, any>, 
        offendingSymbol: any, 
        line: number, 
        charPositionInLine: number, 
        message: string, 
        e: RecognitionException | undefined
    ): void {
        this.errors.push({
            startLineNumber: line,
            endLineNumber: line,
            startColumn: charPositionInLine,
            endColumn: charPositionInLine + 1, // Supongamos que la longitud del error es de solo 1 carácter por simplicidad
            message,
            code: "1" // Este es el código de error, puedes personalizarlos como desees
        });
    }

    getErrors(): IRdevsLangError[] {
        return this.errors;
    }
}

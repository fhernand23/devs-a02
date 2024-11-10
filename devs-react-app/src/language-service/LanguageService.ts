import { StartContext} from "../ANTLR/RDEVSParser";
import { parseAndGetASTRoot, parseAndGetSyntaxErrors } from "./parser";
import { IRdevsLangError } from "./RdevsErrorlistener";

export default class RdevsLangService {
    validate(code: string): IRdevsLangError[] {
        const syntaxErrors: IRdevsLangError[] = parseAndGetSyntaxErrors(code);
        const ast: StartContext = parseAndGetASTRoot(code);
        return syntaxErrors;
    }
    
}
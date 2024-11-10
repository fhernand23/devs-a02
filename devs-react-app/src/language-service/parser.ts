import { RDEVSParser, StartContext } from "../ANTLR/RDEVSParser";
import { RDEVSLexer } from "../ANTLR/RDEVSLexer";
import { ANTLRInputStream, CommonTokenStream } from "antlr4ts";
import RdevsErrorlistener, { IRdevsLangError } from "./RdevsErrorlistener";

function parse(code: string): {ast:StartContext, errors: IRdevsLangError[]} {
    const inputStream = new ANTLRInputStream(code);
    const lexer = new RDEVSLexer(inputStream);
    lexer.removeErrorListeners()
    const rdevsErrorListener = new RdevsErrorlistener();
    lexer.addErrorListener(rdevsErrorListener);
    const tokenStream = new CommonTokenStream(lexer);
    const parser = new RDEVSParser(tokenStream);
    parser.removeErrorListeners();
    parser.addErrorListener(rdevsErrorListener);
    const ast =  parser.start();
    const errors: IRdevsLangError[]  = rdevsErrorListener.getErrors();
    return {ast, errors};
}
export function parseAndGetASTRoot(code: string): StartContext {
    const {ast} = parse(code);
    return ast;
}
export function parseAndGetSyntaxErrors(code: string): IRdevsLangError[] {
    const {errors} = parse(code);
    return errors;
}
// Generated from ./RDEVS.g4 by ANTLR 4.9.0-SNAPSHOT


import { ParseTreeListener } from "antlr4ts/tree/ParseTreeListener";

import { StartContext } from "./RDEVSParser";
import { SentenciaContext } from "./RDEVSParser";
import { EdgesContext } from "./RDEVSParser";
import { MaterializesContext } from "./RDEVSParser";
import { NetworkContext } from "./RDEVSParser";
import { CommentContext } from "./RDEVSParser";
import { ListOfComponentsNodesContext } from "./RDEVSParser";
import { ListOfConnectionsContext } from "./RDEVSParser";
import { ListOfNodesContext } from "./RDEVSParser";
import { IdContext } from "./RDEVSParser";


/**
 * This interface defines a complete listener for a parse tree produced by
 * `RDEVSParser`.
 */
export interface RDEVSListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by `RDEVSParser.start`.
	 * @param ctx the parse tree
	 */
	enterStart?: (ctx: StartContext) => void;
	/**
	 * Exit a parse tree produced by `RDEVSParser.start`.
	 * @param ctx the parse tree
	 */
	exitStart?: (ctx: StartContext) => void;

	/**
	 * Enter a parse tree produced by `RDEVSParser.sentencia`.
	 * @param ctx the parse tree
	 */
	enterSentencia?: (ctx: SentenciaContext) => void;
	/**
	 * Exit a parse tree produced by `RDEVSParser.sentencia`.
	 * @param ctx the parse tree
	 */
	exitSentencia?: (ctx: SentenciaContext) => void;

	/**
	 * Enter a parse tree produced by `RDEVSParser.edges`.
	 * @param ctx the parse tree
	 */
	enterEdges?: (ctx: EdgesContext) => void;
	/**
	 * Exit a parse tree produced by `RDEVSParser.edges`.
	 * @param ctx the parse tree
	 */
	exitEdges?: (ctx: EdgesContext) => void;

	/**
	 * Enter a parse tree produced by `RDEVSParser.materializes`.
	 * @param ctx the parse tree
	 */
	enterMaterializes?: (ctx: MaterializesContext) => void;
	/**
	 * Exit a parse tree produced by `RDEVSParser.materializes`.
	 * @param ctx the parse tree
	 */
	exitMaterializes?: (ctx: MaterializesContext) => void;

	/**
	 * Enter a parse tree produced by `RDEVSParser.network`.
	 * @param ctx the parse tree
	 */
	enterNetwork?: (ctx: NetworkContext) => void;
	/**
	 * Exit a parse tree produced by `RDEVSParser.network`.
	 * @param ctx the parse tree
	 */
	exitNetwork?: (ctx: NetworkContext) => void;

	/**
	 * Enter a parse tree produced by `RDEVSParser.comment`.
	 * @param ctx the parse tree
	 */
	enterComment?: (ctx: CommentContext) => void;
	/**
	 * Exit a parse tree produced by `RDEVSParser.comment`.
	 * @param ctx the parse tree
	 */
	exitComment?: (ctx: CommentContext) => void;

	/**
	 * Enter a parse tree produced by `RDEVSParser.listOfComponentsNodes`.
	 * @param ctx the parse tree
	 */
	enterListOfComponentsNodes?: (ctx: ListOfComponentsNodesContext) => void;
	/**
	 * Exit a parse tree produced by `RDEVSParser.listOfComponentsNodes`.
	 * @param ctx the parse tree
	 */
	exitListOfComponentsNodes?: (ctx: ListOfComponentsNodesContext) => void;

	/**
	 * Enter a parse tree produced by `RDEVSParser.listOfConnections`.
	 * @param ctx the parse tree
	 */
	enterListOfConnections?: (ctx: ListOfConnectionsContext) => void;
	/**
	 * Exit a parse tree produced by `RDEVSParser.listOfConnections`.
	 * @param ctx the parse tree
	 */
	exitListOfConnections?: (ctx: ListOfConnectionsContext) => void;

	/**
	 * Enter a parse tree produced by `RDEVSParser.listOfNodes`.
	 * @param ctx the parse tree
	 */
	enterListOfNodes?: (ctx: ListOfNodesContext) => void;
	/**
	 * Exit a parse tree produced by `RDEVSParser.listOfNodes`.
	 * @param ctx the parse tree
	 */
	exitListOfNodes?: (ctx: ListOfNodesContext) => void;

	/**
	 * Enter a parse tree produced by `RDEVSParser.id`.
	 * @param ctx the parse tree
	 */
	enterId?: (ctx: IdContext) => void;
	/**
	 * Exit a parse tree produced by `RDEVSParser.id`.
	 * @param ctx the parse tree
	 */
	exitId?: (ctx: IdContext) => void;
}


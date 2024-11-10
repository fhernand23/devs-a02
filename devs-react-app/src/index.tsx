import * as React from 'react';
import * as ReactDOM from 'react-dom';
import {setupLanguage} from "./rdevs-lang/setup";
import { Editor } from './components/Editor/Editor';
import { languageID } from './rdevs-lang/config';
import { parseAndGetSyntaxErrors, parseAndGetASTRoot } from './language-service/parser';

setupLanguage();
const App = () => <Editor language={languageID}></Editor>;

ReactDOM.render(<App/>, document.getElementById('container'));
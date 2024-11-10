import * as monaco from "monaco-editor-core";
import IRichLanguageConfiguration = monaco.languages.LanguageConfiguration;
import ILanguage = monaco.languages.IMonarchLanguage;

export const richLanguageConfiguration: IRichLanguageConfiguration = {};

export const monarchLanguage = <ILanguage>{
    ignoreCase: true,
    defaultToken: 'invalid',
    article: [
        'the','from','an','to','and','of','is','are',':','part','as','with'
    ],
    element:[
        'network','component','node','nodes'
    ],
    action:[
        'used','connected', 'defined','performs','perform','receive','receives','send','sends', 'materialize','materializes','includes','is part of'
    ],
    interaction:[
        'behavior','name','behaviors', 'connections', 'input','inputs','output','outputs'
    ],

	symbols: /[=><!~?:&|+\-*]+/,
	escapes: /\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,
	digits: /\d+(_+\d+)*/,
	octaldigits: /[0-7]+(_+[0-7]+)*/,
	binarydigits: /[0-1]+(_+[0-1]+)*/,
	hexdigits: /[[0-9a-fA-F]+(_+[0-9a-fA-F]+)*/,

	tokenizer: { 
		root: [
			[/[a-zA-Z_$][\w$]*/, {
				cases: {
					'@article': { token: 'article' },
                    '@element': { token: 'element' },
                    '@action': { token: 'action' },
                    '@interaction': { token: 'interaction' },
					'@default': 'identifier'
				}
			}],

			{ include: '@whitespace' },

			[/"([^"\\]|\\.)*$/, 'string.invalid'], 
			[/"/, 'string', '@string'],

			[/'[^\\']'/, 'string'],
			[/(')(@escapes)(')/, ['string', 'string.escape', 'string']],
			[/'/, 'string.invalid']
		],

		whitespace: [
			[/[ \t\r\n]+/, ''],
			[/\/\*\*(?!\/)/, 'comment.doc'],
			[/\/\*/, 'comment', '@comment'],
			[/\/\/.*$/, 'comment'],
		],

		comment: [
			[/[^*]+/, 'comment'],
			[/\*\//, 'comment', '@pop'],
			[/[*]/, 'comment']
		],
		string: [
			[/[^\\"]+/, 'string'],
			[/@escapes/, 'string.escape'],
			[/\\./, 'string.escape.invalid'],
			[/"/, 'string', '@pop']
		],
	},
}

monaco.editor.defineTheme('default', {
    base: 'vs',
    inherit: true,
    rules: [
        { token: 'article', foreground: '#0000FF' },      
        { token: 'action', foreground: '#FF0000' },       
        { token: 'element', foreground: '#FFA500' },      
        { token: 'interaction', foreground: '#800080' },  
        { token: 'identifier', foreground: '#000000' }     
    ],
    colors: {}
});

monaco.editor.setTheme('default');
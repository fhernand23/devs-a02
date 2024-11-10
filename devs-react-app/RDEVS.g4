grammar RDEVS;

start:	sentencia+;

sentencia: 	edges
            |materializes
            |network
            |comment;
            
edges:
        THE CONNECTIONS ARE DOS_PUNTOS? lc=listOfConnections 

        |THE? i=id NODE? SENDS OUTPUTS TO_A d=id NODE? 
        
        |THE? i=id NODE? SENDS OUTPUTS TO_AN d=id NODE? 
        
        |THE? i=id NODE? SENDS OUTPUTS TO THE? d=id NODE?
    
        |THE? i=id NODE? SENDS OUTPUTS TO THE? l=listOfNodes  NODES?

        |THE? d=id NODE? RECEIVES INPUTS FROM_A i=id NODE?
        
        |THE? d=id NODE? RECEIVES INPUTS FROM_AN i=id NODE?
    
        |THE? d=id NODE? RECEIVES INPUTS FROM THE? i=id NODE?
            
        |THE? l=listOfNodes NODES? RECEIVE INPUTS FROM_A i=id NODE?
        
        |THE? l=listOfNodes NODES? RECEIVE INPUTS FROM_AN i=id NODE?
        
        |THE? l=listOfNodes NODES? RECEIVE INPUTS FROM THE? i=id NODE?
        
        |THE? OUTPUT OF i=id NODE? IS CONNECTED TO THE INPUTS OF_A d=id NODE?
        
        |THE? OUTPUT OF i=id NODE? IS CONNECTED TO THE INPUTS OF_AN d=id NODE?
        
        |THE? OUTPUT OF i=id NODE? IS CONNECTED TO THE INPUTS OF THE? d=id NODE?
        
        |THE? OUTPUT OF l1=listOfNodes NODES? IS CONNECTED TO THE INPUTS OF THE? l2=listOfNodes NODES?

        |THE? INPUT OF d=id NODE? IS CONNECTED TO THE OUTPUT OF_A i=id NODE?
        
        |THE? INPUT OF d=id NODE? IS CONNECTED TO THE OUTPUT OF_AN i=id NODE?
        
        |THE? INPUT OF d=id NODE? IS CONNECTED TO THE OUTPUT OF THE? i=id NODE?
        
        |THE? INPUT OF l=listOfNodes NODES? IS CONNECTED TO THE OUTPUT OF_A i=id NODE?

        |THE? INPUT OF l=listOfNodes NODES? IS CONNECTED TO THE OUTPUT OF_AN i=id NODE?
        
        |THE? INPUT OF l=listOfNodes NODES? IS CONNECTED TO THE OUTPUT OF THE? i=id NODE?

        |THE? INPUT OF l1=listOfNodes NODES? IS CONNECTED TO THE OUTPUTS OF THE? l2=listOfNodes NODES?;

materializes:
        THE? BEHAVIORS ARE DEFINED AS DOS_PUNTOS?  lp=listOfComponentsNodes

        |THE? d=id COMPONENT? DEFINES THE BEHAVIOR OF THE? l=listOfNodes NODES?

        |THE? d=id COMPONENT? IS USED IN l=listOfNodes NODES?
        |THE? i=id NODE? MATERIALIZES_A d=id COMPONENT? 
        |THE? i=id NODE? MATERIALIZES_ AN? d=id COMPONENT? 
        |THE? i=id NODE? MATERIALIZES_ THE? d=id COMPONENT? 			
        |THE? i=id NODE? PERFORMS THE BEHAVIOR OF_A d=id COMPONENT? 
        |THE? i=id NODE? PERFORMS THE BEHAVIOR OF_AN d=id COMPONENT? 
        |THE? i=id NODE? PERFORMS THE BEHAVIOR OF THE? d=id COMPONENT? 
        |THE? l=listOfNodes NODES? PERFORM THE BEHAVIOR OF_A d=id COMPONENT?

        |THE? l=listOfNodes NODES? PERFORM THE BEHAVIOR OF_AN d=id COMPONENT?
        |THE? l=listOfNodes NODES? PERFORM THE BEHAVIOR OF THE? d=id COMPONENT?
        |THE? l=listOfNodes NODES? MATERIALIZE_A d=id COMPONENT?
        |THE? l=listOfNodes NODES? MATERIALIZE AN d=id COMPONENT? 
        |THE? l=listOfNodes NODES? MATERIALIZE THE? d=id COMPONENT?;
                
network :
        THE? NETWORK_ NAME_NETWORK IS g=id
        |THE? NETWORK_ IS CALLED g=id
        |THE? g=id NETWORK_? INCLUDES THE? listOfNodes NODES? 
        |THE? i=id NODE? IS PART OF_A g=id NETWORK_?
        
        |THE? i=id NODE? IS PART OF_AN g=id NETWORK_? 
        
        |THE? i=id NODE? IS PART OF THE? g=id NETWORK_?
                
        |THE? g=id NETWORK_? INCLUDES_A i=id NODE? 
        
        |THE? g=id NETWORK_? INCLUDES AN i=id NODE? 
        
        |THE? g=id NETWORK_? INCLUDES THE? i=id NODE? 
        
        |THE? l=listOfNodes NODES? ARE PART OF_A g=id NETWORK_?

        
        |THE? l=listOfNodes NODES? ARE PART OF_AN g=id NETWORK_?
	
        |THE? l=listOfNodes NODES? ARE PART OF THE? g=id NETWORK_?;
comment: LINE_COMMENT | MULTILINE_COMMENT;

listOfComponentsNodes returns [ listComponentsNodes[]]
                @init{ 
                    $listComponentsNodes =  [];
                }: 
                    PARENTHESIS_OPEN id1=id 
                    {$listComponentsNodes.add($id1.value_id);}
                    COMMA_WS id2=id {$listComponentsNodes.add($id2.value_id);}
                    PARENTHESIS_CLOSE COMMA 
                    (l=listOfComponentsNodes {$listComponentsNodes.addAll($l.listComponentsNodes);})
                    | 
                    PARENTHESIS_OPEN id1=id COMMA_WS id2=id PARENTHESIS_CLOSE{
                    $listComponentsNodes.add($id1.value_id);
                    $listComponentsNodes.add($id2.value_id);
                    }
                    |
                    PARENTHESIS_OPEN id1=id 
                    {$listComponentsNodes.add($id1.value_id);}
                    COMMA id2=id {$listComponentsNodes.add($id2.value_id);}
                    PARENTHESIS_CLOSE COMMA 
                    (l=listOfComponentsNodes {$listComponentsNodes.addAll($l.listComponentsNodes);})
                    |
                    PARENTHESIS_OPEN id1=id COMMA id2=id PARENTHESIS_CLOSE{
                    $listComponentsNodes.add($id1.value_id);
                    $listComponentsNodes.add($id2.value_id);
                    };
                    
listOfConnections returns [ listConnections[]]
                @init{ 
                    $listConnections = [];
                }:
                    id1=id {$listConnections.add($id1.value_id);} 
                    WITH id2=id {$listConnections.add($id2.value_id);} 
                    COMMA (l=listOfConnections {$listConnections.addAll($l.listConnections);})
                        //$listConnections = "(" + $id1.value_id + ", " + $id2.value_id + "), "
                        //+ $listOfConnections.listConnections;
                        //}
                    | id1=id WITH id2=id AND id3=id WITH id4=id 
                    {
                        $listConnections.add($id1.value_id);
                        $listConnections.add($id2.value_id);
                        $listConnections.add($id3.value_id);
                        $listConnections.add($id4.value_id);
                    };
            
listOfNodes returns [ lista[]]
                @init{ 
                    $lista = [];
                }:
                id {$lista.add($id.value_id);} COMMA (l=listOfNodes {$lista.addAll($l.lista);})
                | i=id AND d=id {
                    $lista.add($i.value_id);
                    $lista.add($d.value_id);
                };

id returns [String value_id]: 
                NAME {
                    $value_id  = $NAME.text;
                };

COMMA: ', ';
COMMA_WS:',';
PARENTHESIS_OPEN: '(';
PARENTHESIS_CLOSE: ')';

AND: 'AND' | 'and' | 'And'; 

OF_A: 'OF A' | 'of a' | 'Of a';
OF_AN: 'OF AN' | 'of an' | 'Of an';
OF: 'OF' | 'of' | 'Of';

TO: 'TO'| 'To' | 'to';
TO_A: 'TO A'| 'To a'| 'to a';
TO_AN: 'TO AN'| 'To an'| 'to an';
FROM: 'FROM' | 'From' | 'from';
FROM_A: 'FROM A' | 'From a' | 'from a';
FROM_AN: 'FROM AN' | 'From an' | 'from an';

IS: 'IS' | 'Is' | 'is';
ARE: 'ARE' | 'are' | 'Are';
PART: 'PART' | 'Part' | 'part';
AN: 'AN' | 'an' | 'An';
THE: 'THE' |'The' |'the';
WITH: 'WITH' | 'With' |'with';
IN: 'IN' | 'in' |'In';
AS: 'AS' | 'as' | 'As';
CALLED: 'CALLED' | 'called'|'Called';
NAME_NETWORK: 'NAME' | 'name' | 'Name';
USED: 'USED'| 'used' | 'Used';
DEFINED: 'DEFINED' | 'defined' | 'Defined';
NODE: 'NODE' | 'Node' | 'node';
NODES:'NODES' | 'Nodes' | 'nodes';
NETWORK_:'NETWORK'| 'Network'|'network';
COMPONENT:'COMPONENT' | 'Component' | 'component';
CONNECTIONS: 'CONNECTIONS'| 'Connections' |'connections';

INCLUDES: 'INCLUDES' | 'Includes' | 'includes';
INCLUDES_A: 'INCLUDES A' | 'Includes a' | 'includes a';

DEFINES: 'Defines' | 'DEFINES' | 'defines';

BEHAVIOR: 'Behavior' | 'BEHAVIOR' | 'behavior' | 'behaviour' | 'BEHAVIOUR' | 'Behaviour';
BEHAVIORS: 'Behaviors' | 'BEHAVIORS' | 'behaviors'| 'Behaviours' | 'BEHAVIOURS' | 'behaviours';

MATERIALIZES_: 'MATERIALIZES' | 'Materializes' |'materializes';
MATERIALIZES_A: 'MATERIALIZES A' | 'Materializes a' |'materializes a';

MATERIALIZE: 'MATERIALIZE' | 'Materialize' | 'materialize';
MATERIALIZE_A: 'MATERIALIZE A' | 'Materialize a' | 'materialize a';

PERFORMS: 'PERFORMS' | 'Performs' | 'performs';
PERFORM: 'PERFORM' | 'Perform' | 'perform';

SENDS: 'SENDS'| 'Sends' | 'sends';

RECEIVE: 'RECEIVE'| 'Receive'  | 'receive'  ;
RECEIVES: 'RECEIVES'| 'Receives'  | 'receives' ;

OUTPUTS: 'OUTPUTS' | 'Outputs' | 'outputs';
OUTPUT: 'OUTPUT' | 'Output' | 'output';

INPUTS: 'INPUTS' | 'Inputs' | 'inputs';
INPUT: 'INPUT' | 'Input' | 'input';

CONNECTED: 'CONNECTED' | 'Connected' |'connected';
DOS_PUNTOS : ': ';

LINE_COMMENT: '//'~[\r\n]* -> skip;
MULTILINE_COMMENT: '/*' .*? '*/'->skip;
NAME: [A-Za-z][a-zA-Z0-9]*;
WS : [ \t\r\n]+ -> skip ; 

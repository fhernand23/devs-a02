package text.to.model.transformation;

import rules.RDEVSBaseListener;
import rules.RDEVSParser;
import java.util.Optional;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import rdevs.webtexteditor.metamodel.*;

public class TextToModelMapper extends RDEVSBaseListener {
	private NetworkSpecification specification; 
	protected MetamodelPackage modelPackage = MetamodelPackage.eINSTANCE;
	protected MetamodelFactory modelFactory = modelPackage.getMetamodelFactory();
	
	public TextToModelMapper() {
		this.specification = (NetworkSpecification) createInstanceFromModel("NetworkSpecification");		
		Network rp1 = (Network) createInstanceFromModel("Network");
		rp1.setNetworkspecification(this.specification);
	}
	
	public NetworkSpecification getSpecification() {
		return specification;
	}

	public void setSpecification(NetworkSpecification specification) {
		this.specification = specification;
	}

	/**
	* Create a new class of the meta-model.
	*/

	public EObject createInstanceFromModel(String name) {
		EClass eClass = (EClass) modelPackage.getEClassifier(name);
		EObject rootObject = modelFactory.create(eClass);
		return rootObject;
	}

	private Optional<Network> incluyeRP(String network){
		Optional<Network> existing_rp = 
				this.specification.getNetwork()
				.stream()
				.filter(r -> network.equals(r.getName())
						|| r.getName() == null)
				.findAny();
		return existing_rp;
	}
	
	@Override
	public void exitNetwork(RDEVSParser.NetworkContext ctx) {
		
		Optional<Network> rp = this.incluyeRP(ctx.g.getText());
		Network actualRP;
		if(rp.isEmpty()) {
			actualRP = (Network) createInstanceFromModel("Network");
			actualRP.setName(ctx.g.getText());
			actualRP.setNetworkspecification(this.specification);
		}
		else {
			if(rp.get().getName()==null) {
				rp.get().setName(ctx.g.getText());
			}
		actualRP = rp.get();
		}
		if(ctx.list_of_nodes.size()>0) {
			//es una regla que incluye lista de nodos
		for(int i=0;i<ctx.list_of_nodes.size();i++) {
			String actual = ctx.list_of_nodes.get(i).toString();
			Optional<Node> existing_component = this.incluye(actual);
			if(existing_component.isEmpty()) {
				Node node = this.createComponent(actual);
				node.setNetwork(actualRP);
			}
		}
		}
		else if(!(ctx.i == null)) {
			//es una regla que solo incluye un nodo y no es de definicion de red			
			String actual = ctx.i.getText();
			Optional<Node> existing_component = this.incluye(actual);
			if(existing_component.isEmpty()) {
				Node node = this.createComponent(actual);
				node.setNetwork(actualRP);
			}
			}
		}
		
	
	@Override 
	public void exitMaterializes(RDEVSParser.MaterializesContext ctx) {
		if(ctx.list_of_components_nodes.size()!= 0) {
			this.recorrerListOfComponentsNodes(ctx);
		}	
		else if(ctx.list_of_nodes.size()!= 0) {
			this.recorrerListOfNodes(ctx);
		}
		else {
			//es una regla que incluye un solo nodo
			//veo si ya existe el nodo
		Optional<Node> existing_component = this.incluye(ctx.i.getText());
				
		if(existing_component.isEmpty() ) {
			//no existe el nodo, lo creamos
			Node node = this.createComponent(ctx.i.getText());
			this.setBehaviourToNode(ctx, node);
		}
		else {
			//ya existe el nodo, lo recuperamos
			Node node = existing_component.get();
			this.setBehaviourToNode(ctx, node);
			}
		}
	}
		
	@Override
	public void exitEdges(RDEVSParser.EdgesContext ctx) {
		//List list_of_nodes, List list_of_nodes1, List list_of_connections
		if(ctx.list_of_connections.size()!=0) {
			this.recorrerListOfConnections(ctx);
		}
		else if(ctx.list_of_nodes.size() != 0 && ctx.list_of_nodes1.size() != 0) {
			this.recorrerTwoListOfNodes(ctx);
		}
		else if(ctx.list_of_nodes.size() != 0 && ctx.list_of_nodes1.size() == 0) {
			this.recorrerListOfNodes(ctx);
		}
		else {
			Edge e = (Edge) createInstanceFromModel("Edge");
			e.setNetwork(this.specification.getNetwork().get(0));
			
			String fromNodeName = ctx.i.getText();
			String toNodeName = ctx.d.getText();
			
			Optional<Node> existing_component_source = this.incluye(fromNodeName);

			if(existing_component_source.isEmpty()) {
				Node fromNode = this.createComponent(fromNodeName);
				e.setSource(fromNode);
			}
			else {
				e.setSource(existing_component_source.get());
			}
			
			Optional<Node> existing_component_dest = this.incluye(toNodeName);
			
			if(existing_component_dest.isEmpty()) {
				Node toNode = this.createComponent(toNodeName);
				e.setDestination(toNode);
			}
			else {
				e.setDestination(existing_component_dest.get());
			}
		}
	}
	
	private void recorrerListOfConnections(RDEVSParser.EdgesContext ctx) {
		//SUPONGO QUE primero origen (i) luego destino (i+1)
		for(int i=0;i<ctx.list_of_connections.size();i+=2) {
			
			Edge e = (Edge) createInstanceFromModel("Edge");
			e.setNetwork(this.specification.getNetwork().get(0));
			
			String fromNodeName = ctx.list_of_connections.get(i).toString();
			String toNodeName = ctx.list_of_connections.get(i+1).toString();
			
			Optional<Node> existing_component_source = this.incluye(fromNodeName);

			if(existing_component_source.isEmpty()) {
				Node fromNode = this.createComponent(fromNodeName);
				e.setSource(fromNode);
			}
			else {
				e.setSource(existing_component_source.get());
			}
			
			Optional<Node> existing_component_dest = this.incluye(toNodeName);
			
			if(existing_component_dest.isEmpty()) {
				Node toNode = this.createComponent(toNodeName);
				e.setDestination(toNode);
			}
			else {
				e.setDestination(existing_component_dest.get());
			}
		}
	}
	
	private void recorrerTwoListOfNodes(RDEVSParser.EdgesContext ctx) {	
	for(int i=0;i<ctx.list_of_nodes.size();i++) {
		for(int j=0;j<ctx.list_of_nodes1.size();j++) {
			
			Edge e = (Edge) createInstanceFromModel("Edge");
			e.setNetwork(this.specification.getNetwork().get(0));
			
			//primero veo si el nodo i existe y sino lo creo
			//luego veo si el nodo j existe y sino lo creo
			
			String fromNodeName = ctx.list_of_nodes.get(i).toString();
			String toNodeName = ctx.list_of_nodes1.get(j).toString();
					
			Optional<Node> existing_component_source = this.incluye(fromNodeName);

			if(existing_component_source.isEmpty()) {
				Node fromNode = this.createComponent(fromNodeName);
				e.setSource(fromNode);
			}
			else {
				e.setSource(existing_component_source.get());
			}
			
			Optional<Node> existing_component_dest = this.incluye(toNodeName);
			
			if(existing_component_dest.isEmpty()) {
				Node toNode = this.createComponent(toNodeName);
				e.setDestination(toNode);
			}
			else {
				e.setDestination(existing_component_dest.get());
			}
		}
	}	
	}
	
	private void recorrerListOfNodes(RDEVSParser.EdgesContext ctx) {
		
		for(int i=0;i<ctx.list_of_nodes.size();i++) {
			
			Edge e = (Edge) createInstanceFromModel("Edge");
			e.setNetwork(this.specification.getNetwork().get(0));
			
			String fromNodeName = ctx.i.getText();
			Optional<Node> existing_component_source = this.incluye(fromNodeName);
			
			if(existing_component_source.isEmpty()) {
				Node fromNode = this.createComponent(fromNodeName);
				e.setSource(fromNode);
			}
			else {
				e.setSource(existing_component_source.get());
			}			
			
			String toNodeName = ctx.list_of_nodes.get(i).toString();
			Optional<Node> existing_component_dest = this.incluye(toNodeName);
			
			if(existing_component_dest.isEmpty()) {
				Node toNode = this.createComponent(toNodeName);
				e.setDestination(toNode);
			}
			else {
				e.setDestination(existing_component_dest.get());
			}
		}
	}
	
	private void recorrerListOfComponentsNodes(RDEVSParser.MaterializesContext ctx) {

		for(int i=0;i<ctx.list_of_components_nodes.size();i+=2) {
			String actual = ctx.list_of_components_nodes.get(i+1).toString();
			Optional<Node> existing_component = this.incluye(actual);
			
			if(existing_component.isEmpty()) {
				Node node = this.createComponent(actual);
				this.setBehaviourToNode(ctx.list_of_components_nodes.get(i).toString(), node);
			}
			else {
				this.setBehaviourToNode(ctx.list_of_components_nodes.get(i).toString(), existing_component.get());
			}
		}
	}
	
	private Optional<NodeType> incluyeB(String cadena){
		Optional<NodeType> existing_behavior = this.specification.getNetwork().get(0).getNodetype().stream()
				.filter(s -> s.getName().equals(cadena))
				.findAny();
		return existing_behavior;
	}
	
	private Optional<Node> incluye(String node) {
		Optional<Node> existing_node = this.specification.getNetwork().get(0)
				.getNode()
				.stream()
				.filter(s -> s.getName().equals(node))
				.findAny();
		return existing_node;
	}

	private void setBehaviourToNode(String comp, Node node) {
		Optional<NodeType> existing_behavior = this.incluyeB(comp);	
		if(existing_behavior.isEmpty()) {
			//si no existe el comportamiento, lo creamos
			NodeType behaviour = createBehaviour(comp);
			EList<NodeType> behaviours = node.getNodetype();
			behaviours.add(behaviour);
			node.setNodeType(behaviours.get(0));
		}
		else {
			NodeType behaviour = existing_behavior.get();
			EList<NodeType> behaviours = node.getNodetype();
			behaviours.add(behaviour);
			node.setNodeType(behaviours.get(0));
		}
	}
	
	private NodeType createBehaviour(String name) {
		NodeType behaviour = (NodeType) createInstanceFromModel("NodeType");
		behaviour.setName(name);
		behaviour.setNetwork(this.specification.getNetwork().get(0));
		return behaviour;
	}
	
	private Node createComponent(String name) {
		Node node = (Node) createInstanceFromModel("Node");
		node.setName(name);
		node.setNetwork(this.specification.getNetwork().get(0));
		return node;
	}
	
	private void setBehaviourToNode(RDEVSParser.MaterializesContext ctx, Node node) {
		//veo si ya existe el comportamiento
		Optional<NodeType> existing_behaviour = this.incluyeB(ctx.d.getText());
		
		if(existing_behaviour.isEmpty()) {
			//si no existe el comportamiento, lo creamos
			NodeType behaviour = createBehaviour(ctx.d.getText());
			EList<NodeType> behaviours = node.getNodetype();
			behaviours.add(behaviour);
			//metodo que agregamos 
			node.setNodeType(behaviours.get(0));
		}
		else {
			NodeType behaviour = existing_behaviour.get();
			EList<NodeType> behaviours = node.getNodetype();
			behaviours.add(behaviour);
			node.setNodeType(behaviours.get(0));
		}
	}
	
	private void recorrerListOfNodes(RDEVSParser.MaterializesContext ctx) {
		for(int i=0;i<ctx.list_of_nodes.size();i++) {
			String actual = ctx.list_of_nodes.get(i).toString();
			Optional<Node> existing_component = 
					this.incluye(actual);
			
			if(existing_component.isEmpty()) {
				Node node = this.createComponent(actual);
				this.setBehaviourToNode(ctx, node);
			}
			else {
				this.setBehaviourToNode(ctx, existing_component.get());
			}
		}
	}
}

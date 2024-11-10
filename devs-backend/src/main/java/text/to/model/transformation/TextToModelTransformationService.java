package text.to.model.transformation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import rdevs.webtexteditor.metamodel.Edge;
import rdevs.webtexteditor.metamodel.Network;
import rdevs.webtexteditor.metamodel.NetworkSpecification;
import rdevs.webtexteditor.metamodel.Node;
import rdevs.webtexteditor.metamodel.NodeType;
import rules.RDEVSLexer;
import rules.RDEVSParser;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import model.to.code.transformation.ModelToCodeMapper;
import model.to.image.transformation.ModelToImageTransformationService;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class TextToModelTransformationService {
    private static URI archivo_modelo;
    private static List<String> semanticErrors = new ArrayList<>();
    private static ModelToCodeMapper modelToCodeMapper = new ModelToCodeMapper();

    public static Boolean semanticValidationService(String fileContent) {
        ANTLRInputStream input = new ANTLRInputStream(fileContent);
        RDEVSLexer lexer = new RDEVSLexer(input);
        lexer.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RDEVSParser parser = new RDEVSParser(tokens);
        parser.removeErrorListeners();

        ParseTree tree = parser.start();
        Boolean retorno = true;

        TextToModelMapper listener = new TextToModelMapper();
        parser.addParseListener(listener);
        ParseTreeWalker.DEFAULT.walk(listener, tree);
        NetworkSpecification specification = listener.getSpecification();

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("metamodel_instance", ".metamodel");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        URI fileURI = URI.createFileURI(tempFile.toFile().getAbsolutePath());
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("metamodel", new XMIResourceFactoryImpl());
        Resource resourceAux = resourceSet.createResource(fileURI);

        if (resourceAux == null) {
            System.err.println("No se pudo crear el recurso: " + fileURI);
            return false;
        }

        resourceAux.getContents().add(specification);
        Map<Object, Object> options = new HashMap<>();
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");

        try {
            resourceAux.save(options);
            archivo_modelo = fileURI;
            modelToCodeMapper.setFile(tempFile.toFile());
            
            // Guardar una copia en el escritorio
         // String userHome = System.getProperty("user.home");
         // Path desktopPath = Path.of(userHome, "Desktop", "metamodel_instance.metamodel");
         //  Files.copy(tempFile, desktopPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            System.err.println("Error al guardar el recurso: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        retorno = validacionSemantica(specification);
        if(retorno) {
        	ModelToImageTransformationService.generateImage(specification);
        }
        
        return retorno;
    }
   
    private static Boolean validacionSemantica(NetworkSpecification specification) {
        TextToModelTransformationService.semanticErrors.clear();
        Boolean retorno = false;
        Diagnostic diagnostic = Diagnostician.INSTANCE.validate(specification);
            if (diagnostic.getSeverity() == Diagnostic.ERROR || diagnostic.getSeverity() == Diagnostic.WARNING) {
                for (Diagnostic childDiagnostic : diagnostic.getChildren()) {
                    for (Object data : childDiagnostic.getData()) {
                        if (data instanceof Edge) {
                            Edge edge = (Edge) data;
                            Node sourceNode = edge.getSource();
                            Node targetNode = edge.getDestination();
                            String sourceNodeName = sourceNode != null ? sourceNode.getName() : "undefined";
                            String targetNodeName = targetNode != null ? targetNode.getName() : "undefined";
                            String error = "- The notSelfInteraction constraint is violated by the existing edge between " +
                                     sourceNodeName + " and " + targetNodeName;
                            semanticErrors.add(error);
                        }
                        else if(data instanceof Network) {
                        	Network network = (Network) data;
                            String networkName = network.getName() != null ? network.getName() : "undefined";
                        	if(childDiagnostic.getMessage().contains("existsStartingComponent")) {
                                String error = "- The existsStartingComponent constraint is violated by the network  " +
                                        networkName;
                               semanticErrors.add(error);
                        	}
                        	else if(childDiagnostic.getMessage().contains("existsStartingComponent")) {
                                String error = "- The existsEndingComponent constraint is violated by the network  " +
                                        networkName;
                               semanticErrors.add(error);
                        	}
                        }
                        else if(data instanceof Node) {
                        	Node node = (Node) data;
                            String nodeName = node.getName() != null ? node.getName() : "undefined";
                        	if(childDiagnostic.getMessage().contains("uniqueBehavior")) {
                                String error = "- The uniqueBehavior constraint is violated by the node " +
                                        nodeName;
                               semanticErrors.add(error);
                        	}
                        	else if(childDiagnostic.getMessage().contains("notIsolated")) {
                                String error = "- The notIsolated constraint is violated by the node  " +
                                        nodeName;
                               semanticErrors.add(error);
                        	}
                        	else if(childDiagnostic.getMessage().contains("multipleInteractions")) {
                                String error = "- The multipleInteractions constraint is violated by the node  " +
                                        nodeName;
                               semanticErrors.add(error);
                        	}
                        }
                        else if(data instanceof NetworkSpecification) {
                        	NetworkSpecification spec = (NetworkSpecification) data;
                        	String error = "- The uniqueNetwork constraint is violated";
                        	semanticErrors.add(error);
                        } 
                        else if(childDiagnostic.getMessage().contains("The feature 'nodetype' of")){
                        	String error = "- There is a node without nodetype";
                        	semanticErrors.add(error);
                        } else {String error = childDiagnostic.getMessage();
                        	semanticErrors.add(error);
                        }
                    }
                }
        } else {
            retorno = true;
        }
        return retorno;
    }

    public static URI getArchivo_modelo() {
        return archivo_modelo;
    }

    public static void setArchivo_modelo(URI archivo_modelo) {
        TextToModelTransformationService.archivo_modelo = archivo_modelo;
    }
    
    public static List<String> getSemanticErrors() {
		return semanticErrors;
	}

	public static void setSemanticErrors(List<String> semanticErrors) {
		TextToModelTransformationService.semanticErrors = semanticErrors;
	}

    public static ModelToCodeMapper getModelToCodeMapper() {
        return modelToCodeMapper;
    }
}

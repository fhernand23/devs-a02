package model.to.image.transformation;

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
import net.sourceforge.plantuml.SourceStringReader;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class ModelToImageTransformationService {
    private static File image;
    private static final String[] colors = {"#7FB26A","#E1998D","#6B9AB6","#A3CAD5","#625C71", "#F2D191","#D46D74","#CF9930"};

    public static void generateImage(NetworkSpecification specification) {
        String plantUML = generatePlantUML(specification);
        image = generateAndSavePlantUMLImage(plantUML);
    }

    public static File generateAndSavePlantUMLImage(String plantUMLCode) {
        try {          
        	SourceStringReader reader = new SourceStringReader(plantUMLCode);

            File outputFile = File.createTempFile("plantuml_diagram", ".png");
            OutputStream outputStream = new FileOutputStream(outputFile);
            reader.generateImage(outputStream);
            outputStream.close();            
            return outputFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }    
    
    private static String generateRandomColor() {
        Random random = new Random();
        return String.format("#%06x", random.nextInt(0xFFFFFF + 1));
    }
    
    public static String generatePlantUML(NetworkSpecification networkSpec) {
    	StringBuilder plantUMLBuilder = new StringBuilder();

        Map<String, String> nodeTypeColors = new HashMap<>();
        plantUMLBuilder.append("@startuml\n");
        plantUMLBuilder.append("<style>\n");
        int i = 0;
        plantUMLBuilder.append("rectangle\n");
        plantUMLBuilder.append("   {");
        for (NodeType nodeType : networkSpec.getNetwork().get(0).getNodetype()) {
            String typeName = nodeType.getName();
            String color = "";
            if(i<=7) {
                color = colors[i];
            } else {
            	color = generateRandomColor();
            }
            plantUMLBuilder.append(String.format("     .%s {\n", typeName));
            plantUMLBuilder.append(String.format("        BackgroundColor %s\n", color));
            plantUMLBuilder.append("    }\n");
            
            if (!nodeTypeColors.containsKey(nodeType)) {
                nodeTypeColors.put(nodeType.getName(), "<<" + typeName + ">>");
            }
            i++;
        }        
        plantUMLBuilder.append("}\n");
        plantUMLBuilder.append("</style>\n");

        for (Node node : networkSpec.getNetwork().get(0).getNode()) {
            String nodeName = node.getName();
            String nodeType = node.getNodetype().get(0).getName();
            String style = nodeTypeColors.get(nodeType);

            plantUMLBuilder.append(String.format("rectangle %s %s {\n", nodeName, style));
            plantUMLBuilder.append("}\n");
            
        }

        for (Edge edge : networkSpec.getNetwork().get(0).getEdge()) {
            String sourceNodeName = edge.getSource().getName();
            String destNodeName = edge.getDestination().getName();
            plantUMLBuilder.append(sourceNodeName).append(" --> ").append(destNodeName).append("\n");
        }

        plantUMLBuilder.append("\ntitle Network: ").append(networkSpec.getNetwork().getFirst().getName()).append("\n");
        plantUMLBuilder.append("@enduml\n");
         
        return plantUMLBuilder.toString();
    }
    
	public static File getImage() {
        return image;
    }


}

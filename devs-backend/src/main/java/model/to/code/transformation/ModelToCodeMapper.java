package model.to.code.transformation;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.FileSystem;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

import org.eclipse.acceleo.module.sample.Generate;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.acceleo.common.IAcceleoConstants;
import org.eclipse.acceleo.model.mtl.resource.EMtlResourceFactoryImpl;

public class ModelToCodeMapper {
    private File file;
    private TransformationData data = new TransformationData();
    private File zipFile; 

    public void traduccion_acceleo(File modelFile) {
        data.setProjectName(modelFile);
        if (data.validProjectName()) {
            Path projectPath = Paths.get(System.getProperty("user.home"), "projects", data.getProjectName());
            try {
                Files.createDirectories(projectPath);
                copyRequiredJARFiles(projectPath);

                Path srcPath = projectPath.resolve("src");
                Files.createDirectory(srcPath);

                // Registrar el archivo .emtl
                // cargarEMTL(); 
                Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(IAcceleoConstants.EMTL_FILE_EXTENSION, new EMtlResourceFactoryImpl());

                // Crear el URI del modelo EMF
                org.eclipse.emf.common.util.URI emfURI = org.eclipse.emf.common.util.URI.createFileURI(data.getEmfInstance().toString());
                
                // Ejecutar la transformación con Acceleo
                Generate generator = new Generate(emfURI, srcPath.toFile(), new ArrayList<>());
                generator.doGenerate(null);

                // Crear el archivo ZIP con los resultados
                zipFile = createZip(srcPath);

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Problems during the Model-To-Code translation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "The name of the project is invalid", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEMTL() {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(IAcceleoConstants.EMTL_FILE_EXTENSION, new EMtlResourceFactoryImpl());
        ResourceSet resourceSet = new ResourceSetImpl();
        
        org.eclipse.emf.common.util.URI emtlURI = org.eclipse.emf.common.util.URI.createFileURI("src/main/java/model/to/code/transformation/generate.emtl");
                Resource resource = resourceSet.getResource(emtlURI, true);
        if (resource == null || !resource.isLoaded()) {
            throw new RuntimeException("El archivo .emtl no pudo ser cargado.");
        }
    }

    private void copyRequiredJARFiles(Path projectPath) throws IOException {
        Path jarDir = Paths.get("jardependencies");

        if (!Files.exists(jarDir)) {
            throw new IOException("El directorio jardependencies no existe");
        }

        Path rdevsJarPath = jarDir.resolve(data.getRDEVSLibraryPath().getFileName());
        Path coreDevsJarPath = jarDir.resolve(data.getCoreDEVSLibraryPath().getFileName());

        if (!Files.exists(rdevsJarPath)) {
            throw new IOException("El archivo " + rdevsJarPath + " no existe");
        }

        if (!Files.exists(coreDevsJarPath)) {
            throw new IOException("El archivo " + coreDevsJarPath + " no existe");
        }

        Files.copy(rdevsJarPath, projectPath.resolve(data.getRDEVSLibraryPath().getFileName()));
        Files.copy(coreDevsJarPath, projectPath.resolve(data.getCoreDEVSLibraryPath().getFileName()));
    }

    private File createZip(Path srcPath) throws IOException {
        Path zipPath = Files.createTempFile("project", ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    ZipEntry zipEntry = new ZipEntry(srcPath.relativize(file).toString());
                    zos.putNextEntry(zipEntry);
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!srcPath.equals(dir)) {
                        ZipEntry zipEntry = new ZipEntry(srcPath.relativize(dir).toString() + "/");
                        zos.putNextEntry(zipEntry);
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        return zipPath.toFile();
    }

    public void setFile(File f) {
        this.file = f;
    }

    public File getFile() {
        return this.file;
    }

    public File getZipFile() {
        return this.zipFile;
    }
}
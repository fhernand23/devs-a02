package model.to.code.transformation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TransformationData {

    public static final int FAIL = 0;
    public static final int SUCCESS = 1;

    private File model;
    private Path sourceProject;
    private String projectName;
    private String RDEVSLibraryName = "rdevs.implementation.jar";
    private String CoreDEVSLibraryName = "CoreDEVS.jar";
    private Path emfInstance;

    public boolean validProjectName() {
        return !projectName.isEmpty();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(File modelFile) {
        this.model = modelFile;
        this.sourceProject = modelFile.toPath().getParent();
        String name = modelFile.getName();
        name = name.substring(0, name.lastIndexOf('.'));
        this.emfInstance = sourceProject.resolve(name + ".metamodel");
        projectName = name.toLowerCase() + ".rdevsimplementation";
    }

    public File getModel() {
        return this.model;
    }

    public Path getRDEVSLibraryPath() {
        return Paths.get(this.RDEVSLibraryName);
    }

    public Path getCoreDEVSLibraryPath() {
        return Paths.get(this.CoreDEVSLibraryName);
    }

    public Path getRDEVSLibraryFullPath(Path projectPath) {
        return projectPath.resolve(this.RDEVSLibraryName);
    }

    public Path getCoreDEVSLibraryFullPath(Path projectPath) {
        return projectPath.resolve(this.CoreDEVSLibraryName);
    }

    public Path getEmfInstance() {
        return this.emfInstance;
    }

    public Path getSourceProjectName() {
        return this.sourceProject;
    }

    public void createTempModelFile(String content) throws IOException {
        Path tempFile = Files.createTempFile("model_", ".metamodel");
        Files.write(tempFile, content.getBytes());
        setProjectName(tempFile.toFile());
    }
}

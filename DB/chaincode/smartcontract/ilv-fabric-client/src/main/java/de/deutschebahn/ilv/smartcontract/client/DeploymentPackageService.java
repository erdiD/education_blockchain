package de.deutschebahn.ilv.smartcontract.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by AlbertLacambraBasil on 11.09.2017.
 */
public class DeploymentPackageService {
    private static final Logger logger = Logger.getLogger(DeploymentPackageService.class.getName());
    private static final String TARGET_LOCATION_KEY = "TARGET_LOCATION";
    private String targetLocation = "D:/DEV/ILV/chaincode/smartcontract/ilv-fabric-client/chaincode-deployment/src";

    public static void main(String[] args) {
        new DeploymentPackageService().copyAll(Paths.get("D:/DEV/ILV/chaincode/smartcontract/demand/src/main/java"));
    }

    public DeploymentPackageService() {
    }

    public void copyAll(Path chaincodePath) {
        Path chaincodeTargetPath = Paths.get(getTargetLocation());
        if (Files.exists(chaincodeTargetPath)) {
            deletePath(chaincodeTargetPath);
        }

        Path domainPath = Paths.get("D:\\DEV\\ILV\\chaincode\\smartcontract\\domain\\src\\main\\java");
        Path commonPath = Paths.get("D:\\DEV\\ILV\\chaincode\\smartcontract\\commons\\src\\main\\java");
        Path businessPath = Paths.get("D:\\DEV\\ILV\\chaincode\\smartcontract\\businessframework\\src\\main\\java");

        if (!Files.exists(domainPath)) {
            throw new RuntimeException("domain path do not exists");
        }

        copyDirectory(domainPath, chaincodeTargetPath);
        copyDirectory(commonPath, chaincodeTargetPath);
        copyDirectory(businessPath, chaincodeTargetPath);
        copyDirectory(chaincodePath, chaincodeTargetPath);
    }

    private void copyDirectory(Path sourceRootPath, Path targetRootPath) {
        try {
            Stream<Path> allFilesPathStream = Files.walk(sourceRootPath);
            allFilesPathStream.forEach(sourcePath -> copy(sourceRootPath, targetRootPath, sourcePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    void copy(Path sourceRootPath, Path targetRootPath, Path sourcePath) {

        try {
            Path targetPath = buildTargetPath(targetRootPath, sourceRootPath, sourcePath);
            if (Files.exists(targetPath)) {
                logger.info("[copy] File exists. Ignoring. Path=" + targetPath.toString());
                return;
            }
            Files.copy(sourcePath, targetPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Path buildTargetPath(Path targetRootPath, Path sourceRootPath, Path sourcePath) {
        String targetPath = targetRootPath.toString();
        String srcPath = sourceRootPath.relativize(sourcePath).toString().replace("..\\", "");
        targetPath += "\\" + srcPath;
        logger.info(String.format("[accept] %s to %s", sourcePath.toString(), Paths.get(targetPath).toString()));

        return Paths.get(targetPath);
    }

    void deletePath(Path pathToDelete) {
        try {
            Files.walk(pathToDelete)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTargetLocation() {
        String location = System.getenv(TARGET_LOCATION_KEY);
        if (location != null && !location.isEmpty()) {
            targetLocation = location;
        }

        logger.info("[getTargetLocation] Using target location=" + targetLocation);
        return targetLocation;
    }
}

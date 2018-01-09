package de.deutschebahn.ilv.smartcontract.client;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by AlbertLacambraBasil on 12.09.2017.
 */
public class DeploymentPackageServiceTest {

    DeploymentPackageService cut;

    @Before
    public void setUp() throws Exception {
        cut = new DeploymentPackageService();
    }

    @Test
    public void buildTargetPath() throws Exception {
        Path sourceRootPath = Paths.get("D:\\DEV\\ILV\\chaincode\\smartcontract\\demand\\server\\src\\main\\java");
        Path targetRootPath = Paths.get("D:\\DEV\\ILV\\chaincode\\smartcontract\\ilv-fabric-client\\chaincode-deployment\\targets");
        Path sourcePath = Paths.get("D:\\DEV\\ILV\\chaincode\\smartcontract\\demand\\server\\src\\main\\java\\de\\deutschebahn\\ilv\\smartcontract\\business\\demand\\DemandFacade.java");
        Path targetPath = cut.buildTargetPath(targetRootPath, sourceRootPath, sourcePath);
        assertThat(targetPath.toString(), is("D:\\DEV\\ILV\\chaincode\\smartcontract\\ilv-fabric-client\\chaincode-deployment\\targets\\de\\deutschebahn\\ilv\\smartcontract\\business\\demand\\DemandFacade.java"));
    }

}
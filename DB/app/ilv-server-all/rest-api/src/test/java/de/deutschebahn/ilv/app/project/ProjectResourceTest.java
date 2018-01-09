package de.deutschebahn.ilv.app.project;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.bussinesobject.*;
import de.deutschebahn.ilv.bussinesobject.delivery.DeliveryFacade;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by AlbertLacambraBasil on 27.10.2017.
 */
public class ProjectResourceTest {

    ProjectResource cut;

    @Before
    public void setUp() throws Exception {
        cut = new ProjectResource();
        cut.demandFacade = mock(DemandFacade.class);
        cut.offerFacade =  mock(OfferFacade.class);
        cut.projectFacade =  mock(ProjectFacade.class);
        cut.contractFacade =  mock(ContractFacade.class);
        cut.deliveryFacade =  mock(DeliveryFacade.class);
        cut.objectHistoryService = mock(ObjectHistoryService.class);
    }

    @Test(expected = ClientException.class)
    public void expectProjectNotFound() throws Exception {
        when(cut.demandFacade.getById(any())).thenReturn(Optional.empty());
        when(cut.offerFacade.getOffersOfProject(any())).thenReturn(Collections.emptyList());
        when(cut.contractFacade.getContractByProject(anyString())).thenReturn(Optional.empty());
        when(cut.deliveryFacade.getDeliveryByProjectId(any())).thenReturn(Optional.empty());
        String pid = "P_" + UUID.randomUUID().toString() + "_D";
        cut.getProject(pid);
    }

}
package de.deutschebahn.ilv.bussinesobject;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by AlbertLacambraBasil on 04.10.2017.
 */
public class BusinessObjectFacadeTest {

    BusinessObjectFacade<?> cut;


    @Before
    public void setUp() throws Exception {

        cut = new OfferFacade();

    }

    @Test
    public void getProjectId() throws Exception {
        String projectId = cut.getProjectId("P_49cae22a-e088-4a28-b72a-bb0256cbfb54\u0000O_2\u0000Y_15");
        assertThat(projectId, is("P_49cae22a-e088-4a28-b72a-bb0256cbfb54"));
    }

}
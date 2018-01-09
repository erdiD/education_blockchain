package de.deutschebahn.ilv.app;

import javax.ejb.EJBException;
import javax.ws.rs.ext.Provider;

@Provider
public class EJBExceptionMapper extends InternalExceptionMapper<EJBException> {
}

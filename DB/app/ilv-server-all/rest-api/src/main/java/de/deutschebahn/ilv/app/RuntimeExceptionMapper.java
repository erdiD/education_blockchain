package de.deutschebahn.ilv.app;

import javax.ws.rs.ext.Provider;

@Provider
public class RuntimeExceptionMapper extends InternalExceptionMapper<RuntimeException> {
}
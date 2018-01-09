package de.deutschebahn.ilv.app.project;

/**
 * Created by AlbertLacambraBasil on 09.06.2017.
 */
public enum ProjectTask {
	
	/**  Demand created, but not published */
    DEMAND_IN_PROGRESS, 
    
	/**  Demand Canceled */
    DEMAND_DENIED, 
    
    /** Demand created and published, no offer existing */
    DEMAND_PUBLISHED, 
    
    /** Offer was created, but not send to approval */
    OFFER_IN_PROGRESS,
    
    /** Offer was send for internal approval */
    OFFER_WAITING_FOR_APPROVAL,  
    
    /** Offer was internally approved, and is now visible to the Customer - Currently waiting for Acceptance */
    OFFER_OFFERED,  
    
    /** Customer accepted the offer - waiting now for comm. and tech. Approvement */
    OFFER_ACCEPTED,
    
    /** Offer approved, contract will be created*/
    OFFER_COMPLETED,
    
    /** Offer not accepted or closed at some point */
    OFFER_DENIED, 
    
    /** Contract was Signed by both participants */
    CONTRACT_SIGNED,
    
    /** Contract was Rejected or Terminated */
    CONTRACT_DENIED,
    
    /** Should only happen if someone Introduces a new State, that is not mapped on the Project side */
    UNKNOWN
    
    
}

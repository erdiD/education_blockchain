package de.deutschebahn.ilv.smartcontract.commons;

/**
 * Created by AlbertLacambraBasil on 13.10.2017.
 */
public enum GenericActions {
    getById,
    create,
    update,
    delete,
    objectUpdated,
    fireAction,
    canRead,
    getAll,
    getByProjectId,
    canPerformDirectActionOnProjectOffers,
    canFireAction,

    //Delivery
    saveNewDeliveryEntries,

    //Project
    getNextId,
    setNextId,

    addPsp,
    attachEntity,
    canAcceptOffers;
}


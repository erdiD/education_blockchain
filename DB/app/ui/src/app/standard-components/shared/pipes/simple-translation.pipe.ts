import { Pipe, PipeTransform } from '@angular/core';


/**
 * A simple translation currently just used for states and contract types.
 */
@Pipe({name: 'simpleTranslation'})
export class SimpleTranslationPipe implements PipeTransform {

  transform(subject: string): string {

    let translation = subject;

    switch(subject) {
      // States
      case "OPENED": {
        translation = "Geöffnet";
        break;
      }
      case "SUBMITTED": {
        translation = "Veröffentlicht";
        break;
      }
      case "LOCKED": {
        translation = "Abgeschlossen";
        break;
      }
      case "CLOSED": {
        translation = "Abgebrochen";
        break;
      }
      case "COMPLETED": {
        translation = "Genehmigt";
        break;
      }
      // offer state
      case "WAITING": {
        translation = "Warte auf Genehmigung";
        break;
      }
      case "OFFERED": {
        translation = "Angebot abgegeben";
        break;
      }
      case "ACCEPTED": {
        translation = "Angebot bestätigt";
        break;
      }
      case "COMM_APPROVED": {
        translation = "Fachliche Genehmigung";
        break;
      }
      case "TECH_APPROVED": {
        translation = "Technische Genehmigung";
        break;
      }
      // contract state
      case "CONTRACT_CREATED": {
        translation = "Vertrag erstellt";
        break;
      }
      case "CONTRACT_CLIENT_SIGNED": {
        translation = "Kundenunterschrift"; // > 21 chars = linebreak
        break;
      }
      case "CONTRACT_SUPPLIER_SIGNED": {
        translation = "Lieferantenunterschrift"; // > 21 chars = linebreak
        break;
      }
      // Contract Types
      case "WORK_AND_SERVICE_CONTRACT": {
        translation = "Werkvertrag";
        break;
      }
      case "SERVICE_CONTRACT": {
        translation = "Dienstvertrag";
        break;
      }
      case "SUBSCRIPTION_CONTRACT": {
        translation = "Leistungsschein";
        break;
      }
      // Payment Types
      case "MONTHLY": {
        translation = "Monatlich";
        break;
      }
      case "OBJECTIVE_ORIENTED": {
        translation = "Meilenstein-Plan";
        break;
      }

      // Project Tasks

      /**  Demand created, but not published */
      case "DEMAND_IN_PROGRESS": {
        translation = "Warte auf Veröffentlichung";
        break;
      }

      case "DEMAND_DENIED": {
        translation = "Bedarf Abgebrochen";
        break;
      }

      /** Demand created and published, no offer existing */
      case "DEMAND_PUBLISHED": {
        translation = "Warte auf Angebot";
        break;
      }

      /** Offer was created, but not send to approval */
      case "OFFER_IN_PROGRESS": {
        translation = "Warte auf Fertigstelltung des Angebots";
        break;
      }

      /** Offer was send for internal approval */
      case "OFFER_WAITING_FOR_APPROVAL": {
        translation = "Warte auf interne Genehmigung des Angebots";
        break;
      }

      /** Offer was internally approved, and is now visible to the Customer - Currently waiting for Acceptance */
      case "OFFER_OFFERED": {
        translation = "Angebot liegt vor, warte auf Bestätigung des Angebots";
        break;
      }

      /** Customer accepted the offer - waiting now for comm. and tech. Approvement */
      case "OFFER_ACCEPTED": {
        translation = "Angebot bestätigt, warte auf Genehmigung";
        break;
      }

      /** Offer approved, contract will be created */
      case "OFFER_COMPLETED": {
        translation = "Vertrag erstellt, warte auf Vertragsunterzeichnung";
        break;
      }

      /** Offer not accepted or closed at some point */
      case "OFFER_DENIED": {
        translation = "Angebot wurde abgelehnt";
        break;
      }

      case "CONTRACT_SIGNED": {
        translation = "Projekt in der Durchführungsphase";
        break;
      }

      case "CONTRACT_DENIED": {
        translation = "Vertrag wurde abgelehnt";
        break;
      }

      case "UNKNOWN": {
        translation = "Unbekannt";
        break;
      }

      default: {
        //statements;
        break;
      }
    }

    return translation;
  }
}

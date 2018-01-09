import {IProject as IProjectProject} from "../../project/interfaces/IProject";
import {IProject as IMarketProject} from "../../market/interfaces/IProject";
import {IProject as IDemandProject} from "../../demand/interfaces/IProject";
import {IProject as IContractProject} from "../../contract/interfaces/IProject";

import {Organization} from "./organization.model";
import {User} from "./user.model";
import {Demand} from "./demand.model";
import {Offer} from "./offer.model";
import {isNullOrUndefined} from "util";
import {Tasks} from "../types/tasks.types";

export class Project implements IProjectProject, IMarketProject, IDemandProject, IContractProject{

  public id: string;
  public state: string;
  public task: string;
  public name: string;
  public budget: string;
  public role: string;
  public ownerOrganization: Organization;
  public creator: User;
  public lastModified:Date;
  public demand: Demand;
  public offers: Offer[];
  public isDraft: boolean;

  constructor(){}

  public static fromJSON(res: any): Project {
    let project = new Project();

    project.id = res.id;
    project.name = res.name;
    project.role = res.role ||'';
    project.ownerOrganization = res.ownerOrganization;
    project.budget = res.budget || '';
    project.lastModified = new Date(res.lastModified);
    if(!isNullOrUndefined(project.demand))project.demand = Demand.fromJSON(res.demand);

    if(!isNullOrUndefined(project.offers))project.offers = Project.mapOffers(res.demand.offers);
    project.state = res.state;
    project.task = Project.getFormattedTask(res.task);
    return project;
  }

  private static mapOffers(demandOffers:any) : Offer[] {
    let offers:Offer[] = [];
    demandOffers.forEach(offer => offers.push(Offer.fromJSON(offer)));

    return offers;
  }

  public static getFormattedTask(task:string):string {
    switch (task) {
      case Tasks.COMPLETE_DEMAND:
        return "veröffentlichen / bearbeiten";
      case Tasks.DEMAND_IN_PROGRESS:
        return "in Bearbeitung";
      case Tasks.WAIT_FOR_ACCEPTANCE:
        return "Warte auf Angebote";
      case Tasks.WAIT_FOR_OFFER:
        return "Warte auf Angebote";
      case Tasks.WAIT_FOR_COMPLETION:
        return "Warte";
      case Tasks.ACCEPT_OFFER:
        return "Angebote verfügbar";
      case Tasks.CORRECT_DEMAND:
        return "Bedarf überarbeiten";
      case Tasks.COMPLETED:
        return "Abgeschlossen";
      case Tasks.ACTIVATE_DEMAND:
        return "Bedarf veröffentlichen";
      case Tasks.CLOSED:
        return "Geschlossen";
      case Tasks.REWORK_OFFER:
        return "Angebot überarbeiten";
      case Tasks.ACTIVATE_OFFER:
        return "Angebot aktivieren";
      case Tasks.ACCEPT_DEMAND:
        return "Angebot abgeben";
      case Tasks.COMPLETE_OFFER:
        return "Angebot abgeben";
      case Tasks.APPROVE_OFFER:
        return "Angebot genehmigen";
      default:
        return "keine";
    }
  }

}

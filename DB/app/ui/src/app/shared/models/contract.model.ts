import {IContract} from "../../contract/interfaces/IContract";
import {IContract as IProjectContract} from "../../project/interfaces/IContract";
import {HistorySection} from "./history-section.model";
import {Organization} from "./organization.model";
import {User} from "./user.model";
import {EntityTypes} from "../types/entitiy.types";
import {isNullOrUndefined} from "util";
import {Demand} from "./demand.model";
import {Offer} from "./offer.model";
import {isEmpty} from "rxjs/operator/isEmpty";

export class Contract implements IContract, IProjectContract{

  public type: string = EntityTypes.ENTITY_TYPE_CONTRACT;
  public typeIcon: string = 'assets/img/icons/icon_Sign.png';
  public typeTitleText: string = 'Vertrag';

  public id: string;
  public projectID: string;
  public creator: User;
  public ownerOrg: Organization;
  public creationTime: Date;
  public description: string;
  public attachments: string[];
  public endDate: Date;
  public messageBoardUrl: string;
  public availableActions: string;
  public state: string;
  public action: string;
  public progressHistory: HistorySection[];

  public demand: Demand;
  public offer: Offer;

  public static fromJSON(data: any): Contract {

    // if (isNullOrUndefined(data) || Object.getOwnPropertyNames(data).length === 0){
    //   return new Contract();
    // }

    let contract = new Contract();
    contract.id = data.id.toString();
    contract.availableActions = data.availableActions || [];
    contract.state = data.state;
    contract.demand = Demand.fromJSON(data.demand);
    contract.offer = Offer.fromJSON(data.offer);
    contract.projectID = contract.demand.id;

    if (!isNullOrUndefined(data.history)) {
      contract.progressHistory = [];
      data.history.forEach(historySection => {
        contract.progressHistory.push(HistorySection.fromJSON(historySection));
      });
    }

    return contract;
  }

  public toJSON (): Object {
    let outputOb = {};

    /*outputOb['id'] = this.id || '';
    outputOb['description'] = this.description || '';
    outputOb['descriptionDocumentURL'] = this.descriptionDocumentURL || '';
    outputOb['action'] = this.action || '';
    outputOb['messageBoardUrl'] = this.messageBoardUrl || '';

    // outputOb['recipient'] = this.recipient || '';*/

    return outputOb;
  }

  public static getDateFromFormattedString (dateString: string): Date {
    let ser = dateString.split('.');
    let date = new Date();

    date.setDate(Number(ser[0]));
    date.setMonth(Number(ser[1]));
    date.setFullYear(Number(ser[2]));

    return date;
  }

}

import {IOffer as IProjectOffer} from "../../project/interfaces/IOffer";
import {IOffer as IOffer} from "../../offer/interfaces/IOffer";
import {IOffer as IContractOffer} from "../../contract/interfaces/IOffer";

import {User} from "./user.model";
import {Organization} from "./organization.model";
import {IHistorySection} from "../../project/interfaces/IHistorySection";
import {isNullOrUndefined} from "util";
import {EntityTypes} from "../types/entitiy.types";
import {HistorySection} from "./history-section.model";

export class Offer implements IProjectOffer, IContractOffer, IOffer {


  public type: string = EntityTypes.ENTITY_TYPE_OFFER;
  public typeIcon: string = 'assets/img/icons/icon_Angebot.png';
  public typeTitleText: string = 'Angebot';

  public demandID: string;
  public id: string;
  public price: string;
  public description: string;
  public messageBoardUrl: string;
  public creationTime: Date;
  public creator: User;
  public ownerOrg: Organization;
  public endDate: Date;
  public startDate: Date;
  public state: string;
  public paymentType: string;
  public contractType: string; //TODO: Missing in REST-RESPONSE
  public progressHistory: IHistorySection[];
  public availableActions: string;
  public attachments: string[];

  public projectID: string;
  public action: string;


  public static fromJSON (data: any) {
    let offer = new Offer();
    offer.demandID = data.demandId;
    offer.id = String(data.id);
    offer.price = data.price.substring(0, data.price.length - 3);
    offer.description = data.description;
    offer.messageBoardUrl = data.messageBoardUrl || '';
    offer.creationTime = Offer.getDateFromFormattedString(data.creationDate);
    offer.creator = User.fromJSON(data.creator);
    offer.ownerOrg = <Organization>data.ownerOrg;
    offer.endDate = Offer.getDateFromFormattedString(data.deliveryDate);
    offer.startDate = Offer.getDateFromFormattedString(data.startDate);
    offer.state = data.state;
    offer.paymentType = data.paymentType;
    offer.contractType = data.contractType || '';

    if (!isNullOrUndefined(data.history)) {
      offer.progressHistory = [];
      data.history.forEach(historySection => {
        offer.progressHistory.push(HistorySection.fromJSON(historySection));
      })
    }

    offer.availableActions = data.availableActions || [];
    offer.attachments = data.attachments || [];
    offer.projectID = data.demandId;

    return offer;
  }

  public toJSON (): Object {
    let outputOb = {};
    outputOb['demandId'] = this.demandID || '';
    outputOb['id'] = this.id || '';
    outputOb['price'] = this.price || '';
    outputOb['description'] = this.description || '';
    outputOb['messageBoardUrl'] = this.messageBoardUrl || '';
    outputOb['deliveryDate'] = Offer.getFormattedStringFromDate(this.endDate);
    outputOb['startDate'] = Offer.getFormattedStringFromDate(this.startDate);
    outputOb['paymentType'] = this.paymentType || '';
    outputOb['contractType'] = this.contractType || '';

    outputOb['action'] = this.action || '';
    return outputOb;
  }


  public static getDateFromFormattedString (dateString: string): Date {
    let ser = dateString.split('.');
    let date = new Date();

    date.setDate(Number(ser[0]));
    date.setMonth(Number(ser[1])-1);
    date.setFullYear(Number(ser[2]));

    return date;
  }

  public static getFormattedStringFromDate (date:Date): string {
    if(isNullOrUndefined(date)){
      return undefined;
    }

    let day: number = date.getDate();
    let month: number = date.getMonth() + 1;

    return day + "." + month + "." + date.getFullYear();
  }

}

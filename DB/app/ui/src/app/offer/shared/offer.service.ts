import { Injectable } from '@angular/core';
import {ApiService} from "../../shared/services/api.service";
import {Observable} from "rxjs/Rx";
import {IProject} from "../interfaces/IProject";
import {IDemand} from "../interfaces/IDemand";
import {Demand} from "../../shared/models/demand.model";
import {Project} from "../../shared/models/project.model";
import {IOffer} from "../interfaces/IOffer";
import {Offer} from "../../shared/models/offer.model";


import {Response} from "@angular/http";

@Injectable()
export class OfferService {

  constructor(private apiService: ApiService) { }

  public getProject (projectID: string): Observable<IProject> {

    const getOfferUrl = this.apiService.get(ApiService.GET_PROJECT_BY_ID_REST_URL + projectID);
    return this.apiService.intercept(getOfferUrl)
      .map( res => res.json())
      .map( resJs => this.mapDetailProjectFromJSON(resJs));
  }


  public createOffer(offer: IOffer): Observable<Response> {
    return this.apiService.post(ApiService.CREATE_OFFER_REST_URL, offer.toJSON());
  }

  public updateOffer(offer: IOffer): Observable<Response> {
    return this.apiService.put(ApiService.UPDATE_OFFER_REST_URL + "/" + offer.id, offer.toJSON());
  }

  public updateOfferState(offerId: string, action: string): Observable<Response> {
    return this.apiService.intercept(this.apiService.put(ApiService.UPDATE_OFFER_STATE_REST_URL + offerId + "?action=" + action, {}));
  }

  public getUploadUrl (offerId: string){
    return this.apiService.apiUrl + ApiService.UPDATE_OFFER_REST_URL + "/" + offerId + '/attachment';
  }

  public getDownloadUrl (offerId: string, fileId: string){
    return this.apiService.apiUrl + ApiService.UPDATE_OFFER_REST_URL + "/" + offerId + '/attachment/' + fileId;
  }

  private mapDetailProjectFromJSON (responseJSON): IProject {
    let demand: IDemand = Demand.fromJSON(responseJSON.demand);

    const iOffers: IOffer[] = responseJSON.offers;
    const realOffers = iOffers.map( o => Offer.fromJSON(o));
    demand.offers = realOffers;

    let project: IProject = Project.fromJSON(responseJSON);
    project.demand = demand;

    project.id = demand.id;
    project.state = demand.state;
    project.name = demand.name;
    project.budget = demand.budget;

    return project;
  }


  stringToDate(_date,_format,_delimiter) {
    const formatLowerCase=_format.toLowerCase();
    const formatItems=formatLowerCase.split(_delimiter);
    const dateItems=_date.split(_delimiter);
    const monthIndex=formatItems.indexOf("mm");
    const dayIndex=formatItems.indexOf("dd");
    const yearIndex=formatItems.indexOf("yyyy");
    let month=parseInt(dateItems[monthIndex]);
    month-=1;
    const formatedDate = new Date(dateItems[yearIndex],month,dateItems[dayIndex]);
    return formatedDate;
  }


}

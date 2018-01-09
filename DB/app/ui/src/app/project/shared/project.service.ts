import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Rx";

import {ApiService} from "../../shared/services/api.service";
import {IProject} from "../interfaces/IProject";
import {IDemand} from "../interfaces/IDemand";
import {Demand} from "../../shared/models/demand.model";
import {Project} from "../../shared/models/project.model";
import {IContract} from "../interfaces/IContract";
import {Contract} from "../../shared/models/contract.model";
import {IOffer} from "../interfaces/IOffer";
import {Offer} from "../../shared/models/offer.model";
import {ReplaySubject} from "rxjs/ReplaySubject";
import {IFullProject} from "../interfaces/IFullProject";
import {IDelivery} from "../interfaces/IDelivery";


@Injectable()
export class ProjectService {
  get currentProjectId(): string {
    return this._currentProjectId;
  }

  set currentProjectId(value: string) {
    this._currentProjectId = value;
  }

  constructor (private apiService: ApiService) {
  }

  private _currentProject = new ReplaySubject(1);

  private _currentDemand: IDemand = new Demand();
  private _currentOffers: IOffer[] = [];
  private _currentContract: IContract = new Contract();
  private _currentDelivery: IDelivery;
  private _currentProjectId: string;


  public getProjects (demandID: string): Observable<IProject> {
    // return this.apiService.intercept(this.apiService.get(ApiService.GET_PROJECT_BY_ID_REST_URL + demandID )).map(res => res.json());
    return this.apiService.intercept(this.apiService.get(ApiService.GET_PROJECT_BY_ID_REST_URL)).map(res => res.json());
  }

  public getProjectById( projectID: string ){
    this._currentProjectId = projectID;
    const getProjectUrl = ApiService.GET_PROJECT_BY_ID_REST_URL + projectID;
    return this.apiService.get(getProjectUrl)
      .map( res => res.json())
      .map( resJs => {
        return this.extractSubObjects(resJs)
          .then( () => {
            console.log("-- sending new extracted project.next");
            this.currentProject.next(resJs)
            return resJs;
          })
      });
  }

  private extractSubObjects(project: any): Promise<any> {
   return new Promise( (resove, reject) => {
     resove( Demand.fromJSON(project['demand']) );
    }).then( (extractedDemand: IDemand) =>{
     this._currentDemand = extractedDemand;
     const iOffers: IOffer[] = project.offers;
     return iOffers.map( o => Offer.fromJSON(o))
   }).then((extractedOffers: IOffer[]) => {
        this._currentOffers = extractedOffers;
        if (Object.getOwnPropertyNames(project.contract).length > 0){
          return Contract.fromJSON(project.contract);
        } else {
          return null;
        }
    }).then( (extractedContract: IContract) => {
     this._currentContract = extractedContract;
     this._currentDelivery = project.delivery;
     // this._currentProjectId = project.id || project.demand.id;
   });
  }


  public getDemand (projectID: string): Observable<IProject> {

    if (this.currentProject.isEmpty) {
      this.getProjectById(projectID);
    }

    console.log("## project.service[getDemand] - started! - this should not be used anymore");

    const getProjectUrl = this.apiService.get(ApiService.GET_PROJECT_BY_ID_REST_URL + projectID);
    return this.apiService.intercept(getProjectUrl)
      .map( res => res.json())
      .map( resJs => this.mapDetailProjectFromJSON(resJs));
  }

  // TODO: this should be removed
  public getContractByDemandID (demandID: string): Observable<IContract> {
    console.log("Project.Service [getContractByDemandID] started, is this realy needed?");

    const gotProject = this.apiService.get(ApiService.GET_PROJECT_BY_ID_REST_URL + demandID );
    return this.apiService.intercept(gotProject)
      .map( res => res.json())
      .map( resJs => Contract.fromJSON(resJs.contract));
  }

  public getContract (contractID: string): Observable<IContract> {
    const getProjectUrl = this.apiService.get(ApiService.GET_PROJECT_BY_ID_REST_URL + contractID + "##4");
    return this.apiService.intercept(getProjectUrl)
      .map( res => res.json())
      // .map( resJs => Contract.fromJSON(resJs));
      .map( resJs => Contract.fromJSON(resJs.contract));
  }

  public updateContractState (contractId: string, action: string, message?:string): Observable<Response> {
    return this.apiService.intercept(this.apiService.put(ApiService.UPDATE_CONTRACT_STATE_REST_URL + contractId + "?action=" + action, {}));
  }

  public updateDemandState (demandId: string, action: string, message?:string): Observable<Response> {
    return this.apiService.intercept(this.apiService.put(ApiService.UPDATE_DEMAND_STATE_REST_URL + demandId + "?action=" + action, {}));
  }

  public updateOfferState (offerId: string, action: string, message?:string): Observable<Response> {
    return this.apiService.intercept(this.apiService.put(ApiService.UPDATE_OFFER_STATE_REST_URL + offerId + "?action=" + action, {}));
  }

  //TODO: NA - Reorganize Remapping of Project <-> Demand ?
  private mapDetailProjectFromJSON (responseJSON): IProject {

    console.log("## project.service.mapDetailProjectFromJSON responseJSON" , responseJSON);
    // this.currentProject.next(responseJSON);


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

    console.log("## project.service.mapDetailProjectFromJSON project" , project);

    return project;
  }


  get currentDemand(): IDemand {
    return this._currentDemand;
  }

  set currentDemand(value: IDemand) {
    this._currentDemand = value;
  }

  get currentOffers(): IOffer[] {
    return this._currentOffers;
  }

  set currentOffers(value: IOffer[]) {
    this._currentOffers = value;
  }

  get currentContract(): IContract {
    return this._currentContract;
  }

  set currentContract(value: IContract) {
    this._currentContract = value;
  }

  get currentDelivery(): IDelivery {
    return this._currentDelivery;
  }

  set currentDelivery(value: IDelivery) {
    this._currentDelivery = value;
  }

  get currentProject(): ReplaySubject<IFullProject> {
    return this._currentProject;
  }

  set currentProject(value: ReplaySubject<IFullProject>) {
    this._currentProject = value;
  }

}

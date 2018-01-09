import { Injectable } from '@angular/core';
import {ApiService} from "../../shared/services/api.service";
import {Observable} from "rxjs/Rx";
import {IProject} from "../interfaces/IProject";
import {IDemand} from "../interfaces/IDemand";
import {Demand} from "../../shared/models/demand.model";
import {Project} from "../../shared/models/project.model";
import {IOffer} from "app/project/interfaces/IOffer";
import {Offer} from "../../shared/models/offer.model";

@Injectable()
export class DemandService {

  constructor(private apiService: ApiService) { }

  public getProject (projectID: string): Observable<IProject> {

    console.log("-- [DemandService] getProjects");

    const getDemandUrl = this.apiService.get(ApiService.GET_PROJECT_BY_ID_REST_URL + projectID + "#1d");
    return this.apiService.intercept(getDemandUrl)
      .map( res => res.json())
      .map( resJs => this.mapDetailProjectFromJSON(resJs));
  }

  public createDemand(demand: IDemand, directSubmit:string = 'false'): Observable<Response> {
    return this.apiService.intercept(this.apiService.post(ApiService.CREATE_DEMAND_REST_URL + "?directSubmit=" + directSubmit,
      demand.toJSON()));
  }

  public updateDemand(demand: IDemand): Observable<Response> {
    return this.apiService.intercept(this.apiService.put(ApiService.UPDATE_DEMAND_REST_URL + "/" + demand.id, demand.toJSON()));
  }

  public updateDemandState (demandId: string, action: string): Observable<Response> {
    const stateUpdateUrl = ApiService.UPDATE_DEMAND_STATE_REST_URL + demandId + "?action=" + action;
    console.log("DemandService::updateDemandState - stateUpdateUrl",stateUpdateUrl );
    return this.apiService.intercept(this.apiService.put(stateUpdateUrl, {}));
  }

  public getUploadUrl (demandId: string): string{
    return this.apiService.apiUrl + ApiService.GET_DEMAND_REST_URL + "/" + demandId + '/attachment';
  }

  public getDownloadUrl (demandId: string, fileId: string): string{
    return this.apiService.apiUrl + ApiService.GET_DEMAND_REST_URL + "/" + demandId + '/attachment/' + fileId;
  }

  public getFileNameFromFileId( fileId: string ) : string {
    const idx = fileId.lastIndexOf("_");
    return fileId.substring(idx + 1);
  }

  private mapDetailProjectFromJSON (responseJSON): IProject {
    let demand: IDemand = Demand.fromJSON(responseJSON.demand); // .demand?

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

}

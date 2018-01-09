import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Rx";
import {ApiService} from "../../shared/services/api.service";
import {IProject} from "../interfaces/IProject";
@Injectable()
export class MarketService {

  constructor(private apiService:ApiService) {

  }

  public getProjects(): Observable<IProject[]> {
    /* return Observable.interval(1000).flatMap(() => this.intercept(this.http.get(ProjectDataService.GET_PROJECTS_REST_URL)).map(ProjectDataService.mapProjects));*/
    return this.apiService.intercept(this.apiService.get(ApiService.GET_PROJECTS_REST_URL)).map(res => res.json());
  }

  public getProjectsFrequently(): Observable<IProject[]> {
    return Observable
      .interval(10000)
      .flatMap(() => {
        return this.getProjects();
      });
  }


}

import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Rx";

import {ApiService} from "../../shared/services/api.service";
import {IProject} from "../interfaces/IProject";


@Injectable()
export class StatsService {

  constructor (private apiService: ApiService) {
  }

  public getStats (demandID: string): Observable<IProject> {
    return this.apiService.intercept(this.apiService.get(ApiService.GET_STATS_BY_ID_REST_URL + demandID)).map(res => res.json());
  }

}

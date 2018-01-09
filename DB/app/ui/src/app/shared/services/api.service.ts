import {Injectable} from "@angular/core";
import {Http, RequestOptionsArgs, Response} from "@angular/http";
import {Observable} from "rxjs/Rx";
import {Router} from "@angular/router";


@Injectable()
export class ApiService {

  private apiEndpointMobileApp:string =  'http://matilda.dbe.aws.db.de:8080/ilv/app/resources';
  public apiEndpointWeb:string =  '/ilv/resources';

  public static LOGIN_REST_URL: string = "";
  public static LOGOUT_REST_URL: string = "";
  public static GET_PROJECTS_REST_URL: string = "/project";
  public static GET_MY_PROJECTS_REST_URL: string = "./assets/dummyData/projects.json";
  public static GET_PROJECT_BY_ID_REST_URL: string = "/project/";
  public static GET_DEMAND_REST_URL: string = "/demand";
  public static GET_CONTRACT_BY_DEMAND_ID_REST_URL: string = "/contract/demand/";
  public static GET_CONTRACT_BY_CONTRACT_ID_REST_URL: string = "/contract/";
  public static UPDATE_CONTRACT_STATE_REST_URL: string = "/contract/state/";
  public static GET_USER_REST_URL: string = "/user";
  public static GET_POSSIBLE_RECIPIENTS_REST_URL: string = "";
  public static CREATE_DEMAND_REST_URL: string = "/demand";
  public static UPDATE_DEMAND_REST_URL: string = "/demand";
  public static DELETE_DEMAND_REST_URL: string = "/demand/delete/";
  public static UPDATE_DEMAND_STATE_REST_URL: string = "/demand/state/";
  public static CREATE_OFFER_REST_URL: string = "/offer";
  public static UPDATE_OFFER_REST_URL: string = "/offer";
  public static UPDATE_OFFER_STATE_REST_URL: string = "/offer/state/";
  public static DELETE_OFFER_REST_URL: string = "/offer/delete/";
  public static GET_STATS_BY_ID_REST_URL: string = "/stats/";


  public apiUrl:string;

  constructor(private http: Http, private router:Router) {
    this.apiUrl = this.apiEndpointWeb;
  }

  get(url: string, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.get(this.apiUrl + url, options);
  }

  post(url: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.post(this.apiUrl + url, body, options);
  }

  put(url: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.put(this.apiUrl + url, body, options);
  }

  delete(url: string, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.delete(this.apiUrl + url, options);
  }

  patch(url: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.patch(this.apiUrl + url, body, options);
  };

  head(url: string, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.head(this.apiUrl + url, options);
  }

  options(url: string, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.options(this.apiUrl + url, options);
  }

  public intercept(observable: Observable<any>) {
    return observable.catch(err => {

      if (err.status === 401) {
        this.router.navigate(['/login']);
        return Observable.empty();

      }
    });
  }

  // public getDownloadUrl (demandId: string, fileId: string){
  //   return this.apiUrl + ApiService.GET_DEMAND_REST_URL + "/" + demandId + '/attachment/' + fileId;
  // }
  //
  // public getUploadUrl (demandId: string){
  //   return this.apiUrl + ApiService.GET_DEMAND_REST_URL + "/" + demandId + '/attachment';
  // }


}

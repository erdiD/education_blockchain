import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {ProjectService} from "../shared/project.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Subscription} from "rxjs/Subscription";
import {ProjectHeaderService} from "../../standard-components/services/project-header.service";
import {IDemand} from "../interfaces/IDemand";
import {Demand} from "../../shared/models/demand.model";
import {StatsService} from "../project-stats/stats.service";
import {Stats} from "../../shared/models/stats.model";
import {IProgressChartDataPoint} from "../../standard-components/interfaces/IProgressChartDataPoint";
import {FileUploader} from "ng2-file-upload";
import {ApiService} from "../../shared/services/api.service";
import {IDelivery} from "../interfaces/IDelivery";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ProgressChartComponent} from "../../standard-components/progress-chart/progress-chart.component";
import {DataPoint} from "../../shared/models/stats-datapoint.model";
import {StatusNotificationService} from "../../standard-components/services/status-notification.service";
import {StatusNotification} from "../../standard-components/status-notifications/StatusNotification";


@Component({
  selector: 'app-project-report',
  templateUrl: './project-report.component.html',
  styleUrls: ['./project-report.component.scss'],
  animations: [
    trigger('projectContentState', [
      state('visible', style({
        opacity: 1,
        transform: 'translateY(0px)'
      })),
      state('invisible', style({
        opacity: 0,
        transform: 'translateY(-25px)'
      })),
      state('toLeft', style({
        opacity: 0,
        transform: 'translateX(-100px)'
      })),
      transition('* => toLeft', animate('700ms cubic-bezier(0.23, 1, 0.32, 1)')),
      transition('* => invisible', animate('700ms cubic-bezier(0.23, 1, 0.32, 1)')),
      transition('void => *', [
        style({
          opacity: 0,
          transform: 'translateY(-25px)'
        }),
        animate('700ms cubic-bezier(0.23, 1, 0.32, 1)')
      ])
    ])
  ]
})
export class ProjectReportComponent implements OnInit, OnDestroy {


  public projectContentState: string;
  public currentDemand: IDemand;
  private routeParamsSubscription: Subscription;

  public currentStats: Stats = new Stats();
  public progressChartDatapoints: IProgressChartDataPoint[] = [];

  private deliveryUploadUrl = this.apiService.apiEndpointWeb + "/delivery/";
  public attachmentUploader: FileUploader = new FileUploader({url: this.deliveryUploadUrl});
  public currentDelivery: IDelivery;
  private currentProjectId: string;
  public contractType: string;

  constructor(private projectService:ProjectService,
              private route:ActivatedRoute,
              private headerStateService: ProjectHeaderService,
              private statsService:StatsService,
              private apiService: ApiService,
              private http: HttpClient,
              private router: Router,
              private statusNotificationService: StatusNotificationService) {

  }

  public ngOnInit():void {
    this.routeParamsSubscription  = this.route.params.subscribe(params => {

      // this.projectService.getProjects(params['projectID']).subscribe((res: any) => {
      //   //TODO: DON'T DO THAT HERE!!! ONLY DUMMY WHILE NEW PROJECT STRUCTURE IS NOT IMPLEMENTED YET
      //   this.currentDemand = Demand.fromJSON(res.demand);
      //   this.currentDelivery = res.delivery;
      //   this.currentProjectId = params['projectID'];
      // });

      this.projectService.currentProject.subscribe((res: any) => {
        this.currentDemand = Demand.fromJSON(res.demand);
        this.currentDelivery = res.delivery;
        this.currentProjectId = params['projectID'];
      });


      this.getStatistics( params['projectID'] );
    });

    this.headerStateService.headerStateChanged.subscribe(evt =>{
      if(evt === ProjectHeaderService.HEADER_CLOSED){
        this.closeContent();
      }
    });

  }

  private getStatistics( projectId: string ){
    //  get the statistics
    this.statsService.getStats(projectId).subscribe((res: any) => {

      console.log("-- getStatisticsFromServer: ", res);
      this.contractType = res.contractType;


      this.currentStats = Stats.fromJsonWithId(res, projectId);
      if (this.currentStats){
        this.progressChartDatapoints = this.currentStats.getProgressChartsDataPoints();
      }

      // for DummyData #test
      if (this.progressChartDatapoints.length === 0) {
        this.createDummyData();
      }

    });
  }

  public closeContent ():void {
    this.projectContentState = 'invisible';
  }

  public ngOnDestroy (): void {
    this.routeParamsSubscription.unsubscribe();
  }


  public sendDeliveryTestData( ev: any ):void {

    const currentDeliveryId = this.currentDelivery['id'];
    this.attachmentUploader.setOptions({url: this.deliveryUploadUrl + currentDeliveryId});
    this.attachmentUploader.queue.forEach( file => file.alias = "attachment");

    this.attachmentUploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
      const responseJs = JSON.parse(response);
      if (status === 200) {
        if (responseJs.errors && responseJs.errors.length > 0) {
          const errMsg: string[] = responseJs.errors
                                   .map( e => e.line + ": " + e.error);
          this.showStatusNotification(StatusNotificationService.SHOW_STATUS_ERROR,
                                      errMsg.join( "<br/>" ) );
        }
      }
    };

    // create psp pspId12
    this.createPsp(currentDeliveryId)
        .then(data => {
          console.log("## psp was created, uploading Data");
          this.attachmentUploader.uploadAll();
     }).then( (data) => {
      // reload data
      console.log("data uplaoded getting stats:", data)
      this.getStatistics( this.currentProjectId );
    });
  }

  public clearUploadQueue(): void {
    this.attachmentUploader.clearQueue();
  }

  private createPsp( deliveryId:string ): Promise<any> {
    const examplePsp: string = "pspId12";
    const pspUrl = this.deliveryUploadUrl + deliveryId + "?psps=" + examplePsp ;
    const promise = new Promise((resolve, reject) => {

      const headers: any = {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
          responseType: 'text'
      }

      this.http.put(pspUrl, null, headers).subscribe(data => {
        resolve(data);
      }, errObj => {
        console.log('Something went wrong while tying to create the PSPs', errObj);
        this.showStatusNotification(StatusNotificationService.SHOW_STATUS_ERROR, JSON.parse(errObj.error).error);
      });
    });
    return promise;
  }

  private showStatusNotification( statusNotification: StatusNotification = StatusNotificationService.SHOW_STATUS_ERROR,
                                  additionalString?: string){
    this.statusNotificationService.showStatusNotification(statusNotification, additionalString);
    let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
      subscription.unsubscribe();
      this.router.navigate(['project/' + this.currentProjectId + "/report"]);
    });
  }


  public createDummyData () {

    // this.currentStats.totalProgress = Math.random() * 100;
    // this.currentStats.totalPaid = Math.random() * 100;
    //
    // this.progressChartDatapoints = [];
    // this.progressChartDatapoints.push( new DataPoint( 0 ,"2017-34", Math.random() * 1500) );
    // this.progressChartDatapoints.push( new DataPoint( 0 ,"2017-35", Math.random() * 1500) );
    // this.progressChartDatapoints.push( new DataPoint( 0 ,"2017-36", Math.random() * 1500) );
    // this.progressChartDatapoints.push( new DataPoint( 0 ,"2017-37", Math.random() * 1500) );
    // this.progressChartDatapoints.push( new DataPoint( 0 ,"2017-38", Math.random() * 1500) );
    // this.progressChartDatapoints.push( new DataPoint( 0 ,"2017-39", Math.random() * 1500) );
    // this.progressChartDatapoints.push( new DataPoint( 0 ,"2017-40", Math.random() * 1500) );
    // this.progressChartDatapoints.push( new DataPoint( 0 ,"2017-41", Math.random() * 1500) );
    // this.progressChartDatapoints.push( new DataPoint( 0 ,"2017-42", Math.random() * 1500) );
    // this.progressChartDatapoints.push( new DataPoint( 0 ,"2017-43", Math.random() * 1500) );

  }

}

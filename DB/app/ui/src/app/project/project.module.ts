import { NgModule } from '@angular/core';
import {APP_BASE_HREF, CommonModule} from '@angular/common';
import {StandardComponentsModule} from "../standard-components/standard-components.module";
import {ProjectService} from "./shared/project.service";
import {ProjectComponent} from "./project.component";
import {ProjectEntityComponent} from "./project-entity/project-entity.component";
import {ProjectHistoryComponent} from "./project-history/project-history";
import {ProjectEntityContentDemandComponent} from "./project-entity-content-demand/project-entity-content-demand.component";
import {ProjectEntityContentOfferComponent} from "./project-entity-content-offer/project-entity-content-offer.component";
import {ProjectRouting} from "./project.routing";
import { ProjectReportComponent } from './project-report/project-report.component';
import { ProjectOverviewComponent } from './project-overview/project-overview.component';
import {ProjectHeaderComponent} from "../standard-components/project-header/project-header.component";
import { ProjectEntityContentContractComponent } from './project-entity-content-contract/project-entity-content-contract.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {StatsService} from "./project-stats/stats.service";
import {FileSelectDirective, FileUploader, FileUploadModule} from "ng2-file-upload";
import {HttpClient, HttpClientModule} from "@angular/common/http";

@NgModule({
  imports: [
    CommonModule,
    StandardComponentsModule,
    FormsModule,
    ReactiveFormsModule,
    ProjectRouting,
    FileUploadModule,
    HttpClientModule
  ],
  declarations: [
    ProjectComponent,
    ProjectEntityComponent,
    ProjectEntityContentDemandComponent,
    ProjectEntityContentOfferComponent,
    ProjectHistoryComponent,
    ProjectReportComponent,
    ProjectOverviewComponent,
    ProjectEntityContentContractComponent,
  ],
  providers:[
    ProjectService,
    ProjectHeaderComponent,
    StatsService
  ]
})
export class ProjectModule { }

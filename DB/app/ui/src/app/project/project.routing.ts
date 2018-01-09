import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {AuthService} from "../shared/services/auth.service";
import {ProjectComponent} from "./project.component";
import {ProjectReportComponent} from "./project-report/project-report.component";
import {ProjectOverviewComponent} from "./project-overview/project-overview.component";

const routes: Routes = [
  {path: '',  component: ProjectComponent, children:[
    {path: ':projectID',  component: ProjectOverviewComponent},
    {path: ':projectID/report',  component: ProjectReportComponent}
  ]}
 // {path: ':projectID', canActivate: [AuthService], component: ProjectComponent},
];

export const ProjectRouting: ModuleWithProviders = RouterModule.forChild(routes);

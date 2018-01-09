import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {AuthService} from "../shared/services/auth.service";
import {DemandComponent} from "./demand.component";

const routes: Routes = [
  {path: '', component: DemandComponent},
  {path: ':projectID/edit/:processStepID', component: DemandComponent},
];

export const DemandRouting: ModuleWithProviders = RouterModule.forChild(routes);

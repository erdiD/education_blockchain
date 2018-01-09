import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {AuthService} from "../shared/services/auth.service";
import {ContractComponent} from "./contract.component";

const routes: Routes = [
  {path: '', canActivate: [AuthService], component: ContractComponent},
  {path: ':projectID/edit/:processStepID', canActivate: [AuthService], component: ContractComponent},
];

export const ContractRouting: ModuleWithProviders = RouterModule.forChild(routes);

import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {AuthService} from "../shared/services/auth.service";
import {OfferComponent} from "./offer.component";

const routes: Routes = [
  {path: '', canActivate: [AuthService], component: OfferComponent},
  {path: ':projectID/editOffer/:processStepID', canActivate: [AuthService], component: OfferComponent },
  {path: ':projectID/newOffer', canActivate: [AuthService], component: OfferComponent}
];

export const OfferRouting: ModuleWithProviders = RouterModule.forChild(routes);

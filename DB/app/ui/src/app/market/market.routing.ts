import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {MarketComponent} from "./market.component";

const routes: Routes = [
  { path: '', component: MarketComponent }
];

export const MarketRouting: ModuleWithProviders = RouterModule.forChild(routes);

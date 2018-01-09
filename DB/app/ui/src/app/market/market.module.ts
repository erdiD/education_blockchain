import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MarketComponent} from "./market.component";
import {StandardComponentsModule} from "../standard-components/standard-components.module";
import {MarketListComponent} from "./market-list/market-list.component";
import {SearchFilterPipe} from "./shared/search-filter-pipe";
import {MarketService} from "./shared/market.service";
import {MarketRouting} from "./market.routing";
import {ArraySortPipe} from "./shared/sort-array-pipe";

@NgModule({
  imports: [
    CommonModule,
    StandardComponentsModule,
    MarketRouting
  ],
  declarations: [
    MarketComponent,
    MarketListComponent,
    SearchFilterPipe,
    ArraySortPipe
  ],
  providers: [
    MarketService
  ],
  exports:[
  ]
})
export class MarketModule { }

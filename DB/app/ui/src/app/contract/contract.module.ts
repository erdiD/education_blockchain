import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {StandardComponentsModule} from "../standard-components/standard-components.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {
  MdCheckboxModule, MdInputModule,
  MdNativeDateModule, MdDatepickerModule, MdSelectModule, MdRadioModule, MdDialogModule
} from "@angular/material";
import {ContractRouting} from "./contract.routing";
import { ContractComponent } from './contract.component';
import {ContractService} from "./shared/contract.service";
import { ContractFormComponent } from './contract-form/contract-form.component';

@NgModule({
  imports: [
    CommonModule,
    StandardComponentsModule,
    FormsModule,
    ReactiveFormsModule,
    ContractRouting,
    MdInputModule,
    MdCheckboxModule,
    MdDatepickerModule,
    MdNativeDateModule,
    MdSelectModule,
    MdRadioModule,
    MdDialogModule
  ],
  declarations: [
    ContractComponent,
    ContractFormComponent
  ],
  providers:[
    ContractService
  ]
})
export class ContractModule { }
